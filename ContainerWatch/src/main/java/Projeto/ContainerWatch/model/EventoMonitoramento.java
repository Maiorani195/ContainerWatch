package Projeto.ContainerWatch.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class EventoMonitoramento {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private LocalDateTime timestamp;
@Enumerated(EnumType.STRING)
private StatusServico status;
private String mensagem;
private Long tempoDeRespostaMs;



    public EventoMonitoramento(){

 }

@ManyToOne
    Servico servico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public StatusServico getStatus() {
        return status;
    }

    public void setStatus(StatusServico status) {
        this.status = status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Long getTempoDeRespostaMs() {
        return tempoDeRespostaMs;
    }

    public void setTempoDeRespostaMs(Long tempoDeRespostaMs) {
        this.tempoDeRespostaMs = tempoDeRespostaMs;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }
}


