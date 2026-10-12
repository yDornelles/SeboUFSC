package br.ufsc.sebo.aplicacao.controladores;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufsc.sebo.aplicacao.dto.usuario.RespostaUsuario;
import br.ufsc.sebo.aplicacao.dto.usuario.SolicitaUsuario;
import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.servico.ServicoUsuario;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;


@RestController 
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class ControladorUsuario {

    private final ServicoUsuario servico;

    public ControladorUsuario(ServicoUsuario servico) {
        this.servico = servico;
    }

    @PostMapping("/cadastrar")
    public RespostaUsuario cadastrarUsuario(@Valid @RequestBody SolicitaUsuario req, HttpServletResponse response) {
        try {
            Usuario usuarioSalvo = servico.cadastrarUsuario(
            req.id(), 
            req.nome(), 
            req.sobrenome(),
            req.nomeUsuario(),
            req.email());
        return new RespostaUsuario(usuarioSalvo.getNomeUsuario(), "Usuário cadastrado com sucesso!");
        } catch(IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new RespostaUsuario(null, e.getMessage());
        }
    }
}
