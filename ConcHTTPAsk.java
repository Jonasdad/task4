import java.net.*;
import java.io.*;
import java.util.*;
//http://localhost:8080/ask?hostname=time.nist.gov&limit=1200&port=13
//httq://localhost:8080/ask?hostname=java.lab.ssvl.kth.se&shutdown=true&limit=1200&port=13&string=hejhejhejhej
public class ConcHTTPAsk {
    public static void main(String[] args) throws Exception {
        // Your code here
        ServerSocket server = new ServerSocket(Integer.parseInt(args[0]));
        while(!server.isClosed()) {
            Socket client = server.accept();
            MyRunnable satan = new MyRunnable(client);
            Thread thread = new Thread(satan);
            thread.start();
            }
        }
    }

