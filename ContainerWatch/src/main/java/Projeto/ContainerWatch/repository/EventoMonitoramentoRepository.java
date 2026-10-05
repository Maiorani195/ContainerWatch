package Projeto.ContainerWatch.repository;

import Projeto.ContainerWatch.model.EventoMonitoramento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoMonitoramentoRepository extends JpaRepository<EventoMonitoramento , Long > {
    List<EventoMonitoramento> findByServicoIdOrderByTimestampDesc(Long servicoId);



}
