package br.ufsc.sebo.aplicacao.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitaMensagem(
    @NotBlank(message = "O ID do usuário é obrigatório.")
    String usuarioId,

    @NotBlank(message = "A mensagem não pode estar vazia.")
    @Size(max = 1000, message = "A mensagem deve ter no máximo 1000 caracteres.")
    String msg
) {}
