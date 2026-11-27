package br.ufsc.sebo.dominio.entidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat {
    public static final String ATIVO = "ATIVO";
    public static final String ENCERRADO = "ENCERRADO";

    private Integer idChat;
    private String status;
    private List<Usuario> participantes;
    private List<Mensagem> mensagens;

    public Chat(Integer idChat, String status, List<Usuario> participantes) {
        this.idChat = idChat;
        this.status = status;
        this.participantes = new ArrayList<>(participantes);
        this.mensagens = new ArrayList<>();
    }

    public void enviarMensagem(Usuario remetente, String texto) {
        if (!estaAtivo()) {
            throw new IllegalArgumentException("Este chat foi encerrado e não aceita mais mensagens.");
        }
        if (!ehParticipante(remetente)) {
            throw new IllegalArgumentException("Você não é participante deste chat.");
        }
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("A mensagem não pode estar vazia.");
        }
        mensagens.add(new Mensagem(mensagens.size() + 1, texto.trim(), new Date(), remetente));
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
    public List<Mensagem> getMensagens() { return new ArrayList<>(mensagens); }
}
