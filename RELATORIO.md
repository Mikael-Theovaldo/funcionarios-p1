# Relatório do Projeto — Cadastro de Funcionários

**Disciplina:** Programação Orientada a Objetos II  
**Aluno(a):** _Mikael Theovaldo Silva Carvalho_  
**Curso:** _Engenharia de Software_

## 1. Funcionalidades implementadas

- **Cadastrar** funcionário com dados pessoais e endereço, validando todos os campos antes de gravar.
- **Excluir** funcionário selecionado na tabela (com confirmação).
- **Consultar** funcionário por matrícula.
- **Listar** todos os funcionários em uma tabela (`TableView`), em ordem alfabética.
- **Persistência em CSV** (separador `;`): o arquivo é carregado ao iniciar e regravado após cada alteração.
- **Relatórios com Stream API** (cinco, o enunciado pedia dois):
  filtro por cargo, filtro por faixa salarial, média salarial por cargo, agrupamento por cidade e agrupamento por estado.

## 2. Validações

| Campo | Regra |
|---|---|
| Matrícula | 6 dígitos numéricos; única |
| Nome | não vazio, mínimo de 3 caracteres, apenas letras |
| CPF | formato válido, dígitos verificadores conferidos, rejeita sequências repetidas; único |
| Nascimento | obrigatório, não futuro, idade mínima de 18 anos |
| Contratação | obrigatória, não futura, posterior ao nascimento |
| Salário | positivo (`BigDecimal`, 2 casas) |
| CEP / Estado | `00000-000` / sigla de 2 letras |

## 3. Decisões de design

- **Arquitetura em camadas (MVC):** `controller` (tela) → `service` (regras) → `repository` (arquivo), com `model` compartilhado.
  O controlador não tem regra de negócio; o service não conhece JavaFX.
- **Modelo:** `Funcionario` possui um `Endereco` (composição). A matrícula é o identificador, por isso `equals/hashCode` usam só ela.
- **Tipos adequados:** `LocalDate` para datas e `BigDecimal` para dinheiro (evita erros de arredondamento do `double`).
- **Interface `FuncionarioRepository`:** o service depende da interface, não do CSV; trocar por banco de dados não exigiria mudar as regras.
- **Tratamento de exceções:** `ValidacaoException` (checked) para erros do usuário, exibidos em alertas;
  `PersistenciaException` (unchecked) para falhas de arquivo. Se a gravação falha, a alteração em memória é desfeita.
- **FXML + controlador:** layout separado da lógica; os services são entregues ao controlador por `controllerFactory` (injeção manual de dependência).
- **Classe `Launcher`:** necessária para iniciar o JavaFX sem `module-info.java`.
- **Streams:** `filter` (filtros), `map` + `reduce` (soma de salários para a média) e `Collectors.groupingBy` (agrupamentos);
  os mapas usam `TreeMap` para ordenar o resultado.
- **CSV seguro:** o caractere `;` dentro de um campo é substituído por `,` ao gravar, para não quebrar a estrutura do arquivo.

## 4. Como executar

Ver `README.md` (abrir o `pom.xml` no IntelliJ e executar a classe `Launcher`).
