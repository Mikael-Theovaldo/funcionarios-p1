package br.unifan.funcionarios.exception;

/**
 * Lançada quando ocorre erro ao ler ou gravar o arquivo CSV.
 * É UNCHECKED (RuntimeException) para não "poluir" todas as assinaturas com throws IOException;
 * o IOException original fica guardado como "causa".
 */
public class PersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensagem) {
        super(mensagem);
    }

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
