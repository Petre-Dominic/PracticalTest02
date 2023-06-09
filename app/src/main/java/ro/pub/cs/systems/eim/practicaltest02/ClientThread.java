package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread{

    private final String address;
    private final int port;
    private final String body;
    private Socket socket;


    private final TextView textViewResult;

    public ClientThread(String address, int port, String body, TextView textViewResult) {
        this.address = address;
        this.port = port;
        this.body = body;
        this.textViewResult = textViewResult;

        Log.i(Constants.TAG, "Create new ClientThread for body: " + body);
    }

    @Override
    public void run() {
        try {
            // tries to establish a socket connection to the server
            socket = new Socket(address, port);

            // gets the reader and writer for the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // sends the city and information type to the server
            printWriter.print(body);
            printWriter.flush();
            String result;

            // reads the weather information from the server
            while ((result = bufferedReader.readLine()) != null) {
                final String finalizedResult = result;

                // updates the UI with the weather information. This is done using postt() method to ensure it is executed on UI thread
                textViewResult.post(() -> textViewResult.setText(finalizedResult));
            }
        } // if an exception occurs, it is logged
        catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            ioException.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    // closes the socket regardless of errors or not
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    ioException.printStackTrace();
                }
            }
        }
    }

}
