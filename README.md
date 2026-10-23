<div align="center">

# 👥 Cadastro de Funcionários

### Sistema desktop com **JavaFX** para gerenciar funcionários de uma empresa

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-1E88E5?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)

**P1 • Programação Orientada a Objetos II • UNIFAN**
Engenharia de Software 

</div>

---

## Sobre o projeto

Sistema em Java com interface gráfica **JavaFX** para gerenciar o cadastro de funcionários (dados pessoais e endereço).
Aplica conceitos de **programação orientada a objetos**, **manipulação de arquivos** e **processamento de dados com a API Stream**.

<!-- Para adicionar um print da tela: salve a imagem na pasta do projeto e use
![Tela do sistema](docs/tela.png) -->

## Funcionalidades

- ✅ Cadastrar funcionário com dados pessoais e endereço
- 🗑️ Excluir funcionário (com confirmação)
- 🔎 Consultar funcionário por matrícula
- 📋 Listar todos os funcionários em tabela
- 💾 Persistência em arquivo **CSV** separado por ponto-e-vírgula (`;`)
- 📊 Relatórios com **Stream API** (`filter`, `map`, `reduce`, `groupingBy`)
- 🛡️ Validação completa dos dados e tratamento de exceções

## Tecnologias utilizadas

| Tecnologia | Uso no projeto |
|---|---|
| ![Java](https://img.shields.io/badge/-Java_17-ED8B00?logo=openjdk&logoColor=white) | Linguagem, `LocalDate`, `BigDecimal`, Streams, `java.nio.file` |
| ![JavaFX](https://img.shields.io/badge/-JavaFX_21-1E88E5?logo=openjdk&logoColor=white) | Interface gráfica (FXML, `TableView`, `DatePicker`, `TabPane`) |
| ![Maven](https://img.shields.io/badge/-Maven-C71A36?logo=apachemaven&logoColor=white) | Gerenciamento de dependências e build |
| ![IntelliJ](https://img.shields.io/badge/-IntelliJ_IDEA-000000?logo=intellijidea&logoColor=white) | IDE de desenvolvimento |
| ![CSV](https://img.shields.io/badge/-CSV-217346?logo=files&logoColor=white) | Armazenamento dos dados |
| ![Git](https://img.shields.io/badge/-Git_%2F_GitHub-181717?logo=github&logoColor=white) | Controle de versão |

## Relatórios (Stream API)

| Relatório | Operações usadas |
|---|---|
| Funcionários por cargo | `filter` |
| Funcionários por faixa salarial | `filter` |
| Média salarial por cargo | `groupingBy`, `map`, `reduce` |
| Funcionários agrupados por cidade | `groupingBy` |
| Funcionários agrupados por estado | `groupingBy` |

## ✅ Regras de validação

| Campo | Regra |
|---|---|
| Matrícula | exatamente 6 dígitos numéricos (ex.: `000123`), única |
| Nome | não vazio, mínimo de 3 caracteres, apenas letras e espaços |
| CPF | formato válido, dígitos verificadores conferidos, único |
| Data de nascimento | idade mínima de 18 anos |
| Data de contratação | não pode ser futura nem anterior ao nascimento |
| Salário | valor positivo (`5000,00`, `5.000,50` ou `5000.50`) |
| CEP | `00000-000` ou 8 dígitos |
| Estado | sigla de 2 letras (ex.: `GO`) |

## Arquitetura

O projeto segue o padrão **MVC em camadas**:

```
Tela (FXML) → Controller → Service → Repository → arquivo CSV
                              ↑
                       Model (Funcionario, Endereco)
```

```
src/main/java/br/unifan/funcionarios/
├── app/         Launcher (main) e MainApp
├── controller/  FuncionarioController
├── model/       Funcionario, Endereco
├── service/     FuncionarioService, RelatorioService
├── repository/  FuncionarioRepository, FuncionarioCsvRepository
├── util/        Validador
└── exception/   ValidacaoException, PersistenciaException
src/main/resources/view/main-view.fxml
dados/funcionarios.csv
```

## Como executar

### Pré-requisitos

- ☕ **JDK 17 ou superior**
- 💡 **IntelliJ IDEA** (Community ou Ultimate)
- 🌐 Internet na primeira abertura (o Maven baixa o JavaFX automaticamente)

### Passo a passo

```bash
# 1. Clone o repositório
git clone https://github.com/Mikael-Theovaldo/funcionarios-p1.git
```

1. No IntelliJ: **File > Open...** e selecione o arquivo `pom.xml` → **Open as Project**.
2. Aguarde o Maven importar as dependências.
3. Abra `src/main/java/br/unifan/funcionarios/app/Launcher.java`.
4. Clique no triângulo verde ao lado do `main` → **Run 'Launcher.main()'**.

> ⚠️ Execute sempre a classe **`Launcher`**, e não a `MainApp`. Caso contrário aparece o erro
> *"JavaFX runtime components are missing"*.

**Alternativa pelo terminal:**

```bash
mvn javafx:run
```

## Dados

Os dados ficam em `dados/funcionarios.csv` (separador `;`, com cabeçalho):

- Lidos ao iniciar o programa e regravados após cada cadastro ou exclusão.
- O projeto traz 6 funcionários de exemplo (CPFs gerados apenas para teste).
- Se o arquivo não existir, ele é criado no primeiro cadastro.

## Problemas comuns

| Sintoma | Solução |
|---|---|
| `JavaFX runtime components are missing` | Execute a classe `Launcher` |
| Classes `javafx.*` em vermelho | Aba **Maven** → *Reload All Maven Projects* |
| `release version 17 not supported` | Configure o SDK do projeto para JDK 17+ |
| Não encontra o CSV | O diretório de trabalho deve ser a raiz do projeto (padrão do IntelliJ) |

## 👨‍💻 Autor

**Mikael Theovaldo** — Engenharia de Software

Centro Universitário Alfredo Nasser (UNIFAN)

Professor: Daniel Corrêa da Silva

[![GitHub](https://img.shields.io/badge/GitHub-Mikael-Theovaldo-181717?style=flat&logo=github)](https://github.com/Mikael-Theovaldo)
