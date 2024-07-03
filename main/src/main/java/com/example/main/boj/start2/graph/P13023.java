package com.example.main.boj.start2.graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

//ABCDE
public class P13023 {

    static int n;
    static int m;
    static boolean[] visited;
    static ArrayList<Integer>[] arr;
    static int result = 0;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        visited = new boolean[n];
        arr = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            arr[i] = new ArrayList<>();
        }
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            int s = Integer.parseInt(st.nextToken());
            int e = Integer.parseInt(st.nextToken());
            arr[s].add(e);
            arr[e].add(s);
        }
        for (int i = 0; i < n; i++) {
            if (result == 0) {
                dfs(i, 1);
            }
        }
        System.out.println(result);
    }

    static void dfs(int s, int depth) {
        if (depth == 5) {
            result = 1;
            return;
        }
        visited[s] = true;
        for (int i: arr[s]) {
            if (!visited[i]) {
                dfs(i, depth + 1);
            }
        }
        visited[s] = false;
    }
}
