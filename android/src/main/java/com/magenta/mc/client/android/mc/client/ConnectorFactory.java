package com.magenta.mc.client.android.mc.client;

/*import javax.microedition.io.Connection;
import javax.microedition.io.Connector;*/

/**
 * Created 02.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class ConnectorFactory {

    private static ConnectionProvider provider = new ConnectionProvider() {
        /*public Connection open(String hostAddress) throws IOException {
            return Connector.open(hostAddress);
        }*/
    };

    public static void use(ConnectionProvider provider) {
        ConnectorFactory.provider = provider;
    }

    public interface ConnectionProvider {
        //Connection open(String hostAddress) throws IOException;
    }

    /*public static Connection open(String hostAddress) throws IOException {
        return provider.open(hostAddress);
    }*/
}
