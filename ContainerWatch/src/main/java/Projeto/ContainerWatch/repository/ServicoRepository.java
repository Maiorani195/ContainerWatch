package Projeto.ContainerWatch.repository;

import Projeto.ContainerWatch.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servico,Long> {

    Optional<Servico> findByContainerName(String containerName);
}
