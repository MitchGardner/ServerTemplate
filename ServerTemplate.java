package servertemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTemplate {

    public static void main(String[] args) throws Exception {
        
        System.out.println("Server has been initialized.");
        int clientNumber = 0;
        //
        ServerSocket chatListener = new ServerSocket(9001);
        ServerSocket capitalizerListener = new ServerSocket(4444);
        try {
            while (true) {
                new ChatApp(chatListener.accept()).start();
                new Capitalizer(capitalizerListener.accept(), clientNumber++).start();
            }
        } finally {
            capitalizerListener.close();
            chatListener.close();
        }
    }
}