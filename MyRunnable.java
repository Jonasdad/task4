import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Throwable;
import java.net.*;
public class MyRunnable implements Runnable{
    
    private Socket clientSocket;

    public MyRunnable(Socket clientSocket){
        this.clientSocket = clientSocket;
    }
    
    @Override
    public void run(){
            try{
                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();
                ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
                int i;
                while ((i = in.read()) != 13) {
                    BAOS.write(i);
                }
                String request = BAOS.toString();
                String[] requestArray = request.split("[ ?=&]");


                String host = findHost(requestArray, "hostname");

                if (!requestArray[0].equals("GET")){
                    out.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                }
                if (!requestArray[1].equals("/ask") || host == null) {
                    out.write(("HTTP/1.1 404 Object Not Found\r\n\r\n").getBytes());
                }
                String protocol = getProtocol(requestArray);
                if(!protocol.equals("HTTP/1.1")){
                    System.out.println("True");
                    out.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                }
                else{

                    
                byte[] toTCPClient = byteArrayToTCPClient(requestArray, "string");

                Boolean shutdown = findShutdown(requestArray, "shutdown");
                
                Integer port = findInt(requestArray, "port");

                TCPClient tcpclient = new TCPClient(shutdown, findInt(requestArray, "timeout"),
                        findInt(requestArray, "limit"));

                 System.out.print("Host: " + host + "\nPort: " + port + "\nShutdown: " + shutdown + "\nTimeout: "
                        + findInt(requestArray, "timeout") + " ms"+ "\nLimit: " + findInt(requestArray, "limit") + " Bytes" + "\nProtocol: " + protocol);
                
                        String toBrowser = new String(tcpclient.askServer(host, port, toTCPClient));
                out.write("HTTP/1.1 200 OK\r\n".getBytes());
                out.write(("Content-length: " + toBrowser.length() + "\r\n").getBytes());
                out.write("Content-type: text/plain\r\n\r\n".getBytes());
                out.write(toBrowser.getBytes());
                out.flush();
                }   
            }
            catch(Throwable e){
                e.printStackTrace();
            }
        }
    
    public static String getProtocol(String[] array){
        int i = array.length - 1;
        String returnString = array[i];
        return returnString;

    }

    public static byte[] byteArrayToTCPClient(String[] array, String key) {
        byte[] empty = new byte[0];
        for (int i = 0; i < array.length; i++) {
            if ((array[i].toLowerCase()).equals(key)) {
                String send = array[i+1] + "\n";
                return send.getBytes();
            }
        }
        return empty;
    }

    public static Integer findInt(String[] array, String key) {
        for (int i = 0; i < array.length; i++) {
            if ((array[i].toLowerCase()).equals(key)) {
                return Integer.parseInt(array[i + 1]);
            }
        }
        return null;

    }

    public static String findHost(String[] array, String host) {
        for (int i = 0; i < array.length; i++) {
            if ((array[i].toLowerCase()).equals(host)) {
                return array[i + 1];
            }
        }
        return null;
    }

    public static boolean findShutdown(String[] array, String shutdown) {
        for (int i = 0; i < array.length; i++) {
            if ((array[i].toLowerCase()).equals(shutdown)) {
                if (array[i + 1].toLowerCase().equals("true")) {
                    return true;
                }
            }
        }
        return false;
    }
}
