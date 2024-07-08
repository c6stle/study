package com.example.main.boj.start2.graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

//서울 지하철 2호선
public class P16947 {

    static int n;
    static int[] result;
    static boolean[] cycle;
    static boolean[] visited;
    static List<Integer>[] arr;
    static Queue<Integer> queue = new LinkedList<>();;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());
        arr = new ArrayList[n + 1];
        for (int i = 1; i < n + 1; i++) {
            arr[i] = new ArrayList<>();
        }
        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int start = Integer.parseInt(st.nextToken());
            int end = Integer.parseInt(st.nextToken());
            arr[start].add(end);
            arr[end].add(start);
        }

        //dfs 로 사이클인 노드를 찾아줌
        cycle = new boolean[n + 1];
        result = new int[n + 1];
        for (int i = 1; i < n + 1; i++) {
            visited = new boolean[n + 1];
            findCycle_dfs(i, i, 1);
        }
        for (int i = 1; i < n + 1; i++) {
            if (cycle[i]) {
                queue.offer(i);
            } else {
                result[i] = -1;
            }
        }
        bfs();

        for (int i = 1; i < n + 1; i++) {
            System.out.print(result[i] + " ");
        }
    }

    static void findCycle_dfs(int start, int now, int cnt) {
        visited[now] = true;
        for (int num: arr[now]) {
            if (!visited[num]) {
                findCycle_dfs(start, num, cnt + 1);
            } else if (num == start && cnt > 2) {
                cycle[num] = true;
                return;
            }
        }
    }

    static void bfs() {
        while (!queue.isEmpty()) {
            int now = queue.poll();
            for (int num : arr[now]) {
                if (result[num] == -1) {
                    result[num] = result[now] + 1;
                    queue.offer(num);
                }
            }
        }
    }
}
