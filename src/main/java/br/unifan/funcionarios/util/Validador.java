package br.unifan.funcionarios.util;

import br.unifan.funcionarios.exception.ValidacaoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;


public final class Validador {

    public static final int TAMANHO_MINIMO_NOME = 3;
    public static final int IDADE_MINIMA = 18;

    private static final Pattern PADRAO_MATRICULA = Pattern.compile("\\d{6}");
    private static final Pattern PADRAO_NOME = Pattern.compile("[\\p{L} .'-]+");
    private static final Pattern PADRAO_CPF = Pattern.compile("\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    private static final Pattern PADRAO_CEP = Pattern.compile("\\d{8}|\\d{5}-\\d{3}");
    private static final Pattern PADRAO_UF = Pattern.compile("[A-Za-z]{2}");
    // 5.000,50  |  5000,50  |  5000
    private static final Pattern SALARIO_BR = Pattern.compile("\\d{1,3}(\\.\\d{3})+(,\\d{1,2})?|\\d+(,\\d{1,2})?");
    // 5000.50
    private static final Pattern SALARIO_US = Pattern.compile("\\d+\\.\\d{1,2}");

    private Validador() {
        // classe utilitária: não deve ser instanciada
    }

    public static String validarObrigatorio(String valor, String campo) throws ValidacaoException {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException("O campo '" + campo + "' é obrigatório.");
        }
        return valor.trim();
    }

    /** Matrícula: exatamente 6 dígitos numéricos (ex.: 000123). */
    public static String validarMatricula(String matricula) throws ValidacaoException {
        String valor = validarObrigatorio(matricula, "Matrícula");
        if (!PADRAO_MATRICULA.matcher(valor).matches()) {
            throw new ValidacaoException("Matrícula inválida: informe exatamente 6 dígitos numéricos (ex.: 000123).");
        }
        return valor;
    }

    /** Nome: não vazio, mínimo de 3 caracteres, só letras/espaços. */
    public static String validarNome(String nome) throws ValidacaoException {
        String valor = validarObrigatorio(nome, "Nome");
        if (valor.length() < TAMANHO_MINIMO_NOME) {
            throw new ValidacaoException("O nome deve ter pelo menos " + TAMANHO_MINIMO_NOME + " caracteres.");
        }
        if (!PADRAO_NOME.matcher(valor).matches()) {
            throw new ValidacaoException("O nome deve conter apenas letras e espaços.");
        }
        return valor;
    }

    /** CPF: formato 000.000.000-00 ou 11 dígitos + verificação dos dígitos. Devolve só os 11 dígitos. */
    public static String validarCpf(String cpf) throws ValidacaoException {
        String valor = validarObrigatorio(cpf, "CPF");
        if (!PADRAO_CPF.matcher(valor).matches()) {
            throw new ValidacaoException("CPF em formato inválido. Use 000.000.000-00 ou 11 dígitos.");
        }
        String digitos = valor.replaceAll("\\D", "");
        if (!digitosDoCpfConferem(digitos)) {
            throw new ValidacaoException("CPF inválido: os dígitos verificadores não conferem.");
        }
        return digitos;
    }

    private static boolean digitosDoCpfConferem(String cpf) {
        // CPFs como 111.111.111-11 passam na conta, mas são inválidos
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }
        return calcularDigitoCpf(cpf, 9) == cpf.charAt(9) - '0'
                && calcularDigitoCpf(cpf, 10) == cpf.charAt(10) - '0';
    }

    private static int calcularDigitoCpf(String cpf, int quantidadeDeDigitos) {
        int soma = 0;
        for (int i = 0; i < quantidadeDeDigitos; i++) {
            int peso = quantidadeDeDigitos + 1 - i; // 10..2 no 1º dígito, 11..2 no 2º
            soma += (cpf.charAt(i) - '0') * peso;
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    /** Data de nascimento: obrigatória, não pode ser futura, idade mínima de 18 anos. */
    public static LocalDate validarDataNascimento(LocalDate data) throws ValidacaoException {
        if (data == null) {
            throw new ValidacaoException("O campo 'Data de nascimento' é obrigatório.");
        }
        LocalDate hoje = LocalDate.now();
        if (data.isAfter(hoje)) {
            throw new ValidacaoException("A data de nascimento não pode estar no futuro.");
        }
        if (Period.between(data, hoje).getYears() < IDADE_MINIMA) {
            throw new ValidacaoException("O funcionário deve ter no mínimo " + IDADE_MINIMA + " anos.");
        }
        return data;
    }

    /** Data de contratação: obrigatória, não pode ser futura nem anterior ao nascimento. */
    public static LocalDate validarDataContratacao(LocalDate contratacao, LocalDate nascimento)
            throws ValidacaoException {
        if (contratacao == null) {
            throw new ValidacaoException("O campo 'Data de contratação' é obrigatório.");
        }
        if (contratacao.isAfter(LocalDate.now())) {
            throw new ValidacaoException("A data de contratação não pode estar no futuro.");
        }
        if (nascimento != null && !contratacao.isAfter(nascimento)) {
            throw new ValidacaoException("A data de contratação deve ser posterior ao nascimento.");
        }
        return contratacao;
    }

    /** Salário: obrigatório e positivo. Devolve com 2 casas decimais. */
    public static BigDecimal validarSalario(BigDecimal salario) throws ValidacaoException {
        if (salario == null) {
            throw new ValidacaoException("O campo 'Salário' é obrigatório.");
        }
        if (salario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("O salário deve ser um valor positivo.");
        }
        return salario.setScale(2, RoundingMode.HALF_UP);
    }

    /** Converte o texto digitado (ex.: "5.000,50", "5000,50", "5000.50") em BigDecimal. */
    public static BigDecimal converterSalario(String texto) throws ValidacaoException {
        String valor = validarObrigatorio(texto, "Salário").replace("R$", "").replace(" ", "");
        if (SALARIO_BR.matcher(valor).matches()) {
            return new BigDecimal(valor.replace(".", "").replace(',', '.'));
        }
        if (SALARIO_US.matcher(valor).matches()) {
            return new BigDecimal(valor);
        }
        throw new ValidacaoException("Salário inválido. Use o formato 5000,00 ou 5.000,00.");
    }

    /** CEP: 8 dígitos (com ou sem hífen). Devolve no formato 00000-000. */
    public static String validarCep(String cep) throws ValidacaoException {
        String valor = validarObrigatorio(cep, "CEP");
        if (!PADRAO_CEP.matcher(valor).matches()) {
            throw new ValidacaoException("CEP inválido. Use 00000-000 ou 8 dígitos.");
        }
        String digitos = valor.replace("-", "");
        return digitos.substring(0, 5) + "-" + digitos.substring(5);
    }

    /** Estado: sigla com 2 letras. Devolve em maiúsculas. */
    public static String validarEstado(String estado) throws ValidacaoException {
        String valor = validarObrigatorio(estado, "Estado");
        if (!PADRAO_UF.matcher(valor).matches()) {
            throw new ValidacaoException("Estado inválido: informe a sigla com 2 letras (ex.: GO).");
        }
        return valor.toUpperCase();
    }

    /** Formata 11 dígitos como 000.000.000-00 (usado só para exibição). */
    public static String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
}
