package com.example.main.boj.start1.math;

import java.util.Scanner;

//소인수분해
public class P11653 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();

        int tmp = n;
        for (int i = 2; i <= n; i++) {
            while (tmp % i == 0) {
                System.out.println(i);
                tmp /= i;
            }
            if (tmp == 1) {
                break;
            }
        }
    }
}
