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
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;
import br.ufsc.sebo.dominio.entidades.proposta.PropostaObserver;
import br.ufsc.sebo.dominio.entidades.proposta.StatusProposta;

@Service
public class ServicoProposta {

    private static int proximoId = 1;

    private final List<Proposta> propostas = new ArrayList<>();
    private final ServicoItem servicoItem;
    private final ServicoUsuario servicoUsuario;
    private final List<PropostaObserver> observadores;

    public ServicoProposta(ServicoItem servicoItem, ServicoUsuario servicoUsuario, List<PropostaObserver> observadores) {
        this.servicoItem = servicoItem;
        this.servicoUsuario = servicoUsuario;
        this.observadores = observadores;
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
        
        if (observadores != null) {
            observadores.forEach(proposta::adicionarObservador);
        }

        propostas.add(proposta);

        proposta.notificarObservadores();

        return proposta;
    }

    public Proposta responderProposta(Integer propostaId, String usuarioId, boolean aceite) {
        Proposta proposta = buscarPorId(propostaId);
        Usuario usuario = servicoUsuario.buscarPorId(usuarioId);

        if (aceite) {
            proposta.aceitar(usuario);
        } else {
            proposta.recusar(usuario);
        }

        if (proposta.getStatus() == StatusProposta.CONFIRMADA) {
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
            if (outra == confirmada || outra.getStatus() != StatusProposta.AGUARDANDO_CONFIRMACAO) {
                continue;
            }
            boolean usaItemReservado = outra.getItens().stream().anyMatch(i -> idsConfirmados.contains(i.getId()));
            if (usaItemReservado) {
                outra.recusar(outra.getParticipantes().get(0));
            }
        }
    }
}