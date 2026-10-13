package br.ufsc.sebo.aplicacao.dto.interesse;

public class RespostaInteresse {
    private String mensagem;

    public RespostaInteresse(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() { return mensagem; }
}