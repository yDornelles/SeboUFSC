package br.ufsc.sebo.dominio.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;

public class Notificacao {

    private static int proximoId = 1;

    private Integer idNotificacao;

    @JsonIgnore
    private Usuario destinatario;
    @JsonIgnoreProperties({"notificacoes", "itensInteresse"})
    private Usuario remetente;
    @JsonIgnoreProperties({"participantes", "aceites"})
    
    private Proposta proposta;
    private String mensagem;
    private boolean lida;
    private Date dataEnvio;

    public Notificacao(Usuario destinatario, String mensagem) {
        this(destinatario, mensagem, null, null);
    }

    public Notificacao(Usuario destinatario, String mensagem, Proposta proposta) {
        this(destinatario, mensagem, proposta, null);
    }

    public Notificacao(Usuario destinatario, String mensagem, Proposta proposta, Usuario remetente) {
        this.idNotificacao = proximoId++;
        this.destinatario = destinatario;
        this.mensagem = mensagem;
        this.proposta = proposta;
        this.remetente = remetente;
        this.lida = false;
        this.dataEnvio = new Date();
    }

    public void marcarComoLida() {
        this.lida = true;
    }

    public Integer getIdNotificacao() { return idNotificacao; }
    public Usuario getDestinatario() { return destinatario; }
    public Usuario getRemetente() { return remetente; }
    public Proposta getProposta() { return proposta; }
    public String getMensagem() { return mensagem; }
    public boolean isLida() { return lida; }
    public Date getDataEnvio() { return dataEnvio; }
}