package br.ufsc.sebo.aplicacao.dto.item;

public class RespostaItem {
    private String id;
    private String mensagem;

    public RespostaItem(String id, String mensagem) {
        this.id = id;
        this.mensagem = mensagem;
    }

    public String getId() { return id; }
    public String getMensagem() { return mensagem; }
}