package com.flyscale.bugmonitor.global;

/**
 * Created by MrBian on 2017/11/23.
 */

public class Constants {

    public static final String SP_NAME = "bugmonitor_sp";
    public static final String FTP_HOSTNAME = "hostname";
    public static final String FTP_PORT = "port";
    public static final String FTP_USERNAME = "username";
    public static final String FTP_PASSWD = "passwd";
    public static final String FTP_DOWNLOAD_FILE_REMOTEPATH = "remote_path";
    public static final String FTP_DOWNLOAD_FILE_LOCALPATH = "local_path";
    public static final String FTP_DOWNLOAD_FILE_NAME = "filename";
    public static final String SRC_BASE = "/data";
    public static final String[] SRC_FILES = {"/slog", "/modem_log"};
    public static final String[] SRC_FILES_ABS = {"/data/slog", "/data/modem_log"};

    public static final String FTP_UPLOAD_SUCCESS_BROADCAST = "com.flyscale.bugmonitor.FTP_UPLOAD_SUCCESS";
    public static final String FTP_UPLOAD_FAILED_BRAOADCAST = "com.flyscale.bugmonitor.FTP_UPLOAD_FAILED";
    public static final String FTP_UPLOAD_SERVICE_RUNNING = "com.flyscale.bugmonitor.UPLOAD_SERVICE_RUNNING";
    public static final String FTP_UPLOAD_START = "com.flyscale.bugmonitor.FTP_UPLOAD_START";
}
