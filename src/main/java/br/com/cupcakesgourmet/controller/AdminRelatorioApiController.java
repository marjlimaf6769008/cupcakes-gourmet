package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.dto.RelatorioPedidoDTO;
import br.com.cupcakesgourmet.model.entity.Pedido;
import br.com.cupcakesgourmet.repository.PedidoRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/admin/api/relatorios")
public class AdminRelatorioApiController {
    private final PedidoRepository pedidoRepository;

    public AdminRelatorioApiController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/pedidos-atualizados")
    public RelatorioAtualizacao pedidosAtualizados(
            @RequestParam(defaultValue = "MENSAL") String periodo,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        Intervalo intervalo = calcularIntervalo(periodo, inicio, fim);
        var inicioData = intervalo.inicio().atStartOfDay();
        var fimData = intervalo.fimExclusivo().atStartOfDay();
        List<Pedido> pedidos = pedidoRepository.findVendasByDataBetween(inicioData, fimData);
        Object[] resumo = pedidoRepository.resumirVendasByDataBetween(inicioData, fimData).get(0);

        return new RelatorioAtualizacao(
                pedidos.stream().map(RelatorioPedidoDTO::from).toList(),
                converterTotal(resumo[1]),
                ((Number) resumo[0]).longValue());
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

    private BigDecimal converterTotal(Object valor) {
        if (valor instanceof BigDecimal decimal) {
            return decimal;
        }
        return valor instanceof Number number
                ? new BigDecimal(number.toString())
                : BigDecimal.ZERO;
    }

    private record Intervalo(LocalDate inicio, LocalDate fim) {
        private LocalDate fimExclusivo() {
            return fim.plusDays(1);
        }
    }

    public record RelatorioAtualizacao(List<RelatorioPedidoDTO> pedidos,
                                       BigDecimal totalVendas,
                                       long quantidadePedidos) {
    }
}
