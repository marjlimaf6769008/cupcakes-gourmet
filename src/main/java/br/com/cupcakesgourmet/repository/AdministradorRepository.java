package br.com.cupcakesgourmet.repository;

import br.com.cupcakesgourmet.model.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
}
