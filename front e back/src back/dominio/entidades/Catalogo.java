package br.ufsc.sebo.dominio.entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.ufsc.sebo.dominio.entidades.item.Item;

public class Catalogo {
    private List<Item> itensAcervo;

    public Catalogo() {
        this.itensAcervo = new ArrayList<>();
    }

    public List<Item> getItensAcervo() {
        return itensAcervo;
    }

    public void setItensAcervo(List<Item> itensAcervo) {
        this.itensAcervo = itensAcervo;
    }

    public boolean adicionarItem(Item item) {
        if (item != null && !itensAcervo.contains(item)) {
            return itensAcervo.add(item);
        }
        return false;
    }

    public boolean removerItem(Item item) {
        return itensAcervo.remove(item);
    }

    public List<Item> filtrarPorTipo(String tipo) {
        if (tipo == null) return new ArrayList<>();
        return itensAcervo.stream()
                .filter(item -> item.getTipoMidia() != null && item.getTipoMidia().name().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }

    public List<Item> filtrarPorPreco(float max) {
        return itensAcervo.stream()
                .filter(item -> item.getPreco() <= max)
                .collect(Collectors.toList());
    }

    public List<Item> buscarPorTitulo(String titulo) {
        if (titulo == null) return new ArrayList<>();
        String tituloLower = titulo.toLowerCase();
        return itensAcervo.stream()
                .filter(item -> item.getTitulo() != null && item.getTitulo().toLowerCase().contains(tituloLower))
                .collect(Collectors.toList());
    }
}