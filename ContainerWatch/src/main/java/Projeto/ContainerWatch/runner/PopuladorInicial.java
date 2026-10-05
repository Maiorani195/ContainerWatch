package Projeto.ContainerWatch.runner;

import Projeto.ContainerWatch.model.Servico;
import Projeto.ContainerWatch.model.StatusServico;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import Projeto.ContainerWatch.repository.ServicoRepository;

@Component
public class PopuladorInicial implements CommandLineRunner {

    private final ServicoRepository servicoRepository;

    public PopuladorInicial(ServicoRepository servicoRepository){
        this.servicoRepository = servicoRepository;
    }


    @Override
    public void run(String... args){
        if (servicoRepository.count() > 0){
            return;
        }

        criarServico("servico-1", "cw-servico-1");
        criarServico("servico-2", "cw-servico-2");
        criarServico("servico-3", "cw-servico-3");

        System.out.println("Servicos iniciais criados: " + servicoRepository.count());

    }

    private void criarServico(String nome, String containerName){
        Servico s = new Servico();
        s.setNome(nome);
        s.setContainerName(containerName);
        s.setStatusAtual(StatusServico.UP);
    servicoRepository.save(s);
    }

}
