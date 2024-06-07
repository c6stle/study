package com.example.main.boj.심화1;

import java.io.IOException;
import java.util.Scanner;

public class P3003 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        int[] arr = new int[]{1, 1, 2, 2, 2, 8};
        int[] outArr = new int[arr.length];

        for (int i = 0; i < arr.length; i++) {
            int n = sc.nextInt();
            outArr[i] = arr[i] - n;
        }

        for (int i = 0; i <outArr.length; i++) {
            if (i == outArr.length - 1) {
                System.out.print(outArr[i]);
            } else {
                System.out.print(outArr[i] + " ");
            }
        }
    }
}
