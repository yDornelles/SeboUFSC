package br.ufsc.sebo.dominio.service;

import br.ufsc.sebo.dominio.model.Calculadora;
import org.springframework.stereotype.Service;

@Service
public class CalculadoraService {

    public double realizarSoma(double valorA, double valorB) {
        Calculadora calculadora = new Calculadora();
        return calculadora.calcularSoma(valorA, valorB);
    }
}