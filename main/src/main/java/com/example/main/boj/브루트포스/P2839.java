package com.example.main.boj.브루트포스;

import java.util.Scanner;

public class P2839 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int kg = sc.nextInt();

        int k1 = 0;
        int k2 = 0;
        loop1 :
        for (int i = 0; i < 1667; i++) {
            for (int j = 0; j <= 1000; j++) {
                if (3 * i + 5 * j == kg) {
                    k1 = i;
                    k2 = j;
                    break loop1;
                }
            }
        }
        if (k1 == 0 && k2 == 0) {
            System.out.println("-1");
        } else {
            System.out.println(k1+k2);
        }
    }
}
