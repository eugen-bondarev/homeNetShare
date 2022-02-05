package common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Address {
    public static String getName() {
        try {
            return InetAddress.getLocalHost().getHostName().toString();
        } catch (UnknownHostException exception) {
            exception.printStackTrace();
        }
        return "unknown-name";
    }

    public static boolean isValidIP(String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static String getAddressInHomeNet() {
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    String currentAddress = ((InetAddress) ee.nextElement()).getHostAddress();
                    if (currentAddress.contains("192.168")) {
                        return currentAddress;
                    }
                }
            }
        }
        catch (SocketException exception) {
            exception.printStackTrace();
        }
        return "192.168.0.1";
    }
}
