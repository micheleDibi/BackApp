import java.util.ArrayList;
import java.util.Iterator;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;

import java.io.*;
import java.util.zip.*;

public class FileManager {
    
    ArrayList<String> filelist;
    String destinationPath;
    private static int sChunk = 8192;

    FileManager(ArrayList<String> filelist, String destinationPath) {
        this.filelist = filelist;
        this.destinationPath = destinationPath;

        compressFile();
    }

    int compressFile() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_yyyy");
        LocalDateTime now = LocalDateTime.now();

        String zipname = destinationPath + "\\" + dtf.format(now) + ".7z";
        GZIPOutputStream zipout = null;

        System.out.println(zipname);

        try {
            FileOutputStream out = new FileOutputStream(zipname);
            zipout = new GZIPOutputStream(out);
        }
        catch (IOException e) {
            System.err.println("Errore nella creazione del file compresso: " + e.getMessage());
        }

        byte[] buffer = new byte[sChunk];

        try {
            Iterator<String> iterator = filelist.iterator();
            
            while(iterator.hasNext()) {

                String filename = iterator.next();
                // System.out.println(filename);
                FileInputStream in = new FileInputStream(filename);
                int length;

                while ((length = in.read(buffer, 0, sChunk)) != -1) {
                    zipout.write(buffer, 0, length);
                }

                in.close();
            }
        }
        catch (IOException e) {
            System.err.println("Errore durante l'inserimento dei file nel file compresso: " + e.getMessage());
        }

        try {
            zipout.close();
        }
        catch (IOException e) {
            System.err.println("Errore durante la chiusura del file compresso: " + e.getMessage());
        }

        return 1;

    }
}
