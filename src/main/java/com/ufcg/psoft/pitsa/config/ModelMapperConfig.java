package com.ufcg.psoft.pitsa.config;

import com.ufcg.psoft.pitsa.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.pitsa.model.Entregador;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Custom mapping for Entregador -> EntregadorResponseDTO
        modelMapper.typeMap(Entregador.class, EntregadorResponseDTO.class)
                .addMapping(Entregador::getVeiculo, EntregadorResponseDTO::setVeiculoResponseDTO);

        return modelMapper;
    }

}
