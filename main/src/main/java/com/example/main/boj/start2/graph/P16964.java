package com.example.main.boj.start2.graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

//DFS 스페셜 저지
public class P16964 {

    static int n;
    static int[] arr;
    static int[] parent;
    static boolean[] visited;
    static List<Integer>[] list;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());

        list = new ArrayList[n + 1];
        for (int i = 1; i < n + 1; i++) {
            list[i] = new ArrayList<>();
        }
        for (int i = 0; i < n - 1; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int s = Integer.parseInt(st.nextToken());
            int e = Integer.parseInt(st.nextToken());
            list[s].add(e);
            list[e].add(s);
        }

        StringTokenizer st = new StringTokenizer(br.readLine());
        arr = new int[n];
        parent = new int[n + 1];
        visited = new boolean[n + 1];
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }

        dfs(1, 0);
        System.out.println(1);
    }

    static void dfs(int now, int idx) {
        visited[now] = true;

        int cnt = 0;
        for (int num : list[now]) {
            if (!visited[num]) {
                visited[num] = true;
                parent[num] = now;
                cnt++;
            }
        }

        idx++;
        for (int i = 0; i < cnt; i++) {
            int num = arr[idx];
            if (parent[num] != now) {
                System.out.println(0);
                System.exit(0);
            }
            dfs(num, idx);
        }
    }
}
