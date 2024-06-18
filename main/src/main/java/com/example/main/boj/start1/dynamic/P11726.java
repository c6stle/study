package com.example.main.boj.start1.dynamic;

import java.util.Scanner;

public class P11726 {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        long[] d = new long[n + 1];
        d[1] = 1;
        d[2] = 2;
        for (int i = 3; i < n + 1; i++) {
            d[i] = (d[i-1] + d[i-2]) % 10007;
        }
        System.out.println(d[n]);
    }
}
