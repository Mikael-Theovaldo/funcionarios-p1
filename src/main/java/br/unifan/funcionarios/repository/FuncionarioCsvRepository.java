package br.unifan.funcionarios.repository;

import br.unifan.funcionarios.exception.PersistenciaException;
import br.unifan.funcionarios.model.Endereco;
import br.unifan.funcionarios.model.Funcionario;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class FuncionarioCsvRepository implements FuncionarioRepository {

    private static final String SEPARADOR = ";";
    private static final int TOTAL_CAMPOS = 14;
    private static final String CABECALHO = String.join(SEPARADOR,
            "matricula", "nome", "cpf", "dataNascimento", "cargo", "salario", "dataContratacao",
            "logradouro", "numero", "complemento", "bairro", "cidade", "estado", "cep");

    private final Path arquivo;

    public FuncionarioCsvRepository(Path arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public List<Funcionario> carregar() {
        List<Funcionario> funcionarios = new ArrayList<>();
        if (!Files.exists(arquivo)) {
            return funcionarios; // primeira execução: ainda não há dados
        }
        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
            for (int i = 1; i < linhas.size(); i++) { // i = 1 pula o cabeçalho
                String linha = linhas.get(i).replace("﻿", "");
                if (!linha.isBlank()) {
                    funcionarios.add(paraFuncionario(linha, i + 1));
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível ler o arquivo " + arquivo + ".", e);
        }
        return funcionarios;
    }

    @Override
    public void salvar(Collection<Funcionario> funcionarios) {
        List<String> linhas = new ArrayList<>();
        linhas.add(CABECALHO);
        funcionarios.stream().map(this::paraLinha).forEach(linhas::add);
        try {
            Path pasta = arquivo.toAbsolutePath().getParent();
            if (pasta != null) {
                Files.createDirectories(pasta);
            }
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível gravar o arquivo " + arquivo + ".", e);
        }
    }

    // ---------- conversões objeto <-> linha CSV ----------

    private String paraLinha(Funcionario f) {
        Endereco e = f.getEndereco();
        return String.join(SEPARADOR,
                limpar(f.getMatricula()),
                limpar(f.getNome()),
                limpar(f.getCpf()),
                f.getDataNascimento().toString(),
                limpar(f.getCargo()),
                f.getSalario().toPlainString(),
                f.getDataContratacao().toString(),
                limpar(e.getLogradouro()),
                limpar(e.getNumero()),
                limpar(e.getComplemento()),
                limpar(e.getBairro()),
                limpar(e.getCidade()),
                limpar(e.getEstado()),
                limpar(e.getCep()));
    }

    private Funcionario paraFuncionario(String linha, int numeroDaLinha) {

        String[] c = linha.split(SEPARADOR, -1);
        if (c.length != TOTAL_CAMPOS) {
            throw new PersistenciaException("Linha " + numeroDaLinha + " do CSV inválida: esperados "
                    + TOTAL_CAMPOS + " campos, encontrados " + c.length + ".");
        }
        try {
            Endereco endereco = new Endereco(c[7], c[8], c[9], c[10], c[11], c[12], c[13]);
            return new Funcionario(c[0], c[1], c[2], LocalDate.parse(c[3]), c[4],
                    new BigDecimal(c[5]), LocalDate.parse(c[6]), endereco);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new PersistenciaException("Linha " + numeroDaLinha + " do CSV com data ou número inválido.", e);
        }
    }

    /** Evita que o conteúdo de um campo quebre o formato do CSV. */
    private String limpar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace(SEPARADOR, ",").replace("\r", " ").replace("\n", " ").trim();
    }
}
