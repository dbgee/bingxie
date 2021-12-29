

package net.rebeyond.behinder.ui.controller;

import java.net.URI;
import java.util.Base64;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import net.rebeyond.behinder.core.Constants;
import net.rebeyond.behinder.core.ShellService;
import net.rebeyond.behinder.dao.ShellManager;
import net.rebeyond.behinder.utils.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateInfoViewController {
    private ShellManager shellManager;
    @FXML
    private TextArea realCmdTextArea;
    @FXML
    private TextField shellPathText;
    @FXML
    private Button realCmdBtn;
    private ShellService currentShellService;
    private JSONObject shellEntity;
    private List<Thread> workList;
    private Label statusLabel;
    @FXML
    private WebView updateInfoWebview;
    @FXML
    private TextArea showRequestHeader;
    @FXML
    private TextArea showResponseHeader;
    @FXML
    private TextField address;
    @FXML
    private Button browerIntranet;

    private Logger logger= LoggerFactory.getLogger(UpdateInfoViewController.class);

    public UpdateInfoViewController() {
    }

    public void init(ShellService shellService, List<Thread> workList, Label statusLabel) {
        this.currentShellService = shellService;
        this.shellEntity = shellService.getShellEntity();
        this.workList = workList;
        this.statusLabel = statusLabel;
        this.initUpdateInfoView();

    }

    private void initUpdateInfoView() {
//        this.checkUpdate();
//        this.loadWebPage();

        String currentURL=this.currentShellService.currentUrl;
        address.setText(currentURL);

    }

    public void loadWebPage(){
        browerIntranet.setDisable(true);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String targetUrl="";
                        try {
                            String temp=address.getText();
                            if(!temp.trim().equals("")){
                                if(temp.substring(0,4).toLowerCase().startsWith("http")){
                                    targetUrl=temp;
                                }
                            }else{
                                targetUrl="http://127.0.0.1/1";
                            }
                            JSONObject resData=new JSONObject(currentShellService.getPageInfo(targetUrl));
                            String content=new String(Base64.getDecoder().decode(resData.getString("body")),"UTF-8");
                            String reqHeader=new String(Base64.getDecoder().decode(resData.getString("reqHeader")),"UTF-8");
                            String resHeader=new String(Base64.getDecoder().decode(resData.getString("resHeader")),"UTF-8");
                            showRequestHeader.setText(reqHeader);
                            showResponseHeader.setText(resHeader);

                            if(content.startsWith("{")){
                                updateInfoWebview.getEngine().loadContent(content,"application/json");
                            }else{
                                updateInfoWebview.getEngine().loadContent(content);
                            }
                            browerIntranet.setDisable(false);
                            logger.info("target webpage loaded");
                            UpdateInfoViewController.this.statusLabel.setText("[+]内网漫步成功。");

                        } catch (Exception e) {
                            UpdateInfoViewController.this.statusLabel.setText("[-]内网漫步失败。");
                            browerIntranet.setDisable(false);
                            e.printStackTrace();
                        }
                    }
                });
            }
        };



        Thread workThrad = new Thread(runnable);
        this.workList.add(workThrad);
        workThrad.start();

    }


    private void checkUpdate() {
        Runnable runner = () -> {
            try {
                String updateInfoText = Utils.sendGetRequest(Constants.UPDATE_URL, "",5000);
                JSONObject updateInfoObj = new JSONObject(updateInfoText);
                String latestVersion=updateInfoObj.getString("version");
                logger.info("version="+latestVersion);
                if (Utils.compareVersion(Constants.VERSION,latestVersion)) {
                    Platform.runLater(() -> {
                        this.statusLabel.setOnMouseClicked((event) -> {
                            if (this.statusLabel.getText().startsWith("发现新版本")) {
                                try {
                                    Utils.openWebpage(new URI(Constants.DOWNLOAD_URL));
                                } catch (Exception var3) {
                                    var3.printStackTrace();
                                }
                            }

                        });
                    });
                }else {
                    Utils.showInfoMessage("通知信息","当前版本为："+Constants.VERSION+"\n 恭喜，已经最新，无需升级。");
                }

            } catch (Exception var4) {
                logger.info(var4.toString());
                Platform.runLater(() -> {
                    this.statusLabel.setText("检查更新出错😒,请检查网络连接情况。");
                });
            }

        };
        Thread workThrad = new Thread(runner);
        this.workList.add(workThrad);
        workThrad.start();
    }
}
