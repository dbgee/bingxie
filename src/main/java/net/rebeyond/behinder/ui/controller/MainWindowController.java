

package net.rebeyond.behinder.ui.controller;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.net.ConnectException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.rebeyond.behinder.core.Constants;
import net.rebeyond.behinder.core.ShellService;
import net.rebeyond.behinder.dao.ShellManager;
import net.rebeyond.behinder.utils.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindowController {
    @FXML
    private GridPane mainGridPane;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private WebView basicInfoView;
    @FXML
    private TextField urlText;
    @FXML
    private Label statusLabel;
    @FXML
    private Label connStatusLabel;
    @FXML
    private Label versionLabel;
    @FXML
    private TextArea sourceCodeTextArea;
    @FXML
    private TextArea sourceResultArea;
    @FXML
    private Button runCodeBtn;
    @FXML
    private Tab realCmdTab;
    private JSONObject shellEntity;
    private ShellService currentShellService;
    private ShellManager shellManager;
    @FXML
    private AnchorPane pluginView;
    @FXML
    private PluginViewController pluginViewController;
    @FXML
    private FileManagerViewController fileManagerViewController;
    @FXML
    private ReverseViewController reverseViewController;
    @FXML
    private DatabaseViewController databaseViewController;
    @FXML
    private CmdViewController cmdViewController;
    @FXML
    private RealCmdViewController realCmdViewController;
    @FXML
    private TunnelViewController tunnelViewController;
    @FXML
    private UpdateInfoViewController updateInfoViewController;
    @FXML
    private UserCodeViewController userCodeViewController;
    @FXML
    private MemoViewController memoViewController;
    private Map<String, String> basicInfoMap = new HashMap();
    private List<Thread> workList = new ArrayList();
    private static final Logger logger= LoggerFactory.getLogger(MainWindowController.class);

    public MainWindowController() {
    }

    public void initialize() {
        this.initControls();
    }

    public List<Thread> getWorkList() {
        return this.workList;
    }

    private void initControls() {
        this.statusLabel.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                MainWindowController.this.statusLabel.setTooltip(new Tooltip(t1));
            }
        });
        this.versionLabel.setText(String.format(this.versionLabel.getText(), Constants.VERSION));
        this.urlText.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                this.statusLabel.setText("正在获取基本信息，请稍后……");
                this.connStatusLabel.setText("正在连接");
                logger.debug("shell[{}] 连接中...",shellEntity.get("id"));

                WebEngine webengine = this.basicInfoView.getEngine();
                Runnable runner = () -> {
                    try {
                        this.doConnect();
                        int randStringLength = (new SecureRandom()).nextInt(3000);
                        String randString = Utils.getRandomString(randStringLength);
                        JSONObject basicInfoObj = new JSONObject(this.currentShellService.getBasicInfo(randString));
                        final String basicInfoStr = new String(Base64.decode(basicInfoObj.getString("basicInfo")), "UTF-8");
                        String driveList = (new String(Base64.decode(basicInfoObj.getString("driveList")), "UTF-8")).replace(":\\", ":/");
                        String currentPath = new String(Base64.decode(basicInfoObj.getString("currentPath")), "UTF-8");
                        String osInfo = (new String(Base64.decode(basicInfoObj.getString("osInfo")), "UTF-8")).toLowerCase();
                        String arch = (new String(Base64.decode(basicInfoObj.getString("arch")), "UTF-8")).toLowerCase();
                        this.basicInfoMap.put("basicInfo", basicInfoStr);
                        this.basicInfoMap.put("driveList", driveList);
                        this.basicInfoMap.put("currentPath", Utils.formatPath(currentPath));
                        this.basicInfoMap.put("workPath", Utils.formatPath(currentPath));
                        this.basicInfoMap.put("osInfo", osInfo.replace("winnt", "windows"));
                        this.basicInfoMap.put("arch", arch);
                        this.shellManager.updateOsInfo(this.shellEntity.getInt("id"), osInfo);
                        Platform.runLater(new Runnable() {
                            public void run() {
                                webengine.loadContent(basicInfoStr);

                                try {
                                    MainWindowController.this.cmdViewController.init(MainWindowController.this.currentShellService, MainWindowController.this.workList, MainWindowController.this.statusLabel, MainWindowController.this.basicInfoMap);
                                    MainWindowController.this.realCmdViewController.init(MainWindowController.this.currentShellService, MainWindowController.this.workList, MainWindowController.this.statusLabel, MainWindowController.this.basicInfoMap);
                                    MainWindowController.this.pluginViewController.init(MainWindowController.this.currentShellService, MainWindowController.this.workList, MainWindowController.this.statusLabel, MainWindowController.this.shellManager);
                                    MainWindowController.this.fileManagerViewController.init(MainWindowController.this.currentShellService, MainWindowController.this.workList, MainWindowController.this.statusLabel, MainWindowController.this.basicInfoMap);
                                    MainWindowController.this.reverseViewController.init(MainWindowController.this.currentShellService, MainWindowController.this.workList, MainWindowController.this.statusLabel, MainWindowController.this.basicInfoMap);
                                    MainWindowController.this.databaseViewController.init(MainWindowController.this.currentShellService, MainWindowController.this.workList, MainWindowController.this.statusLabel);
                                    MainWindowController.this.tunnelViewController.init(MainWindowController.this.currentShellService, MainWindowController.this.workList, MainWindowController.this.statusLabel, MainWindowController.this.basicInfoMap);
                                    MainWindowController.this.updateInfoViewController.init(MainWindowController.this.currentShellService, MainWindowController.this.workList, MainWindowController.this.statusLabel);
                                    MainWindowController.this.userCodeViewController.init(MainWindowController.this.currentShellService, MainWindowController.this.workList, MainWindowController.this.statusLabel);
                                    MainWindowController.this.memoViewController.init(MainWindowController.this.currentShellService, MainWindowController.this.workList, MainWindowController.this.statusLabel, MainWindowController.this.shellManager);
                                } catch (Exception var2) {
                                }

                                MainWindowController.this.connStatusLabel.setText("已连接");
                                MainWindowController.this.connStatusLabel.setTextFill(Color.BLUE);
                                MainWindowController.this.statusLabel.setText("[OK]连接成功，基本信息获取完成。");
                                logger.debug("shell 打开成功");

                            }
                        });
                        this.shellManager.setShellStatus(this.shellEntity.getInt("id"), Constants.SHELL_STATUS_ALIVE);
                        Runnable worker = new Runnable() {
                            public void run() {
                                while (true) {
                                    try {
                                        Thread.sleep((long) (((new Random()).nextInt(5) + 5) * 60 * 1000));
                                        int randomStringLength = (new SecureRandom()).nextInt(3000);
                                        MainWindowController.this.currentShellService.echo(Utils.getRandomString(randomStringLength));
                                    } catch (Exception e) {
                                        if (e instanceof InterruptedException) {
                                            return;
                                        }

                                        Platform.runLater(() -> {
                                            Utils.showErrorMessage("提示", "由于您长时间未操作，当前连接会话已超时，请重新打开该网站。");
                                        });
                                        return;
                                    }
                                }
                            }
                        };
                        Thread keepAliveWorker = new Thread(worker);
                        keepAliveWorker.start();
                        this.workList.add(keepAliveWorker);
                    }catch (ConnectException connectException){
                        Platform.runLater(new Runnable() {
                            public void run() {
                                logger.debug("shell 打开失败");

                                MainWindowController.this.connStatusLabel.setText("连接失败");
                                MainWindowController.this.connStatusLabel.setTextFill(Color.RED);
                                MainWindowController.this.statusLabel.setText("[ERROR]连接失败：" + "请检查与目标URL的网络连接情况 " + ":" + connectException.getMessage());

                                try {
                                    MainWindowController.this.shellManager.setShellStatus(MainWindowController.this.shellEntity.getInt("id"), Constants.SHELL_STATUS_DEAD);
                                } catch (Exception var2) {
                                }

                            }
                        });
                    } catch (final Exception var12) {
                        Platform.runLater(new Runnable() {
                            public void run() {
                                logger.debug("shell 打开失败");

                                MainWindowController.this.connStatusLabel.setText("连接失败");
                                MainWindowController.this.connStatusLabel.setTextFill(Color.RED);
                                MainWindowController.this.statusLabel.setText("[ERROR]连接失败：" + var12.getClass().getName() + ":" + var12.getMessage());

                                try {
                                    MainWindowController.this.shellManager.setShellStatus(MainWindowController.this.shellEntity.getInt("id"), Constants.SHELL_STATUS_DEAD);
                                } catch (Exception var2) {
                                }

                            }
                        });
                    }

                };
                Thread workThrad = new Thread(runner);
                this.workList.add(workThrad);
                workThrad.start();
            } catch (Exception exception) {
                logger.debug("shell 打开失败:{}",exception.toString());
            }

        });
        this.mainTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                String tabId = newTab.getId();
                byte var6 = -1;
                switch(tabId.hashCode()) {
                    case -1356954629:
                        if (tabId.equals("cmdTab")) {
                            var6 = 0;
                        }
                        break;
                    case 0:
                        if (tabId.equals("")) {
                            var6 = 1;
                        }
                }

                switch(var6) {
                    case 0:
                    case 1:
                    default:
                }
            }
        });
    }

    private void doConnect() throws Exception {
        boolean connectResult = this.currentShellService.doConnect();
    }

    public void init(JSONObject shellEntity, ShellManager shellManager, Map<String, Object> currentProxy) throws Exception {
        this.shellEntity = shellEntity;
        this.shellManager = shellManager;
        this.currentShellService = new ShellService(shellEntity);
        ShellService var10000 = this.currentShellService;
        ShellService.setProxy(currentProxy);
        this.urlText.setText(shellEntity.getString("url"));
        this.initTabs();
    }

    private void initTabs() {
        if (this.shellEntity.getString("type").equals("asp")) {
            Iterator var1 = this.mainTabPane.getTabs().iterator();

            while(true) {
                Tab tab;
                do {
                    if (!var1.hasNext()) {
                        return;
                    }

                    tab = (Tab)var1.next();
                } while(!tab.getId().equals("realCmdTab") && !tab.getId().equals("tunnelTab") && !tab.getId().equals("reverseTab"));

                tab.setDisable(true);
            }
        }
    }
}
