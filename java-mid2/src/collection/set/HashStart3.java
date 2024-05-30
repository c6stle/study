package collection.set;

import java.util.Arrays;

public class HashStart3 {
    public static void main(String[] args) {
        //input: {1,2,5,8,14,99}
        //[null,1,2,null,null,5,null,null,8,...,14,...,99]
        Integer[] inputArray = new Integer[100];
        inputArray[0] = 1;
        inputArray[1] = 2;
        inputArray[5] = 5;
        inputArray[8] = 8;
        inputArray[14] = 14;
        inputArray[99] = 99;
        System.out.println("inputArray = " + Arrays.toString(inputArray));

        int searchVal = 8;
        Integer result = inputArray[searchVal];
        System.out.println("result = " + result);
    }
}
