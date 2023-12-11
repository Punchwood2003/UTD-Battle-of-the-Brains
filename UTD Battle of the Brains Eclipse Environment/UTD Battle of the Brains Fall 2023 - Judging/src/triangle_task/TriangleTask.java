package triangle_task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class TriangleTask {
    public static void main(String[] args) throws IOException {
        new TriangleTask().run();
    }
    
    public void run() throws IOException {
        BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        int N = Integer.parseInt(file.readLine());
        for(int i = 0; i < N; i++) {
            StringTokenizer st = new StringTokenizer(file.readLine());
            long H = Integer.parseInt(st.nextToken());
            long V = Integer.parseInt(st.nextToken());
            out.println((H + 1) * (V + 1) * (V + 2) / 2);
        }
        file.close();
        out.close();
    }
}
