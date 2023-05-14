import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileTransferClient {
    private static final int TOTAL_FRAGMENTS = 10;
    private static List<Integer> fragmentOrder;

    public static void main(String[] args) throws RemoteException, NotBoundException, IOException {
        String filename = "Examen3.mp4";
        File file = new File(filename);
        long fileSize = file.length();
        long chunkSize = fileSize / TOTAL_FRAGMENTS;

        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        FileTransfer stub = (FileTransfer) registry.lookup("FileTransfer");

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int bytesRead;
            int index = 1;

            fragmentOrder = generateRandomOrder(TOTAL_FRAGMENTS);

            for (int i = 0; i < fragmentOrder.size(); i++) {
                int fragmentIndex = fragmentOrder.get(i);
                String clientName = "Cliente " + (i + 1);
                try {
                    sendFragmentToClient(bis, chunkSize, clientName, filename, fragmentIndex, stub);
                } catch (RemoteException e) {
                    System.err.println("Error al enviar el fragmento al cliente " + clientName + ": " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Error de E/S al enviar el fragmento al cliente " + clientName + ": " + e.getMessage());
                }
            }
        }
    }

    private static void sendFragmentToClient(BufferedInputStream bis, long chunkSize, String clientName,
                                             String filename, int fragmentIndex, FileTransfer stub) throws IOException, RemoteException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192]; // Tamaño del buffer de lectura

        long bytesRemaining = chunkSize;
        int bytesRead;

        while (bytesRemaining > 0 && (bytesRead = bis.read(buffer, 0, (int) Math.min(buffer.length, bytesRemaining))) != -1) {
            baos.write(buffer, 0, bytesRead);
            bytesRemaining -= bytesRead;
        }

        byte[] fragment = baos.toByteArray();
        stub.transferFile(fragment, filename, fragmentIndex);
    }

    private static List<Integer> generateRandomOrder(int totalFragments) {
        List<Integer> order = new ArrayList<>();
        for (int i = 1; i <= totalFragments; i++) {
            order.add(i);
        }
        Collections.shuffle(order);
        return order;
    }

    public int getTotalReceivedFragments() {
        return 0;
    }

    public int getClientNumber() {
        return 0;
    }
}


