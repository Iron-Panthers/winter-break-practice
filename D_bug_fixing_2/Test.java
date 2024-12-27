package D_bug_fixing_2;

import java.nio.file.OpenOption;
import java.util.Arrays;
import java.util.Dictionary;

public class Test {
    // This is another testing class
    // There aren't any bugs in this one (I hope)

    public static Calculator calculator = new Calculator();

    public static boolean test(TestCase testCase) {
        boolean hasPassed = true;
        for (Calculator.Operation operation : Calculator.Operation.values()) {
            double result = calculator.calculate(testCase.x, testCase.y, operation);
            switch (operation) {
                case ADD:
                    if (result != testCase.sum) {
                        System.out.println("Test failed for ADD");
                        hasPassed = false;
                    }
                    break;
                case SUBTRACT:
                    if (result != testCase.difference) {
                        System.out.println("Test failed for SUBTRACT");
                        hasPassed = false;
                    }
                    break;
                case MULTIPLY:
                    if (result != testCase.product) {
                        System.out.println("Test failed for MULTIPLY");
                        hasPassed = false;
                    }
                    break;
                case DIVIDE:
                    if (result != testCase.quotient) {
                        System.out.println("Test failed for DIVIDE");
                        hasPassed = false;
                    }
                    break;
            }
        }
        return hasPassed;
    }

    record TestCase(double x, double y, double sum, double difference, double product, double quotient) {
    }

    public static TestCase[] testCases = new TestCase[] {
            new TestCase(1, 2, 3, -1, 2, 0.5),
            new TestCase(10, 20, 30, -10, 200, 0.5),
            new TestCase(5, 4, 9, 1, 20, 1.25),
            new TestCase(7, 8, 15, -1, 56, 0.875),
            new TestCase(-1, -2, -3, 1, 2, 0.5),
            new TestCase(100, 100, 200, 0, 10000, 1),
            new TestCase(0, 100, 100, -100, 0, 0),
    };

    public static void main(String[] args) {
        boolean allPassed = true;
        for (TestCase testCase : testCases) {
            if (test(testCase)) {
                System.out.println("test passed");
            } else {
                System.out.println("test failed");
                allPassed = false;
            }
        }
    }
}
