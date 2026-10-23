package br.unifan.funcionarios.exception;

/**
 * Lançada quando um dado informado pelo usuário é inválido
 * (ou quando uma regra de negócio é violada, ex.: matrícula duplicada).
 * É uma exceção CHECKED: quem chama é obrigado a tratar, e a tela mostra a mensagem ao usuário.
 */
public class ValidacaoException extends Exception {

    private static final long serialVersionUID = 1L;

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
