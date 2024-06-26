package com.example.main.boj.start2.bruteforce;

import java.util.Scanner;

//N과 M (1)
public class P15649 {

    static StringBuilder sb = new StringBuilder();
    static int[] result;
    static boolean[] visited;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();

        visited = new boolean[n];
        result = new int[m];

        nm(n, m, 0);

        System.out.println(sb);
    }

    static void nm(int n, int m, int depth) {
        if (m == depth) {
            for (int num : result) {
                sb.append(num).append(" ");
            }
            sb.append("\n");
            return;
        }

        for (int i = 1; i <= n; i++) {
            if (!visited[i - 1]) {
                visited[i - 1] = true;
                result[depth] = i;
                nm(n, m, depth + 1);
                visited[i - 1] = false;
            }
        }
    }
}
