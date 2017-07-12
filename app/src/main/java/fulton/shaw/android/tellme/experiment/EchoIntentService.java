package fulton.shaw.android.tellme.experiment;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class EchoIntentService extends IntentService {

    public EchoIntentService() {
        super("EchoIntentService");
    }

    /**
     *
     * @param intent must provides extra info string: port to denote which port to listen on.But the default value is 5533
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final int port = intent.getIntExtra("PORT",5533);
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while(true) {
                    Socket client = serverSocket.accept();
                    client.getOutputStream().write("HTTP/1.1 200 OK\r\nContent-Length: 5\r\n\r\n".getBytes("ASCII"));
                    client.getOutputStream().write("Hello".getBytes("ASCII"));
                    client.getOutputStream().flush();
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
