package com.fiap.techChallenge.core.domain.exceptions.order;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.fiap.techChallenge.core.domain.exceptions.DomainException;
import com.fiap.techChallenge.core.domain.enums.Category;

public class WrongCategoryOrderException extends DomainException {

    public WrongCategoryOrderException() {
        super("Os produtos devem ser selecionados na seguinte ordem: " + getCategoryList());
    }

    private static String getCategoryList() {
        String categoryList = Arrays.stream(Category.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        return categoryList;
    }

}
