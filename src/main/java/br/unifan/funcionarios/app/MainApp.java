package br.unifan.funcionarios.app;

import br.unifan.funcionarios.controller.FuncionarioController;
import br.unifan.funcionarios.exception.PersistenciaException;
import br.unifan.funcionarios.repository.FuncionarioCsvRepository;
import br.unifan.funcionarios.service.FuncionarioService;
import br.unifan.funcionarios.service.RelatorioService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

public class MainApp extends Application {

    /** Arquivo de dados */
    private static final Path ARQUIVO_CSV = Path.of("dados", "funcionarios.csv");

    @Override
    public void start(Stage stage) throws IOException {
        FuncionarioService funcionarioService;
        try {
            // carrega o CSV ao iniciar
            funcionarioService = new FuncionarioService(new FuncionarioCsvRepository(ARQUIVO_CSV));
        } catch (PersistenciaException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.setTitle("Erro ao carregar os dados");
            alert.setHeaderText(null);
            alert.showAndWait();
            Platform.exit();
            return;
        }
        RelatorioService relatorioService = new RelatorioService(funcionarioService);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main-view.fxml"));
        // entrega os services prontos ao controlador (injeção de dependência manual)
        loader.setControllerFactory(tipo -> new FuncionarioController(funcionarioService, relatorioService));

        stage.setTitle("Cadastro de Funcionários - UNIFAN");
        stage.setScene(new Scene(loader.load(), 1000, 720));
        stage.setMinWidth(850);
        stage.setMinHeight(650);
        stage.show();
    }
}
