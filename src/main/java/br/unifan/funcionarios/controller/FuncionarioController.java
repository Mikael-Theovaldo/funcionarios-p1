package br.unifan.funcionarios.controller;

import br.unifan.funcionarios.exception.PersistenciaException;
import br.unifan.funcionarios.exception.ValidacaoException;
import br.unifan.funcionarios.model.Endereco;
import br.unifan.funcionarios.model.Funcionario;
import br.unifan.funcionarios.service.FuncionarioService;
import br.unifan.funcionarios.service.RelatorioService;
import br.unifan.funcionarios.util.Validador;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador da tela (padrão MVC): recebe os eventos dos botões (onAction),
 * lê os campos, chama os services e mostra o resultado ou a mensagem de erro.
 * Não tem regra de negócio: ela fica nos services.
 */
public class FuncionarioController {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final List<String> ESTADOS = List.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA",
            "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

    /** Tipos de relatório mostrados no ComboBox (toString = texto exibido). */
    private enum TipoRelatorio {
        POR_CARGO("Funcionários por cargo"),
        FAIXA_SALARIAL("Funcionários por faixa salarial"),
        MEDIA_POR_CARGO("Média salarial por cargo"),
        POR_CIDADE("Funcionários agrupados por cidade"),
        POR_ESTADO("Funcionários agrupados por estado");

        private final String descricao;

        TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private final FuncionarioService funcionarioService;
    private final RelatorioService relatorioService;
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private final ObservableList<Funcionario> dadosTabela = FXCollections.observableArrayList();

    // ---- campos do formulário (ligados ao FXML pelo fx:id) ----
    @FXML private TextField txtMatricula;
    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private DatePicker dpNascimento;
    @FXML private TextField txtCargo;
    @FXML private TextField txtSalario;
    @FXML private DatePicker dpContratacao;
    @FXML private TextField txtCep;
    @FXML private TextField txtLogradouro;
    @FXML private TextField txtNumero;
    @FXML private TextField txtComplemento;
    @FXML private TextField txtBairro;
    @FXML private TextField txtCidade;
    @FXML private ComboBox<String> cbEstado;

    // ---- consulta e tabela ----
    @FXML private TextField txtBusca;
    @FXML private TableView<Funcionario> tabela;
    @FXML private TableColumn<Funcionario, String> colMatricula;
    @FXML private TableColumn<Funcionario, String> colNome;
    @FXML private TableColumn<Funcionario, String> colCpf;
    @FXML private TableColumn<Funcionario, String> colCargo;
    @FXML private TableColumn<Funcionario, String> colSalario;
    @FXML private TableColumn<Funcionario, String> colCidade;
    @FXML private TableColumn<Funcionario, String> colEstado;
    @FXML private Label lblStatus;

    // ---- relatórios ----
    @FXML private ComboBox<TipoRelatorio> cbRelatorio;
    @FXML private HBox boxCargo;
    @FXML private HBox boxFaixa;
    @FXML private TextField txtRelCargo;
    @FXML private TextField txtSalMin;
    @FXML private TextField txtSalMax;
    @FXML private TextArea txtaRelatorio;

    public FuncionarioController(FuncionarioService funcionarioService, RelatorioService relatorioService) {
        this.funcionarioService = funcionarioService;
        this.relatorioService = relatorioService;
    }

    /** Chamado automaticamente pelo JavaFX depois que o FXML foi carregado. */
    @FXML
    private void initialize() {
        configurarTabela();
        configurarFormulario();
        configurarRelatorios();
        onListarTodos();
    }

    // ===================== configuração inicial =====================

