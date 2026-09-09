package br.ufsc.sebo.aplicacao.dto.item;

import br.ufsc.sebo.dominio.entidades.item.EstadoConservacao;
import br.ufsc.sebo.dominio.entidades.item.Item;
import br.ufsc.sebo.dominio.entidades.item.OpcaoNegocio;
import br.ufsc.sebo.dominio.entidades.item.StatusItem;
import br.ufsc.sebo.dominio.entidades.item.TipoMidia;

public class RespostaItemPublico {
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

    public RespostaItemPublico(Item item) {
        this.id = item.getId();
        this.usuarioId = item.getUsuarioId();
        this.titulo = item.getTitulo();
        this.tipoMidia = item.getTipoMidia();
        this.autor = item.getAutor();
        this.editora = item.getEditora();
        this.status = item.getStatus();
        this.estadoConservacao = item.getEstadoConservacao();
        this.opcaoNegocio = item.getOpcaoNegocio();
        this.preco = item.getPreco();
        this.descricao = item.getDescricao();
        this.imagem = item.getImagem();
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
