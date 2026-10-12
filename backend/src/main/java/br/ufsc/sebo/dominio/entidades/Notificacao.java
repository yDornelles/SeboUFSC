package br.ufsc.sebo.dominio.entidades;

import java.util.Date;

public class Notificacao {

    private static int proximoId = 1;

    private Integer idNotificacao;
    private String mensagem;
    private boolean lida;
    private Date dataEnvio;

    public Notificacao(String mensagem) {
        this.idNotificacao = proximoId++;
        this.mensagem = mensagem;
        this.lida = false;
        this.dataEnvio = new Date();
    }

    public void marcarComoLida() {
        this.lida = true;
    }

    public Integer getIdNotificacao() { return idNotificacao; }
    public String getMensagem() { return mensagem; }
    public boolean isLida() { return lida; }
    public Date getDataEnvio() { return dataEnvio; }
}
