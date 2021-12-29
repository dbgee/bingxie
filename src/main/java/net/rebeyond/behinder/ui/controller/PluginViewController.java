

package net.rebeyond.behinder.ui.controller;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import net.rebeyond.behinder.core.PluginTools;
import net.rebeyond.behinder.core.ShellService;
import net.rebeyond.behinder.dao.ShellManager;
import net.rebeyond.behinder.utils.Utils;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginViewController {
    private ShellManager shellManager;
    @FXML
    private WebView pluginWebView;
    @FXML
    private Button installLocalBtn;
    @FXML
    private Button installNetBtn;
    @FXML
    private Accordion pluginFlowPane;
    @FXML
    private GridPane pluginDetailGridPane;
    @FXML
    private Label pluginNameLabel;
    @FXML
    private Label pluginAuthorLabel;
    @FXML
    private Hyperlink pluginLinkLabel;
//    private Label pluginLinkLabel;
    @FXML
    private Label pluginCommentLabel;
    @FXML
    private ImageView qrcodeImageView;
    private JSONObject shellEntity;
    private ShellService currentShellService;
    private PluginTools pluginTools;
    private List<Thread> workList;
    private Label statusLabel;
    private static Logger logger= LoggerFactory.getLogger(PluginViewController.class);

    public PluginViewController() {
    }

    public void init(ShellService shellService, List<Thread> workList, Label statusLabel, ShellManager shellManager) {
        this.currentShellService = shellService;
        this.shellEntity = shellService.getShellEntity();
        this.workList = workList;
        this.statusLabel = statusLabel;
        this.shellManager = shellManager;
        this.pluginTools=new PluginTools(shellService,pluginWebView,statusLabel,workList);
        this.initPluginView();
    }

    private void initPluginView() {
        this.initPluginInstall();
        PluginTools pluginTools = new PluginTools(this.currentShellService, this.pluginWebView, this.statusLabel, this.workList);
        WebEngine webEngine = this.pluginWebView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == State.SUCCEEDED) {
                JSObject win = (JSObject)webEngine.executeScript("window");
                win.setMember("PluginTools", pluginTools);
            }

        });

