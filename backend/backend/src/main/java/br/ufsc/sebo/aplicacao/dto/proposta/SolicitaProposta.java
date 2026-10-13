package br.ufsc.sebo.aplicacao.dto.proposta;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record SolicitaProposta(
    @NotEmpty(message = "A proposta precisa de pelo menos um item.")
    List<String> itemIds,

    @NotEmpty(message = "A proposta precisa de pelo menos um participante.")
    List<String> participanteIds
) {}
