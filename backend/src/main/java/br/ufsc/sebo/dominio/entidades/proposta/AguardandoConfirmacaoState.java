package br.ufsc.sebo.dominio.entidades.proposta;

import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.item.StatusItem;

public class AguardandoConfirmacaoState implements EstadoProposta {

    @Override
    public void aceitar(Proposta proposta, Usuario usuario) {
        boolean participante = proposta.getParticipantes().stream()
            .anyMatch(p -> p.getId().equals(usuario.getId()));
        if (!participante) {
            throw new IllegalArgumentException("Você não faz parte desta proposta.");
        }

        boolean todosDisponiveis = proposta.getItens().stream()
            .allMatch(i -> i.getStatus() == StatusItem.DISPONIVEL);
        if (!todosDisponiveis) {
            proposta.setEstado(new CanceladaState());
            proposta.notificarObservadores();
            throw new IllegalArgumentException("Um dos itens desta proposta não está mais disponível. A proposta foi cancelada.");
        }

        proposta.registrarAceite(usuario);

        if (proposta.verificarAceites()) {
            proposta.setEstado(new ConfirmadaState());
            proposta.getItens().forEach(i -> i.setStatus(StatusItem.RESERVADO_PARA_TROCA));
            proposta.notificarObservadores();
        }
    }

    @Override
    public void recusar(Proposta proposta, Usuario usuario) {
        boolean participante = proposta.getParticipantes().stream()
            .anyMatch(p -> p.getId().equals(usuario.getId()));
        if (!participante) {
            throw new IllegalArgumentException("Você não faz parte desta proposta.");
        }

        proposta.setEstado(new CanceladaState());
        proposta.notificarObservadores();
    }

    @Override
    public StatusProposta getStatusEnum() {
        return StatusProposta.AGUARDANDO_CONFIRMACAO;
    }
}