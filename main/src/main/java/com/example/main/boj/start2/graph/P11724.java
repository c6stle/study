package com.example.main.boj.start2.graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//연결 요소의 개수
public class P11724 {

    static int n, m;
    static boolean[] visited;
    static int[][] arr;
    static int cnt = 0;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        visited = new boolean[n + 1];
        arr = new int[n + 1][n + 1];
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            int idx1 = Integer.parseInt(st.nextToken());
            int idx2 = Integer.parseInt(st.nextToken());
            arr[idx1][idx2] = arr[idx2][idx1] = 1;
        }

        for (int i = 1; i < n + 1; i++) {
            if (!visited[i]) {
                dfs(i);
                cnt++;
            }
        }
        System.out.println(cnt);
    }

    static void dfs(int idx) {
        visited[idx] = true;
        for (int i = 1; i < n + 1; i++) {
            if (!visited[i] && arr[idx][i] == 1) {
                dfs(i);
            }
        }
    }
}
