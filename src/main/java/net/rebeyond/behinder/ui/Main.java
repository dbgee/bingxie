

package net.rebeyond.behinder.ui;

import java.io.ByteArrayInputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.rebeyond.behinder.core.Constants;
import net.rebeyond.behinder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    private static Logger logger= LoggerFactory.getLogger(Main.class);
    public Main() {
    }


    public void start(Stage primaryStage) throws Exception {
        logger.info("++++++++++++++++++++++++++++++++++++++++");
        logger.info("冰蝎 {} 启动成功",Constants.VERSION);
        logger.info("++++++++++++++++++++++++++++++++++++++++");
        Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("Main.fxml"));
        primaryStage.setTitle(String.format("冰蝎%s 动态二进制加密Web远程管理客户端", Constants.VERSION));
        primaryStage.getIcons().add(new Image(new ByteArrayInputStream(Utils.getResourceData("net/rebeyond/behinder/resource/logo.jpg"))));
        primaryStage.setScene(new Scene(root, 1200.0D, 600.0D));
        primaryStage.show();
        Utils.disableSslVerification();
    }

    public void stop() throws Exception {
        super.stop();
        logger.info("程序退出");
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
