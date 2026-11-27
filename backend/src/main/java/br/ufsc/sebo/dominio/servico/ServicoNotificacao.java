package br.ufsc.sebo.dominio.servico;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.Notificacao;
import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;
import br.ufsc.sebo.dominio.entidades.proposta.PropostaObserver;
import br.ufsc.sebo.dominio.entidades.proposta.StatusProposta;

@Service
public class ServicoNotificacao implements PropostaObserver {

    private final List<Notificacao> notificacoes = new ArrayList<>();
    private final ServicoEmail servicoEmail;

    public ServicoNotificacao(ServicoEmail servicoEmail) {
        this.servicoEmail = servicoEmail;
    }

    public void criarNotificacao(Usuario destinatario, String mensagem) {
        criarNotificacao(destinatario, mensagem, null, null);
    }

    public void criarNotificacao(Usuario destinatario, String mensagem, Proposta proposta) {
        criarNotificacao(destinatario, mensagem, proposta, null);
    }

    public void criarNotificacao(Usuario destinatario, String mensagem, Proposta proposta, Usuario remetente) {
        Notificacao notificacao = new Notificacao(destinatario, mensagem, proposta, remetente);
        destinatario.adicionarNotificacao(notificacao);
        notificacoes.add(notificacao);
        servicoEmail.enviarEmail(destinatario.getEmail(), "Sebo UFSC - Nova notificação", mensagem);
    }

    public List<Notificacao> listarNotificacoes(String usuarioId) {
        return notificacoes.stream()
            .filter(n -> n.getDestinatario() != null && n.getDestinatario().getId().equals(usuarioId))
            .sorted(Comparator.comparing(Notificacao::getIdNotificacao).reversed())
            .collect(Collectors.toList());
    }

    public void marcarComoLida(Integer notificacaoId) {
        Notificacao notificacao = notificacoes.stream()
            .filter(n -> n.getIdNotificacao().equals(notificacaoId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada."));
        notificacao.marcarComoLida();
    }

    public void marcarTodasComoLidas(String usuarioId) {
        notificacoes.stream()
            .filter(n -> n.getDestinatario() != null && n.getDestinatario().getId().equals(usuarioId))
            .forEach(Notificacao::marcarComoLida);
    }

    @Override
    public void onPropostaAtualizada(Proposta proposta) {
        String mensagem = "";
        StatusProposta status = proposta.getStatus();

        if (status == StatusProposta.CONFIRMADA) {
            mensagem = "Todos os participantes aceitaram! A proposta de troca foi confirmada.";
        } else if (status == StatusProposta.CANCELADA) {
            mensagem = "A proposta de troca foi cancelada ou recusada.";
        } else {
            return;
        }

        for (Usuario participante : proposta.getParticipantes()) {
            String outroParticipanteId = proposta.getParticipantes().stream()
                .map(Usuario::getId)
                .filter(id -> !id.equals(participante.getId()))
                .findFirst()
                .orElse(null);

            Usuario remetente = proposta.getParticipantes().stream()
                .filter(u -> u.getId().equals(outroParticipanteId))
                .findFirst()
                .orElse(null);

            criarNotificacao(participante, mensagem, proposta, remetente);
        }
    }
}