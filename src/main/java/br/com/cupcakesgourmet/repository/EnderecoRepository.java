package br.com.cupcakesgourmet.repository;

import br.com.cupcakesgourmet.model.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByClienteId(Long clienteId);
    Optional<Endereco> findByIdAndClienteId(Long id, Long clienteId);
}
