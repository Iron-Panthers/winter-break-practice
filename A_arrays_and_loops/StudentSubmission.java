package A_arrays_and_loops;

import java.util.ArrayList;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class StudentSubmission {
    /**
     * A method that should reverse any array of integers it is given
     * 
     * @param input - an array of integers to revers
     * @return the reversed array of integers
     */
    public static int[] reverseArray(int[] input) {
        int[] output = new int[input.length];
        for (int i = 0; i < input.length; i++) 
            output[i] = input[input.length-i-1]; // 😭😭😭
        return output; 
    }

    /**
     * A method that should return the sum of all the elements in an array of
     * integers
     * 
     * @param input - an array of integers to sum
     * @return the sum of all the elements in the array
     */
    public static int sumArray(int[] input) {
        return IntStream.of(input).sum(); // lol
    }

    /**
     * A method that should return the average of all the elements in an array of
     * integers
     * 
     * @param input - an array of integers to average
     * @return the average of all the elements in the array
     */
    public static double averageArray(int[] input) {
        return IntStream.of(input).average().getAsDouble(); // ez
    }

    /**
     * A method that should return the maximum value in an array of integers
     * 
     * @param input - an array of integers to find the maximum value
     * @return the maximum value in the array
     */
    public static int maxArray(int[] input) {
        return IntStream.of(input).max().getAsInt(); // 💀
    }
}