package com.example.keygenerator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class GeneratorController {

    @GetMapping("/")
    public String showForm() {
        return "index";
    }

    @PostMapping("/")
    public String generate(
            @RequestParam("type") String type,
            Model model
    ) {
        String generatedValue;

        switch (type) {
            case "codeSecret":
                generatedValue = generateSecretCode();
                break;
            case "iban":
                generatedValue = generateIban();
                break;
            case "carteIdentite":
                generatedValue = generateCarteIdentite();
                break;
            default:
                generatedValue = "Type inconnu";
        }

        model.addAttribute("generatedValue", generatedValue);
        return "index";
    }

    private String generateSecretCode() {
        int code = 100000 + new Random().nextInt(900000);
        return "CodeSecret-" + code;
    }

    private String generateIban() {
        long part = 100000000L + (long)(new Random().nextInt(900000000));
        return "FR76 " + part;
    }

    private String generateCarteIdentite() {
        long num = 100000L + new Random().nextInt(900000);
        return "ID-" + num;
    }
}
