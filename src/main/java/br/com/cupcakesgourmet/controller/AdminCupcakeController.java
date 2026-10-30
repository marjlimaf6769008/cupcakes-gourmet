package br.com.cupcakesgourmet.controller;

import br.com.cupcakesgourmet.model.dto.CupcakeFormDTO;
import br.com.cupcakesgourmet.model.entity.Cupcake;
import br.com.cupcakesgourmet.repository.CupcakeRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/cupcakes")
public class AdminCupcakeController {
    private final CupcakeRepository cupcakeRepository;

    public AdminCupcakeController(CupcakeRepository cupcakeRepository) {
        this.cupcakeRepository = cupcakeRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cupcakes", cupcakeRepository.findAll());
        return "admin/cupcakes/index";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cupcakeForm", new CupcakeFormDTO());
        return "admin/cupcakes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid CupcakeFormDTO cupcakeForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/cupcakes/form";
        }

        Cupcake cupcake = cupcakeForm.getId() == null
                ? new Cupcake(cupcakeForm.getNome(), cupcakeForm.getDescricao(),
                cupcakeForm.getPreco(), cupcakeForm.getFotoUrl())
                : cupcakeRepository.findById(cupcakeForm.getId()).orElse(null);

        if (cupcake == null) {
            bindingResult.reject("cupcake.inexistente", "O cupcake informado não existe.");
            return "admin/cupcakes/form";
        }

        cupcake.setNome(cupcakeForm.getNome());
        cupcake.setDescricao(cupcakeForm.getDescricao());
        cupcake.setPreco(cupcakeForm.getPreco());
        cupcake.setFotoUrl(cupcakeForm.getFotoUrl());
        cupcakeRepository.save(cupcake);

        redirectAttributes.addFlashAttribute("sucesso",
                cupcakeForm.getId() == null
                        ? "Cupcake cadastrado com sucesso."
                        : "Cupcake atualizado com sucesso.");
        return "redirect:/admin/cupcakes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        return cupcakeRepository.findById(id)
                .map(cupcake -> {
                    model.addAttribute("cupcakeForm", new CupcakeFormDTO(cupcake));
                    return "admin/cupcakes/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Cupcake não encontrado.");
                    return "redirect:/admin/cupcakes";
                });
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        if (cupcakeRepository.existsById(id)) {
            cupcakeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Cupcake removido com sucesso.");
        } else {
            redirectAttributes.addFlashAttribute("erro", "Cupcake não encontrado.");
        }
        return "redirect:/admin/cupcakes";
    }
}
