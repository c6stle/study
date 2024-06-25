package com.example.main.boj.start2.bruteforce;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//사탕 게임
public class P3085 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        char[][] arr = new char[n + 1][n + 1];
        for (int i = 1; i < n + 1; i++) {
            String str = br.readLine();
            for (int j = 1; j < n + 1; j++) {
                arr[i][j] = str.charAt(j-1);
            }
        }

        int max = 1;
        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < n; j++) {
                arr = swap(arr, i, j, i, j+1);
                max = search(n, arr, max);
                arr = swap(arr, i, j, i, j+1);
            }
        }

        for (int i = 1; i < n; i++) {
            for (int j = 1; j < n + 1; j++) {
                arr = swap(arr, i + 1, j, i, j);
                max = search(n, arr, max);
                arr = swap(arr, i + 1, j, i, j);
            }
        }

        System.out.println(max);
    }

    private static char[][] swap(char[][] arr, int i1, int j1, int i2, int j2) {
        char temp = arr[i1][j1];
        arr[i1][j1] = arr[i2][j2];
        arr[i2][j2] = temp;
        return arr;
    }

    private static int search(int n, char[][] arr, int max) {
        for (int k = 1; k < n + 1; k++) {
            int cnt = 1;
            for (int l = 1; l < n; l++) {
                if (arr[k][l] == arr[k][l + 1]) {
                    cnt++;
                    max = Math.max(cnt, max);
                } else {
                    cnt = 1;
                }
            }
        }
        for (int k = 1; k < n + 1; k++) {
            int cnt = 1;
            for (int l = 1; l < n; l++) {
                if (arr[l][k] == arr[l + 1][k]) {
                    cnt++;
                    max = Math.max(cnt, max);
                } else {
                    cnt = 1;
                }
            }
        }
        return max;
    }
}
