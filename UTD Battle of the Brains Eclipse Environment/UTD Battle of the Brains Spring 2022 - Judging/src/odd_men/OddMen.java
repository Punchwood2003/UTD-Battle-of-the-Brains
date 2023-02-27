package odd_men;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;

public class OddMen {
    public static void main(String[] args) throws IOException {
        BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out); 
        int n = Integer.parseInt(file.readLine());
        int[] p = new int[n];
        StringTokenizer st = new StringTokenizer(file.readLine());
        for(int i = 0; i < n; i++) {
            p[i] = Integer.parseInt(st.nextToken());
        }
        Arrays.sort(p);
        int count = 0;
        for(int i = 0; i < n; i++) {
            int parity1 = p[i] % 2;
            int parity2 = i % 2;
            count += (parity1 == parity2) ? 1 : 0;
        }
        out.println(count);
        out.close();
    }
}
