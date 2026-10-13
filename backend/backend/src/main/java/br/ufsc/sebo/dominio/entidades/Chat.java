package br.ufsc.sebo.dominio.entidades;

import java.util.List;

public class Chat {
    private Integer idChat;
    private String status;
    private List<Usuario> participantes;
    private List<String> historicoMensagens;

    public Chat(Integer idChat, String status, List<Usuario> participantes, List<String> historicoMensagens) {
        this.idChat = idChat;
        this.status = status;
        this.participantes = participantes;
        this.historicoMensagens = historicoMensagens;
    }

    public void enviarMensagem(Usuario u, String msg) {
        throw new UnsupportedOperationException();
    }

    public List<String> carregarHistorico() {
        throw new UnsupportedOperationException();
    }

    public void encerrarChat() {
        throw new UnsupportedOperationException();
    }
}
