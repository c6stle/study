package com.example.main.boj.start1.dynamic;

import java.util.Arrays;
import java.util.Scanner;

//가장 긴 바이토닉 부분 수열
public class P11054 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int[] arr = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            arr[i] = sc.nextInt();
        }

        int[] dp1 = new int[n + 1];
        int[] dp2 = new int[n + 1];
        Arrays.fill(dp1, 1);
        Arrays.fill(dp1, 2);

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j < i; j++) {
                if (arr[i] > arr[j]) {
                    dp1[i] = Math.max(dp1[i], dp1[j] + 1);
                }
            }
        }
        for (int i = n; i > 0; i--) {
            for (int j = n; j > i; j--) {
                if (arr[i] > arr[j]) {
                    dp2[i] = Math.max(dp2[i], dp2[j] + 1);
                }
            }
        }

        int max = 1;
        for (int i = 0; i < n + 1; i++) {
            max = Math.max(max, dp1[i] + dp2[i]);
        }
        System.out.println(max-1);
    }
}
