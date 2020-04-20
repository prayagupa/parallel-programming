package blocking;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.stream.IntStream;

public class BlockingServerClient {

    public static void main(String[] args) throws IOException {
        IntStream.range(0, 500).forEach(__ -> makeACall(__));
    }

    private static void makeACall(int id) {
        try {
            var socket = new Socket("localhost", 59090);
            var in = new Scanner(socket.getInputStream());
            System.out.println("Server response: " + in.nextLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
