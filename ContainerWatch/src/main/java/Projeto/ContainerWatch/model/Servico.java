package Projeto.ContainerWatch.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String containerName;
    @Enumerated(EnumType.STRING)
    private StatusServico statusAtual;
    @OneToMany(mappedBy = "servico")
    private List<EventoMonitoramento> eventos = new ArrayList<>();



public Servico(){

}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public StatusServico getStatusAtual() {
        return statusAtual;
    }

    public void setStatusAtual(StatusServico statusAtual) {
        this.statusAtual = statusAtual;
    }

    public List<EventoMonitoramento> getEventos() {
        return eventos;
    }
}


