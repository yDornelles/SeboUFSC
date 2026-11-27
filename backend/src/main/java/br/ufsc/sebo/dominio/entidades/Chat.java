package br.ufsc.sebo.dominio.entidades;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public static final String ATIVO = "ATIVO";
    public static final String ENCERRADO = "ENCERRADO";

    private Integer idChat;
    private String status;
    private List<Usuario> participantes;
    private List<Mensagem> mensagens;

    public Chat(Integer idChat, List<Usuario> participantes) {
        this.idChat = idChat;
        this.status = ATIVO;
        this.participantes = new ArrayList<>(participantes);
        this.mensagens = new ArrayList<>();
    }

    public void enviarMensagem(Usuario u, String msg) {
        if (!estaAtivo()) {
            throw new IllegalArgumentException("Este chat foi encerrado e não aceita mais mensagens.");
        }
        if (!ehParticipante(u)) {
            throw new IllegalArgumentException("Você não participa deste chat.");
        }
        if (msg == null || msg.isBlank()) {
            throw new IllegalArgumentException("A mensagem não pode estar vazia.");
        }
        mensagens.add(new Mensagem(mensagens.size() + 1, msg.trim(), u));
    }

    public List<Mensagem> carregarHistorico() {
        return new ArrayList<>(mensagens);
    }

    public void encerrarChat() {
        this.status = ENCERRADO;
    }

    public boolean estaAtivo() {
        return ATIVO.equals(status);
    }

    public boolean ehParticipante(Usuario u) {
        return participantes.stream().anyMatch(p -> p.getId().equals(u.getId()));
    }

    public Integer getIdChat() { return idChat; }
    public String getStatus() { return status; }
    public List<Usuario> getParticipantes() { return new ArrayList<>(participantes); }
}
