package com.example.main.boj.start1.dynamic;

import java.util.Scanner;

//2n 타일링2
public class P11727 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        long[] d = new long[n + 3];
        d[1] = 1;
        d[2] = 3;

        for (int i = 3; i <= n; i++) {
            d[i] = (2 * d[i - 2] + d[i - 1]) % 10007;
        }
        System.out.println(d[n]);
    }
}
