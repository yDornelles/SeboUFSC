package br.ufsc.sebo.dominio.servico;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.Chat;
import br.ufsc.sebo.dominio.entidades.Mensagem;
import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;
import br.ufsc.sebo.dominio.entidades.proposta.PropostaObserver;
import br.ufsc.sebo.dominio.entidades.proposta.StatusProposta;

@Service
public class ServicoChat implements PropostaObserver {

    private static int proximoId = 1;
    private final List<Chat> chats = new ArrayList<>();

    public void enviarMensagem(Integer chatId, Usuario u, String msg) {
        Chat chat = buscarPorId(chatId);
        chat.enviarMensagem(u, msg);
    }

    public List<Mensagem> carregarHistorico(Integer chatId) {
        Chat chat = buscarPorId(chatId);
        return chat.carregarHistorico();
    }

    public Chat buscarPorId(Integer id) {
        return chats.stream()
            .filter(c -> c.getIdChat().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Chat não encontrado."));
    }

    @Override
    public void onPropostaAtualizada(Proposta proposta) {
        StatusProposta status = proposta.getStatus();

        if (status == StatusProposta.CONFIRMADA) {
            if (proposta.getChatSessao() == null) {
                Chat novoChat = new Chat(proximoId++, "ATIVO", proposta.getParticipantes());
                chats.add(novoChat);
                proposta.setChatSessao(novoChat);
            }
        } else if (status == StatusProposta.CANCELADA) {
            if (proposta.getChatSessao() != null) {
                proposta.getChatSessao().encerrarChat();
            }
        }
    }
}