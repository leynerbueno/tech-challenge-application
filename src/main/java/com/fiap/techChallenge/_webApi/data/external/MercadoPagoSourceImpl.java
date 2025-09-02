package com.fiap.techChallenge._webApi.data.external;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fiap.techChallenge.core.application.dto.payment.PaymentRequestDTO;
import com.fiap.techChallenge.core.application.dto.payment.PaymentResponseDTO;
import com.fiap.techChallenge.core.domain.entities.order.Order;
import com.fiap.techChallenge.core.domain.enums.PaymentStatus;
import com.fiap.techChallenge.core.domain.exceptions.payment.PaymentException;
import com.fiap.techChallenge.core.interfaces.MercadoPagoSource;

@Component
public class MercadoPagoSourceImpl implements MercadoPagoSource {

    private final String accessToken;
    private final String collectorId;
    private final String posId;

    private static final String QR_URL_TEMPLATE = "https://api.mercadopago.com/instore/orders/qr/seller/collectors/{collector_id}/pos/{pos_id}/qrs";
    private static final String PAYMENT_URL_TEMPLATE = "https://api.mercadopago.com/v1/payments/{paymentId}";
    private final RestTemplate restTemplate = new RestTemplate();

    public MercadoPagoSourceImpl() {
        this.accessToken = System.getenv("MERCADO_PAGO_ACCESS_TOKEN");
        this.collectorId = System.getenv("MERCADO_PAGO_COLLECTOR_ID");
        this.posId = System.getenv("MERCADO_PAGO_POS_ID");
    }

    @Override
    @SuppressWarnings({"null", "UseSpecificCatch", "unused"})
    public PaymentResponseDTO processPayment(PaymentRequestDTO request, Order order) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, Object> payload = buildPayload(request, order);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            String url = QR_URL_TEMPLATE
                    .replace("{collector_id}", collectorId)
                    .replace("{pos_id}", posId);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                String qrData = (String) body.get("qr_data");
                String externalRef = (String) body.get("external_reference");

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }

                return new PaymentResponseDTO("PENDING", request.getOrderId(), qrData);
            } else {
                throw new PaymentException("Erro ao gerar QR Code Mercado Pago: status " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new PaymentException("Erro na integração com Mercado Pago", e);
        }
    }

    private Map<String, Object> buildPayload(PaymentRequestDTO request, Order order) {
        Map<String, Object> payload = new HashMap<>();

        payload.put("external_reference", request.getOrderId());
        payload.put("title", "Pedido " + request.getOrderId());
        payload.put("description", "Compra no tech challenge ");
        payload.put("total_amount", order.getPrice());

        Map<String, Object> item = new HashMap<>();
        item.put("sku_number", "A123K9191938");
        item.put("category", "marketplace");
        item.put("title", "Compra no tech challenge");
        item.put("description", "Pedido realizado no tech challenge");
        item.put("unit_price", order.getPrice());
        item.put("quantity", 1);
        item.put("unit_measure", "unit");
        item.put("total_amount", order.getPrice());

        payload.put("items", List.of(item));

        Map<String, Object> sponsor = new HashMap<>();
        sponsor.put("id", 662208785);
        payload.put("sponsor", sponsor);

        Map<String, Object> cashOut = new HashMap<>();
        cashOut.put("amount", 0);
        payload.put("cash_out", cashOut);

        return payload;
    }

    @Override
    @SuppressWarnings({"null", "unchecked"})
    public String findIdPayment(UUID orderId) {
        List<Map<String, Object>> results = findPaymentsDetailsByOrderId(orderId);

        if (results != null && !results.isEmpty()) {
            Map<String, Object> firstPayment = results.get(0);
            Number paymentId = (Number) firstPayment.get("id");
            return paymentId.toString();
        } else {
            throw new PaymentException("Pagamento ainda não efetuado para o pedido: " + orderId);
        }
    }

    @Override
    @SuppressWarnings({"null", "unchecked"})
    public PaymentStatus checkStatus(UUID orderId) {
        List<Map<String, Object>> results = findPaymentsDetailsByOrderId(orderId);

        if (results == null || results.isEmpty()) {
            throw new PaymentException("Status de Pagamento não encontrado");
        }

        Map<String, Object> mostRecentPayment = results.get(0);
        String mpStatus = (String) mostRecentPayment.get("status");

        return mapStatus(mpStatus);
    }

    @Override
    @SuppressWarnings({"null", "unchecked", "UseSpecificCatch"})
    public UUID findApprovedOrderByPaymentId(String paymentId) {
        try {
            String url = PAYMENT_URL_TEMPLATE.replace("{paymentId}", paymentId);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                String mpStatus = (String) body.get("status");

                PaymentStatus paymentStatus = mapStatus(mpStatus);

                if (paymentStatus == PaymentStatus.APPROVED) {
                    String orderId = (String) body.get("external_reference");
                    return UUID.fromString(orderId);
                } else {
                    throw new PaymentException("Pagamento não efetuado, status atual do pagamento: " + mpStatus);
                }
            } else {
                throw new PaymentException("Erro ao consultar os detalhes do pagamento: " + response.getStatusCode());
            }
        } catch (PaymentException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentException("Erro ao consultar os detalhes do pagamento", e);
        }
    }

    private List<Map<String, Object>> findPaymentsDetailsByOrderId(UUID orderId) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString("https://api.mercadopago.com/v1/payments/search")
                    .queryParam("external_reference", orderId.toString())
                    .queryParam("sort", "date_created")
                    .queryParam("criteria", "desc")
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (List<Map<String, Object>>) response.getBody().get("results");
            } else {
                throw new PaymentException("Erro ao buscar pagamentos: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new PaymentException("Erro ao buscar pagamentos para o orderId: " + orderId, e);
        }
    }

    private PaymentStatus mapStatus(String mpStatus) {
        return switch (mpStatus) {
            case "approved" ->
                PaymentStatus.APPROVED;
            case "rejected" ->
                PaymentStatus.REJECTED;
            case "cancelled" ->
                PaymentStatus.CANCELLED;
            default ->
                PaymentStatus.PENDING;
        };
    }
}
