package mikroe.com.myapplication;

/**
 * Created by Rega on 7.4.2015.
 *
 */

public class RN41 {

    enum SOCKET_STATE {
        NOTHING,
        CREATED,
        CONNECTED
    }

    public static String NAME = null;
    public static Boolean FOUND = false;
    public static SOCKET_STATE SOCKET = SOCKET_STATE.NOTHING;
    public static byte writeBuff[] = {0,0,0,0,0,0,0};
    public static byte readBuff[] = {0,0,0,0,0,0,0,0};
}
