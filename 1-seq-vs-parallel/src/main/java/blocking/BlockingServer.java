package blocking;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.time.LocalDateTime;

// https://medium.com/coderscorner/tale-of-client-server-and-socket-a6ef54a74763
public class BlockingServer {
    public static void main(String[] args) throws IOException {
        try (var listener = new ServerSocket(59090)) {
            System.out.println("The date server is running...");
            while (true) {
                try (var socket = listener.accept()) { //blocking
                    var out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(LocalDateTime.now());
                }
            }
        }
    }
}
