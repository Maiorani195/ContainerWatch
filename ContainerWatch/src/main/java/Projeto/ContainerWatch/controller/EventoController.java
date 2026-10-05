package Projeto.ContainerWatch.controller;

import Projeto.ContainerWatch.model.EventoMonitoramento;
import Projeto.ContainerWatch.model.Servico;
import Projeto.ContainerWatch.model.StatusServico;
import Projeto.ContainerWatch.repository.EventoMonitoramentoRepository;
import Projeto.ContainerWatch.repository.ServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private EventoMonitoramentoRepository eventoMonitoramentoRepository;
    private final ServicoRepository servicoRepository;


    public EventoController(EventoMonitoramentoRepository eventoMonitoramentoRepository, ServicoRepository servicoRepository) {
        this.eventoMonitoramentoRepository = eventoMonitoramentoRepository;
        this.servicoRepository = servicoRepository;
    }

    @GetMapping
    public List<EventoMonitoramento> listarTodos() {
        return eventoMonitoramentoRepository.findAll();
    }

    @GetMapping("/{servicoId}")
    public List<EventoMonitoramento> listarPorServico(@PathVariable Long servicoId) {
        return eventoMonitoramentoRepository.findByServicoIdOrderByTimestampDesc(servicoId);
    }

    @PostMapping
    public EventoMonitoramento registrar(@RequestBody RegistroEventoRequest request) {
        Servico servico = servicoRepository.findByContainerName(request.containerName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Serviço nao encontrado!" + request.containerName())
                );
        servico.setStatusAtual(request.status());
        servicoRepository.save(servico);

        EventoMonitoramento evento = new EventoMonitoramento();
        evento.setServico(servico);
        evento.setStatus(request.status());
        evento.setMensagem(request.mensagem);
        evento.setTempoDeRespostaMs(request.tempoDeRespostaMS());
        evento.setTimestamp(LocalDateTime.now());

        return eventoMonitoramentoRepository.save(evento);
    }

    public record RegistroEventoRequest(
            String containerName,
            StatusServico status,
                    String mensagem,
            Long tempoDeRespostaMS
    ) {


    }

}
