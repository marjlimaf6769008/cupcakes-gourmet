package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.entity.Cupcake;
import br.com.cupcakesgourmet.model.session.Carrinho;
import br.com.cupcakesgourmet.repository.CupcakeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CarrinhoController {
    private final CupcakeRepository cupcakeRepository;
    private final Carrinho carrinho;

    public CarrinhoController(CupcakeRepository cupcakeRepository, Carrinho carrinho) {
        this.cupcakeRepository = cupcakeRepository;
        this.carrinho = carrinho;
    }

    @GetMapping("/carrinho")
    public String exibirCarrinho(Model model) {
        model.addAttribute("carrinho", carrinho);
        return "carrinho";
    }

    @PostMapping("/carrinho/adicionar")
    public String adicionar(@RequestParam Long cupcakeId,
                             @RequestParam(defaultValue = "1") int quantidade,
                             RedirectAttributes redirectAttributes) {
        if (quantidade <= 0) {
            redirectAttributes.addFlashAttribute("erro", "A quantidade deve ser positiva.");
            return "redirect:/catalogo";
        }

        Cupcake cupcake = cupcakeRepository.findById(cupcakeId).orElse(null);
        if (cupcake == null) {
            redirectAttributes.addFlashAttribute("erro", "Cupcake não encontrado.");
            return "redirect:/catalogo";
        }

        carrinho.adicionarCupcake(cupcake, quantidade);
        redirectAttributes.addFlashAttribute("sucesso", "Cupcake adicionado ao carrinho.");
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/remover/{id}")
    public String remover(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        carrinho.removerItem(id);
        redirectAttributes.addFlashAttribute("sucesso", "Item removido do carrinho.");
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/atualizar")
    public String atualizar(@RequestParam Long cupcakeId,
                            @RequestParam int quantidade,
                            RedirectAttributes redirectAttributes) {
        carrinho.atualizarQuantidade(cupcakeId, quantidade);
        redirectAttributes.addFlashAttribute("sucesso", "Carrinho atualizado.");
        return "redirect:/carrinho";
    }
}
