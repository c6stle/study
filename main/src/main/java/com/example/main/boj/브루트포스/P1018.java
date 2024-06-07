package com.example.main.boj.브루트포스;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class P1018 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int row = Integer.parseInt(st.nextToken());
        int col = Integer.parseInt(st.nextToken());

        char[][] arr = new char[row][col];
        for (int i = 0; i < row; i++) {
            String rowStr = br.readLine();
            for (int j = 0; j < col; j++) {
                arr[i][j] = rowStr.charAt(j);
            }
        }
        
        int min = 64;
        for (int i = 0; i < row - 7; i++) {
            for (int j = 0; j < col - 7; j++) {
                min = Math.min(min, cal(i, j, arr));
            }
        }
        System.out.println(min);
    }

    private static int cal(int i, int j, char[][] arr) {
        int count = 0;
        char color = 'W';
        for (int k = i; k < i + 8; k++) {
            for (int l = j; l < j + 8; l++) {
                if (arr[k][l] != color) {
                    count++;
                }
                if (color == 'W') {
                    color = 'B';
                } else {
                    color = 'W';
                }
            }
            if (color == 'W') {
                color = 'B';
            } else {
                color = 'W';
            }
        }
        return Math.min(count, 64 - count);
    }
}
