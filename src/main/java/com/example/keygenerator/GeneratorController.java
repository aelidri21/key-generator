package com.example.keygenerator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
    private static final String TOKEN_CHARACTERS = LOWERCASE + UPPERCASE + DIGITS;
    private static final String HEX_CHARACTERS = "0123456789abcdef";
    private static final int DEFAULT_SECRET_LENGTH = 16;
    private static final int MAX_SECRET_LENGTH = 64;
    private static final int DEFAULT_PIN_LENGTH = 6;
    private static final int DEFAULT_TOKEN_LENGTH = 32;
    private static final int DEFAULT_HEX_LENGTH = 32;

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
            @RequestParam(value = "pinLength", defaultValue = "6") int pinLength,
            @RequestParam(value = "tokenPrefix", defaultValue = "sk") String tokenPrefix,
            @RequestParam(value = "tokenLength", defaultValue = "32") int tokenLength,
            @RequestParam(value = "hexLength", defaultValue = "32") int hexLength,
            Model model) {
        addSelectedOptions(
                model,
                type,
                length,
                lowercase,
                uppercase,
                digits,
                specialCharacters,
                pinLength,
                tokenPrefix,
                tokenLength,
                hexLength);

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
            case "pin":
                if (!isValidPinLength(pinLength)) {
                    model.addAttribute("error", "La longueur du PIN doit être 4, 6 ou 8.");
                    return "index";
                }
                result = randomString(DIGITS, pinLength);
                break;
            case "uuid":
                result = UUID.randomUUID().toString();
                break;
            case "apiToken":
                if (!isValidTokenLength(tokenLength)) {
                    model.addAttribute("error", "La longueur du token doit être 32, 48 ou 64.");
                    return "index";
                }
                result = sanitizeTokenPrefix(tokenPrefix) + "_" + randomString(TOKEN_CHARACTERS, tokenLength);
                break;
            case "hexKey":
                if (!isValidHexLength(hexLength)) {
                    model.addAttribute("error", "La longueur de la clé hexadécimale doit être 32, 48 ou 64.");
                    return "index";
                }
                result = randomString(HEX_CHARACTERS, hexLength);
                break;
            case "iban":
                result = generateFrenchIban();
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
        model.addAttribute("selectedPinLength", DEFAULT_PIN_LENGTH);
        model.addAttribute("tokenPrefix", "sk");
        model.addAttribute("selectedTokenLength", DEFAULT_TOKEN_LENGTH);
        model.addAttribute("selectedHexLength", DEFAULT_HEX_LENGTH);
    }

    private void addSelectedOptions(
            Model model,
            String type,
            int length,
            boolean lowercase,
            boolean uppercase,
            boolean digits,
            boolean specialCharacters,
            int pinLength,
            String tokenPrefix,
            int tokenLength,
            int hexLength) {
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedLength", length);
        model.addAttribute("lowercase", lowercase);
        model.addAttribute("uppercase", uppercase);
        model.addAttribute("digits", digits);
        model.addAttribute("specialCharacters", specialCharacters);
        model.addAttribute("selectedPinLength", pinLength);
        model.addAttribute("tokenPrefix", sanitizeTokenPrefix(tokenPrefix));
        model.addAttribute("selectedTokenLength", tokenLength);
        model.addAttribute("selectedHexLength", hexLength);
    }

    private boolean isValidSecretLength(int length) {
        return length >= DEFAULT_SECRET_LENGTH && length <= MAX_SECRET_LENGTH && length % DEFAULT_SECRET_LENGTH == 0;
    }

    private boolean isValidPinLength(int length) {
        return length == 4 || length == 6 || length == 8;
    }

    private boolean isValidTokenLength(int length) {
        return length == 32 || length == 48 || length == 64;
    }

    private boolean isValidHexLength(int length) {
        return length == 32 || length == 48 || length == 64;
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

    private String randomString(String characters, int length) {
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < length; index++) {
            result.append(randomCharacterFrom(characters));
        }
        return result.toString();
    }

    private String generateFrenchIban() {
        String bankCode = randomString(DIGITS, 5);
        String branchCode = randomString(DIGITS, 5);
        String accountNumber = randomString(DIGITS, 11);
        String ribKey = randomString(DIGITS, 2);
        String bban = bankCode + branchCode + accountNumber + ribKey;
        String checkDigits = calculateIbanCheckDigits(bban);

        return groupByFour("FR" + checkDigits + bban);
    }

    private String calculateIbanCheckDigits(String bban) {
        String value = bban + "FR00";
        int remainder = 0;

        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            String numericValue = Character.isLetter(character)
                    ? String.valueOf(Character.toUpperCase(character) - 'A' + 10)
                    : String.valueOf(character);

            for (int digitIndex = 0; digitIndex < numericValue.length(); digitIndex++) {
                remainder = (remainder * 10 + Character.getNumericValue(numericValue.charAt(digitIndex))) % 97;
            }
        }

        return String.format("%02d", 98 - remainder);
    }

    private String groupByFour(String value) {
        StringBuilder groupedValue = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            if (index > 0 && index % 4 == 0) {
                groupedValue.append(' ');
            }
            groupedValue.append(value.charAt(index));
        }
        return groupedValue.toString();
    }

    private String sanitizeTokenPrefix(String tokenPrefix) {
        if (tokenPrefix == null || tokenPrefix.isBlank()) {
            return "sk";
        }
        String sanitizedPrefix = tokenPrefix.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
        return sanitizedPrefix.isBlank() ? "sk" : sanitizedPrefix;
    }
}
