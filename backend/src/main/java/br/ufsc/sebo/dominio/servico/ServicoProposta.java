package br.ufsc.sebo.dominio.servico;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.item.Item;
import br.ufsc.sebo.dominio.entidades.item.StatusItem;
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;
import br.ufsc.sebo.dominio.entidades.proposta.StatusProposta;

@Service
public class ServicoProposta {

    private static int proximoId = 1;

    private final List<Proposta> propostas = new ArrayList<>();
    private final ServicoItem servicoItem;
    private final ServicoUsuario servicoUsuario;
    private final ServicoNotificacao servicoNotificacao;

    public ServicoProposta(ServicoItem servicoItem, ServicoUsuario servicoUsuario, ServicoNotificacao servicoNotificacao) {
        this.servicoItem = servicoItem;
        this.servicoUsuario = servicoUsuario;
        this.servicoNotificacao = servicoNotificacao;
    }

    public Proposta criarProposta(List<String> itemIds, List<String> participanteIds) {
        List<Item> itens = itemIds.stream()
            .map(servicoItem::buscarPorId)
            .collect(Collectors.toList());
        List<Usuario> participantes = participanteIds.stream()
            .map(servicoUsuario::buscarPorId)
            .collect(Collectors.toList());
        return registrarProposta(itens, participantes);
    }

    public Proposta registrarProposta(List<Item> itensEnvolvidos, List<Usuario> participantes) {
        Proposta existente = buscarPendenteComMesmosItens(itensEnvolvidos);
        if (existente != null) {
            return existente;
        }

        Proposta proposta = new Proposta(proximoId++, new Date(), StatusProposta.AGUARDANDO_CONFIRMACAO,
            itensEnvolvidos, participantes, null);
        propostas.add(proposta);

        String remetenteIdPadrao = participantes.isEmpty() ? null : participantes.get(0).getId();

        for (Usuario participante : participantes) {
            String outroParticipanteId = participantes.stream()
                .map(Usuario::getId)
                .filter(id -> !id.equals(participante.getId()))
                .findFirst()
                .orElse(remetenteIdPadrao);

            servicoNotificacao.criarNotificacao(
                participante,
                "Uma nova proposta de troca envolvendo um item seu foi criada. Acesse Propostas para revisar os itens e confirmar.",
                proposta.getIdProposta(), 
                outroParticipanteId 
            );
        }

        return proposta;
    }

    public Proposta responderProposta(Integer propostaId, String usuarioId, boolean aceite) {
        Proposta proposta = buscarPorId(propostaId);
        Usuario usuario = servicoUsuario.buscarPorId(usuarioId);

        boolean participante = proposta.getParticipantes().stream()
            .anyMatch(p -> p.getId().equals(usuario.getId()));
        if (!participante) {
            throw new IllegalArgumentException("Você não faz parte desta proposta.");
        }

        if (proposta.getStatus() != StatusProposta.AGUARDANDO_CONFIRMACAO) {
            String situacao = proposta.getStatus() == StatusProposta.CONFIRMADA ? "confirmada" : "cancelada";
            throw new IllegalArgumentException("Esta proposta já foi " + situacao + " e não pode mais ser respondida.");
        }

        if (!aceite) {
            proposta.setStatus(StatusProposta.CANCELADA);
            notificarParticipantes(proposta,
                "A proposta de troca foi recusada por " + usuario.getNomeUsuario() + " e foi cancelada.", usuario.getId());
            return proposta;
        }

        boolean todosDisponiveis = proposta.getItens().stream()
            .allMatch(i -> i.getStatus() == StatusItem.DISPONIVEL);
        if (!todosDisponiveis) {
            proposta.setStatus(StatusProposta.CANCELADA);
            notificarParticipantes(proposta,
                "A proposta de troca foi cancelada porque um dos itens não está mais disponível.", null);
            throw new IllegalArgumentException(
                "Um dos itens desta proposta não está mais disponível. A proposta foi cancelada.");
        }

        proposta.registrarAceite(usuario);

        if (proposta.verificarAceites()) {
            proposta.setStatus(StatusProposta.CONFIRMADA);
            proposta.getItens().forEach(i -> i.setStatus(StatusItem.RESERVADO_PARA_TROCA));
            notificarParticipantes(proposta, "Todos os participantes aceitaram! A proposta de troca foi confirmada.", usuario.getId());
            cancelarPendentesConflitantes(proposta);
        }

        return proposta;
    }

    public Proposta buscarPorId(Integer id) {
        return propostas.stream()
            .filter(p -> p.getIdProposta().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Proposta não encontrada."));
    }

    public List<Proposta> listarPorUsuario(String usuarioId) {
        return propostas.stream()
            .filter(p -> p.getParticipantes().stream().anyMatch(u -> u.getId().equals(usuarioId)))
            .sorted(Comparator.comparing(Proposta::getIdProposta).reversed())
            .collect(Collectors.toList());
    }

    private void notificarParticipantes(Proposta proposta, String mensagem, String remetenteIdExplicito) {
        for (Usuario participante : proposta.getParticipantes()) {
            String remetenteFinal = remetenteIdExplicito;
            if (remetenteFinal != null && remetenteFinal.equals(participante.getId())) {
                remetenteFinal = proposta.getParticipantes().stream()
                    .map(Usuario::getId)
                    .filter(id -> !id.equals(participante.getId()))
                    .findFirst()
                    .orElse(null);
            }

            servicoNotificacao.criarNotificacao(
                participante, 
                mensagem, 
                proposta.getIdProposta(), 
                remetenteFinal
            );
        }
    }

    private Proposta buscarPendenteComMesmosItens(List<Item> itens) {
        Set<String> ids = itens.stream().map(Item::getId).collect(Collectors.toSet());
        return propostas.stream()
            .filter(p -> p.getStatus() == StatusProposta.AGUARDANDO_CONFIRMACAO)
            .filter(p -> p.getItens().stream().map(Item::getId).collect(Collectors.toSet()).equals(ids))
            .findFirst()
            .orElse(null);
    }

    private void cancelarPendentesConflitantes(Proposta confirmada) {
        Set<String> idsConfirmados = new HashSet<>();
        confirmada.getItens().forEach(i -> idsConfirmados.add(i.getId()));

        for (Proposta outra : propostas) {
            if (outra == confirmada || outra.getStatus() != StatusProposta.AGUARDANDO_CONFIRMACAO && outra.getStatus() != StatusProposta.AGUARDANDO_CONFIRMACAO) {
                continue;
            }
            boolean usaItemReservado = outra.getItens().stream().anyMatch(i -> idsConfirmados.contains(i.getId()));
            if (usaItemReservado) {
                outra.setStatus(StatusProposta.CANCELADA);
                notificarParticipantes(outra,
                    "A proposta de troca foi cancelada porque um dos itens foi usado em outra troca confirmada.", null);
            }
        }
    }
}