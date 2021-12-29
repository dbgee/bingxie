

package net.rebeyond.behinder.core;

public class Constants {
    public static String[] userAgents = new String[]{"Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4389.114 Safari/537.36", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:87.0) Gecko/20100101 Firefox/87.0", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:79.0) Gecko/20100101 Firefox/79.0", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:79.0) Gecko/20100101 Firefox/79.0", "Mozilla/5.0 (Linux x86_64; rv:79.0) Gecko/20100101 Firefox/79.0", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2)", "Mozilla/5.0 (Windows NT 6.1; Trident/7.0; rv:11.0) like Gecko", "Mozilla/5.0 (Windows NT 6.2; Trident/7.0; rv:11.0) like Gecko", "Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko"};
    public static String UPDATE_URL="https://raw.githubusercontent.com/dbgee/bingxie/master/images/updateinfo.json";
    public static String DOWNLOAD_URL="https://github.com/dbgee/bingxie/releases";
    public static String VERSION = "v4.1";
    public static int MENU_CUT = 1;
    public static int MENU_COPY = 16;
    public static int MENU_PASTE = 256;
    public static int MENU_CLEAR = 4096;
    public static int MENU_SELECT_ALL = 65536;
    public static int MENU_ALL = 69905;
    public static int ENCRYPT_TYPE_AES = 0;
    public static int ENCRYPT_TYPE_XOR = 1;
    public static int REALCMD_RUNNING = 0;
    public static int REALCMD_STOPPED = 1;
    public static int PROXY_ENABLE = 0;
    public static int PROXY_DISABLE = 1;
    public static int COLUMN_DATA_TYPE_INT = 0;
    public static int COLUMN_DATA_TYPE_STRING = 1;
    public static int FILE_TYPE_DIRECTORY = 0;
    public static int FILE_TYPE_FILE = 1;
    public static final int SCRIPT_TYPE_ASP = 0;
    public static final int SCRIPT_TYPE_ASPX = 1;
    public static final int SCRIPT_TYPE_PHP = 2;
    public static final int SCRIPT_TYPE_JAVA = 3;
    public static int PLUGIN_TYPE_SCAN = 0;
    public static int PLUGIN_TYPE_EXPLOIT = 1;
    public static int PLUGIN_TYPE_TOOL = 2;
    public static int PLUGIN_TYPE_OTHER = 3;
    public static int OS_TYPE_WINDOWS = 0;
    public static int OS_TYPE_LINUX = 1;
    public static int OS_TYPE_UNIX = 2;
    public static int OS_TYPE_MAC = 3;
    public static int HISTORY_DIRECTION_UP = 0;
    public static int HISTORY_DIRECTION_DOWN = 1;
    public static int SHELL_STATUS_ALIVE = 0;
    public static int SHELL_STATUS_DEAD = 1;
    public static int LIST_STAGE_STARTED = 0;
    public static int LIST_STAGE_DONE = 1;
    public static int LIST_STAGE_FAIL = 2;
    public static int MEMSHELL_TYPE_FILE = 0;
    public static int MEMSHELL_TYPE_AGENT = 1;
    public static int MEMSHELL_TYPE_FILTER = 2;
    public static int MEMSHELL_TYPE_SERVLET = 3;
    public static String[] cookieProperty = new String[]{"expires", "max-age", "domain", "path", "secure", "httponly", "samesite"};

    public Constants() {
    }
}
