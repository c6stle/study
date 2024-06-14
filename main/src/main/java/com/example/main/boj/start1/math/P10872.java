package com.example.main.boj.start1.math;

import java.util.Scanner;

//팩토리얼
public class P10872 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int result = 1;
        if (n != 0) {
            for (int i = 1; i <= n; i++) {
                result = result * i;
            }
        }
        System.out.println(result);
    }
}
