import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;


public class Serv {
    
     public static void main(String args[])
    {

        if(args.length < 1){
            System.out.println("Port number is missing in input");
            System.exit(1);
        }

        int port = 0;

        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Port number should be a number");
            System.exit(1);
        }

        //int port = 35786;
        
        System.setProperty("javax.net.ssl.keyStore","samsher.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","123456");
        //System.setProperty("javax.net.debug","all");
        try
        {
            SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket)sslServerSocketfactory.createServerSocket(port);

            System.out.println("Server Started & Ready to accept Client Connection");

            while(true)
            {
                
                SSLSocket sslSocket = (SSLSocket)sslServerSocket.accept();

                DataInputStream inputStream = new DataInputStream(sslSocket.getInputStream());

                DataOutputStream outputStream = new DataOutputStream(sslSocket.getOutputStream());

                while(true){
                    String userName = inputStream.readUTF();
                    System.out.println("User name is : " + userName);

                    String password = inputStream.readUTF();
                    System.out.println("Password is : "+password);

                    if(checkUser(userName, password)){
                        outputStream.writeUTF("Correct ID and password");
                        outputStream.writeBoolean(true);
                        break;
                    }else{
                        outputStream.writeUTF("The ID/password is incorrect");
                        outputStream.writeBoolean(false);
                    }
                }
                
            }
        }
        catch(Exception ex)
        {
            System.err.println("Error Happened : "+ex.toString());
        }
    }

    private static boolean checkUser(String userId, String password){

        boolean isValid = false;

        String filePath = "hashpasswd.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] strArray = line.split("\\s+");

                if(strArray[0].equals(userId) && strArray[1].equals(generateHashedString(password))){
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

        return isValid;
    }

    private static String generateHashedString(String pwd){

        String hashedPwd = "";

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(pwd.getBytes(StandardCharsets.UTF_8));
            hashedPwd = bytesToHex(encodedHash);
            //System.out.println("Hashed String: " + hashedString);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Exception: " + e.getMessage());
        }

        return hashedPwd;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
