package com.example.main.boj.약수배수소수;

import java.util.Scanner;

public class P2581 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        int sum = 0;
        int min = 10_000;
        for (int i = n; i <= m; i++) {
            int tmpSum = 0;
            for (int j = 1; j <= i / 2; j++) {
                if (i % j == 0) {
                    tmpSum += j;
                }
            }
            if (tmpSum == 1) {
                sum += i;
                if (min > i) {
                    min = i;
                }
            }
        }
        if (sum == 0) {
            System.out.println("-1");
        } else {
            System.out.println(sum);
            System.out.println(min);
        }
    }
}
