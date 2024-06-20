package com.example.main.boj.start1.dynamic;

import java.util.Scanner;

//제곱수의 합
public class P1699 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] dp = new int[n + 1];

        for (int i = 1; i < n + 1; i++) {
            dp[i] = i;
            for (int j = 1; j * j <= i; j++) {
                int temp = dp[i - j * j] + 1;
                if (dp[i] > temp) {
                    dp[i] = temp;
                }
            }
        }
        System.out.println(dp[n]);


        //왜 안되는지 모르겠음
        //int temp = n;
        //int cnt = 0;
        //while (temp >= 4) {
        //    double sqrt = Math.sqrt(temp);
        //    int d = (int) Math.floor(sqrt);
        //    temp = temp - (int) Math.pow(d, 2);
        //    cnt++;
        //}
        //cnt += temp;
        //System.out.println(cnt);
    }
}
