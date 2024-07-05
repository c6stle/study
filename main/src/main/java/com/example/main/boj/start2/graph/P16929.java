package com.example.main.boj.start2.graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//Two Dots
public class P16929 {

    static int n, m, startx, starty;
    static char[][] arr;
    static boolean[][] visited;
    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0};

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        arr = new char[n][m];
        for (int i = 0; i < n; i++) {
            String str = br.readLine();
            for (int j = 0; j < m; j++) {
                arr[i][j] = str.charAt(j);
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                visited = new boolean[n][m];
                startx = i;
                starty = j;
                dfs(i, j, 1);
            }
        }
        System.out.println("No");
    }

    static void dfs(int x, int y, int cnt) {
        visited[x][y] = true;
        for (int i = 0; i < 4; i++) {
            int nx = dx[i] + x;
            int ny = dy[i] + y;
            if (nx >= 0 && ny >= 0 && nx < n && ny < m && arr[x][y] == arr[nx][ny]) {
                if (!visited[nx][ny]) {
                    visited[nx][ny] = true;
                    dfs(nx, ny,  cnt + 1);
                } else {
                    if (cnt >= 4 && startx == nx && starty == ny) {
                        System.out.println("Yes");
                        System.exit(0);
                    }
                }
            }
        }
    }
}
