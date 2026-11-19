package br.ufsc.sebo.dominio.entidades.proposta;

import br.ufsc.sebo.dominio.entidades.Usuario;

public class CanceladaState implements EstadoProposta {

    @Override
    public void aceitar(Proposta proposta, Usuario usuario) {
        throw new IllegalArgumentException("Esta proposta foi cancelada e não pode mais ser respondida.");
    }

    @Override
    public void recusar(Proposta proposta, Usuario usuario) {
        throw new IllegalArgumentException("Esta proposta já está cancelada.");
    }

    @Override
    public StatusProposta getStatusEnum() {
        return StatusProposta.CANCELADA;
    }
}