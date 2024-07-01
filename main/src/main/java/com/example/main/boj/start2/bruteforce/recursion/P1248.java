package com.example.main.boj.start2.bruteforce.recursion;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//Guess
public class P1248 {

    static int n;
    static int[] result;
    static char[][] operators;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());
        result = new int[n];
        operators = new char[n][n];

        String str = br.readLine();
        int idx = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                operators[i][j] = str.charAt(idx++);
            }
        }
        result = new int[n];
        dfs(0);
    }

    static void dfs(int idx) {
        if (idx == n) {
            StringBuilder sb = new StringBuilder();
            for (int val : result) {
                sb.append(val).append(" ");
            }
            System.out.println(sb);
            System.exit(0);
        }

        for (int i = -10; i < 11; i++) {
            result[idx] = i;
            if (check(idx + 1)) {
                dfs(idx + 1);
            }
        }
    }

    static boolean check(int n) {
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = i; j < n; j++) {
                sum += result[j];
                char c;
                if (sum == 0) {
                    c = '0';
                } else if (sum > 0) {
                    c = '+';
                } else {
                    c = '-';
                }
                if (operators[i][j] != c) {
                    return false;
                }
            }
        }
        return true;
    }
}
