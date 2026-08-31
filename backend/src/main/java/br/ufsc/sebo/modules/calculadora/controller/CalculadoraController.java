package br.ufsc.sebo.modules.calculadora.controller;

import br.ufsc.sebo.modules.calculadora.dto.AdicaoRequest;
import br.ufsc.sebo.modules.calculadora.dto.AdicaoResponse;
import br.ufsc.sebo.modules.calculadora.service.CalculadoraService;
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
        return service.somar(request);
    }

    @GetMapping("/teste")
    public String testarServidor() {
        return "O servidor Java está online!";
    }
}