package com.example.main.boj.약수배수소수;

import java.util.Scanner;

public class P1978 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int cnt = 0;
        for (int i = 0; i < n; i++) {
            int num = sc.nextInt();
            int sum = 0;
            for (int j = 1; j <= num / 2; j++) {
                if (num % j == 0) {
                    sum += j;
                }
            }
            if (sum == 1) {
                cnt ++;
            }
        }
        System.out.println(cnt);
    }
}
