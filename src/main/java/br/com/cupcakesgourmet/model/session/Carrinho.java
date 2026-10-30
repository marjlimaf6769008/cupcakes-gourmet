package br.com.cupcakesgourmet.model.session;

import br.com.cupcakesgourmet.model.dto.ItemCarrinhoDTO;
import br.com.cupcakesgourmet.model.entity.Cupcake;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Carrinho {
    private final List<ItemCarrinhoDTO> itens = new ArrayList<>();

    public void adicionarCupcake(Cupcake cupcake, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser positiva");
        }

        Optional<ItemCarrinhoDTO> itemExistente = encontrarItem(cupcake.getId());
        if (itemExistente.isPresent()) {
            ItemCarrinhoDTO item = itemExistente.get();
            item.definirQuantidade(item.getQuantidade() + quantidade);
        } else {
            itens.add(new ItemCarrinhoDTO(cupcake, quantidade));
        }
    }

    public void removerItem(Long cupcakeId) {
        itens.removeIf(item -> item.getCupcake().getId().equals(cupcakeId));
    }

    public void atualizarQuantidade(Long cupcakeId, int quantidade) {
        if (quantidade <= 0) {
            removerItem(cupcakeId);
            return;
        }
        encontrarItem(cupcakeId).ifPresent(item -> item.definirQuantidade(quantidade));
    }

    public BigDecimal calcularTotalGeral() {
        return itens.stream()
                .map(ItemCarrinhoDTO::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void limparCarrinho() {
        itens.clear();
    }

    public List<ItemCarrinhoDTO> getItens() {
        return Collections.unmodifiableList(itens);
    }

    private Optional<ItemCarrinhoDTO> encontrarItem(Long cupcakeId) {
        return itens.stream()
                .filter(item -> item.getCupcake().getId().equals(cupcakeId))
                .findFirst();
    }
}
