package br.ufsc.sebo.aplicacao.controladores;

import br.ufsc.sebo.aplicacao.dto.item.RespostaItem;
import br.ufsc.sebo.aplicacao.dto.item.SolicitaItem;
import br.ufsc.sebo.dominio.entidades.item.*;
import br.ufsc.sebo.dominio.servico.ServicoItem;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

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
}