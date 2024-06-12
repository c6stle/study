package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

//에디터
public class P1406 {

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<Character> arr = new LinkedList<>();
        for (char c : br.readLine().toCharArray()) {
            arr.add(c);
        }

        int n = Integer.parseInt(br.readLine());
        ListIterator<Character> listIterator = arr.listIterator(arr.size());
        for (int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            String method = st.nextToken();
            if ("P".equals(method)) {
                char c = st.nextToken().charAt(0);
                listIterator.add(c);
            } else if ("L".equals(method)) {
                if (listIterator.hasPrevious()) {
                    listIterator.previous();
                }
            } else if ("D".equals(method)) {
                if (listIterator.hasNext()) {
                    listIterator.next();
                }
            } else if ("B".equals(method)) {
                if (listIterator.hasPrevious()) {
                    listIterator.previous();
                    listIterator.remove();
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (char c : arr) {
            sb.append(c);
        }
        System.out.println(sb);
    }
}
