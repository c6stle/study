package com.example.main.boj.start2.bruteforce.permutation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//로또
public class P6603 {

    static boolean[] visited;
    static int[] arr;
    static int[] ans;
    static StringBuilder sb;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int k = Integer.parseInt(st.nextToken());
        while (k != 0) {
            visited = new boolean[k];
            arr = new int[k];
            ans = new int[6];
            sb = new StringBuilder();
            for (int i = 0; i < k; i++) {
                arr[i] = Integer.parseInt(st.nextToken());
            }
            backtracking(0, k, 0);
            System.out.println(sb);
            st = new StringTokenizer(br.readLine());
            k = Integer.parseInt(st.nextToken());
        }

    }

    static void backtracking(int s, int k, int depth) {
        if (depth == 6) {
            for (int num : ans) {
                sb.append(num).append(" ");
            }
            sb.append("\n");
            return;
        }
        for (int i = s; i < k; i++) {
            if (!visited[i]) {
                ans[depth] = arr[i];
                visited[i] = true;
                backtracking(i + 1, k, depth + 1);
                visited[i] = false;
            }
        }
    }
}
