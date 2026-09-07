package br.ufsc.sebo.dominio.entidades.usuario;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String id;
    private String nome;
    private String sobrenome;
    private String nomeUsuario;
    private String email;
    private Catalogo catalogo;
    private List<Venda> listaVenda;
    private List<Troca> listaTroca;

    public Usuario (String id, String nome, String sobrenome, String nomeUsuario, String email)  {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        catalogo = new Catalogo();
        listaVenda = new ArrayList<>();
        listaTroca = new ArrayList<>();
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

class Venda {}

class Troca {}