# QManager

API em Spring Boot para um sistema de gerenciamento de fila de atendimentos.

## Tecnologias Utilizadas

Este projeto utiliza as seguintes tecnologias:

* **Java 21**
* **Spring Boot 3.x**
* **Spring Web:** Para a criação de APIs REST.
* **Spring Data JPA:** Para persistência de dados.
* **Spring Security:** Para autenticação e autorização (RBAC).
* **PostgreSQL:** Como banco de dados.
* **Lombok:** Para redução de boilerplate.
* **Maven:** Como gerenciador de dependências.

## Como Executar o Projeto

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/franciscopaulinoq/qmanager.git
    cd qmanager
    ```

2.  **Configure o Banco de Dados:**
    * Crie um banco de dados no PostgreSQL (ex: `qmanager_db`).
    * Abra o arquivo `src/main/resources/application.properties`.
    * Adicione as configurações do seu banco de dados:

    ```properties
    # Configuração do Banco de Dados PostgreSQL
    spring.datasource.url=jdbc:postgresql://localhost:5432/qmanager_db
    spring.datasource.username=seu_usuario_postgres
    spring.datasource.password=sua_senha_postgres

    # Configurações do JPA/Hibernate
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    ```

3.  **Execute a aplicação:**
    ```bash
    mvn spring-boot:run
    ```

A API estará disponível em `http://localhost:8080`.