# Cadastro de Funcionários (JavaFX) — P1 de Programação Orientada a Objetos III

Sistema em Java com interface gráfica JavaFX para gerenciar o cadastro de funcionários de uma empresa
(dados pessoais + endereço), com persistência em arquivo CSV e relatórios feitos com a API Stream.

## Requisitos

- **JDK 17 ou superior** (testado com o JDK 21)
- **IntelliJ IDEA** (Community ou Ultimate) — o Maven já vem embutido
- Internet na primeira abertura (o Maven baixa o JavaFX automaticamente; não é preciso instalar o JavaFX à mão)

## Como executar pelo IntelliJ

1. `File > Open...` e selecione o arquivo **`pom.xml`** (ou a pasta do projeto) → **Open as Project**.
2. Aguarde o IntelliJ importar o Maven (barra de progresso no canto inferior direito).
3. Confira o JDK em `File > Project Structure > Project > SDK` (17 ou superior).
4. Abra `src/main/java/br/unifan/funcionarios/app/Launcher.java` e clique no triângulo verde ao lado do `main`
   → **Run 'Launcher.main()'**.

> Execute sempre a classe **`Launcher`** (e não a `MainApp`). Sem `module-info.java`, o Java recusa
> iniciar diretamente uma classe que herda de `Application` (erro *"JavaFX runtime components are missing"*).

### Alternativa pelo terminal

```bash
mvn javafx:run
```

## Onde ficam os dados

Arquivo **`dados/funcionarios.csv`** (na raiz do projeto), separado por ponto-e-vírgula (`;`), com cabeçalho.

- É lido ao iniciar o programa e regravado após cada cadastro ou exclusão.
- O projeto já traz 6 funcionários de exemplo (CPFs gerados apenas para teste). Para começar vazio,
  apague as linhas de dados e mantenha só o cabeçalho (ou apague o arquivo: ele será recriado).
- Se o arquivo não for encontrado, o programa inicia vazio e cria o arquivo no primeiro cadastro.

## Como usar

**Aba "Funcionários"**
- Preencha o formulário e clique em **Cadastrar**. Erros de validação aparecem em uma janela de aviso.
- **Consultar por matrícula** mostra apenas o funcionário encontrado; **Listar todos** volta à lista completa.
- Selecione uma linha da tabela e clique em **Excluir selecionado** (pede confirmação).

**Aba "Relatórios"** — escolha o relatório e clique em **Gerar relatório**:
- Funcionários por cargo
- Funcionários por faixa salarial
- Média salarial por cargo
- Funcionários agrupados por cidade
- Funcionários agrupados por estado

### Formatos aceitos

| Campo | Regra |
|---|---|
| Matrícula | exatamente 6 dígitos (ex.: `000123`), única |
| Nome | mínimo de 3 caracteres, apenas letras e espaços |
| CPF | `000.000.000-00` ou 11 dígitos; dígitos verificadores conferidos; único |
| Data de nascimento / contratação | `dd/mm/aaaa` (ou escolhida no calendário); idade mínima de 18 anos |
| Salário | valor positivo: `5000,00`, `5.000,50` ou `5000.50` |
| CEP | `00000-000` ou 8 dígitos |
| Estado | sigla da lista (GO, SP, ...) |

## Estrutura do projeto

```
src/main/java/br/unifan/funcionarios/
├── app/         Launcher (main) e MainApp (monta as camadas e abre a janela)
├── controller/  FuncionarioController (eventos da tela)
├── model/       Funcionario, Endereco
├── service/     FuncionarioService (regras), RelatorioService (Streams)
├── repository/  FuncionarioRepository (interface), FuncionarioCsvRepository
├── util/        Validador (regras de validação dos campos)
└── exception/   ValidacaoException, PersistenciaException
src/main/resources/view/main-view.fxml   layout da tela
dados/funcionarios.csv                   arquivo de dados
```

## Problemas comuns

| Sintoma | Solução |
|---|---|
| `JavaFX runtime components are missing` | Execute a classe `Launcher`, não a `MainApp` |
| Classes/`javafx.*` em vermelho | Aba **Maven** > botão de recarregar (*Reload All Maven Projects*) e aguarde o download |
| `release version 17 not supported` | Configure o SDK do projeto para o JDK 17+ |
| Acentos quebrados no CSV | Abra o CSV como UTF-8 (no Excel: *Dados > De Texto/CSV > UTF-8*) |
| Programa abre vazio / não acha o CSV | Confirme que o diretório de trabalho da execução é a raiz do projeto (o padrão do IntelliJ) |

## Documentos que acompanham o projeto

- `RELATORIO.md` — relatório de funcionalidades e decisões de design (item exigido na entrega)
- `GUIA_DE_ESTUDO.md` — passo a passo de como o sistema foi construído e como aplicar o método em outros projetos
