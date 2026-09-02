package br.ufsc.sebo.dominio.servico;

import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.Calculadora;

@Service
public class CalculadoraService {

    public double realizarSoma(double valorA, double valorB) {
        Calculadora calculadora = new Calculadora();
        return calculadora.calcularSoma(valorA, valorB);
    }
}