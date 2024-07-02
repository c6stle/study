package com.example.main.boj.start2.bruteforce.bitmask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

//집합
public class P11723 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int m = Integer.parseInt(br.readLine());
        int s = 0;

        StringBuilder sb = new StringBuilder();
        StringTokenizer st;
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            String method = st.nextToken();
            if ("all".equals(method)) {
                s = (1 << 21) - 1;
            } else if ("empty".equals(method)) {
                s = 0;
            } else {
                int x = Integer.parseInt(st.nextToken());
                if ("add".equals(method)) {
                    s |= (1 << x);
                } else if ("remove".equals(method)) {
                    s &= ~(1 << x);
                } else if ("check".equals(method)) {
                    if ((s & (1 << x)) != 0) {
                        sb.append(1).append("\n");
                    } else {
                        sb.append(0).append("\n");
                    }
                } else if ("toggle".equals(method)) {
                    s ^= (1 << x);
                }
            }
        }
        System.out.println(sb);
    }
}


