package com.ufcg.psoft.pitsa.service.pizza;

import com.ufcg.psoft.pitsa.dto.pizza.PizzaRequestDTO;
import com.ufcg.psoft.pitsa.exception.EntidadeNaoExisteException;
import com.ufcg.psoft.pitsa.exception.QuantidadeDeSaboresInvalidaException;
import com.ufcg.psoft.pitsa.exception.SaborNaoEstaDisponivelException;
import com.ufcg.psoft.pitsa.model.Estabelecimento;
import com.ufcg.psoft.pitsa.model.Pizza;
import com.ufcg.psoft.pitsa.model.Sabor;
import com.ufcg.psoft.pitsa.model.enums.TamanhoPizza;
import com.ufcg.psoft.pitsa.repository.SaborRepository;
import com.ufcg.psoft.pitsa.validator.SaborExistenteValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PizzaServiceImpl implements PizzaService<PizzaRequestDTO, Pizza> {

    @Autowired
    private SaborRepository saborRepository;

    @Autowired
    private SaborExistenteValidator saborExistenteValidator;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<Pizza> validarEConverterPizzas(List<PizzaRequestDTO> pizzasDTO, Estabelecimento estabelecimento) {
        List<Pizza> pizzas = new ArrayList<>();
        for (PizzaRequestDTO pizzaRequestDTO : pizzasDTO) {
            // valida sabor1
            Sabor sabor1 = validarSabor(pizzaRequestDTO.getSabor1(), estabelecimento);

            // valida sabor2
            Sabor sabor2 = null;
            if (pizzaRequestDTO.getSabor2() != null) {
                if (pizzaRequestDTO.getTamanho().equals(TamanhoPizza.MEDIA)) {
                    throw new QuantidadeDeSaboresInvalidaException();
                }
                sabor2 = validarSabor(pizzaRequestDTO.getSabor2(), estabelecimento);
            }

            // cria pizzas a partir do DTO
            Pizza pizza = criarPizza(pizzaRequestDTO, sabor1, sabor2);
            pizzas.add(pizza);
        }
        return pizzas;
    }

    @Override
    public double calcularPreco(Pizza pizza) {
        double precoSabor1;
        double precoSabor2 = 0;

        Sabor sabor1 = pizza.getSabor1();
        Sabor sabor2 = pizza.getSabor2();
        TamanhoPizza tamanho = pizza.getTamanho();

        if (tamanho.equals(TamanhoPizza.GRANDE)) {
            precoSabor1 = sabor1.getPrecoGrande();
            if (sabor2 != null) {
                precoSabor2 = sabor2.getPrecoGrande();
            }
        } else {
            precoSabor1 = sabor1.getPrecoMedia();
        }

        return (sabor2 != null) ? (precoSabor1 + precoSabor2) / 2 : precoSabor1;
    }

    private Sabor validarSabor(String nomeSabor, Estabelecimento estabelecimento) {
        Sabor sabor = saborRepository.findByNome(nomeSabor).orElseThrow(() -> new EntidadeNaoExisteException("sabor"));
        saborExistenteValidator.validar(sabor.getId(), estabelecimento);

        if (Boolean.FALSE.equals(sabor.getDisponivel())) {
            throw new SaborNaoEstaDisponivelException();
        }

        return sabor;
    }

    private Pizza criarPizza(PizzaRequestDTO pizzaRequestDTO, Sabor sabor1, Sabor sabor2) {
        Pizza pizza = modelMapper.map(pizzaRequestDTO, Pizza.class);
        pizza.setSabor1(sabor1);
        pizza.setSabor2(sabor2);
        return pizza;
    }
}