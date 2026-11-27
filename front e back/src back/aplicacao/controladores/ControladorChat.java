package br.ufsc.sebo.aplicacao.controladores;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufsc.sebo.dominio.servico.ServicoChat;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
public class ControladorChat {

    private final ServicoChat servicoChat;

    public ControladorChat(ServicoChat servicoChat) {
        this.servicoChat = servicoChat;
    }

    @PostMapping("/{chatId}/mensagens")
    public void enviarMensagem(@PathVariable Integer chatId, String usuarioId, String msg) {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{chatId}/historico")
    public List<String> carregarHistorico(@PathVariable Integer chatId) {
        throw new UnsupportedOperationException();
    }
}
