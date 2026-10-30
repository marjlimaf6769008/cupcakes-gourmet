package br.com.cupcakesgourmet.repository;

import br.com.cupcakesgourmet.model.entity.Cupcake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CupcakeRepositoryTest {
    @Autowired
    private CupcakeRepository repository;

    @BeforeEach
    void limparDadosDoScriptDeCarga() {
        repository.deleteAll();
        repository.flush();
    }

    @Test
    @DisplayName("deve inserir e buscar cupcake por id")
    void deveInserirEBuscarPorId() {
        Cupcake salvo = repository.saveAndFlush(
                new Cupcake("Red Velvet", "Massa de cacau", new BigDecimal("12.50"), "/img/red.jpg"));

        assertThat(repository.findById(salvo.getId()))
                .isPresent()
                .get()
                .satisfies(cupcake -> {
                    assertThat(cupcake.getNome()).isEqualTo("Red Velvet");
                    assertThat(cupcake.getPreco()).isEqualByComparingTo("12.50");
                });
    }

    @Test
    @DisplayName("deve listar todos os cupcakes cadastrados")
    void deveListarTodos() {
        repository.save(new Cupcake("Red Velvet", "Cacau", new BigDecimal("12.50"), null));
        repository.save(new Cupcake("Limao", "Creme", new BigDecimal("10.00"), null));

        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("deve remover cupcake e não encontrá-lo depois")
    void deveRemoverCupcake() {
        Cupcake salvo = repository.saveAndFlush(
                new Cupcake("Morango", "Frutas vermelhas", new BigDecimal("14.00"), null));

        repository.deleteById(salvo.getId());
        repository.flush();

        assertThat(repository.findById(salvo.getId())).isEmpty();
    }
}
