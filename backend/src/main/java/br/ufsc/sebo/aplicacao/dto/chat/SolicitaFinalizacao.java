package br.ufsc.sebo.aplicacao.dto.chat;

import jakarta.validation.constraints.NotBlank;

public record SolicitaFinalizacao(
    @NotBlank(message = "O ID do usuário é obrigatório.")
    String usuarioId
) {}
