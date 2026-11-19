package br.ufsc.sebo.aplicacao.controladores;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.ufsc.sebo.dominio.entidades.Mensagem;
import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.servico.ServicoChat;
import br.ufsc.sebo.dominio.servico.ServicoUsuario;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
public class ControladorChat {

    private final ServicoChat servicoChat;
    private final ServicoUsuario servicoUsuario;

    public ControladorChat(ServicoChat servicoChat, ServicoUsuario servicoUsuario) {
        this.servicoChat = servicoChat;
        this.servicoUsuario = servicoUsuario;
    }

    @PostMapping("/{chatId}/mensagens")
    public void enviarMensagem(
            @PathVariable Integer chatId,
            @RequestParam String usuarioId,
            @RequestParam String msg) {
        Usuario usuario = servicoUsuario.buscarPorId(usuarioId);
        servicoChat.enviarMensagem(chatId, usuario, msg);
    }

    @GetMapping("/{chatId}/historico")
    public List<Mensagem> carregarHistorico(@PathVariable Integer chatId) {
        return servicoChat.carregarHistorico(chatId);
    }
}