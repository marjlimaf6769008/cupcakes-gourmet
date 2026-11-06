package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.entity.Cupcake;
import br.com.cupcakesgourmet.repository.CupcakeRepository;
import br.com.cupcakesgourmet.repository.FavoritoRepository;
import br.com.cupcakesgourmet.model.session.Carrinho;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class CupcakeController {
    private final CupcakeRepository cupcakeRepository;
    private final FavoritoRepository favoritoRepository;
    private final Carrinho carrinho;

    public CupcakeController(CupcakeRepository cupcakeRepository,
                             FavoritoRepository favoritoRepository,
                             Carrinho carrinho) {
        this.cupcakeRepository = cupcakeRepository;
        this.favoritoRepository = favoritoRepository;
        this.carrinho = carrinho;
    }

    @GetMapping({"/", "/catalogo"})
    public String catalogo(Model model, HttpSession session) {
        model.addAttribute("cupcakes", cupcakeRepository.findAll());
        model.addAttribute("carrinho", carrinho);
        Object clienteId = session.getAttribute("clienteId");
        if (clienteId instanceof Number number) {
            Set<Long> favoritos = favoritoRepository.findByClienteId(number.longValue())
                    .stream()
                    .map(favorito -> favorito.getCupcake().getId())
                    .collect(Collectors.toSet());
            model.addAttribute("cupcakesFavoritos", favoritos);
        } else {
            model.addAttribute("cupcakesFavoritos", Set.of());
        }
        return "catalogo";
    }
}
