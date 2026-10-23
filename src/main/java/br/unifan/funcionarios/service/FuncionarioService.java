package br.unifan.funcionarios.service;

import br.unifan.funcionarios.exception.ValidacaoException;
import br.unifan.funcionarios.model.Endereco;
import br.unifan.funcionarios.model.Funcionario;
import br.unifan.funcionarios.repository.FuncionarioRepository;
import br.unifan.funcionarios.util.Validador;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Regras de negócio do cadastro: valida, impede duplicidade e sincroniza com o arquivo.
 * Mantém os funcionários em memória (Map por matrícula) e grava no CSV a cada alteração.
 */
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final Map<String, Funcionario> funcionarios = new LinkedHashMap<>();

    /** Ao criar o service, os dados do arquivo já são carregados. */
    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
        repository.carregar().forEach(f -> funcionarios.put(f.getMatricula(), f));
    }

    public Funcionario cadastrar(Funcionario f) throws ValidacaoException {
        if (f == null || f.getEndereco() == null) {
            throw new ValidacaoException("Os dados do funcionário e do endereço são obrigatórios.");
        }
        validarENormalizar(f);

        if (funcionarios.containsKey(f.getMatricula())) {
            throw new ValidacaoException("Já existe um funcionário com a matrícula " + f.getMatricula() + ".");
        }
        boolean cpfRepetido = funcionarios.values().stream()
                .anyMatch(outro -> outro.getCpf().equals(f.getCpf()));
        if (cpfRepetido) {
            throw new ValidacaoException("Já existe um funcionário cadastrado com este CPF.");
        }

        funcionarios.put(f.getMatricula(), f);
        try {
            repository.salvar(funcionarios.values());
        } catch (RuntimeException e) {
            funcionarios.remove(f.getMatricula()); // desfaz se não conseguiu gravar
            throw e;
        }
        return f;
    }

    public void excluir(String matricula) throws ValidacaoException {
        String chave = Validador.validarObrigatorio(matricula, "Matrícula");
        Funcionario removido = funcionarios.remove(chave);
        if (removido == null) {
            throw new ValidacaoException("Não existe funcionário com a matrícula " + chave + ".");
        }
        try {
            repository.salvar(funcionarios.values());
        } catch (RuntimeException e) {
            funcionarios.put(chave, removido); // desfaz se não conseguiu gravar
            throw e;
        }
    }

    public Optional<Funcionario> buscarPorMatricula(String matricula) throws ValidacaoException {
        String chave = Validador.validarObrigatorio(matricula, "Matrícula");
        return Optional.ofNullable(funcionarios.get(chave));
    }

    /** Lista todos, em ordem alfabética de nome. */
    public List<Funcionario> listarTodos() {
        return funcionarios.values().stream()
                .sorted(Comparator.comparing(Funcionario::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private void validarENormalizar(Funcionario f) throws ValidacaoException {
        f.setMatricula(Validador.validarMatricula(f.getMatricula()));
        f.setNome(Validador.validarNome(f.getNome()));
        f.setCpf(Validador.validarCpf(f.getCpf()));
        f.setDataNascimento(Validador.validarDataNascimento(f.getDataNascimento()));
        f.setCargo(Validador.validarObrigatorio(f.getCargo(), "Cargo"));
        f.setSalario(Validador.validarSalario(f.getSalario()));
        f.setDataContratacao(Validador.validarDataContratacao(f.getDataContratacao(), f.getDataNascimento()));

        Endereco e = f.getEndereco();
        e.setLogradouro(Validador.validarObrigatorio(e.getLogradouro(), "Logradouro"));
        e.setNumero(Validador.validarObrigatorio(e.getNumero(), "Número"));
        e.setComplemento(e.getComplemento() == null ? "" : e.getComplemento().trim()); // opcional
        e.setBairro(Validador.validarObrigatorio(e.getBairro(), "Bairro"));
        e.setCidade(Validador.validarObrigatorio(e.getCidade(), "Cidade"));
        e.setEstado(Validador.validarEstado(e.getEstado()));
        e.setCep(Validador.validarCep(e.getCep()));
    }
}
