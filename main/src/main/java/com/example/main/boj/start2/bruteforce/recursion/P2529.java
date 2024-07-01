package com.example.main.boj.start2.bruteforce.recursion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

//부등호
public class P2529 {

    static int n;
    static char[] arr;
    static boolean[] visited;
    static List<String> result = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        n = Integer.parseInt(br.readLine());
        StringTokenizer st = new StringTokenizer(br.readLine());

        visited = new boolean[10]; //0~9
        arr = new char[n];
        for (int i = 0; i < n; i++) {
            arr[i] = st.nextToken().charAt(0);
        }
        dfs("", 0);

        result.sort(String::compareTo);
        System.out.println(result.get(result.size() - 1));
        System.out.println(result.get(0));
    }

    static void dfs(String numStr, int depth) {
        if (n + 1 == depth) {
            result.add(numStr);
            return;
        }

        for (int i = 0; i <= 9; i++) {
            if (!visited[i] && (depth == 0 || compare(numStr.charAt(depth - 1) - '0', i, arr[depth - 1]))) {
                visited[i] = true;
                dfs(numStr + i, depth + 1);
                visited[i] = false;
            }
        }
    }

    static boolean compare(int a, int b, char c) {
        if (c == '>') {
            return a > b;
        } else {
            return a < b;
        }
    }
}
