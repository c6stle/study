package com.example.main.boj.start2.graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

//DFS와 BFS
public class P1260 {

    static int n, m, s;
    static boolean[] visited;
    static int[][] arr;
    static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        s = Integer.parseInt(st.nextToken());

        visited = new boolean[n + 1];
        arr = new int[n + 1][n + 1];
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            int start = Integer.parseInt(st.nextToken());
            int end = Integer.parseInt(st.nextToken());
            arr[start][end] = arr[end][start] = 1;
        }
        dfs(s);
        visited = new boolean[n + 1];
        sb.append("\n");
        bfs(s);
        System.out.println(sb);
    }

    static void dfs(int num) {
        visited[num] = true;
        sb.append(num).append(" ");
        for (int i = 1; i <= n; i++) {
            if (arr[num][i] == 1 && !visited[i]) {
                dfs(i);
            }
        }
    }

    static void bfs(int num) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(num);
        visited[num] = true;
        sb.append(num).append(" ");
        while (!queue.isEmpty()) {
            int tmp = queue.poll();
            for (int i = 1; i <= n; i++) {
                if (arr[tmp][i] == 1 && !visited[i]) {
                    queue.add(i);
                    visited[i] = true;
                    sb.append(i).append(" ");
                }
            }
        }
    }
}
