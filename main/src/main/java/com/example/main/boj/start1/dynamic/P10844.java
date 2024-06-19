package com.example.main.boj.start1.dynamic;

import java.util.Arrays;
import java.util.Scanner;

public class P10844 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        long[][] d = new long[n+1][10];
        //1의자리 경우의 수 각 1개씩
        for (int i = 1; i < 10; i++) {
            d[1][i] = 1;
        }

        long mod = 1_000_000_000;
        for (int i = 2; i <= n; i++) {
            for (int j = 0; j < 10; j++) {
                if (j == 0) {
                    d[i][j] = d[i - 1][1] % mod;
                } else if (j == 9) {
                    d[i][j] = d[i - 1][8] % mod;
                } else {
                    d[i][j] = (d[i - 1][j - 1] + d[i - 1][j + 1]) % mod;
                }
            }
        }
        System.out.println(Arrays.stream(d[n]).sum() % mod);
    }
}
