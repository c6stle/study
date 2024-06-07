package com.example.main.boj.약수배수소수;

import java.util.Scanner;

public class P2501 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int k = sc.nextInt();

        int[] arr = new int[n];
        int tempN = 0;
        for (int i = 1; i <= n; i++) {
            if (n % i == 0) {
                arr[tempN] = i;
                tempN++;
            }
        }
        System.out.println(arr[k-1]);
    }
}
