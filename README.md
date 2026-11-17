# hexagonal-architecture-template

Projeto base em Spring Boot organizado em arquitetura hexagonal (ports and adapters). Uso ele como ponto de partida para serviços novos e também como material de referência em aula.

O domínio de exemplo é um cadastro de pedidos. É pequeno de propósito: dá para ler o projeto inteiro em meia hora e ainda assim ele cobre o que aparece num serviço real, como validação, regra de negócio, persistência, eventos, tratamento de erro e testes em cada camada.

## Stack

- Java 21
- Spring Boot 4.1 (Web MVC, Validation, Data JPA, Actuator)
- PostgreSQL 17 com migrações via Flyway
- springdoc-openapi para a documentação da API
- JUnit 5, AssertJ, Mockito e ArchUnit nos testes, com H2 em modo PostgreSQL nos testes de integração

## Como rodar

Requisitos: JDK 21, Maven 3.9+ e Docker.

Suba o banco:

```bash
docker compose up -d postgres
```

Rode a aplicação:

```bash
mvn spring-boot:run
```

A API fica em `http://localhost:8080`, o Swagger UI em `http://localhost:8080/swagger-ui.html` e o health check em `http://localhost:8080/actuator/health`.

Para subir banco e aplicação juntos em containers:

```bash
docker compose --profile app up --build
```

A conexão com o banco é configurada por variáveis de ambiente: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` e `DB_PASSWORD`. Os valores padrão batem com o `compose.yaml`.

## Testes

```bash
mvn verify
```

Os testes não dependem de Docker. O banco usado nos testes de integração é um H2 em memória, e as mesmas migrações do Flyway rodam nele.

## Estrutura

```
src/main/java/com/example/orders
├── domain
│   ├── model          Order, OrderItem, Money, OrderId, OrderStatus
│   ├── event          OrderCreated, OrderCancelled
│   └── exception      exceções de regra de negócio
├── application
│   ├── port/in        casos de uso expostos (interfaces)
│   ├── port/out       o que a aplicação precisa do mundo externo (interfaces)
│   ├── service        implementação dos casos de uso
│   ├── pagination     PageQuery e PageResult
│   └── exception      OrderNotFoundException
├── adapter
│   ├── in/web         controller REST, DTOs e tratamento de erros
│   ├── out/persistence  entidades JPA, repositório Spring Data e adaptador
│   └── out/event      publicador de eventos (hoje só registra em log)
└── config             montagem dos casos de uso como beans
```

A regra é uma só: dependência aponta para dentro. `domain` não conhece ninguém. `application` conhece `domain`. Os adaptadores conhecem as portas e o domínio, mas nunca os serviços diretamente nem uns aos outros. A única classe que enxerga tudo é a `UseCaseConfig`, que liga as implementações às interfaces.

Isso não fica só na documentação. O `ArchitectureTest` usa ArchUnit para quebrar o build se alguém importar Spring, JPA ou Hibernate dentro de `domain` ou `application`, ou se um controller passar a depender de um adaptador de persistência.

## Decisões que vale conhecer

O núcleo não tem anotação de framework. Os serviços são classes Java comuns, instanciadas na `UseCaseConfig`. Assim os testes de domínio e de aplicação rodam sem subir contexto Spring, usando implementações em memória das portas de saída (veja `InMemoryOrderRepository` nos testes).

Entidade JPA e entidade de domínio são classes separadas. `OrderJpaEntity` vive no adaptador de persistência e o `OrderPersistenceMapper` faz a conversão nos dois sentidos. Dá mais código, mas o domínio fica livre para ter construtores privados, invariantes e imutabilidade sem brigar com as exigências do Hibernate.

O controle transacional está no adaptador de persistência. Para o exemplo isso basta, porque cada caso de uso faz uma única escrita. Se um caso de uso precisar de várias escritas atômicas, o caminho é envolver a porta de entrada num decorator transacional dentro de `config`, sem levar `@Transactional` para a camada de aplicação.

O horário vem de um `Clock` injetado. Os testes usam `Clock.fixed` e não dependem da hora em que rodam.

Os erros saem no formato Problem Details (RFC 9457). Validação de entrada retorna 400 com a lista de campos inválidos, pedido inexistente retorna 404 e violação de regra de negócio retorna 422.

## Endpoints

| Método | Caminho | Descrição |
| --- | --- | --- |
| POST | `/api/v1/orders` | cria um pedido |
| GET | `/api/v1/orders/{id}` | busca um pedido |
| GET | `/api/v1/orders?page=0&size=20` | lista pedidos paginados |
| POST | `/api/v1/orders/{id}/cancel` | cancela um pedido |

Exemplo de criação:

```bash
curl -i -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
        "customerId": "customer-42",
        "items": [
          { "productId": "book-1", "quantity": 1, "unitPrice": 59.90 },
          { "productId": "pen-7", "quantity": 3, "unitPrice": 4.50 }
        ]
      }'
```

## Adicionando um novo caso de uso

Um roteiro que funciona bem, usando como exemplo "confirmar pedido":

1. Coloque a regra no domínio, por exemplo um método `confirm()` em `Order` que valida o status atual.
2. Crie a porta de entrada `ConfirmOrderUseCase` em `application/port/in`.
3. Implemente `ConfirmOrderService` em `application/service`, usando só as portas de saída.
4. Escreva o teste do serviço com os fakes em memória antes de pensar em HTTP.
5. Registre o bean na `UseCaseConfig`.
6. Exponha o endpoint no `OrderController` e cubra com um teste `@WebMvcTest`.

Se o caso de uso precisar de algo novo do mundo externo (um gateway de pagamento, por exemplo), crie uma porta em `application/port/out` e um adaptador em `adapter/out`.

## Usando como template

Ao criar um projeto a partir deste repositório, renomeie o pacote `com.example.orders`, ajuste `groupId` e `artifactId` no `pom.xml` e troque o domínio de pedidos pelo seu. A estrutura de pastas, a configuração de testes e o `ArchitectureTest` continuam valendo.
