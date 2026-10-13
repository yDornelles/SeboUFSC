package br.ufsc.sebo.aplicacao.dto.proposta;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.ufsc.sebo.aplicacao.dto.item.RespostaItemPublico;
import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.item.Item;
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;
import br.ufsc.sebo.dominio.entidades.proposta.StatusProposta;
import br.ufsc.sebo.dominio.servico.ServicoItem;

public class RespostaProposta {

    public record Participante(
        String id,
        String nomeUsuario,
        boolean aceitou,
        List<String> entrega,
        List<String> recebe
    ) {}

    private final Integer id;
    private final Date data;
    private final StatusProposta status;
    private final List<RespostaItemPublico> itens;
    private final List<Participante> participantes;

    private RespostaProposta(Integer id, Date data, StatusProposta status,
                             List<RespostaItemPublico> itens, List<Participante> participantes) {
        this.id = id;
        this.data = data;
        this.status = status;
        this.itens = itens;
        this.participantes = participantes;
    }

    public static RespostaProposta de(Proposta proposta, ServicoItem servicoItem) {
        List<RespostaItemPublico> itens = proposta.getItens().stream()
            .map(i -> new RespostaItemPublico(i, servicoItem.buscarNomeProprietario(i.getUsuarioId())))
            .collect(Collectors.toList());

        Set<String> idsQueAceitaram = proposta.getAceites().stream()
            .map(Usuario::getId)
            .collect(Collectors.toSet());

        List<Participante> participantes = proposta.getParticipantes().stream()
            .map(u -> {
                Set<String> idsInteresse = u.getItensInteresse().stream()
                    .map(Item::getId)
                    .collect(Collectors.toSet());
                List<String> entrega = proposta.getItens().stream()
                    .filter(i -> u.getId().equals(i.getUsuarioId()))
                    .map(Item::getId)
                    .collect(Collectors.toList());
                List<String> recebe = proposta.getItens().stream()
                    .filter(i -> !u.getId().equals(i.getUsuarioId()) && idsInteresse.contains(i.getId()))
                    .map(Item::getId)
                    .collect(Collectors.toList());
                return new Participante(u.getId(), u.getNomeUsuario(),
                    idsQueAceitaram.contains(u.getId()), entrega, recebe);
            })
            .collect(Collectors.toList());

        return new RespostaProposta(proposta.getIdProposta(), proposta.getData(), proposta.getStatus(),
            itens, participantes);
    }

    public Integer getId() { return id; }
    public Date getData() { return data; }
    public StatusProposta getStatus() { return status; }
    public List<RespostaItemPublico> getItens() { return itens; }
    public List<Participante> getParticipantes() { return participantes; }
}
