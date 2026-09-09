package br.ufsc.sebo.dominio.servico;

import br.ufsc.sebo.dominio.entidades.usuario.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ServicoUsuario {
    private final List<Usuario> bancoDeDadosFalsoUsuario = new ArrayList<>();

    public Usuario cadastrarUsuario(String idFirebase, String nome, String sobrenome, String nomeUsuario, String email) {

        boolean usuarioExiste = bancoDeDadosFalsoUsuario.stream()
                .anyMatch(u -> u.getId().equals(idFirebase));

        if (usuarioExiste) {
            throw new IllegalArgumentException("Usuário já cadastrado no sistema.");
        }

        boolean emailJaExiste = bancoDeDadosFalsoUsuario.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));

        if (emailJaExiste) {
            throw new IllegalArgumentException("O e-mail informado já está em uso.");
        }

        boolean nomeUsuarioJaExiste = bancoDeDadosFalsoUsuario.stream()
                .anyMatch(u -> u.getNomeUsuario().equals(nomeUsuario));

        if (nomeUsuarioJaExiste) {
            throw new IllegalArgumentException("O nome de usuário informado já está em uso.");
        }

        Usuario novoUsuario = new Usuario(idFirebase, nome, sobrenome, nomeUsuario, email);
        bancoDeDadosFalsoUsuario.add(novoUsuario);

        return novoUsuario;
    }
}
