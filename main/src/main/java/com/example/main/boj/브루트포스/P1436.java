package com.example.main.boj.브루트포스;

import java.util.Scanner;

public class P1436 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int count = 0;
        int start = 666;
        while (count < n) {
            if (String.valueOf(start).contains("666")) {
                count++;
            }
            start++;
        }
        System.out.println(start - 1);
    }
}
