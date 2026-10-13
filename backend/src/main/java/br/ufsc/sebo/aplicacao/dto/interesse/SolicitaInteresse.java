package br.ufsc.sebo.aplicacao.dto.interesse;

import jakarta.validation.constraints.NotBlank;

public record SolicitaInteresse(
    @NotBlank(message = "O ID do usuário é obrigatório.")
    String usuarioId,

    @NotBlank(message = "O ID do item é obrigatório.")
    String itemId
) {}