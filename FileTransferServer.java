import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class FileTransferServer extends UnicastRemoteObject implements FileTransfer {
    private int totalFragmentsReceived = 0;
    private int totalFragments = 10;

    public FileTransferServer() throws RemoteException {
        super();
    }

    @Override
    public void transferFile(byte[] data, String filename, int fragmentIndex) throws RemoteException {
        try (FileOutputStream fos = new FileOutputStream(filename, true)) {
            fos.write(data);
            totalFragmentsReceived++;

            double percentage = (double) totalFragmentsReceived / totalFragments * 100;

            System.out.println("Soy el cliente " + getClientNumber() + " y he recibido el fragmento " +
                    fragmentIndex + " del archivo " + filename + ", resta recibir un " +
                    (100 - percentage) + "% del archivo");

            if (totalFragmentsReceived == totalFragments) {
                System.out.println("Soy el cliente " + getClientNumber() + " y se ha recibido el 100% del archivo " +
                        filename + ".");
                System.out.println("Soy el cliente " + getClientNumber() + " y el archivo " + filename +
                        " se ha procesado en su totalidad y está disponible para reproducción.");
            }
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }

    private int getClientNumber() {
        return 1;
    }

    public static void main(String[] args) throws RemoteException {
        try {
            FileTransferServer server = new FileTransferServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("FileTransfer", server);
            System.out.println("Servidor listo");
        } catch (Exception e) {
            System.err.println("Excepción del servidor: " + e.toString());
            e.printStackTrace();
        }
    }
}






