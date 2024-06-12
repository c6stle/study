package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

public class P10866 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        StringBuilder sb = new StringBuilder();
        Deque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            String method = st.nextToken();
            if ("push_front".equals(method)) {
                deque.addFirst(Integer.parseInt(st.nextToken()));
            } else if ("push_back".equals(method)) {
                deque.addLast(Integer.parseInt(st.nextToken()));
            } else if ("pop_front".equals(method)) {
                if (!deque.isEmpty()) {
                    sb.append(deque.pollFirst()).append("\n");
                } else {
                    sb.append(-1).append("\n");
                }
            } else if ("pop_back".equals(method)) {
                if (!deque.isEmpty()) {
                    sb.append(deque.pollLast()).append("\n");
                } else {
                    sb.append(-1).append("\n");
                }
            } else if ("size".equals(method)) {
                sb.append(deque.size()).append("\n");
            } else if ("empty".equals(method)) {
                if (!deque.isEmpty()) {
                    sb.append(0).append("\n");
                } else {
                    sb.append(1).append("\n");
                }
            } else if ("front".equals(method)) {
                if (!deque.isEmpty()) {
                    sb.append(deque.getFirst()).append("\n");
                } else {
                    sb.append(-1).append("\n");
                }
            } else if ("back".equals(method)) {
                if (!deque.isEmpty()) {
                    sb.append(deque.getLast()).append("\n");
                } else {
                    sb.append(-1).append("\n");
                }
            }
        }
        System.out.println(sb);
    }
}
