# ContainerWatch
# ContainerWatch: Monitoramento e Auto-recuperação de Containers

<p>
<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
<img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
<img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Data JPA" />
<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
<img src="https://img.shields.io/badge/Shell_Script-121011?style=for-the-badge&logo=gnu-bash&logoColor=white" alt="Shell Script" />
</p>

Ferramenta de observabilidade e auto-recuperação para containers Docker. Monitora continuamente múltiplos serviços, detecta falhas, tenta reiniciá-los automaticamente e registra todo o histórico de eventos em banco de dados — uma versão simplificada do que ferramentas como Kubernetes e Prometheus fazem em escala.

## O que o sistema faz

Um conjunto de serviços roda em containers isolados. O ContainerWatch fica de fora, verificando a saúde de cada um continuamente. Quando um serviço cai:

1. A falha é detectada em segundos
2. O sistema tenta reiniciar o container automaticamente
3. O evento (queda, tentativa de restart, resultado) é persistido no banco via Spring Data JPA
4. O histórico fica disponível para consulta via API REST

## Arquitetura

```
docker-compose
├── app          → Spring Boot + JPA, expõe /actuator/health e API de eventos
├── db           → PostgreSQL, persistência dos eventos
├── servico-1    → container monitorado (simulado)
├── servico-2    → container monitorado (simulado)
└── servico-3    → container monitorado (simulado)
```

## Modelo de dados

```
Servico (1) ────── (N) EventoMonitoramento
```

**Servico**: nome, nome do container no Docker, status atual

**EventoMonitoramento**: timestamp, status detectado, mensagem, tempo de resposta (ms)

**StatusServico** (enum): `UP`, `DOWN`, `REINICIANDO`, `FALHA_REINICIO`

## Scripts de automação

| Script | Função |
|---|---|
| `build.sh` | builda a imagem da aplicação (Dockerfile multi-stage) |
| `deploy.sh` | sobe todo o ambiente via Docker Compose, aguarda o health check antes de liberar o terminal |
| `healthcheck.sh` | roda em loop verificando cada serviço monitorado; reinicia automaticamente o que estiver fora do ar e registra o evento |
| `logwatch.sh` | agrega e filtra logs de todos os containers monitorados, destacando erros |
| `backup.sh` | gera backup do banco (`pg_dump`) com timestamp |
| `rollback.sh` | reverte para a imagem anterior se detectar instabilidade repetida após um deploy |

## Como executar

```bash
./scripts/build.sh
./scripts/deploy.sh
./scripts/healthcheck.sh
```

Para simular uma falha e observar a auto-recuperação em tempo real:

```bash
docker stop cw-servico-1
```

## Decisões de projeto

- **Multi-stage build no Dockerfile:** separa o ambiente de compilação (Maven completo) da imagem final (apenas JRE + `.jar`), resultando em uma imagem de produção bem mais enxuta.
- **Sem front-end:** o foco do projeto é a automação de infraestrutura, não uma interface de usuário. A observação do sistema acontece via terminal, logs dos scripts e API REST — reflete como ferramentas reais desse tipo costumam ser consumidas.
- **`ddl-auto=update`:** o Hibernate cria o schema automaticamente a partir das entidades, diferente da abordagem manual usada em projetos anteriores — opção consciente para um projeto desse porte.
- **Senha do banco via variável de ambiente:** nunca versionada no `docker-compose.yml`, seguindo a mesma prática de segurança aplicada no projeto LogSentinel.

## Status

Em desenvolvimento.
