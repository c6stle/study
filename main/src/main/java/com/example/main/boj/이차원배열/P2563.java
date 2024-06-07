package com.example.main.boj.이차원배열;

import java.io.IOException;
import java.util.Scanner;

public class P2563 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int total = 0;
        boolean[][] arr = new boolean[101][101];
        for (int i = 0; i < n; i++) {
            int x = sc.nextInt();
            int y = sc.nextInt();
            for (int j = x; j < x + 10; j++) {
                for (int k = y; k < y + 10; k++) {
                    if (!arr[j][k]) {
                        arr[j][k] = true;
                        total++;
                    }
                }
            }
        }
        System.out.println(total);
    }
}
