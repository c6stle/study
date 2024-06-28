package com.example.main.boj.start2.bruteforce.permutation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//외판원 순회 2
public class P10971 {

    static boolean[] visited;
    static int[][] w;
    static int result = Integer.MAX_VALUE;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());
        w = new int[n][n];
        StringTokenizer st;
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                w[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 0; i < n; i++) {
            visited = new boolean[n];
            visited[i] = true;
            backtracking(n, i, i, 0);
        }
        System.out.println(result);
    }

    static void backtracking(int n, int s, int now, int cost) {
        boolean check = true;
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                check = false;
                break;
            }
        }
        if (check) {
            if (w[now][s] != 0) {
                result = Math.min(result, cost + w[now][s]);
            }
            return;
        }
        for (int i = 1; i < n; i++) {
            if (!visited[i] && w[now][i] != 0) {
                visited[i] = true;
                backtracking(n, s, i, cost + w[now][i]);
                visited[i] = false;
            }
        }

    }
}
