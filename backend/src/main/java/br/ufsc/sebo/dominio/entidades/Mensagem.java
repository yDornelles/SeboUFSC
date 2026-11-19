package br.ufsc.sebo.dominio.entidades;

import java.util.Date;

public class Mensagem {
    private Integer idMensagem;
    private String texto;
    private Date dataHora;
    private Usuario remetente;

    public Mensagem(Integer idMensagem, String texto, Date dataHora, Usuario remetente) {
        this.idMensagem = idMensagem;
        this.texto = texto;
        this.dataHora = dataHora;
        this.remetente = remetente;
    }

    public Integer getIdMensagem() {
        return idMensagem;
    }

    public String getTexto() {
        return texto;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public Usuario getRemetente() {
        return remetente;
    }
}