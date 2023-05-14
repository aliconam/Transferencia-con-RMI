import java.io.*;
import java.util.*;

public class FileFragmenter {
    public static void main(String[] args) throws IOException {
        String filename = "Examen3.mp4";
        File file = new File(filename);
        long fileSize = file.length();
        long chunkSize = fileSize / 10;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[(int) chunkSize];
            int bytesRead = 0;
            int index = 1;

            List<byte[]> fragments = new ArrayList<>();

            while ((bytesRead = bis.read(buffer)) != -1) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(buffer, 0, bytesRead);
                byte[] chunk = baos.toByteArray();
                fragments.add(chunk);
                index++;
            }

            Collections.shuffle(fragments); // Mezcla los fragmentos en orden aleatorio

            // Aquí puedes llamar a los clientes y enviar los fragmentos
            for (int i = 0; i < fragments.size(); i++) {
                byte[] fragment = fragments.get(i);
                String clientName = "Cliente " + (i + 1);
                sendFragmentToClient(fragment, clientName, filename);
            }
        }
    }

    private static void sendFragmentToClient(byte[] fragment, String clientName, String filename) {
        
    }
}





