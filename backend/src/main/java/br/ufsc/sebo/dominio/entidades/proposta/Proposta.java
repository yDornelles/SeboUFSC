package br.ufsc.sebo.dominio.entidades.proposta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufsc.sebo.dominio.entidades.Chat;
import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.item.Item;

public class Proposta {
    private Integer idProposta;
    private Date data;
    private StatusProposta status;

    @JsonIgnore
    private EstadoProposta estado;
    @JsonIgnore
    private List<PropostaObserver> observadores;
    @JsonIgnoreProperties({"notificacoes", "itensInteresse"})
    private List<Usuario> participantes;
    @JsonIgnoreProperties({"notificacoes", "itensInteresse"})
    private List<Usuario> aceites;

    private List<Item> itensEnvolvidos;
    private Chat chatSessao;

    public Proposta(Integer idProposta, Date data, StatusProposta status, List<Item> itensEnvolvidos,
                     List<Usuario> participantes, Chat chatSessao) {
        this.idProposta = idProposta;
        this.data = data;
        this.status = status;
        this.itensEnvolvidos = itensEnvolvidos;
        this.participantes = participantes;
        this.aceites = new ArrayList<>();
        this.observadores = new ArrayList<>();
        this.chatSessao = chatSessao;

        this.estado = new AguardandoConfirmacaoState();
    }

    public void aceitar(Usuario usuario) {
        if (this.estado == null) {
            this.estado = new AguardandoConfirmacaoState();
        }
        this.estado.aceitar(this, usuario);
    }

    public void recusar(Usuario usuario) {
        if (this.estado == null) {
            this.estado = new AguardandoConfirmacaoState();
        }
        this.estado.recusar(this, usuario);
    }

    public void setEstado(EstadoProposta novoEstado) {
        this.estado = novoEstado;
        this.status = novoEstado.getStatusEnum();
    }

    public void adicionarObservador(PropostaObserver observador) {
        if (observador == null) return;
        if (this.observadores == null) {
            this.observadores = new ArrayList<>();
        }
        if (!this.observadores.contains(observador)) {
            this.observadores.add(observador);
        }
    }

    public void removerObservador(PropostaObserver observador) {
        if (this.observadores != null) {
            this.observadores.remove(observador);
        }
    }

    public void notificarObservadores() {
        if (this.observadores == null) return;
        for (PropostaObserver obs : observadores) {
            obs.onPropostaAtualizada(this);
        }
    }

    public void registrarAceite(Usuario u) {
        boolean participante = participantes.stream().anyMatch(p -> p.getId().equals(u.getId()));
        if (!participante) {
            throw new IllegalArgumentException("Usuário não faz parte desta proposta.");
        }
        boolean jaAceitou = aceites.stream().anyMatch(a -> a.getId().equals(u.getId()));
        if (!jaAceitou) {
            aceites.add(u);
        }
    }

    public boolean verificarAceites() {
        return aceites.size() == participantes.size();
    }

    public void setStatus(StatusProposta s) {
        this.status = s;
    }

    public List<Item> getItens() {
        return itensEnvolvidos;
    }

    public Integer getIdProposta() { return idProposta; }
    public Date getData() { return data; }
    public StatusProposta getStatus() { return status; }
    public List<Usuario> getParticipantes() { return participantes; }
    public List<Usuario> getAceites() { return aceites; }
    public Chat getChatSessao() { return chatSessao; }
    public void setChatSessao(Chat chatSessao) { this.chatSessao = chatSessao; }
}