package br.ufsc.sebo.aplicacao.dto;

public class AdicaoResponse {
    private double resultado;

    public AdicaoResponse(double resultado) {
        this.resultado = resultado;
    }

    public double getResultado() { return resultado; }
    public void setResultado(double resultado) { this.resultado = resultado; }
}