package br.ufsc.sebo.dominio.servico;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.Notificacao;
import br.ufsc.sebo.dominio.entidades.Usuario;

@Service
public class ServicoNotificacao {

    private final List<Notificacao> notificacoes = new ArrayList<>();
    private final ServicoEmail servicoEmail;

    public ServicoNotificacao(ServicoEmail servicoEmail) {
        this.servicoEmail = servicoEmail;
    }

    public void criarNotificacao(Usuario destinatario, String mensagem) {
        criarNotificacao(destinatario, mensagem, null);
    }

    public void criarNotificacao(Usuario destinatario, String mensagem, Integer propostaId) {
        Notificacao notificacao = new Notificacao(destinatario.getId(), mensagem, propostaId);
        destinatario.adicionarNotificacao(notificacao);
        notificacoes.add(notificacao);
        servicoEmail.enviarEmail(destinatario.getEmail(), "Sebo UFSC - Nova notificação", mensagem);
    }

    public List<Notificacao> listarNotificacoes(String usuarioId) {
        return notificacoes.stream()
            .filter(n -> n.getUsuarioId().equals(usuarioId))
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
            .filter(n -> n.getUsuarioId().equals(usuarioId))
            .forEach(Notificacao::marcarComoLida);
    }
}
