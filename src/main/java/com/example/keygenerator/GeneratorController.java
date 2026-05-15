package com.example.keygenerator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GeneratorController {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%&*+-_?=.";
    private static final int DEFAULT_SECRET_LENGTH = 16;
    private static final int MAX_SECRET_LENGTH = 64;

    @GetMapping("/")
    public String showForm(Model model) {
        addDefaultOptions(model);
        return "index";
    }

    @PostMapping("/")
    public String generate(
            @RequestParam("type") String type,
            @RequestParam(value = "length", defaultValue = "16") int length,
            @RequestParam(value = "lowercase", defaultValue = "false") boolean lowercase,
            @RequestParam(value = "uppercase", defaultValue = "false") boolean uppercase,
            @RequestParam(value = "digits", defaultValue = "false") boolean digits,
            @RequestParam(value = "specialCharacters", defaultValue = "false") boolean specialCharacters,
            Model model) {
        addSelectedOptions(model, type, length, lowercase, uppercase, digits, specialCharacters);

        String result;
        switch (type) {
            case "codeSecret":
                if (!isValidSecretLength(length)) {
                    model.addAttribute("error", "La longueur doit être 16, 32, 48 ou 64.");
                    return "index";
                }
                if (!lowercase && !uppercase && !digits && !specialCharacters) {
                    model.addAttribute("error", "Sélectionnez au moins un type de caractère.");
                    return "index";
                }
                result = generateSecret(length, lowercase, uppercase, digits, specialCharacters);
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

    private void addDefaultOptions(Model model) {
        model.addAttribute("selectedType", "codeSecret");
        model.addAttribute("selectedLength", DEFAULT_SECRET_LENGTH);
        model.addAttribute("lowercase", true);
        model.addAttribute("uppercase", true);
        model.addAttribute("digits", true);
        model.addAttribute("specialCharacters", false);
    }

    private void addSelectedOptions(
            Model model,
            String type,
            int length,
            boolean lowercase,
            boolean uppercase,
            boolean digits,
            boolean specialCharacters) {
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedLength", length);
        model.addAttribute("lowercase", lowercase);
        model.addAttribute("uppercase", uppercase);
        model.addAttribute("digits", digits);
        model.addAttribute("specialCharacters", specialCharacters);
    }

    private boolean isValidSecretLength(int length) {
        return length >= DEFAULT_SECRET_LENGTH && length <= MAX_SECRET_LENGTH && length % DEFAULT_SECRET_LENGTH == 0;
    }

    private String generateSecret(
            int length,
            boolean includeLowercase,
            boolean includeUppercase,
            boolean includeDigits,
            boolean includeSpecialCharacters) {
        List<Character> characters = new ArrayList<>();
        StringBuilder availableCharacters = new StringBuilder();

        addCharacterSet(characters, availableCharacters, LOWERCASE, includeLowercase);
        addCharacterSet(characters, availableCharacters, UPPERCASE, includeUppercase);
        addCharacterSet(characters, availableCharacters, DIGITS, includeDigits);
        addCharacterSet(characters, availableCharacters, SPECIAL_CHARACTERS, includeSpecialCharacters);

        while (characters.size() < length) {
            characters.add(randomCharacterFrom(availableCharacters.toString()));
        }

        Collections.shuffle(characters, RANDOM);

        StringBuilder secret = new StringBuilder();
        for (Character character : characters) {
            secret.append(character);
        }
        return secret.toString();
    }

    private void addCharacterSet(
            List<Character> characters,
            StringBuilder availableCharacters,
            String characterSet,
            boolean includeCharacterSet) {
        if (!includeCharacterSet) {
            return;
        }

        availableCharacters.append(characterSet);
        characters.add(randomCharacterFrom(characterSet));
    }

    private Character randomCharacterFrom(String characters) {
        return characters.charAt(RANDOM.nextInt(characters.length()));
    }
}
