package br.ufsc.sebo.dominio.servico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public synchronized void onPropostaAtualizada(Proposta p) {
        Chat chat = p.getChatSessao();

        if (p.getStatus() == StatusProposta.CONFIRMADA && chat == null) {
            Chat novo = new Chat(proximoId++, p.getParticipantes());
            chats.add(novo);
            propostasPorChat.put(novo.getIdChat(), p);
            p.setChatSessao(novo);
        } else if (p.getStatus() == StatusProposta.CANCELADA && chat != null) {
            chat.encerrarChat();
        }
    }
}
