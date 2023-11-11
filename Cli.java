import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Cli {
    
     public static void main(String args[])
    {
        //int serverPort = 35786;
        //String serverName = "localhost";

        String serverName = args[0];
        int serverPort = Integer.parseInt(args[1]);
       
        //Security.addProvider(new Provider());
        System.setProperty("javax.net.ssl.trustStore","trustStore.jts");
        System.setProperty("javax.net.ssl.trustStorePassword","123456");
        //System.setProperty("javax.net.debug","all");
        try
        {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket)sslsocketfactory.createSocket(serverName,serverPort);

            System.out.println("Client Connected to Server");

            DataOutputStream outputStream = new DataOutputStream(sslSocket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(sslSocket.getInputStream());

            while (true)
            {
                System.out.println("Enter User ID : ");
                String userId = System.console().readLine();
                outputStream.writeUTF(userId);

                System.out.println("Enter password : ");
                String password = System.console().readLine();
                outputStream.writeUTF(password);

                String serverResponse = inputStream.readUTF();
                System.out.println(serverResponse);

                Boolean isValid = inputStream.readBoolean();
                
                if(isValid){
                    outputStream.close();
                    inputStream.close();
                    sslSocket.close();
                    break;
                }
            }
        }
        catch(Exception ex)
        {
            System.err.println("Error Happened : "+ex.toString());
        }
    }
}
