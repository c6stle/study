package com.example.main.boj.start2.graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

//BFS 스페셜 저지
public class P16940 {

    static int n;
    static int[] arr;
    static boolean[] visited;
    static List<Integer>[] list;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());
        list = new ArrayList[n + 1];
        for (int i = 1; i <= n; i++) {
            list[i] = new ArrayList<>();
        }

        for (int i = 0; i < n - 1; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int start = Integer.parseInt(st.nextToken());
            int end = Integer.parseInt(st.nextToken());
            list[start].add(end);
            list[end].add(start);
        }

        StringTokenizer st = new StringTokenizer(br.readLine());
        arr = new int[n];
        visited = new boolean[n + 1];
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }

        if (arr[0] != 1) {
            System.out.println(0);
            return;
        }

        System.out.println(bfs() ? 1 : 0);
    }

    static boolean bfs() {
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(1);
        visited[1] = true;

        int idx = 1;

        while (!queue.isEmpty()) {
            int now = queue.poll();
            Set<Integer> set = new HashSet<>();
            for (int num : list[now]) {
                if (!visited[num]) {
                    set.add(num);
                }
            }

            int setSize = set.size();
            for (int i = 0; i < setSize; i++) {
                if (idx >= n || !set.contains(arr[idx])) {
                    return false;
                }
                queue.offer(arr[idx]);
                visited[arr[idx]] = true;
                idx++;
            }
        }
        return idx == n;
    }
}
