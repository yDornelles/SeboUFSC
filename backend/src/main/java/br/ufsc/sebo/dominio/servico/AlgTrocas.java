package br.ufsc.sebo.dominio.servico;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.item.Item;
import br.ufsc.sebo.dominio.entidades.item.OpcaoNegocio;
import br.ufsc.sebo.dominio.entidades.item.StatusItem;
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;

@Service
public class AlgTrocas {

    private static final int MIN_PARTICIPANTES = 2;
    private static final int MAX_PARTICIPANTES = 5;

    private final ServicoUsuario servicoUsuario;

    public AlgTrocas(ServicoUsuario servicoUsuario) {
        this.servicoUsuario = servicoUsuario;
    }

    public List<Item> buscarCiclo(Usuario u, Item i) {
        if (i == null || !aceitaTroca(i)) {
            return new ArrayList<>();
        }
        
        Usuario donoDoItem = encontrarDono(i);
        if (donoDoItem == null || donoDoItem.getId().equals(u.getId())) {
            return new ArrayList<>();
        }

        Queue<Caminho> filaDeExploracao = new ArrayDeque<>();
        Caminho caminhoInicial = new Caminho(u).estender(donoDoItem, i);
        filaDeExploracao.add(caminhoInicial);

        while (!filaDeExploracao.isEmpty()) {
            Caminho caminhoAtual = filaDeExploracao.poll();
            Usuario ultimoUsuario = caminhoAtual.ultimoUsuario();

            if (caminhoAtual.participantes.size() > MAX_PARTICIPANTES) {
                continue;
            }

            for (Item itemDeInteresse : ultimoUsuario.getItensInteresse()) {
                if (!aceitaTroca(itemDeInteresse)) {
                    continue;
                }
                Usuario dono = encontrarDono(itemDeInteresse);
                if (dono == null) {
                    continue;
                }

                if (dono.getId().equals(u.getId())) {
                    int participantesNoCiclo = caminhoAtual.participantes.size();
                    if (participantesNoCiclo >= MIN_PARTICIPANTES && participantesNoCiclo <= MAX_PARTICIPANTES) {
                        List<Item> itensDoCiclo = new ArrayList<>(caminhoAtual.itens);
                        itensDoCiclo.add(itemDeInteresse);
                        return itensDoCiclo;
                    }
                    continue;
                }

                if (caminhoAtual.contem(dono)) {
                    continue;
                }

                filaDeExploracao.add(caminhoAtual.estender(dono, itemDeInteresse));
            }
        }

        return new ArrayList<>();
    }

    public List<Item> buscarCicloAlternativo(Proposta p) {
        for (Item itemAfetado : p.getItens()) {
            Usuario dono = encontrarDono(itemAfetado);
            if (dono == null) {
                continue;
            }
            for (Item itemDeInteresse : new ArrayList<>(dono.getItensInteresse())) {
                List<Item> cicloAlternativo = buscarCiclo(dono, itemDeInteresse);
                if (!cicloAlternativo.isEmpty()) {
                    return cicloAlternativo;
                }
            }
        }
        return new ArrayList<>();
    }

    private boolean aceitaTroca(Item item) {
        return item.getStatus() == StatusItem.DISPONIVEL
            && item.getOpcaoNegocio() != OpcaoNegocio.SO_VENDA;
    }

    private Usuario encontrarDono(Item item) {
        return servicoUsuario.listarTodosUsuarios().stream()
            .filter(usuario -> usuario.getId() != null && usuario.getId().equals(item.getUsuarioId()))
            .findFirst()
            .orElse(null);
    }

    private static final class Caminho {
        private final List<Usuario> participantes;
        private final List<Item> itens;

        Caminho(Usuario usuarioInicial) {
            this.participantes = new ArrayList<>();
            this.participantes.add(usuarioInicial);
            this.itens = new ArrayList<>();
        }

        private Caminho(List<Usuario> participantes, List<Item> itens) {
            this.participantes = participantes;
            this.itens = itens;
        }

        Usuario ultimoUsuario() {
            return participantes.get(participantes.size() - 1);
        }

        boolean contem(Usuario usuario) {
            return participantes.stream().anyMatch(p -> p.getId().equals(usuario.getId()));
        }

        Caminho estender(Usuario novoUsuario, Item itemQueLigaAoNovoUsuario) {
            List<Usuario> novosParticipantes = new ArrayList<>(participantes);
            novosParticipantes.add(novoUsuario);
            List<Item> novosItens = new ArrayList<>(itens);
            novosItens.add(itemQueLigaAoNovoUsuario);
            return new Caminho(novosParticipantes, novosItens);
        }
    }
}