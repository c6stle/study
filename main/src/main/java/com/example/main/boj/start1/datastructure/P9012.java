package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//괄호
public class P9012 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            String str = br.readLine();
            int count = 0;
            boolean flag = true;
            for (char c : str.toCharArray()) {
                if (c == '(') {
                    count++;
                    if (count == 0) {
                        sb.append("NO");
                        flag = false;
                        break;
                    }
                } else {
                    count--;
                }
            }
            if (flag) {
                if (count == 0) {
                    sb.append("YES");
                } else {
                    sb.append("NO");
                }
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }
}
