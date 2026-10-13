package br.ufsc.sebo.aplicacao.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufsc.sebo.aplicacao.dto.item.RespostaItem;
import br.ufsc.sebo.aplicacao.dto.item.RespostaItemPublico;
import br.ufsc.sebo.aplicacao.dto.item.SolicitaItem;
import br.ufsc.sebo.dominio.entidades.item.EstadoConservacao;
import br.ufsc.sebo.dominio.entidades.item.Item;
import br.ufsc.sebo.dominio.entidades.item.OpcaoNegocio;
import br.ufsc.sebo.dominio.entidades.item.StatusItem;
import br.ufsc.sebo.dominio.entidades.item.TipoMidia;
import br.ufsc.sebo.dominio.servico.ServicoItem;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/itens")
@CrossOrigin(origins = "*")
public class ControladorItem {

    private final ServicoItem servico;

    public ControladorItem(ServicoItem servico) {
        this.servico = servico;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<RespostaItem> cadastrarItem(@Valid @RequestBody SolicitaItem req) {
        try {
            TipoMidia midia = TipoMidia.valueOf(req.getTipoMidia().toUpperCase());
            StatusItem status = StatusItem.valueOf(req.getStatus().toUpperCase());
            EstadoConservacao estado = EstadoConservacao.valueOf(req.getEstadoConservacao().toUpperCase());
            OpcaoNegocio negocio = OpcaoNegocio.valueOf(req.getOpcaoNegocio().toUpperCase());

            Item itemSalvo = servico.cadastrarItem(
                req.getUsuarioId(), req.getTitulo(), midia, req.getAutor(), 
                req.getEditora(), status, estado, negocio, 
                req.getPreco(), req.getDescricao(), req.getImagem()
            );
            
            return ResponseEntity.ok(new RespostaItem(itemSalvo.getId(), "Item cadastrado com sucesso!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RespostaItem(null, e.getMessage()));
        }
    }

    @GetMapping("/listar")
    public List<RespostaItemPublico> listarItens() {
        return servico.listarItensDisponiveis()
            .stream()
            .map(item -> new RespostaItemPublico(item, servico.buscarNomeProprietario(item.getUsuarioId())))
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public RespostaItemPublico buscarItemPorId(@PathVariable String id) {
        Item item = servico.buscarPorId(id);
        String nomeDono = servico.buscarNomeProprietario(item.getUsuarioId());
        return new RespostaItemPublico(item, nomeDono);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<RespostaItemPublico> listarItensDoUsuario(@PathVariable String usuarioId) {
        String nomeDono = servico.buscarNomeProprietario(usuarioId);
        return servico.listarItensPorUsuario(usuarioId)
            .stream()
            .map(item -> new RespostaItemPublico(item, nomeDono))
            .collect(Collectors.toList());
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<RespostaItem> editarItem(@PathVariable String id, @Valid @RequestBody SolicitaItem req) {
        try {
            TipoMidia midia = TipoMidia.valueOf(req.getTipoMidia().toUpperCase());
            EstadoConservacao estado = EstadoConservacao.valueOf(req.getEstadoConservacao().toUpperCase());
            OpcaoNegocio negocio = OpcaoNegocio.valueOf(req.getOpcaoNegocio().toUpperCase());

            Item itemAtualizado = servico.atualizarItem(
                id, req.getUsuarioId(), req.getTitulo(), midia, req.getAutor(), 
                req.getEditora(), estado, negocio, 
                req.getPreco(), req.getDescricao(), req.getImagem()
            );
            
            return ResponseEntity.ok(new RespostaItem(itemAtualizado.getId(), "Item atualizado com sucesso!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RespostaItem(null, e.getMessage()));
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaItem> handleExceptions(Exception e) {
        String mensagem = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            if (ex.getBindingResult().getFieldError() != null) {
                mensagem = ex.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        return ResponseEntity.badRequest().body(new RespostaItem(null, mensagem != null ? mensagem : "Erro de requisição inválida."));
    }
}