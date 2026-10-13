package br.ufsc.sebo.aplicacao.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SolicitaUsuario(
    @NotBlank(message = "O ID do Firebase é obrigatório.")
    String id,
    
    @NotBlank(message = "O nome do usuário é obrigatório.")
    String nome,

    @NotBlank(message = "O sobrenome do usuário é obrigatório.")
    String sobrenome,

    @NotBlank(message = "O nome de usuário é obrigatório.")
    @Pattern(regexp = "^\\S+$", message = "O nome de usuário não pode conter espaços em branco.")
    @Size(min = 6, max = 20, message = "O nome de usuário deve ter entre 6 e 20 caracteres.")
    String nomeUsuario,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail deve ser válido.")
    String email
){}
