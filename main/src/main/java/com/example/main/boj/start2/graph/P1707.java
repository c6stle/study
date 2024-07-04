package com.example.main.boj.start2.graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

//이분 그래프
public class P1707 {

    static int k, v, e;
    static ArrayList<Integer>[] arr;
    static boolean[] visited;
    static int[] check;
    static boolean result;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        k = Integer.parseInt(br.readLine());
        for (int i = 0; i < k; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            v = Integer.parseInt(st.nextToken());
            e = Integer.parseInt(st.nextToken());
            visited = new boolean[v + 1];
            check = new int[v + 1];
            arr = new ArrayList[v + 1];
            for (int j = 1; j <= v; j++) {
                arr[j] = new ArrayList<>();
            }
            for (int j = 0; j < e; j++) {
                st = new StringTokenizer(br.readLine());
                int start = Integer.parseInt(st.nextToken());
                int end = Integer.parseInt(st.nextToken());
                arr[start].add(end);
                arr[end].add(start);
            }
            result = true;
            for (int j = 1; j <= v; j++) {
                if (result) {
                    dfs(j);
                } else {
                    break;
                }
            }

            if (result) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }

    static void dfs(int idx) {
        visited[idx] = true;
        for (int i: arr[idx]) {
            if (!visited[i]) {
                check[i] = (check[idx] + 1) % 2;
                dfs(i);
            } else if (check[idx] == check[i]) {
                result = false;
            }
        }
    }
}