    private void configurarTabela() {
        colMatricula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMatricula()));
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colCpf.setCellValueFactory(c -> new SimpleStringProperty(Validador.formatarCpf(c.getValue().getCpf())));
        colCargo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCargo()));
        colSalario.setCellValueFactory(c -> new SimpleStringProperty(moeda.format(c.getValue().getSalario())));
        colCidade.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndereco().getCidade()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndereco().getEstado()));
        tabela.setItems(dadosTabela);
    }

    private void configurarFormulario() {
        cbEstado.setItems(FXCollections.observableArrayList(ESTADOS));
        configurarDatePicker(dpNascimento);
        configurarDatePicker(dpContratacao);
    }

    private void configurarDatePicker(DatePicker datePicker) {
        datePicker.setPromptText("dd/mm/aaaa");
        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate data) {
                return data == null ? "" : FORMATO_DATA.format(data);
            }

            @Override
            public LocalDate fromString(String texto) {
                try {
                    return (texto == null || texto.isBlank()) ? null : LocalDate.parse(texto.trim(), FORMATO_DATA);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });
    }

    private void configurarRelatorios() {
        cbRelatorio.setItems(FXCollections.observableArrayList(TipoRelatorio.values()));
        // quando o tipo muda, mostra só os campos que ele precisa
        cbRelatorio.valueProperty().addListener((obs, antigo, novo) -> atualizarParametros(novo));
        cbRelatorio.setValue(TipoRelatorio.POR_CARGO);
    }

    private void atualizarParametros(TipoRelatorio tipo) {
        boolean usaCargo = tipo == TipoRelatorio.POR_CARGO;
        boolean usaFaixa = tipo == TipoRelatorio.FAIXA_SALARIAL;
        boxCargo.setVisible(usaCargo);
        boxCargo.setManaged(usaCargo);   // managed=false: não ocupa espaço quando escondido
        boxFaixa.setVisible(usaFaixa);
        boxFaixa.setManaged(usaFaixa);
    }

    // ===================== eventos da aba "Funcionários" =====================

    @FXML
    private void onCadastrar() {
        try {
            Funcionario funcionario = lerFormulario();
            funcionarioService.cadastrar(funcionario);
            limparFormulario();
            onListarTodos();
            lblStatus.setText("Funcionário " + funcionario.getMatricula() + " cadastrado com sucesso.");
        } catch (ValidacaoException e) {
            alerta(Alert.AlertType.WARNING, "Dados inválidos", e.getMessage());
        } catch (PersistenciaException e) {
            alerta(Alert.AlertType.ERROR, "Erro ao gravar o arquivo", e.getMessage());
        }
    }

    @FXML
    private void onLimpar() {
        limparFormulario();
        lblStatus.setText("Formulário limpo.");
    }

    @FXML
    private void onExcluir() {
        Funcionario selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            alerta(Alert.AlertType.INFORMATION, "Excluir", "Selecione um funcionário na tabela.");
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir o funcionário " + selecionado.getNome() + " (matrícula " + selecionado.getMatricula() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText(null);
        if (confirmacao.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }
        try {
            funcionarioService.excluir(selecionado.getMatricula());
            onListarTodos();
            lblStatus.setText("Funcionário " + selecionado.getMatricula() + " excluído.");
        } catch (ValidacaoException e) {
            alerta(Alert.AlertType.WARNING, "Não foi possível excluir", e.getMessage());
        } catch (PersistenciaException e) {
            alerta(Alert.AlertType.ERROR, "Erro ao gravar o arquivo", e.getMessage());
        }
    }

    @FXML
    private void onBuscar() {
        try {
            Optional<Funcionario> encontrado = funcionarioService.buscarPorMatricula(txtBusca.getText());
            if (encontrado.isPresent()) {
                dadosTabela.setAll(encontrado.get());
                lblStatus.setText("1 funcionário encontrado. Clique em \"Listar todos\" para ver a lista completa.");
            } else {
                alerta(Alert.AlertType.INFORMATION, "Consulta",
                        "Nenhum funcionário com a matrícula " + txtBusca.getText().trim() + ".");
            }
        } catch (ValidacaoException e) {
            alerta(Alert.AlertType.WARNING, "Consulta", e.getMessage());
        }
    }

    @FXML
    private void onListarTodos() {
        dadosTabela.setAll(funcionarioService.listarTodos());
        txtBusca.clear();
        lblStatus.setText(dadosTabela.size() + " funcionário(s) cadastrado(s).");
    }

    // ===================== evento da aba "Relatórios" =====================

    @FXML
    private void onGerarRelatorio() {
        TipoRelatorio tipo = cbRelatorio.getValue();
        if (tipo == null) {
            alerta(Alert.AlertType.INFORMATION, "Relatório", "Escolha o tipo de relatório.");
            return;
        }
        try {
            txtaRelatorio.setText(switch (tipo) {
                case POR_CARGO -> relatorioPorCargo();
                case FAIXA_SALARIAL -> relatorioPorFaixa();
                case MEDIA_POR_CARGO -> relatorioMediaPorCargo();
                case POR_CIDADE -> relatorioAgrupado("cidade", relatorioService.agruparPorCidade());
                case POR_ESTADO -> relatorioAgrupado("estado", relatorioService.agruparPorEstado());
            });
        } catch (ValidacaoException e) {
            alerta(Alert.AlertType.WARNING, "Relatório", e.getMessage());
        }
    }

    private String relatorioPorCargo() throws ValidacaoException {
        List<Funcionario> lista = relatorioService.filtrarPorCargo(txtRelCargo.getText());
        return "RELATÓRIO: funcionários do cargo \"" + txtRelCargo.getText().trim() + "\"\n"
                + "Total encontrado: " + lista.size() + "\n\n"
                + formatarLista(lista);
    }

    private String relatorioPorFaixa() throws ValidacaoException {
        BigDecimal minimo = Validador.converterSalario(txtSalMin.getText());
        BigDecimal maximo = Validador.converterSalario(txtSalMax.getText());
        List<Funcionario> lista = relatorioService.filtrarPorFaixaSalarial(minimo, maximo);
        return "RELATÓRIO: salário entre " + moeda.format(minimo) + " e " + moeda.format(maximo) + "\n"
                + "Total encontrado: " + lista.size() + "\n\n"
                + formatarLista(lista);
    }

    private String relatorioMediaPorCargo() {
        Map<String, BigDecimal> medias = relatorioService.mediaSalarialPorCargo();
        if (medias.isEmpty()) {
            return "RELATÓRIO: média salarial por cargo\n\nNenhum funcionário cadastrado.";
        }
        String linhas = medias.entrySet().stream()
                .map(e -> String.format("%-30s %15s", e.getKey(), moeda.format(e.getValue())))
                .collect(Collectors.joining("\n"));
        return "RELATÓRIO: média salarial por cargo\n\n" + linhas;
    }

    private String relatorioAgrupado(String criterio, Map<String, List<Funcionario>> grupos) {
        if (grupos.isEmpty()) {
            return "RELATÓRIO: funcionários agrupados por " + criterio + "\n\nNenhum funcionário cadastrado.";
        }
        String corpo = grupos.entrySet().stream()
                .map(e -> "== " + e.getKey() + " (" + e.getValue().size() + ") ==\n" + formatarLista(e.getValue()))
                .collect(Collectors.joining("\n\n"));
        return "RELATÓRIO: funcionários agrupados por " + criterio + "\n\n" + corpo;
    }

    private String formatarLista(List<Funcionario> lista) {
        if (lista.isEmpty()) {
            return "Nenhum funcionário encontrado.";
        }
        return lista.stream()
                .map(f -> String.format("%s | %-28s | %-22s | %14s | %s/%s",
                        f.getMatricula(), f.getNome(), f.getCargo(), moeda.format(f.getSalario()),
                        f.getEndereco().getCidade(), f.getEndereco().getEstado()))
                .collect(Collectors.joining("\n"));
    }

    // ===================== apoio =====================

    /** Lê os campos da tela e monta o objeto. A validação completa é feita pelo service. */
    private Funcionario lerFormulario() throws ValidacaoException {
        Endereco endereco = new Endereco(
                txtLogradouro.getText(), txtNumero.getText(), txtComplemento.getText(),
                txtBairro.getText(), txtCidade.getText(), cbEstado.getValue(), txtCep.getText());

        return new Funcionario(
                txtMatricula.getText(),
                txtNome.getText(),
                txtCpf.getText(),
                lerData(dpNascimento, "Data de nascimento"),
                txtCargo.getText(),
                Validador.converterSalario(txtSalario.getText()),
                lerData(dpContratacao, "Data de contratação"),
                endereco);
    }

    /** Lê o texto digitado no DatePicker (assim funciona mesmo se o usuário digitou e não deu Enter). */
    private LocalDate lerData(DatePicker datePicker, String campo) throws ValidacaoException {
        String texto = datePicker.getEditor().getText();
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw new ValidacaoException("Data inválida em '" + campo + "'. Use o formato dd/mm/aaaa.");
        }
    }

    private void limparFormulario() {
        for (TextField campo : List.of(txtMatricula, txtNome, txtCpf, txtCargo, txtSalario, txtCep,
                txtLogradouro, txtNumero, txtComplemento, txtBairro, txtCidade)) {
            campo.clear();
        }
        dpNascimento.setValue(null);
        dpNascimento.getEditor().clear();
        dpContratacao.setValue(null);
        dpContratacao.getEditor().clear();
        cbEstado.setValue(null);
        txtMatricula.requestFocus();
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
