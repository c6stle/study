package com.example.main.boj.start2.graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

//BFS 스페셜 저지
public class P16940 {

    static int n;
    static int[] result;
    static int[] parent;
    static boolean[] visited;
    static List<Integer>[] arr;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());
        arr = new ArrayList[n + 1];
        visited = new boolean[n + 1];
        for (int i = 1; i <= n; i++) {
            arr[i] = new ArrayList<>();
        }
        StringTokenizer st;
        for (int i = 0; i < n - 1; i++) {
            st = new StringTokenizer(br.readLine());
            int start = Integer.parseInt(st.nextToken());
            int end = Integer.parseInt(st.nextToken());
            arr[start].add(end);
            arr[end].add(start);
        }
        result = new int[n];
        parent = new int[n + 1];
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) {
            result[i] = Integer.parseInt(st.nextToken());
        }

        bfs(); //TODO: 시간초과남 다시볼것
    }

    static void bfs() {
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(1);
        visited[1] = true;

        if (result[0] != 1) {
            System.out.println(0);
            System.exit(0);
        }

        int idx = 1;
        while (!queue.isEmpty()) {
            int now = queue.remove();
            int cnt = 0;
            for (int num : arr[now]) {
                if (!visited[num]) {
                    visited[num] = true;
                    parent[num] = now;
                    cnt++;
                }
            }
            for (int i = 0; i < cnt; i++) {
                int num = result[idx];
                if (parent[num] != now) {
                    System.out.println(0);
                    System.exit(0);
                }
                queue.offer(now);
                idx++;
            }
        }
        System.out.println(1);
    }
}
