package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

//요세푸스(큐 활용)
public class P1158 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int k = Integer.parseInt(st.nextToken());

        Queue<Integer> arr = new LinkedList<>();
        for (int i = 1; i <= n; i++) {
            arr.offer(i);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<");

        while (arr.size() > 1) {
            for (int i = 0; i < k - 1; i++) {
                arr.offer(arr.poll());
            }
            sb.append(arr.poll()).append(", ");
        }
        sb.append(arr.poll()).append(">");
        System.out.println(sb);
    }
}
