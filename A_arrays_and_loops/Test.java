package A_arrays_and_loops;

import java.util.Arrays;
import java.util.Dictionary;

public class Test {
    // If you're looking at this, I know the code is a bit scuffed.
    // I don't really care about your opinion on it, I just want to get the job
    // done.
    // Run it to see if your code works.
    // Thanks

    public static boolean test(TestCase testCase) {
        boolean hasPassed = true;
        if (!Arrays.equals(StudentSubmission.reverseArray(testCase.input), testCase.reversed)) {
            System.out.println("Test failed for reverseArray");
            hasPassed = false;
        } else if (StudentSubmission.sumArray(testCase.input) != testCase.sum) {
            System.out.println("Test failed for sumArray");
            hasPassed = false;
        } else if (StudentSubmission.averageArray(testCase.input) != testCase.average) {
            System.out.println("Test failed for averageArray");
            hasPassed = false;
        } else if (StudentSubmission.maxArray(testCase.input) != testCase.max) {
            System.out.println("Test failed for maxArray");
            hasPassed = false;
        }
        return hasPassed;
    }

    record TestCase(int[] input, int[] reversed, int sum, double average, int max) {
    }

    public static TestCase[] testCases = new TestCase[] {
            new TestCase(new int[] { 1, 2, 3, 4, 5 }, new int[] { 5, 4, 3, 2, 1 }, 15, 3.0, 5),
            new TestCase(new int[] { 10, 20, 30, 40, 50 }, new int[] { 50, 40, 30, 20, 10 }, 150, 30.0, 50),
            new TestCase(new int[] { 5, 4, 3, 2, 1 }, new int[] { 1, 2, 3, 4, 5 }, 15, 3.0, 5),
            new TestCase(new int[] { 7, 8, 9 }, new int[] { 9, 8, 7 }, 24, 8.0, 9),
            new TestCase(new int[] { 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0 }, 0, 0.0, 0),
            new TestCase(new int[] { -1, -2, -3, -4 }, new int[] { -4, -3, -2, -1 }, -10, -2.5, -1),
            new TestCase(new int[] { 100 }, new int[] { 100 }, 100, 100.0, 100)
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
