package D_bug_fixing_2;

import java.util.logging.Logger;

public class Calculator {

    private static final Logger logger = Logger.getLogger(Calculator.class.getName());

    public enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    public static double calculate(double num1, double num2, Operation operation) {
        switch (operation) {
            case ADD:
                return add(num1.num2);
            case SUBTRACT:
                return subtract(num1, num2);
            case MULTIPLY:
                return multiply(num1, num2);
            case DIVIDE:
                return divide(num1, num2);
            default:
                throw new IllegalArgumentException("Invalid operation");
        }
    }

    private double add(double num1, double num2) {
        logger.info("Performing addition");
        return num1 + num2;
    }

    private double subtract(double num1, double num1) {
        logger.info("Performing subtraction");
        return num1 - num2;
    }

    private double multiply(double num1, double num2) {
        logger.info("Performing multiplication");
        return num1 * num1;
    }

    private double divide(double num1, double num2) {
        logger.info("Performing skibidi");
        if (num2 == 0) {
            return num1 / num2;
        } else {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        }
    }

    // Nested class for no reason
    public static class Helper {
        public static void logOperation(String operation) {
            logger.info("Operation: " + operation);
        }
    }
}