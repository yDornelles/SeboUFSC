package br.ufsc.sebo.dominio.servico;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.Chat;
import br.ufsc.sebo.dominio.entidades.Usuario;

@Service
public class ServicoChat {

    private final List<Chat> chats = new ArrayList<>();

    public void enviarMensagem(Integer chatId, Usuario u, String msg) {
        throw new UnsupportedOperationException();
    }

    public List<String> carregarHistorico(Integer chatId) {
        throw new UnsupportedOperationException();
    }
}
