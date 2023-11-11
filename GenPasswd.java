import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class GenPasswd {

    public static void main(String[] args) {
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try{

            System.out.println("Enter User ID: ");
            String userId = br.readLine();

            while(!isUserIdValid(userId)){
                System.out.println("The ID should only contain lower-case letters");
                System.out.println("Re-Enter User ID: ");

                userId = br.readLine();
            }

            while(isUserPresent(userId)){
                System.out.println("The ID already exists");
                
                System.out.println("Re-Enter User ID: ");

                userId = br.readLine();
            }

            System.out.println("Enter Password: ");
            String pwd = br.readLine();

            while(!isPassWordValid(pwd)){
                System.out.println("The password should contain at least 8 characters");
                System.out.println("Re-Enter Password: ");

                pwd = br.readLine();
            }

            String hashedPwd = generateHashedString(pwd);
            writeFile(userId, hashedPwd);

            System.out.println("Would you like to enter another ID and Password (Y/N)?");
            String reEnter = br.readLine().toUpperCase();

            while(reEnter.equals("Y")){
                
                System.out.println("Enter User ID: ");
                userId = br.readLine();

                while(!isUserIdValid(userId)){
                    System.out.println("The ID should only contain lower-case letters");
                    System.out.println("Re-Enter User ID: ");

                    userId = br.readLine();
                }

                if(!isUserPresent(userId)){
                    System.out.println("Enter Password: ");
                    pwd = br.readLine();

                    while(!isPassWordValid(pwd)){
                        System.out.println("The password should contain at least 8 characters");
                        System.out.println("Re-Enter Password: ");

                        pwd = br.readLine();
                    }

                    hashedPwd = generateHashedString(pwd);
                    writeFile(userId, hashedPwd);
                }else{
                    System.out.println("The ID already exists");
                }

                System.out.println("Would you like to enter another ID and Password (Y/N)?");
                reEnter = br.readLine().toUpperCase();
            }



        }catch(Exception ex){
            System.err.println("Exception: " + ex.getMessage());
        }
        
    }

    private static boolean isUserIdValid(String userId){

        boolean isValid = false;

        if(userId.matches("[a-z]+")){
            isValid =  true;
        }

        return isValid;
    }

     private static boolean isPassWordValid(String pwd){

        boolean isValid = false;

        if(pwd.length() >= 8){
            isValid =  true;
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

    private static void writeFile(String userId, String hashedPwd){

        // Specify the file path
        String filePath = "hashpasswd.txt";

        // Try-with-resources to automatically close the resources
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(userId + " " + hashedPwd + " " + Instant.now());
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static boolean isUserPresent(String userId){

        boolean isUserPresent = false;

        String filePath = "hashpasswd.txt";

        Path path = FileSystems.getDefault().getPath(filePath);

        if(Files.exists(path)){
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] strArray = line.split("\\s+");

                    if(strArray[0].equals(userId)){
                        return true;
                    }
                }
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
        return isUserPresent;
    }

}