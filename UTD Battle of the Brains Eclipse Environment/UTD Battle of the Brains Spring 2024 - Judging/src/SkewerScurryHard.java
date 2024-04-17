import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class SkewerScurryHard {
    
    private int[] dr = {1, -1, 0, 0};
    private int[] dc = {0, 0, 1, -1};
    
    private boolean[][][] safe;
    private boolean[][] dp, tmp;

    public static void main(String[] args) throws IOException {
        new SkewerScurryHard().run();
    }
    
    public void run() throws IOException {
        BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        
        StringTokenizer st = new StringTokenizer(file.readLine());
        int R = Integer.parseInt(st.nextToken());
        int C = Integer.parseInt(st.nextToken());
        int startR = Integer.parseInt(st.nextToken());
        int startC = Integer.parseInt(st.nextToken());

        char[][] board = new char[R][C];
        safe = new boolean[R][C][5];
        for(int i = 0; i < R; i++) {
            String line = file.readLine();
            for(int j = 0; j < C; j++) {
                board[i][j] = line.charAt(j);
                safe[i][j][0] = board[i][j] != '#';
            }
        }
        
        for(int i = 0; i < R; i++) {
            for(int j = 0; j < C; j++) {
                if(board[i][j] == '#') {
                    for(int k = j + 1; k < C; j++) {
                        if(board[i][k] != '#') {
                            safe[i][k][1] = true;
                        }
                    }
                    break;
                }
            }
            for(int j = C-1; j >= 0; j--) {
                if(board[i][j] == '#') {
                    for(int k = j - 1; k >= 0; k--) {
                        if(board[i][k] != '#') {
                            safe[i][k][2] = true;
                        }
                    }
                    break;
                }
            }
        }
        
        for(int j = 0; j < C; j++) {
            for(int i = 0; i < R; i++) {
                if(board[i][j] == '#') {
                    for(int k = i + 1; k < R; k++) {
                        if(board[k][j] != '#') {
                            safe[k][j][3] = true;
                        }
                    }
                    break;
                }
            }
            for(int i = R-1; i >= 0; i--) {
                if(board[i][j] == '#') {
                    for(int k = i - 1; k >= 0; k--) {
                        if(board[k][j] != '#') {
                            safe[k][j][4] = true;
                        }
                    }
                    break;
                }
            }
        }
        
        String s = file.readLine();
        file.close();
        
        dp = new boolean[R][C];
        dp[startR][startC] = true;
        for(int c = 0; c < s.length(); c++) {
            int skewerDirection = 0;
            char ch = s.charAt(c);
            if(C == 'E') {
                skewerDirection = 1;
            } else if(ch == 'W') {
                skewerDirection = 2;
            } else if(ch == 'N') {
                skewerDirection = 3;
            } else if(ch == 'S') {
                skewerDirection = 4;
            }
            
            tmp = new boolean[R][C];
            for(int i = 0; i < R; i++) {
                for(int j = 0; j < C; j++) {
                    if(safe[i][j][skewerDirection]) {
                        for(int d = 0; d < 4; d++) {
                            int newR = i + dr[d];
                            int newC = j + dc[d];
                            if(inBounds(R, C, newR, newC) && dp[newR][newC]) {
                                tmp[i][j] = true;
                            }
                            if(dp[i][j]) {
                                tmp[i][j] = true;
                            }
                        }
                    }
                }
            }
            
            for(int i = 0; i < R; i++) {
                for(int j = 0; j < C; j++) {
                    dp[i][j] = tmp[i][j];
                }
            }
        }
        
        for(int i = 0; i < R; i++) {
            for(int j = 0; j < C; j++) {
                if(dp[i][j]) {
                    out.println("Go Comets!");
                    out.close();
                    return;
                }
            }
        }
        
        out.println("Ouch!!!");
        out.close();
    }
    
    private boolean inBounds(int R, int C, int i, int j) {
        return 0 <= i && i < R && 0 <= j && j < C;
    }
}