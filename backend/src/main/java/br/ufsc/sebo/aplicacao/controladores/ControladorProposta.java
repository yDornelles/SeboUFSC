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
import br.ufsc.sebo.aplicacao.dto.proposta.RespostaProposta;
import br.ufsc.sebo.aplicacao.dto.proposta.SolicitaProposta;
import br.ufsc.sebo.aplicacao.dto.proposta.SolicitaRespostaProposta;
import br.ufsc.sebo.dominio.servico.ServicoItem;
import br.ufsc.sebo.dominio.servico.ServicoProposta;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/propostas")
@CrossOrigin(origins = "*")
public class ControladorProposta {

    private final ServicoProposta servicoProposta;
    private final ServicoItem servicoItem;

    public ControladorProposta(ServicoProposta servicoProposta, ServicoItem servicoItem) {
        this.servicoProposta = servicoProposta;
        this.servicoItem = servicoItem;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<RespostaProposta> listarPropostasDoUsuario(@PathVariable String usuarioId) {
        return servicoProposta.listarPorUsuario(usuarioId).stream()
            .map(p -> RespostaProposta.de(p, servicoItem))
            .collect(Collectors.toList());
    }

    @GetMapping("/{propostaId}")
    public ResponseEntity<?> buscarProposta(@PathVariable Integer propostaId) {
        try {
            return ResponseEntity.ok(RespostaProposta.de(servicoProposta.buscarPorId(propostaId), servicoItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new RespostaErro(e.getMessage()));
        }
    }

    @PostMapping("/criar")
    public ResponseEntity<?> criarProposta(@Valid @RequestBody SolicitaProposta req) {
        try {
            return ResponseEntity.ok(RespostaProposta.de(
                servicoProposta.criarProposta(req.itemIds(), req.participanteIds()), servicoItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RespostaErro(e.getMessage()));
        }
    }

    @PutMapping("/{propostaId}/responder")
    public ResponseEntity<?> responderProposta(@PathVariable Integer propostaId,
                                               @Valid @RequestBody SolicitaRespostaProposta req) {
        try {
            return ResponseEntity.ok(RespostaProposta.de(
                servicoProposta.responderProposta(propostaId, req.usuarioId(), req.aceite()), servicoItem));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RespostaErro(e.getMessage()));
        }
    }
}
