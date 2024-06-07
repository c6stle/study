package com.example.main.boj.이차원배열;

import java.io.IOException;
import java.util.Scanner;

public class P2566 {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        int max = 0;
        int row = 1;
        int col = 1;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int value = sc.nextInt();
                if (max < value) {
                    max = value;
                    row = i + 1;
                    col = j + 1;
                }
            }
        }
        System.out.println(max);
        System.out.println(row + " " + col);
    }
}
