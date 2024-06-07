package com.example.main.boj.일반수학1;

import java.util.Scanner;

public class P2292 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        int cnt = 1;
        int endNum = 1;
        while (true) {
            if (endNum >= n) {
                break;
            } else {
                endNum = endNum + 6 * cnt;
                cnt++;
            }
        }
        System.out.println(cnt);
    }
}
