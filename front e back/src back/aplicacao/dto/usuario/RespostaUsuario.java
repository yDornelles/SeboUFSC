package br.ufsc.sebo.aplicacao.dto.usuario;

public class RespostaUsuario {
    private String nomeUsuario;
    private String mensagem;

    public RespostaUsuario(String nomeUsuario, String mensagem) {
        this.nomeUsuario = nomeUsuario;
        this.mensagem = mensagem;
    }

    public String getNomeUsuario() { return nomeUsuario; }
    public String getMensagem() { return mensagem; }
}