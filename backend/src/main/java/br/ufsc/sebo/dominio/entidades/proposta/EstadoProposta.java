package br.ufsc.sebo.dominio.entidades.proposta;

import br.ufsc.sebo.dominio.entidades.Usuario;

public interface EstadoProposta {
    void aceitar(Proposta proposta, Usuario usuario);
    void recusar(Proposta proposta, Usuario usuario);
    StatusProposta getStatusEnum();
}