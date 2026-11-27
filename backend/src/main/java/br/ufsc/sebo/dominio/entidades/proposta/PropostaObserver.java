package br.ufsc.sebo.dominio.entidades.proposta;

/**
 * Padrão Observer: quem quiser reagir a mudanças de uma Proposta
 * (ServicoChat, ServicoNotificacao, ServicoEmail) implementa esta interface.
 */
public interface PropostaObserver {
    void onPropostaAtualizada(Proposta proposta);
}
