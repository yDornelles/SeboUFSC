package br.ufsc.sebo.modules.calculadora.service;

import br.ufsc.sebo.modules.calculadora.dto.AdicaoRequest;
import br.ufsc.sebo.modules.calculadora.dto.AdicaoResponse;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraService {
    
    public AdicaoResponse somar(AdicaoRequest request) {
        double soma = request.getValorA() + request.getValorB();
        return new AdicaoResponse(soma);
    }
}