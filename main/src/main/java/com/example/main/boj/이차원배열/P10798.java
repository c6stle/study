package com.example.main.boj.이차원배열;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class P10798 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[][] arr = new String[5][15];

        for (int i = 0; i < 5; i++) {
            String temp = br.readLine();
            for (int j = 0; j < temp.length(); j++) {
                arr[i][j] = temp.substring(j, j+1);
            }
        }

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 5; j++) {
                if (arr[j][i] == null) {
                    System.out.print("");
                } else {
                    System.out.print(arr[j][i]);
                }
            }
        }
    }
}
