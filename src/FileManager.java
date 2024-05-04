import java.util.ArrayList;
import java.util.Iterator;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.io.*;
import java.util.zip.*;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class FileManager {

    private ArrayList<String> filelist;
    private String destinationPath;

    public void setFileList(ArrayList<String> filelist) {
        this.filelist = filelist;
    }

    public void setDestinationPath(String destPath) {
        this.destinationPath = destPath;
    }

    void compressioneFile(ArrayList<String> filelist, String destPath) throws Exception {
        this.filelist = filelist;
        this.destinationPath = destPath;

        compressioneFile();
    }

    void cryptFile(String pathfile) {
        String password = generateSecurePassword();

        File fileToCrypt = new File(pathfile);

        execCryptDecrypt(Cipher.ENCRYPT_MODE, password, fileToCrypt, fileToCrypt);

        execCryptDecrypt(Cipher.DECRYPT_MODE, password, fileToCrypt, fileToCrypt);
    }

    private void compressioneFile() throws Exception {

        if (filelist == null) {
            throw new Exception("filelist nullo");
        }

        if (destinationPath == null) {
            throw new Exception("destinationpath nullo");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_yyyy");
        LocalDateTime now = LocalDateTime.now();

        String zipname = destinationPath + "\\" + dtf.format(now) + ".zip";

        try {
            File zip = new File(zipname);
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));

            Iterator<String> iterator = filelist.iterator();

            try {
                while (iterator.hasNext()) {
                    ZipEntry ze = new ZipEntry(iterator.next());
                    out.putNextEntry(ze);
                }

            } catch (IOException e) {
                System.out.println("Error in creating the zip file.");
            }

            try {
                out.close();
            } catch (IOException e) {
            }

            cryptFile(zipname);

        } catch (FileNotFoundException e) {
            System.out.println("The output file was not found.");
        }
    }

    private static String generateSecurePassword() {

        CharacterRule LCR = new CharacterRule(EnglishCharacterData.LowerCase);
        LCR.setNumberOfCharacters(6);

        CharacterRule UCR = new CharacterRule(EnglishCharacterData.UpperCase);
        UCR.setNumberOfCharacters(6);

        CharacterRule DR = new CharacterRule(EnglishCharacterData.Digit);
        DR.setNumberOfCharacters(6);

        CharacterRule SR = new CharacterRule(EnglishCharacterData.Special);
        SR.setNumberOfCharacters(6);

        PasswordGenerator passwordGenerator = new PasswordGenerator();

        return passwordGenerator.generatePassword(24, SR, LCR, UCR, DR);
    }

    private static void execCryptDecrypt(int cipherMode, String key, File inputFile, File outputFile) {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(cipherMode, secretKey);

            byte[] inputBytes;

            try (FileInputStream inputStream = new FileInputStream(inputFile)) {
                inputBytes = new byte[(int) inputFile.length()];
                inputStream.read(inputBytes);
            }

            byte[] outputBytes = cipher.doFinal(inputBytes);

            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(outputBytes);
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
        }
    }
}
