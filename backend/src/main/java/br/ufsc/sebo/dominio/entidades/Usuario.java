package br.ufsc.sebo.dominio.entidades;

import java.util.ArrayList;
import java.util.List;

import br.ufsc.sebo.dominio.entidades.item.Item;

public class Usuario {
    private String id;
    private String nome;
    private String sobrenome;
    private String nomeUsuario;
    private String email;
    private List<Item> itensCadastrados;
    private List<Item> itensInteresse;
    private List<Notificacao> notificacoes;

    public Usuario() {

    }

    public Usuario (String id, String nome, String sobrenome, String nomeUsuario, String email)  {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        itensCadastrados = new ArrayList<>();
        itensInteresse = new ArrayList<>();
        notificacoes = new ArrayList<>();
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

    public List<Item> getItensCadastrados() { return itensCadastrados; }
    public List<Item> getItensInteresse() { return itensInteresse; }
    public List<Notificacao> getNotificacoes() { return notificacoes; }

    public boolean adicionarInteresse(Item i) {
        boolean jaExiste = itensInteresse.stream()
            .anyMatch(item -> item.getId().equals(i.getId()));
        if (jaExiste) {
            return false;
        }
        return itensInteresse.add(i);
    }

    public boolean removerInteresse(Item i) {
        return itensInteresse.removeIf(item -> item.getId().equals(i.getId()));
    }

    public void lerNotificacoes() {
        for (Notificacao notificacao : notificacoes) {
            notificacao.marcarComoLida();
        }
    }
}

