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

1. A falha é detectada no ciclo seguinte do health check
2. O sistema tenta reiniciar o container automaticamente
3. O evento (queda, tentativa de restart, resultado) é persistido no banco via Spring Data JPA, através de uma API REST
4. O histórico fica disponível para consulta

## Arquitetura

```
docker-compose
├── app          → Spring Boot + JPA, API REST de serviços e eventos
├── db           → PostgreSQL, persistência dos eventos
├── cw-servico-1 → container monitorado (simulado)
├── cw-servico-2 → container monitorado (simulado)
└── cw-servico-3 → container monitorado (simulado)
```

Fora do Compose, os scripts Shell orquestram o ciclo de vida da aplicação e o monitoramento ativo dos containers.

## Modelo de dados

```
Servico (1) ────── (N) EventoMonitoramento
```

**Servico**: nome, nome do container no Docker, status atual

**EventoMonitoramento**: timestamp, status detectado, mensagem, tempo de resposta (ms)

**StatusServico** (enum): `UP`, `DOWN`, `REINICIANDO`, `FALHA_REINICIO`

## API

| Método | Rota | Função |
|---|---|---|
| GET | `/servicos` | Lista todos os serviços e status atual |
| GET | `/eventos` | Histórico completo de eventos |
| GET | `/eventos/{servicoId}` | Eventos de um serviço específico |
| POST | `/eventos` | Registra um novo evento e atualiza o status do serviço |

## Scripts de automação

| Script | Função |
|---|---|
| `build.sh` | Builda a imagem da aplicação (Dockerfile multi-stage) |
| `deploy.sh` | Sobe todo o ambiente em segundo plano e aguarda o health check |
| `healthcheck.sh` | Loop contínuo: verifica cada serviço, reinicia o que cair e registra o evento via API |
| `logwatch.sh` | Agrega os logs de todos os containers em paralelo, destacando erros |
| `backup.sh` | Gera backup do banco (`pg_dump`) com timestamp |
| `rollback.sh` | Reverte para a imagem anterior da aplicação, com confirmação |

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

Para acompanhar os logs agregados:

```bash
./scripts/logwatch.sh
```

## Decisões de projeto

- **Multi-stage build com Maven Wrapper:** o Dockerfile usa `eclipse-temurin:25-jdk` como base, em vez de uma imagem Maven pronta, porque não havia imagem oficial `maven` compatível com Java 25 no momento da construção do projeto. O `mvnw` baixa a versão certa do Maven dentro do próprio build.
- **`@JsonIgnore` no relacionamento `Servico → eventos`:** evita um ciclo infinito de serialização JSON (evento referencia serviço, que referenciaria a lista de eventos de volta).
- **Sem front-end:** o foco do projeto é a automação de infraestrutura. A observação acontece via terminal, scripts e API REST — reflete como ferramentas reais desse tipo costumam ser consumidas.
- **Postgres em vez de SQLite:** diferente do projeto anterior (LogSentinel), aqui a aplicação já nasce distribuída entre containers, então um banco com servidor próprio (acessível pela rede do Compose) faz mais sentido que um banco em arquivo local.

graph TD
    Scripts["🖥️ Shell Scripts<br/>(healthcheck.sh / automação)"]

    subgraph "Docker Compose"
        API["⚙️ ContainerWatch API<br/>(Spring Boot + JPA)"]
        DB[("🗄️ PostgreSQL<br/>(Banco de Dados)")]
        
        subgraph "Containers Monitorados"
            S1["📦 cw-servico-1<br/>(Nginx)"]
            S2["📦 cw-servico-2<br/>(Nginx)"]
            S3["📦 cw-servico-3<br/>(Nginx)"]
        end
    end

    Scripts -- "1. Verifica Saúde (cURL)" --> S1
    Scripts -. "Verifica" .-> S2
    Scripts -. "Verifica" .-> S3
    
    Scripts -- "2. Detecta falha e<br/>chama API (POST)" --> API
    
    API -- "3. Grava evento" --> DB

    classDef docker fill:#2496ED,stroke:#fff,stroke-width:2px,color:#fff,font-weight:bold;
    classDef db fill:#336791,stroke:#fff,stroke-width:2px,color:#fff,font-weight:bold;
    classDef spring fill:#6DB33F,stroke:#fff,stroke-width:2px,color:#fff,font-weight:bold;
    classDef script fill:#2B2D42,stroke:#fff,stroke-width:2px,color:#fff,font-weight:bold;

    class S1,S2,S3 docker;
    class DB db;
    class API spring;
    class Scripts script;


## Status

Concluído.
