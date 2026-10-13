package br.ufsc.sebo.aplicacao.dto.proposta;

import jakarta.validation.constraints.NotBlank;

public record SolicitaRespostaProposta(
    @NotBlank(message = "O ID do usuário é obrigatório.")
    String usuarioId,

    boolean aceite
) {}
