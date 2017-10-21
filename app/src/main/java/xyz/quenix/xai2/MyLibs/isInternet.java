package xyz.quenix.xai2.MyLibs;

import java.net.HttpURLConnection;
import java.net.URL;

public class isInternet {

    public static String HOST = "http://rapoo.mysit.ru/";

    public static Boolean active()
    {
        Boolean result = false;
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(HOST).openConnection();
            con.setRequestMethod("HEAD");
            result = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String host()
    {
        return HOST;
    }

}
