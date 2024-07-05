package com.example.main.boj.start2.graph;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

//토마토
public class P7576 {

    static int n, m, result;
    static int[][] arr;
    static boolean[][] visited;
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    static Queue<int[]> queue = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        result = 0;
        visited = new boolean[m][n];
        arr = new int[m][n];
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
                if (arr[i][j] == 1) {
                    queue.offer(new int[]{i, j});
                }
            }
        }
        bfs();
        System.out.println(result);
    }

    static void bfs() {
        while (!queue.isEmpty()) {
            int[] now = queue.poll();
            for (int i = 0; i < 4; i++) {
                int nx = now[0] + dx[i];
                int ny = now[1] + dy[i];
                if (nx >= 0 && ny >= 0 && nx < m && ny < n && !visited[nx][ny] && arr[nx][ny] == 0) {
                    arr[nx][ny] = arr[now[0]][now[1]] + 1;
                    queue.offer(new int[]{nx, ny});
                }
            }
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (arr[i][j] == 0) {
                    result = -1;
                    return;
                }
            }
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (result < arr[i][j]) {
                    result = arr[i][j];
                }
            }
        }
        result = result - 1;
    }
}
