package br.ufsc.sebo.aplicacao.dto.chat;

import java.util.List;
import java.util.stream.Collectors;

import br.ufsc.sebo.dominio.entidades.Chat;
import br.ufsc.sebo.dominio.entidades.Mensagem;
import br.ufsc.sebo.dominio.entidades.item.StatusItem;
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;
import br.ufsc.sebo.dominio.servico.ServicoUsuario;

public record RespostaChat(
    Integer id,
    Integer propostaId,
    boolean ativo,
    List<ItemChat> itens,
    List<ParticipanteChat> participantes,
    RespostaMensagem ultimaMensagem
) {
    public record ItemChat(String id, String usuarioId, String nomeProprietario,
                           String titulo, String imagem, StatusItem status) {}

    public record ParticipanteChat(String id, String nomeUsuario) {}

    public static RespostaChat de(Chat chat, Proposta proposta, ServicoUsuario servicoUsuario) {
        List<ItemChat> itens = proposta.getItens().stream()
            .map(i -> new ItemChat(i.getId(), i.getUsuarioId(),
                servicoUsuario.buscarPorId(i.getUsuarioId()).getNomeUsuario(),
                i.getTitulo(), i.getImagem(), i.getStatus()))
            .collect(Collectors.toList());

        List<ParticipanteChat> participantes = chat.getParticipantes().stream()
            .map(u -> new ParticipanteChat(u.getId(), u.getNomeUsuario()))
            .collect(Collectors.toList());

        List<Mensagem> historico = chat.carregarHistorico();
        RespostaMensagem ultima = historico.isEmpty()
            ? null
            : RespostaMensagem.de(historico.get(historico.size() - 1));

        return new RespostaChat(chat.getIdChat(), proposta.getIdProposta(), chat.estaAtivo(),
            itens, participantes, ultima);
    }
}
