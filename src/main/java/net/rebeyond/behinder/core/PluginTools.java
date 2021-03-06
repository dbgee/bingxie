

package net.rebeyond.behinder.core;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import net.rebeyond.behinder.utils.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import sun.plugin2.main.server.Plugin;

public class PluginTools {
    private ShellService currentShellService;
    private Label statusLabel;
    private WebView pluginWebview;
    private JSONObject shellEntity;
    private Map<String, String> taskMap = new HashMap();
    private List<Thread> workList;
    private static Logger logger= LoggerFactory.getLogger(PluginTools.class);

    public PluginTools(ShellService shellService, WebView pluginWebview, Label statusLabel, List<Thread> workList) {
        this.currentShellService = shellService;
        this.shellEntity = shellService.shellEntity;
        this.workList = workList;
        this.pluginWebview = pluginWebview;
        this.statusLabel = statusLabel;
    }

    public PluginTools(ShellService shellService, Label statusLabel, List<Thread> workList) {
        this.currentShellService = shellService;
        this.shellEntity = shellService.shellEntity;
        this.workList = workList;
        this.statusLabel = statusLabel;
    }

    public void sendTask(String pluginName, String paramStr) throws Exception {
        String type = this.shellEntity.getString("type");
        if (type.equals("php")) {
            type = "php";
        }

//        String payloadPath = String.format("/Users/rebeyond/Documents/Behinder/plugin/%s/payload/%s.payload", pluginName, type);
        String payloadPath = String.format("/Users/rebeyond/Documents/Behinder/plugin/%s/payload/%s.payload", pluginName, type);
        payloadPath="D:\\androidProject\\bingxie01\\src\\main\\resources\\net\\rebeyond\\payload\\php\\BasicInfo.php";
        JSONObject paramObj = new JSONObject(paramStr);
        Map<String, String> params = Utils.jsonToMap(paramObj);
        params.put("taskID", pluginName);
        logger.debug("taskID:{}",pluginName);
        this.statusLabel.setText("????????????????????????");
        String finalPayloadPath = payloadPath;
        Runnable runner = () -> {
            try {
                JSONObject resultObj = this.currentShellService.submitPluginTask(pluginName, finalPayloadPath, params);
                String status = resultObj.getString("status");
                String msg = resultObj.getString("msg");
                Platform.runLater(() -> {
                    this.statusLabel.setText(msg);
                });
            } catch (Exception var7) {
                Platform.runLater(() -> {
                    this.statusLabel.setText("??????????????????");
                });
            }

        };
        Thread workThrad = new Thread(runner);
        this.workList.add(workThrad);
        workThrad.start();
    }

    public void sendTaskBackground(String pluginName, Map<String, String> params, PluginSubmitCallBack callBack) throws Exception {
        String type = this.shellEntity.getString("type");
        if (type.equals("jsp")) {
            type = "java";
        }

        String payloadPath = String.format("/Users/rebeyond/Documents/Behinder/plugin/%s/payload/%s.payload", pluginName, type);
        params.put("taskID", pluginName);
        Runnable runner = () -> {
            try {
                JSONObject resultObj = this.currentShellService.submitPluginTask(pluginName, payloadPath, params);
                String status = resultObj.getString("status");
                String msg = resultObj.getString("msg");
                callBack.onPluginSubmit(status, msg);
            } catch (Exception var8) {
                callBack.onPluginSubmit("fail", var8.getMessage());
            }

        };
        Thread workThrad = new Thread(runner);
        this.workList.add(workThrad);
        workThrad.start();
    }

    public String queryTaskList() {
        String result = "";
        return result;
    }

    public String queryTask(String taskName) {
        String result = "";
        return result;
    }

    public void getTaskResult(String pluginName) {
        this.statusLabel.setText("????????????????????????????????????");
        Runnable runner = () -> {
            try {
                JSONObject resultObj = this.currentShellService.getPluginTaskResult(pluginName);
                String status = resultObj.getString("status");
                String msg = resultObj.getString("msg");
                JSONObject msgObj = new JSONObject(msg);
                String pluginResult = new String(Base64.getDecoder().decode(msgObj.getString("result")), "UTF-8");
                String pluginRunning = msgObj.getString("running");
                Platform.runLater(() -> {
                    if (status.equals("success")) {
                        this.statusLabel.setText("??????????????????");

                        try {
                            this.pluginWebview.getEngine().executeScript(String.format("onResult('%s','%s','%s')", status, pluginResult, pluginRunning));
                        } catch (Exception var5) {
                            this.statusLabel.setText("?????????????????????????????????????????????????????????????????????:" + var5.getMessage());
                        }
                    } else {
                        this.statusLabel.setText("??????????????????");
                    }

                });
            } catch (Exception var8) {
                Platform.runLater(() -> {
                    this.statusLabel.setText("??????????????????:" + var8.getMessage());
                });
            }

        };
        Thread workThrad = new Thread(runner);
        this.workList.add(workThrad);
        workThrad.start();
    }

    public void getTaskResultBackground(String pluginName, PluginResultCallBack callBack) {
        Runnable runner = () -> {
            String running = "true";

            try {
                while(running.equals("true")) {
                    JSONObject resultObj = this.currentShellService.getPluginTaskResult(pluginName);
                    String status = resultObj.getString("status");
                    String msg = resultObj.getString("msg");
                    JSONObject msgObj = new JSONObject(msg);
                    String pluginResult = new String(Base64.getDecoder().decode(msgObj.getString("result")), "UTF-8");
                    String pluginRunning = msgObj.getString("running");
                    running = pluginRunning;
                    callBack.onPluginResult(status, pluginResult, pluginRunning);
                    Thread.sleep(3000L);
                }
            } catch (Exception var10) {
                callBack.onPluginResult("fail", var10.getMessage(), "false");
            }

        };
        Thread workThrad = new Thread(runner);
        this.workList.add(workThrad);
        workThrad.start();
    }
}
