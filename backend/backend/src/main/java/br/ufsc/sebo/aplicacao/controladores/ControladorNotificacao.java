package br.ufsc.sebo.aplicacao.controladores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufsc.sebo.dominio.entidades.Notificacao;
import br.ufsc.sebo.dominio.servico.ServicoNotificacao;

@RestController
@RequestMapping("/api/notificacoes")
@CrossOrigin(origins = "*")
public class ControladorNotificacao {

    private final ServicoNotificacao servicoNotificacao;

    public ControladorNotificacao(ServicoNotificacao servicoNotificacao) {
        this.servicoNotificacao = servicoNotificacao;
    }

    @GetMapping("/{usuarioId}")
    public List<Notificacao> listarNotificacoes(@PathVariable String usuarioId) {
        return servicoNotificacao.listarNotificacoes(usuarioId);
    }

    @PutMapping("/{notificacaoId}/lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Integer notificacaoId) {
        try {
            servicoNotificacao.marcarComoLida(notificacaoId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/usuario/{usuarioId}/lidas")
    public ResponseEntity<Void> marcarTodasComoLidas(@PathVariable String usuarioId) {
        servicoNotificacao.marcarTodasComoLidas(usuarioId);
        return ResponseEntity.ok().build();
    }
}
