package br.ufsc.sebo.dominio.entidades;

import java.util.Date;

public class Notificacao {

    private static int proximoId = 1;

    private Integer idNotificacao;
    private String usuarioId;
    private String remetenteId;
    private String mensagem;
    private boolean lida;
    private Date dataEnvio;
    private Integer propostaId;

    public Notificacao(String usuarioId, String mensagem) {
        this(usuarioId, mensagem, null, null);
    }

    public Notificacao(String usuarioId, String mensagem, Integer propostaId) {
        this(usuarioId, mensagem, propostaId, null);
    }

    public Notificacao(String usuarioId, String mensagem, Integer propostaId, String remetenteId) {
        this.idNotificacao = proximoId++;
        this.usuarioId = usuarioId;
        this.mensagem = mensagem;
        this.lida = false;
        this.dataEnvio = new Date();
        this.propostaId = propostaId;
        this.remetenteId = remetenteId;
    }

    public void marcarComoLida() {
        this.lida = true;
    }

    public Integer getIdNotificacao() { return idNotificacao; }
    public String getUsuarioId() { return usuarioId; }
    public String getRemetenteId() { return remetenteId; } 
    public String getMensagem() { return mensagem; }
    public boolean isLida() { return lida; }
    public Date getDataEnvio() { return dataEnvio; }
    public Integer getPropostaId() { return propostaId; }
}