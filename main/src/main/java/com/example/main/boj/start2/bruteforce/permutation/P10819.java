package com.example.main.boj.start2.bruteforce.permutation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//차이를 최대로
public class P10819 {

    static int[] arr;
    static int[] ans;
    static boolean[] visited;
    static int result;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        visited = new boolean[n];
        arr = new int[n];
        ans = new int[n];
        StringTokenizer st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }
        backtracking(n, 0);
        System.out.println(result);
    }

    static void backtracking(int n, int depth) {
        if (n == depth) {
            int sum = 0;
            for (int i = 0; i < n - 1; i++) {
                sum += Math.abs(ans[i] - ans[i + 1]);
            }
            result = Math.max(result, sum);
            return;
        }
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                ans[depth] = arr[i];
                visited[i] = true;
                backtracking(n, depth + 1);
                visited[i] = false;
            }
        }
    }
}
