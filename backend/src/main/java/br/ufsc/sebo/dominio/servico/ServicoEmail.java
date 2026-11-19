package br.ufsc.sebo.dominio.servico;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.ufsc.sebo.dominio.entidades.Usuario;
import br.ufsc.sebo.dominio.entidades.proposta.Proposta;
import br.ufsc.sebo.dominio.entidades.proposta.PropostaObserver;
import br.ufsc.sebo.dominio.entidades.proposta.StatusProposta;

@Service
public class ServicoEmail implements PropostaObserver {

    private final JavaMailSender mailSender;

    public ServicoEmail(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarEmail(String destinatario, String assunto, String corpo) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo(destinatario);
            mensagem.setSubject(assunto);
            mensagem.setText(corpo);
            mailSender.send(mensagem);
        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail para " + destinatario + ": " + e.getMessage());
        }
    }

    @Override
    public void onPropostaAtualizada(Proposta proposta) {
        String assunto = "Sebo UFSC - Atualização na Proposta de Troca";
        String corpo = "";
        StatusProposta status = proposta.getStatus();

        if (status == StatusProposta.AGUARDANDO_CONFIRMACAO) {
            assunto = "Sebo UFSC - Nova Proposta de Troca Criada";
            corpo = "Uma nova proposta de troca envolvendo os seus itens foi registrada. Acesse a plataforma para revisar os itens e confirmar.";
        } else if (status == StatusProposta.CONFIRMADA) {
            assunto = "Sebo UFSC - Proposta Confirmada!";
            corpo = "Parabéns! Todos os participantes confirmaram a proposta de troca. O chat de negociação já está liberado.";
        } else if (status == StatusProposta.CANCELADA) {
            assunto = "Sebo UFSC - Proposta Cancelada";
            corpo = "Informamos que a proposta de troca foi cancelada ou recusada.";
        } else {
            return;
        }

        for (Usuario participante : proposta.getParticipantes()) {
            if (participante.getEmail() != null && !participante.getEmail().isEmpty()) {
                enviarEmail(participante.getEmail(), assunto, corpo);
            }
        }
    }
}