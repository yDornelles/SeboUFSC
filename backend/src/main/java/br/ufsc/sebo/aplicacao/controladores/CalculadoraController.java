package br.ufsc.sebo.aplicacao.controladores;

import br.ufsc.sebo.aplicacao.dto.AdicaoRequest;
import br.ufsc.sebo.aplicacao.dto.AdicaoResponse;
import br.ufsc.sebo.dominio.servico.CalculadoraService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calculadora")
@CrossOrigin(origins = "*")
public class CalculadoraController {

    private final CalculadoraService service;

    public CalculadoraController(CalculadoraService service) {
        this.service = service;
    }

    @PostMapping("/somar")
    public AdicaoResponse somarValores(@RequestBody AdicaoRequest request) {
        double a = request.getValorA();
        double b = request.getValorB();
        
        double resultadoPuro = service.realizarSoma(a, b);
        
        return new AdicaoResponse(resultadoPuro);
    }
}