package br.ufsc.sebo.aplicacao.dto.chat;

import java.util.Date;

import br.ufsc.sebo.dominio.entidades.Mensagem;

public record RespostaMensagem(
    Integer id,
    String texto,
    Date dataHora,
    String remetenteId,
    String remetenteNome
) {
    public static RespostaMensagem de(Mensagem m) {
        return new RespostaMensagem(m.getIdMensagem(), m.getTexto(), m.getDataHora(),
            m.getRemetente().getId(), m.getRemetente().getNomeUsuario());
    }
}
