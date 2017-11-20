package servertemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

//Example of a multithreaded chat room.
public class ChatApp extends Thread {

    //server port for chat
    private static final int PORT = 9001;
    //set of all names of clients in the chat room.
    private static HashSet<String> names = new HashSet<String>();
    //set of all the print writers for clients.
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    //Handler thread class. Handlers are spawned from the listening loop and are
    //responsible for dealingn with a single client and broadcasting its messages
    private String name;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    //Constructs a handler thread, squirreling away the socket.
    //All the interesting work is done in the run method.
    public ChatApp(Socket socket) {
        this.socket = socket;
    }

    //repeatedly requests a unique screen name until one is found, then
    //acknowledges the name and registers the output stream for the client in
    //a global set. Then gets inputs and broadcasts them.
    public void run() {
        try {
            //create character streams for the socket.
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            //request a name from this client.
            while (true) {
                out.println("SUBMITNAME");
                name = in.readLine();
                if (name == null) {
                    return;
                }
                synchronized (names) {
                    if (!names.contains(name)) {
                        names.add(name);
                        break;
                    }
                }
            }

            //now that the name has been chosen, add the socket's print writer
            //to the set of all writers so this client can receive broadcasts.
            out.println("NAMEACCEPTED");
            writers.add(out);

            //accept messages from this client and broadcast them.
            //Ignore other clients that cannot be broadcasted to.
            while (true) {
                String input = in.readLine();
                if (input == null) {
                    return;
                }
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + ": " + input);
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        } finally {
            //This client is going down! Remove its name and its print writer
            //from the sets, and close its socket.
            if (name != null) {
                names.remove(name);
            }
            if (out != null) {
                writers.remove(out);
            }
        }
        try {
            socket.close();
        } catch (IOException e) {

        }
    }
}
