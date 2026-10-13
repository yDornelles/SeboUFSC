package br.ufsc.sebo.dominio.servico;

import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.item.Item;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ServicoInteresse {

    private final ServicoUsuario servicoUsuario;
    private final ServicoItem servicoItem;

    public ServicoInteresse(ServicoUsuario servicoUsuario, ServicoItem servicoItem) {
        this.servicoUsuario = servicoUsuario;
        this.servicoItem = servicoItem;
    }

    public void adicionarInteresse(String usuarioId, String itemId) {
        Item item = servicoItem.buscarPorId(itemId);
        
        if (item.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("Você não pode manifestar interesse no seu próprio item.");
        }

        Usuario usuario = servicoUsuario.buscarPorId(usuarioId);

        if (!usuario.adicionarInteresse(item)) {
            throw new IllegalArgumentException("Este item já consta nos interesses do usuário.");
        }
    }

    public void removerInteresse(String usuarioId, String itemId) {
        Item item = servicoItem.buscarPorId(itemId);
        Usuario usuario = servicoUsuario.buscarPorId(usuarioId);

        if (!usuario.removerInteresse(item)) {
            throw new IllegalArgumentException("O item não foi encontrado na lista de interesses.");
        }
    }

    public List<Item> listarInteresses(String usuarioId) {
        Usuario usuario = servicoUsuario.buscarPorId(usuarioId);
        return usuario.getItensInteresse();
    }
}