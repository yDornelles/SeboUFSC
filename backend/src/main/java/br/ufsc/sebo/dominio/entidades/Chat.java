package br.ufsc.sebo.dominio.entidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat {
    private Integer idChat;
    private String status;
    private List<Usuario> participantes;
    private List<Mensagem> mensagens;

    public Chat(Integer idChat, String status, List<Usuario> participantes) {
        this.idChat = idChat;
        this.status = status;
        this.participantes = participantes;
        this.mensagens = new ArrayList<>();
    }

    public void enviarMensagem(Usuario remetente, String texto) {
        boolean participante = participantes.stream().anyMatch(p -> p.getId().equals(remetente.getId()));
        if (!participante) {
            throw new IllegalArgumentException("Você não é participante deste chat.");
        }
        Mensagem novaMensagem = new Mensagem(mensagens.size() + 1, texto, new Date(), remetente);
        mensagens.add(novaMensagem);
    }

    public List<Mensagem> carregarHistorico() {
        return mensagens;
    }

    public void encerrarChat() {
        this.status = "ENCERRADO";
    }

    public Integer getIdChat() { return idChat; }
    public String getStatus() { return status; }
    public List<Usuario> getParticipantes() { return participantes; }
    public List<Mensagem> getMensagens() { return mensagens; }
}