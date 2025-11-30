import java.util.Scanner;

public class InputValidator {
    private final Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    public int getValidIntInput(int min, int max, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Input cannot be empty. Please try again.");
                    continue;
                }
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
    }


    public String getValidStringInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            } else if (input.length() > 50) {
                System.out.println("Input too long. Please keep it under 50 characters.");
            } else {
                return input;
            }
        }
    }

    public String getValidYesNoInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y") || input.equals("YES") || input.equals("N") || input.equals("NO")) {
                return input.substring(0, 1); // Return just "Y" or "N"
            } else {
                System.out.println("Please enter Y (Yes) or N (No).");
            }
        }
    }

    /**
     * Get the next line from scanner (for compatibility)
     */
    public String nextLine() {
        return scanner.nextLine();
    }
}

