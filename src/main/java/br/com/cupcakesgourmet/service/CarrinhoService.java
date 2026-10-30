package br.com.cupcakesgourmet.service;

import br.com.cupcakesgourmet.model.entity.Cupcake;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CarrinhoService {
    private final Map<Cupcake, Integer> itens = new LinkedHashMap<>();

    public void adicionar(Cupcake cupcake, int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade deve ser positiva");
        itens.merge(cupcake, quantidade, Integer::sum);
    }

    public void remover(Cupcake cupcake) {
        itens.remove(cupcake);
    }

    public BigDecimal calcularTotal() {
        return itens.entrySet().stream()
                .map(item -> item.getKey().getPreco().multiply(BigDecimal.valueOf(item.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Cupcake, Integer> getItens() {
        return Map.copyOf(itens);
    }
}
