package com.example.main.boj.start2.bruteforce.nm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//N과 M (2)
public class P15650 {

    static boolean[] visited;
    static int[] result;
    static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());

        visited = new boolean[n];
        result = new int[m];

        nm(1, n, m, 0);

        System.out.println(sb);
    }

    static void nm(int s, int n, int m, int depth) {
        if (m == depth) {
            for (int num : result) {
                sb.append(num).append(" ");
            }
            sb.append("\n");
            return;
        }

        for (int i = s; i <= n; i++) {
            if (!visited[i - 1]) {
                visited[i - 1] = true;
                result[depth] = i;
                //nm(i, n, m, depth + 1);  //가능함, 이미 체크한 숫자 한번 더 체크함
                nm(i + 1, n, m, depth + 1);
                visited[i - 1] = false;
            }
        }
    }
}



