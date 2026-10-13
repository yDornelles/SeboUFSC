package br.ufsc.sebo.dominio.servico;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.item.Item;
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;

@Service
public class ServicoInteresse {

    private final ServicoUsuario servicoUsuario;
    private final ServicoItem servicoItem;
    private final ServicoNotificacao servicoNotificacao;
    private final AlgTrocas algTrocas;
    private final ServicoProposta servicoProposta;

    public ServicoInteresse(ServicoUsuario servicoUsuario, ServicoItem servicoItem,
                             ServicoNotificacao servicoNotificacao, AlgTrocas algTrocas,
                             ServicoProposta servicoProposta) {
        this.servicoUsuario = servicoUsuario;
        this.servicoItem = servicoItem;
        this.servicoNotificacao = servicoNotificacao;
        this.algTrocas = algTrocas;
        this.servicoProposta = servicoProposta;
    }

    public Proposta adicionarInteresse(String usuarioId, String itemId) {
        Item item = servicoItem.buscarPorId(itemId);

        if (item.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("Você não pode manifestar interesse no seu próprio item.");
        }

        Usuario usuario = servicoUsuario.buscarPorId(usuarioId);

        if (!usuario.adicionarInteresse(item)) {
            throw new IllegalArgumentException("Este item já consta nos interesses do usuário.");
        }

        Usuario dono = servicoUsuario.buscarPorId(item.getUsuarioId());
        servicoNotificacao.criarNotificacao(dono,
            usuario.getNomeUsuario() + " demonstrou interesse no seu item \"" + item.getTitulo() + "\".", null, usuario.getId());

        List<Item> ciclo = algTrocas.buscarCiclo(usuario, item);
        if (ciclo.isEmpty()) {
            return null;
        }
        List<Usuario> participantes = resolverParticipantesDoCiclo(usuario, ciclo);
        return servicoProposta.registrarProposta(ciclo, participantes);
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

    private List<Usuario> resolverParticipantesDoCiclo(Usuario usuarioInicial, List<Item> ciclo) {
        Set<String> idsJaAdicionados = new LinkedHashSet<>();
        List<Usuario> participantes = new ArrayList<>();

        participantes.add(usuarioInicial);
        idsJaAdicionados.add(usuarioInicial.getId());

        for (Item item : ciclo) {
            Usuario dono = servicoUsuario.buscarPorId(item.getUsuarioId());
            if (idsJaAdicionados.add(dono.getId())) {
                participantes.add(dono);
            }
        }

        return participantes;
    }
}
