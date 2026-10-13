package br.ufsc.sebo.dominio.entidades.proposta;

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
    private List<Item> itensEnvolvidos;
    private List<Usuario> participantes;
    private List<Usuario> aceites;
    private Chat chatSessao;

    public Proposta(Integer idProposta, Date data, StatusProposta status, List<Item> itensEnvolvidos,
                     List<Usuario> participantes, Chat chatSessao) {
        this.idProposta = idProposta;
        this.data = data;
        this.status = status;
        this.itensEnvolvidos = itensEnvolvidos;
        this.participantes = participantes;
        this.aceites = new ArrayList<>();
        this.chatSessao = chatSessao;
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
