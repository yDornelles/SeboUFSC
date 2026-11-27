package br.ufsc.sebo.aplicacao.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufsc.sebo.aplicacao.dto.RespostaErro;
import br.ufsc.sebo.aplicacao.dto.chat.RespostaChat;
import br.ufsc.sebo.aplicacao.dto.chat.RespostaMensagem;
import br.ufsc.sebo.aplicacao.dto.chat.SolicitaFinalizacao;
import br.ufsc.sebo.aplicacao.dto.chat.SolicitaMensagem;
import br.ufsc.sebo.dominio.servico.ServicoChat;
import br.ufsc.sebo.dominio.servico.ServicoUsuario;
import jakarta.validation.Valid;

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

    // Extra: aba "Chat" lista todas as conversas de que o usuário participa
    @GetMapping("/usuario/{usuarioId}")
    public List<RespostaChat> listarChatsDoUsuario(@PathVariable String usuarioId) {
        return servicoChat.listarPorUsuario(usuarioId).stream()
            .map(c -> RespostaChat.de(c, servicoChat.buscarPropostaDoChat(c.getIdChat()), servicoUsuario))
            .collect(Collectors.toList());
    }

    // Extra: detalhes de uma conversa (itens, participantes, status)
    @GetMapping("/{chatId}")
    public ResponseEntity<?> buscarChat(@PathVariable Integer chatId) {
        try {
            return ResponseEntity.ok(RespostaChat.de(servicoChat.buscarPorId(chatId),
                servicoChat.buscarPropostaDoChat(chatId), servicoUsuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new RespostaErro(e.getMessage()));
        }
    }

    @PostMapping("/{chatId}/mensagens")
    public ResponseEntity<?> enviarMensagem(@PathVariable Integer chatId, @Valid @RequestBody SolicitaMensagem req) {
        try {
            servicoChat.enviarMensagem(chatId, servicoUsuario.buscarPorId(req.usuarioId()), req.msg());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RespostaErro(e.getMessage()));
        }
    }

    @GetMapping("/{chatId}/historico")
    public ResponseEntity<?> carregarHistorico(@PathVariable Integer chatId) {
        try {
            List<RespostaMensagem> historico = servicoChat.carregarHistorico(chatId).stream()
                .map(RespostaMensagem::de)
                .collect(Collectors.toList());
            return ResponseEntity.ok(historico);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new RespostaErro(e.getMessage()));
        }
    }

    // Extra em relação ao diagrama: opção para o vendedor/trocador finalizar a transação (US 2)
    @PutMapping("/{chatId}/finalizar")
    public ResponseEntity<?> finalizarTransacao(@PathVariable Integer chatId, @Valid @RequestBody SolicitaFinalizacao req) {
        try {
            servicoChat.finalizarTransacao(chatId, servicoUsuario.buscarPorId(req.usuarioId()));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RespostaErro(e.getMessage()));
        }
    }
}
