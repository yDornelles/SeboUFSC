package br.ufsc.sebo.dominio.servico;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.item.EstadoConservacao;
import br.ufsc.sebo.dominio.entidades.item.Item;
import br.ufsc.sebo.dominio.entidades.item.OpcaoNegocio;
import br.ufsc.sebo.dominio.entidades.item.StatusItem;
import br.ufsc.sebo.dominio.entidades.item.TipoMidia;

@Service
public class ServicoItem {

    private final List<Item> bancoDeDadosFalso = new ArrayList<>();

    public Item cadastrarItem(String usuarioId, String titulo, TipoMidia tipoMidia, 
        String autor, String editora, StatusItem status, 
        EstadoConservacao estadoConservacao, OpcaoNegocio opcaoNegocio, 
        double preco, String descricao, String imagem) {

        if (opcaoNegocio == OpcaoNegocio.SO_TROCA && preco > 0) {
            throw new IllegalArgumentException("Preço deve ser zero para itens de troca.");
        }

        if ((opcaoNegocio == OpcaoNegocio.SO_VENDA || opcaoNegocio == OpcaoNegocio.TROCA_E_VENDA) && preco <= 0) {
            throw new IllegalArgumentException("Itens disponíveis para venda devem ter um preço maior que R$ 0,00.");
        }

        if (status != StatusItem.DISPONIVEL) {
            throw new IllegalArgumentException("Um novo item não pode ser cadastrado com status diferente de DISPONIVEL.");
        }

        if (preco > 500.00) {
            throw new IllegalArgumentException("O valor máximo permitido na plataforma do Sebo é de R$ 500,00.");
        }

        Item novoItem = new Item(usuarioId, titulo, tipoMidia, autor, editora, 
        status, estadoConservacao, opcaoNegocio, preco, descricao, imagem);
        
        bancoDeDadosFalso.add(novoItem);

        System.out.println("Novo item registrado no sistema: " + novoItem.getTitulo());
        return novoItem;
    }

    public List<Item> listarTodosOsItens() {
        return bancoDeDadosFalso;
    }

    public List<Item> listarItensDisponiveis() {
        return bancoDeDadosFalso.stream()
            .filter(item -> item.getStatus() == StatusItem.DISPONIVEL)
            .collect(Collectors.toList());
    }
}