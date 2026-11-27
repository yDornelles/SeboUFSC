package br.ufsc.sebo.dominio.servico;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.Chat;
import br.ufsc.sebo.dominio.entidades.Mensagem;
import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.item.Item;
import br.ufsc.sebo.dominio.entidades.item.StatusItem;
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;
import br.ufsc.sebo.dominio.entidades.proposta.PropostaObserver;
import br.ufsc.sebo.dominio.entidades.proposta.StatusProposta;

/**
 * Observer de Proposta: cria (libera) o chat quando a proposta é confirmada
 * e o encerra quando ela é cancelada.
 */
@Service
public class ServicoChat implements PropostaObserver {

    private static int proximoId = 1;

    private final List<Chat> chats = new ArrayList<>();
    // "banco de dados falso": proposta a que cada chat pertence (no banco será Chat.ID_PROPOSTA)
    private final Map<Integer, Proposta> propostasPorChat = new HashMap<>();

    public synchronized void enviarMensagem(Integer chatId, Usuario u, String msg) {
        buscarPorId(chatId).enviarMensagem(u, msg);
    }

    public synchronized List<Mensagem> carregarHistorico(Integer chatId) {
        return buscarPorId(chatId).carregarHistorico();
    }

    public synchronized Chat buscarPorId(Integer id) {
        return chats.stream()
            .filter(c -> c.getIdChat().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Chat não encontrado."));
    }

    // Extra: usado pela aba "Chat" para listar as conversas de um usuário (mais recentes primeiro)
    public synchronized List<Chat> listarPorUsuario(String usuarioId) {
        return chats.stream()
            .filter(c -> c.getParticipantes().stream().anyMatch(u -> u.getId().equals(usuarioId)))
            .sorted(Comparator.comparing(Chat::getIdChat).reversed())
            .collect(Collectors.toList());
    }

    public synchronized Proposta buscarPropostaDoChat(Integer chatId) {
        buscarPorId(chatId);
        return propostasPorChat.get(chatId);
    }

    /**
     * Finaliza a negociação: marca os itens como TROCADO/VENDIDO e encerra o chat.
     * Só quem entrega algum item da proposta (vendedor ou trocador) pode finalizar.
     */
    public synchronized void finalizarTransacao(Integer chatId, Usuario u) {
        Chat chat = buscarPorId(chatId);
        Proposta proposta = propostasPorChat.get(chatId);

        if (!chat.estaAtivo()) {
            throw new IllegalArgumentException("Esta negociação já foi finalizada.");
        }
        boolean entregaItem = proposta.getItens().stream()
            .anyMatch(i -> i.getUsuarioId().equals(u.getId()));
        if (!entregaItem) {
            throw new IllegalArgumentException("Apenas quem entrega itens nesta negociação pode finalizá-la.");
        }

        for (Item item : proposta.getItens()) {
            if (item.getStatus() == StatusItem.RESERVADO_PARA_TROCA) {
                item.setStatus(StatusItem.TROCADO);
            } else if (item.getStatus() == StatusItem.RESERVADO) {
                // TODO: ajustar para RESERVADO_PARA_VENDA quando o enum for atualizado (parte da venda)
                item.setStatus(StatusItem.VENDIDO);
            }
        }
        chat.encerrarChat();
    }

    @Override
    public synchronized void onPropostaAtualizada(Proposta proposta) {
        StatusProposta status = proposta.getStatus();
        Chat chat = proposta.getChatSessao();

        if (status == StatusProposta.CONFIRMADA && chat == null) {
            Chat novo = new Chat(proximoId++, Chat.ATIVO, proposta.getParticipantes());
            chats.add(novo);
            propostasPorChat.put(novo.getIdChat(), proposta);
            proposta.setChatSessao(novo);
        } else if (status == StatusProposta.CANCELADA && chat != null) {
            chat.encerrarChat();
        }
    }
}
