package com.example.main.boj.start2.graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

//미로 탐색
public class P2178 {

    static int[][] arr;
    static boolean[][] visited;
    static int n, m;
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, -1, 0, 1};

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        arr = new int[n][m];
        visited = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            String str = br.readLine();
            for (int j = 0; j < m; j++) {
                arr[i][j] = str.charAt(j) - '0';
            }
        }

        bfs(0, 0);

        System.out.println(arr[n - 1][m - 1]);
    }

    static void bfs(int x, int y) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{x, y});
        while (!queue.isEmpty()) {
            int[] now = queue.poll();
            visited[x][y] = true;
            for (int i = 0; i < 4; i++) {
                int nx = now[0] + dx[i];
                int ny = now[1] + dy[i];
                if (nx >= 0 && ny >= 0 && nx < n && ny < m && arr[nx][ny] != 0 && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    arr[nx][ny] = arr[now[0]][now[1]] + 1;
                    queue.add(new int[]{nx, ny});
                }
            }
        }
    }
}
