package com.example.main.boj.start2.bruteforce.bitmask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//종이 조각(비트마스크 풀이 모름..)
public class P14391 {

    static int n;
    static int m;
    static int max;
    static int[][] arr;
    static boolean[][] visited;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        arr = new int[n][m];
        visited = new boolean[n][m];

        for (int i = 0; i < n; i++) {
            char[] str = br.readLine().toCharArray();
            for (int j = 0; j < m; j++) {
                arr[i][j] = str[j] - '0';
            }
        }
        dfs(0, 0);
        System.out.println(max);
    }

    static void dfs(int x, int y) {
        if (x == n) {
            calc();
            return;
        }
        if (y == m) {
            dfs(x + 1, 0);
            return;
        }
        visited[x][y] = true;
        dfs(x, y + 1);
        visited[x][y] = false;
        dfs(x, y + 1);
    }

    static void calc() {
        int result = 0;
        int tmp;

        for (int i = 0; i < n; i++) {
            tmp = 0;
            for (int j = 0; j < m; j++) {
                if (visited[i][j]) {
                    tmp *= 10;
                    tmp += arr[i][j];
                } else {
                    result += tmp;
                    tmp = 0;
                }
            }
            result += tmp;
        }

        for (int i = 0; i < m; i++) {
            tmp = 0;
            for (int j = 0; j < n; j++) {
                if (!visited[j][i]) {
                    tmp *= 10;
                    tmp += arr[j][i];
                } else {
                    result += tmp;
                    tmp = 0;
                }
            }
            result += tmp;
        }

        max = Math.max(max, result);
    }
}
