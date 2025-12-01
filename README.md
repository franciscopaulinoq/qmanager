# QManager

API em Spring Boot para um sistema de gerenciamento de fila de atendimentos.

## Visão Geral

O QManager é um sistema completo gerenciar filas de atendimento, permitindo:

- Criar e gerenciar categorias e prioridades de atendimento
- Gerar tickets com códigos sequenciais automáticos
- Chamar próximos clientes com diferentes estratégias (STRICT ou FIFO)
- Rastrear status dos tickets (WAITING, IN_PROGRESS, PENDING, CLOSED, EXPIRED)
- Gerenciar tentativas de chamada com limite configurável

## Estrutura do Projeto

```
qmanager/
├── src/
│   ├── main/
│   │   ├── java/io/github/franciscopaulinoq/qmanager/
│   │   │   ├── controller/          # REST Controllers
│   │   │   ├── service/             # Lógica de negócio
│   │   │   ├── repository/          # Acesso a dados (JPA)
│   │   │   ├── model/               # Entidades JPA
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── mapper/              # MapStruct mappers
│   │   │   ├── exception/           # Exceções customizadas
│   │   │   └── QManagerApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/        # Scripts Flyway
│   └── test/
│       └── java/...
├── docker-compose.yml               # Orquestração com Docker
├── Dockerfile                       # Build da aplicação
└── pom.xml                         # Configuração Maven
```

## Como Executar

### Pré-requisitos
- Java 21
- Maven 3.9+
- Docker e Docker Compose (opcional)
- PostgreSQL 16+ (se executar localmente)

### Opção 1: Com Docker Compose (Recomendado)

```bash
# Clone o repositório
git clone https://github.com/franciscopaulinoq/qmanager.git
cd qmanager

# Execute tudo com Docker
docker-compose up -d
```

A API estará disponível em `http://localhost:8080`

### Opção 2: Execução Local

```bash
# Configure o banco de dados PostgreSQL
# Edite src/main/resources/application.properties com suas credenciais

# Execute a aplicação
mvn spring-boot:run
```

## Configuração

### application.properties

```properties
# Banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5432/qmanager_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# Estratégia de fila: STRICT (por prioridade) ou FIFO
qmanager.queue.strategy=STRICT

# Máximo de tentativas antes de expirar o ticket
qmanager.queue.max.call.attempts=3

# Tamanho máximo de páginas
qmanager.web.max-page-size=10
```

## API Endpoints

### Categorias
```http
GET    /api/v1/categories              # Listar todas
GET    /api/v1/categories/{id}         # Buscar por ID
POST   /api/v1/categories              # Criar nova
PUT    /api/v1/categories/{id}         # Atualizar
DELETE /api/v1/categories/{id}         # Deletar (soft delete)
```

### Prioridades
```http
GET    /api/v1/priorities              # Listar todas
GET    /api/v1/priorities/{id}         # Buscar por ID
POST   /api/v1/priorities              # Criar nova
PUT    /api/v1/priorities/{id}         # Atualizar
DELETE /api/v1/priorities/{id}         # Deletar (soft delete)
```

### Tickets
```http
POST   /api/v1/tickets                 # Criar novo ticket
GET    /api/v1/tickets                 # Listar (paginado)
PATCH  /api/v1/tickets/{id}            # Atualizar status
POST   /api/v1/tickets/next            # Chamar próximo
POST   /api/v1/tickets/{id}/hold       # Colocar em espera
POST   /api/v1/tickets/{id}/reactivate # Reativar ticket
```

### Painel de Fila
```http
GET    /api/v1/queue-panel             # Obter status atual da fila
```

## Fluxo de Atendimento

```
1. Atendente cria ticket (WAITING)
                ↓
2. Sistema chama próximo ticket (IN_PROGRESS)
                ↓
        ┌─────────────┐
        │             │
3a. Cliente atendido  3b. Colocar em espera
    (CLOSED)              (PENDING)
                              ↓
                    4. Reativar ticket (WAITING ou IN_PROGRESS)
```

## Testes

Executar suite de testes:

```bash
mvn test
```

O projeto inclui testes unitários para a `TicketService` com cobertura de:
- Criação de tickets com validação
- Estratégias STRICT e FIFO
- Mudança de status
- Tratamento de exceções

## CI/CD

O projeto utiliza **GitHub Actions** para:
- Executar testes em cada push para `main` e `develop`
- Validar builds
- Verificar qualidade de código

Ver configuração em ci.yml