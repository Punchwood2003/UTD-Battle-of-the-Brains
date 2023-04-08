import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class AdditionalAssistance {
	public static void main(String[] args) throws IOException {
		new AdditionalAssistance().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);	
		out.println(Arrays.asList(file.readLine().split(" ")).stream().skip(1).map(string -> Long.parseLong(string)).mapToLong(Long::longValue).sum());
		file.close();
		out.close();
	}
}
