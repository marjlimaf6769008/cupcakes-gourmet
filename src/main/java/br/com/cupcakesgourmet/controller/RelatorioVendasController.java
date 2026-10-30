package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.entity.Pedido;
import br.com.cupcakesgourmet.repository.PedidoRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin/relatorios")
public class RelatorioVendasController {
    private final PedidoRepository pedidoRepository;

    public RelatorioVendasController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public String vendas(
            @RequestParam(defaultValue = "MENSAL") String periodo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim,
            Model model) {
        Intervalo intervalo = calcularIntervalo(periodo, inicio, fim);
        List<Pedido> pedidos = pedidoRepository.findVendasByDataBetween(
                intervalo.inicio().atStartOfDay(), intervalo.fimExclusivo().atStartOfDay());
        List<Object[]> resumoList = pedidoRepository.resumirVendasByDataBetween(
                intervalo.inicio().atStartOfDay(), intervalo.fimExclusivo().atStartOfDay());

        Object[] resumo = resumoList.get(0);

        model.addAttribute("periodo", periodo);
        model.addAttribute("inicio", intervalo.inicio());
        model.addAttribute("fim", intervalo.fim());
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("quantidadePedidos", ((Number) resumo[0]).longValue());
        model.addAttribute("totalVendas", converterTotal(resumo[1]));
        return "admin/relatorios";
    }

    private BigDecimal converterTotal(Object valor) {
        if (valor instanceof BigDecimal decimal) {
            return decimal;
        }
        return valor instanceof Number number
                ? new BigDecimal(number.toString())
                : BigDecimal.ZERO;
    }

    private Intervalo calcularIntervalo(String periodo, LocalDate inicio, LocalDate fim) {
        if ("CUSTOM".equalsIgnoreCase(periodo)) {
            LocalDate inicioCustomizado = inicio == null ? LocalDate.now() : inicio;
            LocalDate fimCustomizado = fim == null ? inicioCustomizado : fim;
            if (fimCustomizado.isBefore(inicioCustomizado)) {
                throw new IllegalArgumentException("A data final deve ser igual ou posterior à inicial.");
            }
            return new Intervalo(inicioCustomizado, fimCustomizado);
        }

        LocalDate hoje = LocalDate.now();
        return switch (periodo.toUpperCase(Locale.ROOT)) {
            case "DIARIO" -> new Intervalo(hoje, hoje);
            case "SEMANAL" -> {
                LocalDate segunda = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                yield new Intervalo(segunda, segunda.plusDays(6));
            }
            default -> new Intervalo(hoje.withDayOfMonth(1), hoje.withDayOfMonth(1)
                    .plusMonths(1).minusDays(1));
        };
    }

    private record Intervalo(LocalDate inicio, LocalDate fim) {
        private LocalDate fimExclusivo() {
            return fim.plusDays(1);
        }

    }
}
