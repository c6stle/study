package collection.set;

import java.util.Arrays;

public class HashStart2 {
    public static void main(String[] args) {
        Integer[] inputArray = new Integer[10];
        inputArray[0] = 1;
        inputArray[1] = 2;
        inputArray[5] = 5;
        inputArray[8] = 8;
        System.out.println("inputArray = " + Arrays.toString(inputArray));

        int searchVal = 8;
        Integer result = inputArray[searchVal];
        System.out.println("result = " + result);
    }
}
