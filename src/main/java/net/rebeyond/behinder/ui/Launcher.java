package net.rebeyond.behinder.ui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.net.URI;
import javax.swing.JOptionPane;
import net.rebeyond.behinder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
    private static Logger logger= LoggerFactory.getLogger(Launcher.class);
    public Launcher() {
    }

    public static void main(String[] args) {

        try {
            ClassLoader.getSystemClassLoader().loadClass("javafx.application.Application");
            logger.info("程序加载中...");
            Main.main(args);
        } catch (ClassNotFoundException var9) {
            try {
                String selfPath = Utils.getSelfPath();
                String javafxPath = selfPath + File.separator + "lib";
                String cmd = "\"" + System.getProperty("java.home") + File.separator + "bin" + File.separator + "java\" --module-path \"" + javafxPath + "\" --add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.graphics,javafx.web -jar";
                cmd = cmd + " " + Utils.getSelfJarPath();
                Process p;
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    p=Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", cmd});
                } else {
                    p = Runtime.getRuntime().exec(new String[]{"bash", "-c", cmd});
                }

                if (p.waitFor() == 1) {
                    Utils.setClipboardString(cmd);
                    int response = JOptionPane.showConfirmDialog((Component)null, "本地未检测到JavaFX环境，推荐使用Java1.8 版本，Java11以后的版本不再集成Javafx，需要单独下载\n下载后可将javaFX SDK的lib目录拷贝至冰蝎同目录下，冰蝎会自动调用(如果调用失败，请同时配置JavaFX 的bin 目录到系统PATH 环境变量中)；也可通过命令行手动指定SDK目录(命令已拷贝至系统剪切板,可以黏贴使用)\n是否打开网页下载？", "错误", 0);
                    if (response == 0) {
                        String url = "https://openjfx.cn/dl/";
                        openWebpage(new URI(url));
                    }
                }
            } catch (Exception var8) {
                logger.info("程序启动失败，请检查Java 、JavaFX 是否已正确配置。",var8);
            }
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
}
