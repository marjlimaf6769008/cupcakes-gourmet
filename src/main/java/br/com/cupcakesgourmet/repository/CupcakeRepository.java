package br.com.cupcakesgourmet.repository;

import br.com.cupcakesgourmet.model.entity.Cupcake;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CupcakeRepository extends JpaRepository<Cupcake, Long> {
}
