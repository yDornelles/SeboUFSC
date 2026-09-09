package br.ufsc.sebo.aplicacao.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public RespostaItem cadastrarItem(@Valid @RequestBody SolicitaItem req) {
        TipoMidia midia = TipoMidia.valueOf(req.getTipoMidia().toUpperCase());
        StatusItem status = StatusItem.valueOf(req.getStatus().toUpperCase());
        EstadoConservacao estado = EstadoConservacao.valueOf(req.getEstadoConservacao().toUpperCase());
        OpcaoNegocio negocio = OpcaoNegocio.valueOf(req.getOpcaoNegocio().toUpperCase());

        Item itemSalvo = servico.cadastrarItem(
            req.getUsuarioId(), req.getTitulo(), midia, req.getAutor(), 
            req.getEditora(), status, estado, negocio, 
            req.getPreco(), req.getDescricao(), req.getImagem()
        );
        
        return new RespostaItem(itemSalvo.getId(), "Item cadastrado com sucesso!");
    }

    @GetMapping("/listar")
    public List<RespostaItemPublico> listarItens() {
        return servico.listarItensDisponiveis()
            .stream()
            .map(RespostaItemPublico::new)
            .collect(Collectors.toList());
    }
}