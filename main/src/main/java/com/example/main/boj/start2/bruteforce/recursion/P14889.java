package com.example.main.boj.start2.bruteforce.recursion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//스타트와 링크
public class P14889 {

    static int n;
    static int[][] w;
    static boolean[] visited;
    static int result = Integer.MAX_VALUE;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());
        w = new int[n][n];
        visited = new boolean[n];

        StringTokenizer st;
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                w[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        dfs(0, 0);
        System.out.println(result);

    }

    private static void dfs(int idx, int cnt) {
        if (cnt == n / 2) {
            int teamS = 0;
            int teamL = 0;
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (visited[i] && visited[j]) {
                        teamS += w[i][j];
                        teamS += w[j][i];
                    } else if (!visited[i] && !visited[j]) {
                        teamL += w[i][j];
                        teamL += w[j][i];
                    }
                }
            }
            int temp = Math.abs(teamS - teamL);
            result = Math.min(result, temp);
            return;
        }

        for (int i = idx; i < n; i++) {
            if (!visited[i]) {
                visited[i] = true;
                dfs(i + 1, cnt + 1);
                visited[i] = false;
            }
        }
    }
}
