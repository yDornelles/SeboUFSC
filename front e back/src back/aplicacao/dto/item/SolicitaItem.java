package br.ufsc.sebo.aplicacao.dto.item;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SolicitaItem {
    @NotBlank(message = "O ID do usuário é obrigatório.")
    private String usuarioId;
    @NotBlank(message = "O título é obrigatório.")
    @Size(min = 2, max = 100, message = "O título deve ter entre 2 e 100 caracteres.")
    private String titulo;
    @NotBlank(message = "O tipo de mídia é obrigatório.")
    private String tipoMidia;
    @NotBlank(message = "O autor é obrigatório.")
    private String autor;
    private String editora;
    @NotBlank(message = "O status do item é obrigatório.")
    private String status;
    @NotBlank(message = "O estado de conservação é obrigatório.")
    private String estadoConservacao;
    @NotBlank(message = "A opção de negócio é obrigatória.")
    private String opcaoNegocio;
    @NotNull(message = "O preço é obrigatório.")
    @PositiveOrZero(message = "O preço não pode ser negativo.")
    private double preco;
    @NotBlank(message = "A descrição é obrigatória para ajudar os outros usuários.")
    @Size(min = 10, max = 500, message = "A descrição deve ter entre 10 e 500 caracteres.")
    private String descricao;
    @NotBlank(message = "A imagem é obrigatória.")
    private String imagem;

    public String getUsuarioId() { return usuarioId; }
    public String getTitulo() { return titulo; }
    public String getTipoMidia() { return tipoMidia; }
    public String getAutor() { return autor; }
    public String getEditora() { return editora; }
    public String getStatus() { return status; }
    public String getEstadoConservacao() { return estadoConservacao; }
    public String getOpcaoNegocio() { return opcaoNegocio; }
    public double getPreco() { return preco; }
    public String getDescricao() { return descricao; }
    public String getImagem() { return imagem; }

    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setTipoMidia(String tipoMidia) { this.tipoMidia = tipoMidia; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setEditora(String editora) { this.editora = editora; }
    public void setStatus(String status) { this.status = status; }
    public void setEstadoConservacao(String estadoConservacao) { this.estadoConservacao = estadoConservacao; }
    public void setOpcaoNegocio(String opcaoNegocio) { this.opcaoNegocio = opcaoNegocio; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setImagem(String imagem) { this.imagem = imagem; }
}