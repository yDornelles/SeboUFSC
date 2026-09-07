package br.ufsc.sebo.dominio.servico;

import br.ufsc.sebo.dominio.entidades.item.EstadoConservacao;
import br.ufsc.sebo.dominio.entidades.item.Item;
import br.ufsc.sebo.dominio.entidades.item.OpcaoNegocio;
import br.ufsc.sebo.dominio.entidades.item.StatusItem;
import br.ufsc.sebo.dominio.entidades.item.TipoMidia;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicoItemTest {

    @Test
    void listarItensDisponiveisDeveRetornarApenasItensComStatusDisponivel() {
        ServicoItem servico = new ServicoItem();

        // Item disponível: deve aparecer no catálogo público
        servico.cadastrarItem("usuario-1", "Dom Casmurro", TipoMidia.LIVRO, "Machado de Assis",
            "Editora Ática", StatusItem.DISPONIVEL, EstadoConservacao.USADO,
            OpcaoNegocio.SO_VENDA, 20.0, "Livro em bom estado, poucas marcas de uso.", "img1.png");

        // Item de troca também deve aparecer, pois ainda está disponível
        servico.cadastrarItem("usuario-2", "Watchmen", TipoMidia.HQ, "Alan Moore",
            "Panini", StatusItem.DISPONIVEL, EstadoConservacao.SEMI_NOVO,
            OpcaoNegocio.SO_TROCA, 0.0, "HQ completa, capa dura, sem rasuras.", "img2.png");

        List<Item> disponiveis = servico.listarItensDisponiveis();

        assertEquals(2, disponiveis.size());
        assertTrue(disponiveis.stream().allMatch(item -> item.getStatus() == StatusItem.DISPONIVEL));
    }
}
