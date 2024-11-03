package com.ufcg.psoft.pitsa.service.pizza;

import com.ufcg.psoft.pitsa.model.Estabelecimento;

import java.util.List;

public interface PizzaService<I, O> {
    List<O> validarEConverterPizzas(List<I> pizzasRequestDTO, Estabelecimento estabelecimento);

    double calcularPreco(O pizza);
}