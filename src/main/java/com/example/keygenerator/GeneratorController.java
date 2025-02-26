package com.example.keygenerator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GeneratorController {

    @GetMapping("/")
    public String showForm() {
        return "index"; // correspond à templates/index.html
    }

    @PostMapping("/")
    public String generate(@RequestParam("type") String type, Model model) {
        // Exemple simple : en fonction de type, on génère une chaîne
        String result;
        switch (type) {
            case "codeSecret":
                result = "CodeSecret-" + (int)(Math.random() * 1_000_000);
                break;
            case "iban":
                result = "IBAN-FR-" + (int)(Math.random() * 10_000_000);
                break;
            default:
                result = "Type inconnu";
        }
        model.addAttribute("generatedValue", result);
        return "index";
    }
}
