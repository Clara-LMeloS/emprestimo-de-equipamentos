# Empréstimo de Equipamentos

Sistema para gerenciamento de equipamentos, colaboradores e empréstimos, desenvolvido com Java e Spring Boot.

## Acesso à aplicação

**Aplicação publicada no Render:**

https://emprestimo-de-equipamentos.onrender.com

**Repositório GitHub:**

https://github.com/Clara-LMeloS/emprestimo-de-equipamentos

## Tecnologias utilizadas

- Java 25
- Spring Boot
- Spring Data JPA
- Maven
- Banco de dados H2
- HTML, CSS e JavaScript
- Docker
- Render

## Como executar o projeto localmente

### Pré-requisitos

- Java 25
- IntelliJ IDEA ou outra IDE Java
- Git

### 1. Clonar o repositório

```bash
git clone https://github.com/Clara-LMeloS/emprestimo-de-equipamentos.git
```

### 2. Entrar na pasta do projeto

```bash
cd emprestimo-de-equipamentos
```

### 3. Executar a aplicação

Com Maven instalado:

```bash
mvn spring-boot:run
```

Ou, utilizando o Maven Wrapper no Windows:

```bash
mvnw.cmd spring-boot:run
```

Também é possível abrir o projeto no IntelliJ IDEA e executar a classe principal da aplicação Spring Boot.

### 4. Acessar a aplicação

Abra o navegador:

```text
http://localhost:8080
```

## Rotas da API

### Colaboradores

| Método | Rota | Descrição |
|---|---|---|
| POST | `/colaboradores` | Cadastrar colaborador |
| GET | `/colaboradores` | Listar colaboradores |
| GET | `/colaboradores/{id}` | Buscar colaborador por ID |
| GET | `/colaboradores/{id}/emprestimos` | Consultar empréstimos do colaborador |

### Equipamentos

| Método | Rota | Descrição |
|---|---|---|
| POST | `/equipamentos` | Cadastrar equipamento |
| GET | `/equipamentos` | Listar equipamentos |
| GET | `/equipamentos/{id}` | Buscar equipamento por ID |
| PUT | `/equipamentos/{id}` | Atualizar equipamento |
| DELETE | `/equipamentos/{id}` | Excluir equipamento |

**Filtros disponíveis na listagem:**

- `tipo`: filtra pelo tipo de equipamento.
- `disponivel`: filtra pela disponibilidade (`true` ou `false`).

Exemplos:

```text
GET /equipamentos?tipo=NOTEBOOK
GET /equipamentos?disponivel=true
```

### Empréstimos

| Método | Rota | Descrição |
|---|---|---|
| POST | `/emprestimos?equipamentoId={id}&colaboradorId={id}` | Registrar empréstimo |
| GET | `/emprestimos` | Listar empréstimos |
| GET | `/emprestimos/{id}` | Buscar empréstimo por ID |
| PUT | `/emprestimos/{id}/devolucao` | Registrar devolução |

**Filtro disponível:**

```text
GET /emprestimos?atrasados=true
```

Retorna os empréstimos em atraso.

## Regras de negócio implementadas

- Equipamentos são cadastrados inicialmente como disponíveis.
- Ao realizar um empréstimo, o equipamento passa a ficar indisponível.
- Ao registrar a devolução, o equipamento volta a ficar disponível.
- O prazo de empréstimo é de 14 dias.
- Não é permitido emprestar um equipamento que já esteja emprestado.
- Colaboradores com atrasos pendentes não podem realizar novos empréstimos.
- Não é permitido realizar a devolução de um empréstimo mais de uma vez.
- Não é permitido excluir equipamentos que possuem empréstimos registrados.

## Bônus implementados

- **Filtros:** pesquisa de equipamentos por tipo e disponibilidade.
- **Controle de atrasos:** identificação de empréstimos em atraso.
- **Histórico:** consulta dos empréstimos realizados por colaborador.
- **Atualização:** edição dos dados dos equipamentos.
- **Proteção de exclusão:** bloqueio da exclusão de equipamentos com empréstimos registrados.
- **Testes automatizados:** testes unitários com JUnit.
- **Docker:** configuração para execução da aplicação em container.
- **Deploy:** aplicação publicada no Render.
- **Interface web:** frontend integrado à API para interação com o sistema.

## Banco de dados

O projeto utiliza o banco de dados H2 em memória.

**Importante:** por utilizar armazenamento em memória, os dados podem ser perdidos quando a aplicação é reiniciada ou passa por um novo deploy.

---

## Autora

Desenvolvido como projeto prático de aprendizado em Java e Spring Boot.