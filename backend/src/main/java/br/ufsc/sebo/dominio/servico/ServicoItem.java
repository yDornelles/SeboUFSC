package br.ufsc.sebo.dominio.servico;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.Catalogo;
import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.item.EstadoConservacao;
import br.ufsc.sebo.dominio.entidades.item.Item;
import br.ufsc.sebo.dominio.entidades.item.OpcaoNegocio;
import br.ufsc.sebo.dominio.entidades.item.StatusItem;
import br.ufsc.sebo.dominio.entidades.item.TipoMidia;

@Service
public class ServicoItem {

    private final Catalogo catalogo = new Catalogo();
    private final ServicoUsuario servicoUsuario;

    public ServicoItem(ServicoUsuario servicoUsuario) {
        this.servicoUsuario = servicoUsuario;
    }

    public Item cadastrarItem(String usuarioId, String titulo, TipoMidia tipoMidia, 
        String autor, String editora, StatusItem status, 
        EstadoConservacao estadoConservacao, OpcaoNegocio opcaoNegocio, 
        double preco, String descricao, String imagem) {

        validarRegrasDeNegocio(opcaoNegocio, preco);

        if (status != StatusItem.DISPONIVEL) {
            throw new IllegalArgumentException("Um novo item não pode ser cadastrado com status diferente de DISPONIVEL.");
        }

        Item novoItem = new Item(usuarioId, titulo, tipoMidia, autor, editora, 
        status, estadoConservacao, opcaoNegocio, preco, descricao, imagem);
        
        catalogo.adicionarItem(novoItem);

        System.out.println("Novo item registrado no sistema: " + novoItem.getTitulo());
        return novoItem;
    }

    public Item atualizarItem(String id, String usuarioId, String titulo, TipoMidia tipoMidia, 
                              String autor, String editora, EstadoConservacao estadoConservacao, 
                              OpcaoNegocio opcaoNegocio, double preco, String descricao, String imagem) {
        
        Item itemExistente = buscarPorId(id);

        if (!itemExistente.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("Acesso negado: Você só pode editar os seus próprios itens.");
        }

        validarRegrasDeNegocio(opcaoNegocio, preco);

        itemExistente.setTitulo(titulo);
        itemExistente.setTipoMidia(tipoMidia);
        itemExistente.setAutor(autor);
        itemExistente.setEditora(editora);
        itemExistente.setEstadoConservacao(estadoConservacao);
        itemExistente.setOpcaoNegocio(opcaoNegocio);
        itemExistente.setPreco(preco);
        itemExistente.setDescricao(descricao);
        itemExistente.setImagem(imagem);

        System.out.println("Item atualizado no sistema: " + itemExistente.getTitulo());
        
        return itemExistente;
    }

    private void validarRegrasDeNegocio(OpcaoNegocio opcaoNegocio, double preco) {
        if (opcaoNegocio == OpcaoNegocio.SO_TROCA && preco > 0) {
            throw new IllegalArgumentException("Preço deve ser zero para itens de troca.");
        }

        if ((opcaoNegocio == OpcaoNegocio.SO_VENDA || opcaoNegocio == OpcaoNegocio.TROCA_E_VENDA) && preco <= 0) {
            throw new IllegalArgumentException("Itens disponíveis para venda devem ter um preço maior que R$ 0,00.");
        }

        if (preco > 500.00) {
            throw new IllegalArgumentException("O valor máximo permitido na plataforma do Sebo é de R$ 500,00.");
        }
    }

    public List<Item> listarTodosOsItens() {
        return catalogo.getItensAcervo();
    }

    public List<Item> listarItensDisponiveis() {
        return catalogo.getItensAcervo().stream()
                .filter(item -> item.getStatus() == StatusItem.DISPONIVEL)
                .collect(Collectors.toList());
    }

    public Item buscarPorId(String id) {
        return catalogo.getItensAcervo().stream()
            .filter(i -> i.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));
    }

    public List<Item> listarItensPorUsuario(String usuarioId) {
        return catalogo.getItensAcervo().stream()
                .filter(item -> item.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }

    public String buscarNomeProprietario(String usuarioId) {
        try {
            Usuario usuario = servicoUsuario.buscarPorId(usuarioId);
            return usuario.getNomeUsuario();
        } catch (Exception e) {
            return "Desconhecido";
        }
    }
}