package br.unifan.funcionarios.repository;

import br.unifan.funcionarios.model.Funcionario;

import java.util.Collection;
import java.util.List;

public interface FuncionarioRepository {

    /** Lê todos os funcionários gravados (lista vazia se ainda não existir arquivo). */
    List<Funcionario> carregar();

    /** Grava TODOS os funcionários, substituindo o conteúdo anterior. */
    void salvar(Collection<Funcionario> funcionarios);
}
