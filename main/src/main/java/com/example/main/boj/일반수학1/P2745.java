package com.example.main.boj.일반수학1;

import java.util.Scanner;

public class P2745 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String numStr = sc.next();
        int n = sc.nextInt();
        long tmp = 1;
        long sum = 0;
        for (int i = numStr.length() - 1; i >= 0; i--) {
            char c = numStr.charAt(i);

            if ('A' <= c && 'Z' >= c) {
                sum += (c - 'A' + 10) * tmp;
            } else {
                sum += (c - '0') * tmp;
            }
            tmp *= n;
        }
        System.out.println(sum);
    }
}
