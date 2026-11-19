package br.ufsc.sebo.dominio.entidades.proposta;

import br.ufsc.sebo.dominio.entidades.Usuario;

public class ConfirmadaState implements EstadoProposta {

    @Override
    public void aceitar(Proposta proposta, Usuario usuario) {
        throw new IllegalArgumentException("Esta proposta já foi confirmada e não pode mais ser alterada.");
    }

    @Override
    public void recusar(Proposta proposta, Usuario usuario) {
        throw new IllegalArgumentException("Esta proposta já foi confirmada e não pode mais ser recusada.");
    }

    @Override
    public StatusProposta getStatusEnum() {
        return StatusProposta.CONFIRMADA;
    }
}