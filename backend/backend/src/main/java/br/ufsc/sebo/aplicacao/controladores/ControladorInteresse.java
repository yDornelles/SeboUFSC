package br.ufsc.sebo.aplicacao.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufsc.sebo.aplicacao.dto.interesse.RespostaInteresse;
import br.ufsc.sebo.aplicacao.dto.interesse.SolicitaInteresse;
import br.ufsc.sebo.aplicacao.dto.item.RespostaItemPublico;
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;
import br.ufsc.sebo.dominio.servico.ServicoInteresse;
import br.ufsc.sebo.dominio.servico.ServicoItem;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/interesses")
@CrossOrigin(origins = "*")
public class ControladorInteresse {

    private final ServicoInteresse servico;
    private final ServicoItem servicoItem; 

    public ControladorInteresse(ServicoInteresse servico, ServicoItem servicoItem) {
        this.servico = servico;
        this.servicoItem = servicoItem;
    }

    @PostMapping("/adicionar")
    public RespostaInteresse adicionarInteresse(@Valid @RequestBody SolicitaInteresse req, HttpServletResponse response) {
        try {
            Proposta proposta = servico.adicionarInteresse(req.usuarioId(), req.itemId());
            String mensagem = "Item adicionado aos interesses com sucesso!";
            if (proposta != null) {
                mensagem += " Encontramos uma possível troca! Confira em Propostas.";
            }
            return new RespostaInteresse(mensagem);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new RespostaInteresse(e.getMessage());
        }
    }

    @DeleteMapping("/remover/{usuarioId}/{itemId}")
    public RespostaInteresse removerInteresse(
            @PathVariable String usuarioId, 
            @PathVariable String itemId, 
            HttpServletResponse response) {
        try {
            servico.removerInteresse(usuarioId, itemId);
            return new RespostaInteresse("Item removido dos interesses com sucesso!");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new RespostaInteresse(e.getMessage());
        }
    }

    @GetMapping("/listar/{usuarioId}")
    public List<RespostaItemPublico> listarInteresses(@PathVariable String usuarioId, HttpServletResponse response) {
        try {
            return servico.listarInteresses(usuarioId)
                .stream()
                .map(item -> new RespostaItemPublico(
                    item, 
                    servicoItem.buscarNomeProprietario(item.getUsuarioId())
                ))
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }
}