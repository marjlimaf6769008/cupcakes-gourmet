package br.com.cupcakesgourmet.model.dto;

import br.com.cupcakesgourmet.model.entity.Cliente;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class PerfilDTO {
    @NotBlank @Size(min = 2, max = 80)
    private String primeiroNome;
    @NotBlank @Size(min = 2, max = 120)
    private String sobrenome;
    @NotBlank @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")
    private String cpf;
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;
    @NotBlank @Pattern(regexp = "\\(\\d{2}\\) \\d{5}-\\d{4}")
    private String telefone;
    private String email;

    public PerfilDTO() {}

    public PerfilDTO(Cliente cliente) {
        this.primeiroNome = cliente.getPrimeiroNome();
        this.sobrenome = cliente.getSobrenome();
        this.cpf = cliente.getCpf();
        this.dataNascimento = cliente.getDataNascimento();
        this.telefone = cliente.getTelefone();
        this.email = cliente.getEmail();
    }

    public String getPrimeiroNome() { return primeiroNome; }
    public void setPrimeiroNome(String value) { primeiroNome = value; }
    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String value) { sobrenome = value; }
    public String getCpf() { return cpf; }
    public void setCpf(String value) { cpf = value; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate value) { dataNascimento = value; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String value) { telefone = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { email = value; }
}
