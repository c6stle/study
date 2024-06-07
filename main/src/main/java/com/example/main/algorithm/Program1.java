package com.example.main.algorithm;

import java.util.Scanner;

//숫자의합 11720
public class Program1 {
    public static void doing() {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        String sNum = sc.next();
        char[] sChar = sNum.toCharArray();
        long sum = 0;
        for (int i=0; i<n; i++) {
            sum += sChar[i] - '0';
        }
        System.out.println(sum);
    }
}
