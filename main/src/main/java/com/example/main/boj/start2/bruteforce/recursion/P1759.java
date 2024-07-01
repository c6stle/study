package com.example.main.boj.start2.bruteforce.recursion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

//암호 만들기
public class P1759 {

    static boolean[] visited;
    static char[] arr;
    static char[] ans;
    static int l;
    static int c;

    public static void main(String[] args) throws Exception {
        //한개의 모음과 최소 두개의 자음
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        l = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken());

        visited = new boolean[c];
        arr = new char[c];
        ans = new char[l];
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < c; i++) {
            arr[i] = st.nextToken().charAt(0);
        }
        Arrays.sort(arr);
        dfs(0, 0);
    }

    static void dfs(int s, int depth) {
        if (l == depth) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < l; i++) {
                sb.append(ans[i]);
            }
            String temp = sb.toString();

            if (check(temp)) {
                System.out.println(temp);
            }
            return;
        }

        for (int i = s; i < c; i++) {
            if (!visited[i]) {
                visited[i] = true;
                ans[depth] = arr[i];
                dfs(i + 1, depth + 1);
                visited[i] = false;
            }
        }
    }

    static boolean check(String temp) {
        int vowelCnt = 0;
        for (int i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) == 'a' || temp.charAt(i) == 'e' || temp.charAt(i) == 'i' ||
                    temp.charAt(i) == 'o' || temp.charAt(i) == 'u') {
                vowelCnt++;
            }
        }
        return vowelCnt >= 1 && vowelCnt <= temp.length() - 2;
    }
}