//        异步执行插件加载，避免长时间loading
        Runnable runnable=()->{
            Platform.runLater(()->{
                try {
                    PluginViewController.this.loadPlugins();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            });
        };
        Thread thread=new Thread(runnable);
        thread.start();

        this.pluginDetailGridPane.setOpacity(0.0D);
    }

    private void loadPluginDetail(JSONObject pluginObj) {
        this.pluginNameLabel.setText(String.format(this.pluginNameLabel.getText(), pluginObj.getString("name"), pluginObj.getString("version")));
        this.pluginAuthorLabel.setText(String.format(this.pluginAuthorLabel.getText(), pluginObj.getString("author")));
        this.pluginLinkLabel.setText(String.format(this.pluginLinkLabel.getText(), pluginObj.getString("link")));
        this.pluginLinkLabel.setOnAction(event -> {
            String url=pluginObj.getString("link");
            if(!url.toLowerCase().startsWith("https:/") && !url.toLowerCase().startsWith("http://")){
                url="http://"+url;
            }
            String finalUrl = url;
            Platform.runLater(()->{
                try {
                    Utils.openWebpage(new URI(finalUrl));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            });
        });
        this.pluginCommentLabel.setText(String.format(this.pluginCommentLabel.getText(), pluginObj.getString("comment")));
        String pathFormat = "file://%s/Plugins/%s/%s";

        try {
            String qrcodeFilePath = String.format(pathFormat, Utils.getSelfPath(), pluginObj.getString("name"), pluginObj.getString("qrcode"));
            this.qrcodeImageView.setImage(new Image(qrcodeFilePath));
        } catch (Exception var4) {
            this.statusLabel.setText("插件开发者赞赏二维码加载失败");
        }

    }

    private void loadPlugins() throws Exception {
        String scriptType = this.shellEntity.getString("type");
        JSONArray pluginList = this.shellManager.listPlugin(scriptType);

        for(int i = 0; i < pluginList.length(); ++i) {
            JSONObject pluginObj = pluginList.getJSONObject(i);
            this.addPluginBox(pluginObj);
        }

    }

    private boolean checkPluginExist(JSONObject pluginObj) throws Exception {
        String pluginName = pluginObj.getString("name");
        String scriptType = pluginObj.getString("scriptType");
        return this.shellManager.findPluginByName(scriptType, pluginName) != null;
    }

    private void showErrorMessage(String title, String msg) {
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

    private void addPluginBox(JSONObject pluginObj) throws Exception {
        String pluginName = pluginObj.getString("name");
        String pluginCommnet = pluginObj.getString("comment");
        String pathFormat = "file:///%s/Plugins/%s/%s";
        String entryFilePath = String.format(pathFormat, Utils.getSelfPath(), pluginName, pluginObj.getString("entryFile"));
        String iconFilePath = String.format(pathFormat, Utils.getSelfPath(), pluginName, pluginObj.getString("icon"));
        int type = 0;
        String var8 = pluginObj.getString("type");
        byte var9 = -1;
        switch(var8.hashCode()) {
            case -1309148789:
                if (var8.equals("exploit")) {
                    var9 = 1;
                }
                break;
            case 3524221:
                if (var8.equals("scan")) {
                    var9 = 0;
                }
                break;
            case 3565976:
                if (var8.equals("tool")) {
                    var9 = 2;
                }
                break;
            case 106069776:
                if (var8.equals("other")) {
                    var9 = 3;
                }
        }

        switch(var9) {
            case 0:
                type = 0;
                break;
            case 1:
                type = 1;
                break;
            case 2:
                type = 2;
                break;
            case 3:
                type = 3;
        }

        FlowPane flowPane = (FlowPane)((AnchorPane)((TitledPane)this.pluginFlowPane.getPanes().get(type)).getContent()).getChildren().get(0);
        VBox box = new VBox();

        ImageView pluginIcon = new ImageView(new Image(iconFilePath));
        pluginIcon.setFitHeight(30.0D);
        pluginIcon.setPreserveRatio(true);
        Label pluginLabel = new Label(pluginName);
        box.getChildren().add(pluginIcon);
        box.getChildren().add(pluginLabel);
        box.setPadding(new Insets(5.0D));
        box.setAlignment(Pos.CENTER);
        Tooltip tip = new Tooltip();
        tip.setText(pluginCommnet);
        Tooltip.install(box, tip);
        Platform.runLater(()->{
            box.setOnMouseClicked((e) -> {
                Platform.runLater(()->{
                    this.statusLabel.setText("[!]选中插件:"+pluginName);
                });

                if(e.getButton()== MouseButton.PRIMARY){
                    try {
                        this.pluginWebView.getEngine().load(entryFilePath);
                        logger.info("plugin：{} 开始执行,entryFilePath:{}",pluginName,entryFilePath);

                        this.pluginDetailGridPane.setOpacity(1.0D);
                        this.loadPluginDetail(pluginObj);

                        String tempParam="{\n" +
                                "    \"name\":\"scan_host\",\n" +
                                "    \"version\":\"v2.2.1\"\n" +
                                "}";
                        this.pluginTools.sendTask(pluginName,tempParam);
                    } catch (Exception var5) {
                    }
                }else if(e.getButton()==MouseButton.SECONDARY){
                    ContextMenu contextMenu=new ContextMenu();
                    MenuItem updatePlugin=new MenuItem("更新插件");
                    MenuItem unloadPlugin=new MenuItem("卸载插件");

                    unloadPlugin.setOnAction((event)->{
                        try {
                            this.pluginWebView.getEngine().loadContent("插件卸载中");
                            pluginIcon.setImage(null);

                            String pluginPath=Utils.getSelfPath()+File.separator+"Plugins"+File.separator+pluginName;
                            if(!Utils.deleteFileForce(pluginPath)){
                                logger.error("插件目录删除失败,轻稍后重试。");
                                this.pluginWebView.getEngine().loadContent(String.format("%s 卸载失败,请稍后重试。",pluginName));
                                return;
                            }else {
                                boolean delFlag=this.shellManager.delPlugin(pluginName);
                                if (delFlag){
                                    box.getChildren().removeAll(pluginIcon,pluginLabel);
                                    this.pluginWebView.getEngine().loadContent(String.format("%s 卸载成功",pluginName));
                                    logger.debug("插件已卸载:{}",pluginName);

                                    box.getChildren().removeAll(pluginIcon,pluginLabel);
                                }
                            }

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });

                    updatePlugin.setOnAction((event)->{
                        Utils.showInfoMessage("操作提示","目前近支持本地安装，请先卸载，再重装即可。");
                    });

                    contextMenu.getItems().addAll(updatePlugin,unloadPlugin);
                    box.setOnContextMenuRequested((contextEvent)->{
                        contextMenu.show(pluginIcon,e.getScreenX(),e.getScreenY());
                        contextMenu.show(pluginLabel,e.getScreenX(),e.getScreenY());
                    });

                }
            });
        });
        box.setOnMouseEntered((e) -> {
            VBox v = (VBox)e.getSource();
            v.setStyle("-fx-background-color:blue");
        });
        box.setOnMouseExited((e) -> {
            VBox v = (VBox)e.getSource();
            v.setStyle("-fx-background-color:transparent");
        });
        flowPane.getChildren().add(box);
        ((TitledPane)this.pluginFlowPane.getPanes().get(type)).setExpanded(true);
    }

    private void initPluginInstall() {
        this.installLocalBtn.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("请选择需要安装的插件包");
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter[]{new ExtensionFilter("All ZIP Files", new String[]{"*.zip"})});
            File pluginFile = fileChooser.showOpenDialog(this.pluginFlowPane.getScene().getWindow());

            try {
                JSONObject pluginEntity = Utils.parsePluginZip(pluginFile.getAbsolutePath());
                if (this.checkPluginExist(pluginEntity)) {
                    this.showErrorMessage("错误", "安装失败，插件已存在");
                    return;
                }

                this.addPluginBox(pluginEntity);
                this.shellManager.addPlugin(pluginEntity.getString("name"), pluginEntity.getString("version"), pluginEntity.getString("entryFile"), pluginEntity.getString("scriptType"), pluginEntity.getString("type"), pluginEntity.getInt("isGetShell"), pluginEntity.getString("icon"), pluginEntity.getString("author"), pluginEntity.getString("link"), pluginEntity.getString("qrcode"), pluginEntity.getString("comment"));
                this.statusLabel.setText("插件安装成功。");
                this.pluginWebView.getEngine().loadContent(String.format("%s 安装成功",pluginEntity.getString("name")));
            } catch (Exception var5) {
                var5.printStackTrace();
                this.statusLabel.setText("插件安装失败:" + var5.getMessage());
            }

        });
    }
}
