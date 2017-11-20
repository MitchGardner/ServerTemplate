package servertemplate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import static java.rmi.server.LogStream.log;

//This class creates a private thread to handle capitalization requests on a particular
//socket. The client terminates the dialogue by sending a single line
//only containing a period  IE: "."


class Capitalizer extends Thread {
    
    private Socket socket;
    private int clientNumber;
    
    public Capitalizer(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        log("New connection with client #" + clientNumber + "at " + socket); 
    }
    
    //Services this thread's client by first sending the client a welcome message
    //then repeatedly reading strings and sending back the capitalized version of the string
    public void run() {
        try {
            //allows the stream to send characters instead of just bytes.
            //Ensure output is flushed after every newline.
            BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
  
            //Send welcome message to clients
            out.println("Welcome, you are client #" + clientNumber + ".");
            out.println("Enter a line with only a period to quit\n");
    
        while (true) {
            String input = in.readLine();
            if (input == null || input.equals(".")) {
                break;
            }
            out.println(input.toUpperCase());
        }
    
    } catch (IOException e) {
    log("Error handling client #" + clientNumber + ": " + e);
} finally {
    try {
        socket.close();
    } catch (IOException e) {
        log("Couldn't close a socket, what's going on?");
    }
    log("Connection with client # " + clientNumber + "closed.");
        }
    }
}

