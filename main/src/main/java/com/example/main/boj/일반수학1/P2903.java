package com.example.main.boj.일반수학1;

import java.util.Scanner;

public class P2903 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int num = 2;
        for (int i = 0; i < n; i++) {
            num = 2 * num - 1;
        }

        System.out.println(num*num);
    }
}
