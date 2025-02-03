# Concord Chat - Back-end (WIP)

**Versão Atual**: `0.1.0-beta (aberta para testes)`  
**README em Inglês**: [Clique aqui para ler em Inglês](/README.md)  

**GitHub Projects (Back-end)**: [Quadro do Projeto Back-end](https://github.com/username/backend-project-link)  
**Repositório Front-end**: [Concord Chat Front-end](http://github.com/mmiiranda/concord)  
**Deploy do Projeto**: [Live Demo do Front-end](http://164.68.101.141:8081/)  

---

## Índice
1. [Colaboradores](#colaboradores)  
2. [Fluxo de Desenvolvimento & Boas Práticas](#fluxo-de-desenvolvimento--boas-práticas)  
3. [Tecnologias & Dependências](#tecnologias--dependências)  
   - [Tecnologias Principais](#tecnologias-principais)  
   - [Spring Framework & Bibliotecas Relacionadas](#spring-framework--bibliotecas-relacionadas)  
   - [Stack de Testes](#stack-de-testes)  
4. [Funcionalidades Atuais](#funcionalidades-atuais)  
   - [Autenticação & Segurança](#autenticação--segurança)  
   - [Gerenciamento de Arquivos](#gerenciamento-de-arquivos)  
   - [Sistema de Amizade & Notificações em Tempo Real](#sistema-de-amizade--notificações-em-tempo-real)  
   - [Logging](#logging)  
   - [Programação Orientada a Aspectos (AOP)](#programação-orientada-a-aspectos-aop)  
   - [Documentação](#documentação)  
   - [Armazenamento de Histórico de Mensagens](#armazenamento-de-histórico-de-mensagens)  
   - [Mensagens de Texto em Tempo Real](#mensagens-de-texto-em-tempo-real)  
   - [Arquitetura de WebSockets](#arquitetura-de-websockets)  
   - [Estados de Amizade & Máquina de Estados](#estados-de-amizade--máquina-de-estados)  
   - [Testes Unitários & Qualidade](#testes-unitários--qualidade)  
   - [Redis para Escalabilidade Horizontal](#redis-para-escalabilidade-horizontal)  
   - [Arquitetura do Projeto & Padrões de Design](#arquitetura-do-projeto--padrões-de-design)  
   - [Docker Compose & Deploy](#docker-compose--deploy)  
5. [Trabalho em Progresso (WIP)](#trabalho-em-progresso-wip)  
6. [Como Executar](#como-executar)  
   - [Linux e Windows](#linux-e-windows)  
7. [Curiosidade](#curiosidade)  
8. [Estrutura do Projeto](#estrutura-do-projeto)  
9. [Contato & Feedback](#contato--feedback)  
10. [Licença](#licença)  

---

## Colaboradores

Abaixo estão os principais colaboradores do projeto (em ordem alfabética). Clique no card para visitar seus perfis no GitHub:

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/marcusnogueiraa">
        <img src="https://avatars.githubusercontent.com/marcusnogueiraa" width="100px;" alt="Marcus Nogueira"/>
        <br /><sub><b>Marcus Nogueira</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/mmiiranda">
        <img src="https://avatars.githubusercontent.com/mmiiranda" width="100px;" alt="Mauricio Miranda"/>
        <br /><sub><b>Mauricio Miranda</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/sheiely">
        <img src="https://avatars.githubusercontent.com/sheiely" width="100px;" alt="Sheiely Nascimento"/>
        <br /><sub><b>Sheiely Nascimento</b></sub>
      </a>
    </td>
  </tr>
</table>

---

## Fluxo de Desenvolvimento & Boas Práticas

- **GitFlow**: Seguimos o [modelo GitFlow](https://nvie.com/posts/a-successful-git-branching-model/), que inclui as branches `release`, `hotfix`, `feature` e `develop` para organizar o desenvolvimento.  
- **Kanban para Gerenciamento de Processos**: Nossas tarefas e issues são visualizadas e acompanhadas via um [sistema Kanban](https://www.atlassian.com/agile/kanban), garantindo fluxo contínuo de trabalho e rápida adaptação.  
- **Conventional Commits**: Adotamos o padrão [Conventional Commits](https://www.conventionalcommits.org/pt-br/v1.0.0/) para mensagens de commit mais claras e versionamento automático.  
- **Code Review & Pair Programming**: Todos os pull requests são revisados para manter a qualidade; ocasionalmente praticamos [Pair Programming](https://www.geeksforgeeks.org/pair-programming/) para melhorar a colaboração e o compartilhamento de conhecimento.  

---

## Tecnologias & Dependências

### Tecnologias Principais
- **Java 17**: Linguagem principal do projeto, garantindo recursos modernos e boa performance.  
- **Spring Boot**: Framework que facilita o desenvolvimento de aplicações de forma rápida e pronta para produção.  
- **Gradle**: Ferramenta de automação de builds, usada para gerenciar dependências e tarefas.  
- **PostgreSQL**: Banco de dados relacional para dados estruturados e transacionais.  
- **Redis**: Armazenamento de dados em memória para cache distribuído e suporte a escalabilidade horizontal.  
- **MongoDB**: Banco de dados NoSQL para armazenamento eficiente e flexível de grandes volumes de mensagens.  

### Spring Framework & Bibliotecas Relacionadas

1. **Spring Security**  
   Fornece recursos de autenticação, autorização e segurança (integrado ao JWT para autenticação baseada em tokens).

2. **Spring Data Redis**  
   Gerencia a integração com o Redis, usado para caching, mensagens e manutenção de estado entre múltiplas instâncias.

3. **Spring Data JPA**  
   Abstrações poderosas para acesso a banco de dados (usado com PostgreSQL via Hibernate).

4. **Spring Mongo**  
   Simplifica o uso do MongoDB para armazenar históricos de chats e outros cenários de big data.

5. **Spring DevTools**  
   Melhora a experiência de desenvolvimento com recarregamento automático e feedback rápido.

6. **Spring AOP**  
   Permite Programação Orientada a Aspectos, separando preocupações transversais (ex.: logs) da lógica de negócio.

7. **Spring WebSockets**  
   Suporte de baixo nível para WebSockets; estamos construindo nossa própria arquitetura de eventos em cima dele.

8. **Spring Mail**  
   Responsável pelo envio de emails, essencial para verificação de contas e fluxos de reset de senha.

9. **Hibernate**  
   Ferramenta de ORM (Object-Relational Mapping), usada em conjunto com o JPA para entidades no PostgreSQL.

10. **SLF4J (Simple Logging Facade for Java)**  
    Interface de logging unificada, com vários bindings de implementação.

11. **Logback**  
    Framework de logging (usado com SLF4J) para configurações avançadas de logs, saída no console e arquivos.

12. **Swagger**  
    Gera documentação interativa da API, facilitando a exploração e teste dos endpoints.

13. **Lombok**  
    Reduz boilerplate em código, gerando getters, setters, construtores etc. automaticamente.

### Stack de Testes

- **JUnit**  
  Framework padrão para testes unitários e de integração.
- **Mockito**  
  Usado para simular dependências em testes unitários, garantindo isolamento.
- **H2 Database**  
  Banco de dados relacional em memória para testar funcionalidades com rapidez e de forma isolada.

---

## Funcionalidades Atuais

### Autenticação & Segurança
- **Autenticação via JWT**: Utiliza tokens assinados com **HMAC256**; senhas de usuários são criptografadas com **BCrypt**.  
- **Verificação de E-mail**: Após o registro, é enviado um e-mail para verificar a propriedade da conta.  
- **Max Retry com IP Jail**: Impede ataques de força bruta ao bloquear o IP por 15 minutos após diversas tentativas de login falhas.  
- **Recuperação de Senha & Alteração Interna**: Fluxo de “Esqueci minha senha” e opção para o usuário alterar a senha logado.

### Gerenciamento de Arquivos
- **Serviço de Upload & Download Manual**: Abordagem simplificada para armazenar e recuperar arquivos enviados pelos usuários.

### Sistema de Amizade & Notificações em Tempo Real
- Usuários podem enviar solicitações de amizade, aceitar/recusar/cancelar e remover amizades.  
- **WebSockets** são usados para entregar notificações em tempo real (ex.: pedido de amizade instantâneo para o destinatário).

### Logging
- **SLF4J & Logback**: Logs gerados tanto no terminal quanto em arquivos externos, fornecendo rastreabilidade e informações de depuração.

### Programação Orientada a Aspectos (AOP)
- Preocupações transversais (como logging) são separadas da lógica de negócio, tornando o código mais limpo e fácil de manter.  
- [Saiba mais sobre AOP](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop)

### Documentação
- **Swagger**: Gera documentação de API de forma automática, acessível em uma interface web interativa.

### Armazenamento de Histórico de Mensagens
- **MongoDB** é utilizado para armazenar dados de mensagens de forma eficiente. Como o volume de mensagens pode crescer consideravelmente, uma solução NoSQL como o MongoDB é ideal para consultas rápidas, indexação e escalabilidade horizontal.

### Mensagens de Texto em Tempo Real
- **Conexões WebSocket** garantem entrega instantânea das mensagens.  
- Mensagens também são armazenadas offline para recuperação quando o usuário se reconecta.

### Arquitetura de WebSockets
- Construída **do zero**, sem uso de sub-protocolos como STOMP.  
- Temos o enum `EventType`:
  ```java
  public enum EventType {
      CONNECT,
      USER_MESSAGE,
      FRIEND_REQUEST,
      CHANNEL_MESSAGE,
      USER_STATUS
  }
  ```
- Eventos recebidos são delegados a handlers específicos (ex.: `UserMessageHandler`), enquanto eventos de saída são disparados via um **Notification Service** para atualizações em tempo real.

### Estados de Amizade & Máquina de Estados
- Uma amizade pode estar nos estados: **pending**, **accepted**, **denied**, **canceled**, ou **removed**.  
- Regras:  
  - Só quem enviou pode *cancelar* um pedido pendente.  
  - Só quem recebeu pode *recusar* ou *aceitar* um pedido pendente.  
  - Ambos podem *remover* uma amizade aceita.  
- O **Friendship Service** implementa essas regras. (Diagrama de máquina de estados abaixo.)

<img src="./docs/images/friendship_states.jpeg" alt="Diagrama de Máquina de Estados de Amizade" width="1000"/>

### Testes Unitários & Qualidade
- **Mockito + JUnit + H2**: Os testes simulam cenários reais com um banco de dados em memória.  
- **Nenhum Teste Quebrado**: É **proibido** subir código com testes falhando, garantindo alta confiabilidade.

### Redis para Escalabilidade Horizontal
- Planejamos escalar a aplicação em múltiplas instâncias atrás de um balanceador de carga.  
- **Redis** é usado para cache distribuído e para sincronizar estado (ex.: dados de sessão, presença de usuário) entre diferentes nós da aplicação.

### Arquitetura do Projeto & Padrões de Design
- **Arquitetura em Camadas**: Separa preocupações por domínio e fluxo (controllers, services, repositories, domain objects).  
- **Padrões DTO & Mapper**: Isolam a camada de persistência das representações externas, garantindo maior flexibilidade e facilidade de manutenção.  
- **Programação Orientada a Aspectos**: Como mencionado, é utilizada para lidar com preocupações transversais.  
  [Mais sobre AOP](https://www.baeldung.com/spring-aop)

### Docker Compose & Deploy
- Usamos **Docker Compose** para subir todo o ambiente (PostgreSQL, Redis, MongoDB etc.) para testes de integração e deploy.  
- **Gradle bootRun** é utilizado no desenvolvimento local, junto ao **DevTools** para hot reload.

---

## Trabalho em Progresso (WIP)

Alguns recursos futuros (ou parcialmente implementados):

- **Canais & Servidores**  
  Introduzir estruturas hierárquicas similares a “Servidores” e canais dedicados (texto/voz), como no Discord.

- **Envio de Arquivos**  
  Melhorias no compartilhamento de arquivos dentro do chat, com checagens de segurança aprimoradas.

- **Gravação de Áudio**  
  Possibilidade de gravar (e talvez transmitir) canais de áudio em tempo real.

- **Otimização de Cache**  
  Identificar e adicionar caches estratégicos para reduzir a carga de processamento no back-end.

- **Observabilidade com Grafana**  
  Integrar métricas e logs com ferramentas como o Grafana para maior visibilidade do desempenho do sistema.

- **Testes End-to-End (e2e)**  
  Testes que abrangem desde o front-end até o back-end para garantir uma experiência de usuário consistente.

- **Pipelines de CI/CD**  
  Processos automatizados de build, teste e deploy para agilizar e garantir confiabilidade nas publicações.

- **Chat de Voz**  
  Comunicação de voz em tempo real, incluindo gerenciamento de sessões e otimizações de latência.

---

## Como Executar

> **Nota**: Certifique-se de ter **Java 17**, **Gradle** e **Docker** instalados.

### Linux e Windows

1. **Clonar o repositório**:
   ```bash
   git clone https://github.com/username/concord-backend.git
   ```
2. **Fazer o build do projeto**:
   ```bash
   cd concord-backend
   gradle build
   ```
3. **Iniciar os serviços Docker**:
   ```bash
   docker compose up
   ```
   Isso irá subir as instâncias do Spring Boot, PostgreSQL, Redis e MongoDB (e quaisquer outras dependências).
   A aplicação de back-end iniciará na porta configurada (por exemplo, `8080`).

---

## Curiosidade

Este projeto já ultrapassou **5000 linhas de código** (e aumentando), refletindo a complexidade e diversidade de funcionalidades em desenvolvimento.

---

## Estrutura do Projeto

Abaixo está uma visão simplificada das pastas e arquivos principais do **Concord Chat Back-end**:

```
concord/src
├───main
│   ├───java
│   │   └───com
│   │       └───concord
│   │           └───concordapi
│   │               │   ConcordapiApplication.java
│   │               │   
│   │               ├───auth
│   │               │   ├───controller
│   │               │   │       AuthController.java
│   │               │   │
│   │               │   ├───dto
│   │               │   │       ConfirmationCode.java
│   │               │   │       CreateUserDto.java
│   │               │   │       ForgotPasswordRequest.java
│   │               │   │       LoginUserDto.java
│   │               │   │       RecoveryJwtTokenDto.java
│   │               │   │       ResetPasswordRequest.java
│   │               │   │       ValidadeJwtTokenDto.java
│   │               │   │
│   │               │   ├───entity
│   │               │   │       UserDetailsImpl.java
│   │               │   │
│   │               │   ├───exception
│   │               │   │       IncorrectCodeException.java
│   │               │   │       IncorrectTokenException.java
│   │               │   │       MaxRetryException.java
│   │               │   │       UserAlreadyExistsException.java
│   │               │   │
│   │               │   ├───filter
│   │               │   │       UserAuthenticationFilter.java
│   │               │   │
│   │               │   └───service
│   │               │           AuthService.java
│   │               │           JwtTokenService.java
│   │               │           UserDetailsServiceImpl.java
│   │               │
│   │               ├───channel
│   │               │   ├───controller
│   │               │   │       ChannelController.java
│   │               │   │
│   │               │   ├───dto
│   │               │   │   ├───request
│   │               │   │   │       ChannelCreateBodyDto.java
│   │               │   │   │       ChannelPutBodyDto.java
│   │               │   │   │
│   │               │   │   └───response
│   │               │   │           ChannelDto.java
│   │               │   │
│   │               │   ├───entity
│   │               │   │       Channel.java
│   │               │   │
│   │               │   ├───mapper
│   │               │   │       ChannelMapper.java
│   │               │   │
│   │               │   ├───repository
│   │               │   │       ChannelRepository.java
│   │               │   │
│   │               │   └───service
│   │               │           ChannelService.java
│   │               │
│   │               ├───fileStorage
│   │               │   ├───controller
│   │               │   │       FileStorageController.java
│   │               │   │
│   │               │   ├───dto
│   │               │   │       FileResponseDto.java
│   │               │   │       FileUploadResponseDto.java
│   │               │   │
│   │               │   ├───entity
│   │               │   │       FilePrefix.java
│   │               │   │       FileType.java
│   │               │   │
│   │               │   └───service
│   │               │           FileCleanupService.java
│   │               │           FileStorageService.java
│   │               │
│   │               ├───friendship
│   │               │   ├───controller
│   │               │   │       FriendshipController.java
│   │               │   │
│   │               │   ├───dto
│   │               │   │   ├───request
│   │               │   │   │       FriendshipCreateDTO.java
│   │               │   │   │
│   │               │   │   └───response
│   │               │   │           FriendshipDto.java
│   │               │   │
│   │               │   ├───entity
│   │               │   │       Friendship.java
│   │               │   │       FriendshipStatus.java
│   │               │   │
│   │               │   ├───mapper
│   │               │   │       FriendshipMapper.java
│   │               │   │
│   │               │   ├───repository
│   │               │   │       FriendshipRepository.java
│   │               │   │
│   │               │   └───service
│   │               │           FriendshipService.java
│   │               │
│   │               ├───logging
│   │               │   └───aspect
│   │               │           AuthLoggingAspect.java
│   │               │           ChannelLoggingAspect.java
│   │               │           FriendshipLoggingAspect.java
│   │               │           MessageLoggingAspect.java
│   │               │           ServerLoggingAspect.java
│   │               │           UserLoggingAspect.java
│   │               │           WebSocketLoggingAspect.java
│   │               │
│   │               ├───messsage
│   │               │   ├───controller
│   │               │   │       UserMessageController.java
│   │               │   │
│   │               │   ├───dto
│   │               │   │   ├───request
│   │               │   │   │       UserMessageRequestDto.java
│   │               │   │   │
│   │               │   │   └───response
│   │               │   │       UserChatSummaryDto.java
│   │               │   │       UserMessageResponseDto.java
│   │               │   │
│   │               │   ├───entity
│   │               │   │       UserChatSummary.java
│   │               │   │       UserMessage.java
│   │               │   │
│   │               │   ├───repository
│   │               │   │       UserMessageRepository.java
│   │               │   │
│   │               │   └───service
│   │               │           UserMessageService.java
│   │               │
│   │               ├───server
│   │               │   ├───controller
│   │               │   │       ServerController.java
│   │               │   │
│   │               │   ├───dto
│   │               │   │   ├───request
│   │               │   │   │       ServerCreateBodyDTO.java
│   │               │   │   │       ServerPutBodyDTO.java
│   │               │   │   │
│   │               │   │   └───response
│   │               │   │       ServerDto.java
│   │               │   │       ServerSummaryDto.java
│   │               │   │
│   │               │   ├───entity
│   │               │   │       Server.java
│   │               │   │
│   │               │   ├───mapper
│   │               │   │       ServerMapper.java
│   │               │   │
│   │               │   ├───repository
│   │               │   │       ServerRepository.java
│   │               │   │
│   │               │   └───service
│   │               │           ServerService.java
│   │               │
│   │               ├───shared
│   │               │   ├───config
│   │               │   │       AspectConfig.java
│   │               │   │       RedisConfig.java
│   │               │   │       SecurityConfiguration.java
│   │               │   │       WebConfig.java
│   │               │   │
│   │               │   ├───dto
│   │               │   │       ErrorResponseDTO.java
│   │               │   │
│   │               │   ├───exception
│   │               │   │       EmptyFileException.java
│   │               │   │       EntityNotFoundException.java
│   │               │   │       FailManipulationFriendship.java
│   │               │   │       FileFormatException.java
│   │               │   │       FileNotFoundException.java
│   │               │   │       FileStorageException.java
│   │               │   │       InternalServerErrorException.java
│   │               │   │       SMTPServerException.java
│   │               │   │
│   │               │   ├───handler
│   │               │   │       GlobalExceptionHandler.java
│   │               │   │
│   │               │   └───service
│   │               │           EmailService.java
│   │               │           RedisService.java
│   │               │
│   │               ├───user
│   │               │   ├───controller
│   │               │   │       UserController.java
│   │               │   │       UserPreferenceController.java
│   │               │   │
│   │               │   ├───dto
│   │               │   │   ├───request
│   │               │   │   │       UserPatchImage.java
│   │               │   │   │       UserPatchName.java
│   │               │   │   │       UserPatchUsername.java
│   │               │   │   │       UserPreferenceRequestDto.java
│   │               │   │   │
│   │               │   │   └───response
│   │               │   │       UserDto.java
│   │               │   │       UserPreferenceDto.java
│   │               │   │
│   │               │   ├───entity
│   │               │   │       User.java
│   │               │   │       UserPreference.java
│   │               │   │
│   │               │   ├───mapper
│   │               │   │       UserMapper.java
│   │               │   │       UserPreferenceMapper.java
│   │               │   │
│   │               │   ├───repository
│   │               │   │       UserPreferenceRepository.java
│   │               │   │       UserRepository.java
│   │               │   │
│   │               │   └───service
│   │               │           UserPreferenceService.java
│   │               │           UserService.java
│   │               │
│   │               └───websocket
│   │                   ├───config
│   │                   │       WebSocketConfig.java
│   │                   │
│   │                   ├───entity
│   │                   │   │   ClientMessage.java
│   │                   │   │   EventType.java
│   │                   │   │
│   │                   │   └───content
│   │                   │           ChannelMessageContent.java
│   │                   │           ConnectContent.java
│   │                   │           FriendRequestContent.java
│   │                   │           UserMessageContent.java
│   │                   │
│   │                   ├───handler
│   │                   │       ChannelMessageHandler.java
│   │                   │       ConnectHandler.java
│   │                   │       EventHandler.java
│   │                   │       UserMessageHandler.java
│   │                   │       WebSocketHandler.java
│   │                   │
│   │                   └───service
│   │                           NotificationService.java
│   │                           SessionService.java
│   │
│   └───resources
│           application.properties
│           logback.xml
│
└───test
    └───java
        └───com
            └───concord
                └───concordapi
                    │   ConcordapiApplicationTests.java
                    │
                    ├───auth
                    │   └───service
                    │           AuthServiceTest.java
                    │
                    ├───channel
                    │   └───service
                    │           ChannelServiceTest.java
                    │
                    ├───friendship
                    │   └───service
                    │           FriendshipServiceTest.java
                    │
                    ├───server
                    │   └───service
                    │           ServerServiceTest.java
                    │
                    ├───shared
                    │   ├───config
                    │   │       AppConfig.java
                    │   │
                    │   ├───service
                    │   │       EmailServiceTest.java
                    │   │
                    │   └───util
                    │           UtilsMethods.java
                    │
                    └───user
                        └───service
                                UserServiceTest.java
```

---

## Contato & Feedback

- **Informações de Deploy**: A versão `0.1.0-beta` está em um VPS Ubuntu 24.04 para testes públicos.  
- **Feedback & Sugestões**: Envie suas opiniões para [concord.chat@gmail.com](mailto:concord.chat@gmail.com).

---

## Licença

[Licença MIT](https://opensource.org/licenses/MIT)

---

<img src="./docs/images/concord_art.png" alt="Concord Art" width="500"/>

---

*Agradecemos por conferir o Back-end do Concord Chat! Estamos ansiosos pelas suas contribuições e feedback.*