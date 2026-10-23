package br.unifan.funcionarios.app;

import javafx.application.Application;

/**
 * Classe de entrada (a que tem o main).
 * Ela existe separada do MainApp porque, sem module-info.java, o Java recusa iniciar
 * direto uma classe que herda de Application ("JavaFX runtime components are missing").
 * No IntelliJ: clique com o botão direito nesta classe > Run 'Launcher.main()'.
 */
public class Launcher {

    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
