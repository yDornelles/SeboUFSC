package br.ufsc.sebo.dominio.entidades.item;

import java.util.UUID;

public class Item {
    private String id;
    private String usuarioId;
    private String titulo;
    private TipoMidia tipoMidia;
    private String autor;
    private String editora;
    private StatusItem status;
    private EstadoConservacao estadoConservacao;
    private OpcaoNegocio opcaoNegocio;
    private double preco;
    private String descricao;
    private String imagem;

    public Item(String usuarioId, String titulo, TipoMidia tipoMidia, String autor, String editora,
                StatusItem status, EstadoConservacao estadoConservacao, OpcaoNegocio opcaoNegocio, double preco, String descricao,
                String imagem) {
        this.id = UUID.randomUUID().toString();
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.tipoMidia = tipoMidia;
        this.autor = autor;
        this.editora = editora;
        this.status = status;
        this.estadoConservacao = estadoConservacao;
        this.opcaoNegocio = opcaoNegocio;
        this.preco = preco;
        this.descricao = descricao;
        this.imagem = imagem;
    }

    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public String getTitulo() { return titulo; }
    public TipoMidia getTipoMidia() { return tipoMidia; }
    public String getAutor() { return autor; }
    public String getEditora() { return editora; }
    public StatusItem getStatus() { return status; }
    public EstadoConservacao getEstadoConservacao() { return estadoConservacao; }
    public OpcaoNegocio getOpcaoNegocio() { return opcaoNegocio; }
    public double getPreco() { return preco; }
    public String getDescricao() { return descricao; }
    public String getImagem() { return imagem; }
}
//  