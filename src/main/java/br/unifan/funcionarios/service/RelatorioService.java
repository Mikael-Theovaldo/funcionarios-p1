package br.unifan.funcionarios.service;

import br.unifan.funcionarios.exception.ValidacaoException;
import br.unifan.funcionarios.model.Funcionario;
import br.unifan.funcionarios.util.Validador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Relatórios feitos com a API Stream do Java.
 * Cada método lê a lista atual de funcionários e devolve o resultado já processado
 * (a tela só se preocupa em mostrar).
 */
public class RelatorioService {

    private final FuncionarioService funcionarioService;

    public RelatorioService(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    /** filter: só os funcionários do cargo informado (sem diferenciar maiúsculas/minúsculas). */
    public List<Funcionario> filtrarPorCargo(String cargo) throws ValidacaoException {
        String procurado = Validador.validarObrigatorio(cargo, "Cargo");
        return funcionarioService.listarTodos().stream()
                .filter(f -> f.getCargo().equalsIgnoreCase(procurado))
                .toList();
    }

    /** filter: salário entre mínimo e máximo (inclusive). */
    public List<Funcionario> filtrarPorFaixaSalarial(BigDecimal minimo, BigDecimal maximo)
            throws ValidacaoException {
        if (minimo == null || maximo == null) {
            throw new ValidacaoException("Informe o salário mínimo e o máximo.");
        }
        if (minimo.compareTo(maximo) > 0) {
            throw new ValidacaoException("O salário mínimo não pode ser maior que o máximo.");
        }
        return funcionarioService.listarTodos().stream()
                .filter(f -> f.getSalario().compareTo(minimo) >= 0 && f.getSalario().compareTo(maximo) <= 0)
                .toList();
    }

    /** groupingBy + map + reduce: média salarial de cada cargo. */
    public Map<String, BigDecimal> mediaSalarialPorCargo() {
        return funcionarioService.listarTodos().stream()
                .collect(Collectors.groupingBy(
                        Funcionario::getCargo,
                        () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
                        Collectors.collectingAndThen(Collectors.toList(), this::media)));
    }

    /** groupingBy: funcionários agrupados por cidade. */
    public Map<String, List<Funcionario>> agruparPorCidade() {
        return funcionarioService.listarTodos().stream()
                .collect(Collectors.groupingBy(
                        f -> f.getEndereco().getCidade(),
                        () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
                        Collectors.toList()));
    }

    /** groupingBy: funcionários agrupados por estado. */
    public Map<String, List<Funcionario>> agruparPorEstado() {
        return funcionarioService.listarTodos().stream()
                .collect(Collectors.groupingBy(
                        f -> f.getEndereco().getEstado(),
                        TreeMap::new,
                        Collectors.toList()));
    }

    /** map (pega o salário) + reduce (soma) e depois divide pela quantidade. */
    private BigDecimal media(List<Funcionario> grupo) {
        BigDecimal soma = grupo.stream()
                .map(Funcionario::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(BigDecimal.valueOf(grupo.size()), 2, RoundingMode.HALF_UP);
    }
}
