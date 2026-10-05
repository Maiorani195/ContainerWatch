package Projeto.ContainerWatch.controller;


import Projeto.ContainerWatch.model.Servico;
import Projeto.ContainerWatch.repository.ServicoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoRepository servicoRepository;

    public ServicoController(ServicoRepository servicoRepository){
        this.servicoRepository =servicoRepository;
    }

    @GetMapping
    public List<Servico> listarTodos(){
        return  servicoRepository.findAll();
    }

}
