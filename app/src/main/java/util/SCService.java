package util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by bgm on 3/21/2016 AD.
 */
public class SCService {
    public static final String EVENT = "EVENT";
    public static final String DATA = "DATA";
    public static final String ERROR = "ERROR";

    public static final String START_APP = "StartApp";
    public static final String END_APP = "EndApp";
    public static boolean isRecreateFile = false;

    public static String CERSocialFile = "/sdcard/cerSocial.txt";

    public static void recreateFileOnce() throws IOException {
        if (!isRecreateFile) {
            U.d("recreate CERSocialFile: " + CERSocialFile);
            FileUtils.write(new File(CERSocialFile), "");
            isRecreateFile = true;
        }
    }

    public static void writeInstagramERROR(String msg) {
        writeInstagram(ERROR, msg);
    }

    public static void writeInstagramEVENT(String msg) {
        writeInstagram(EVENT, msg);
    }

    public static void writeInstagramDATA(String msg) {
        writeInstagram(DATA, msg);
    }

    private static void writeInstagram(String recordType, String msg) {
        fileAppendLine("DownloadPhoto", "Instagram2", recordType, msg);
    }

    public static void writeYoutubeERROR(String msg) {
        writeYoutube(ERROR, msg);
    }

    public static void writeYoutubeEVENT(String msg) {
        writeYoutube(EVENT, msg);
    }

    public static void writeYoutubeDATA(String msg) {
        writeYoutube(DATA, msg);
    }

    private static void writeYoutube(String recordType, String msg) {
        fileAppendLine("Video", "Youtube2", recordType, msg);
    }

    private static void fileAppendLine(String cat, String appName, String recordType, String msg) {
        try {
            FileUtils.write(new File(CERSocialFile), U.getCurDateStr() + "CER.txt:" + recordType + " " + appName + "|" + cat + "|" + msg+"\n", true);
        } catch (IOException e) {
            U.e(e);
        }
    }

    public static String readFile()   {
        try {
            return FileUtils.readFileToString(new File(CERSocialFile));
        } catch (IOException e) {
            U.e(e);
        }
        return null;
    }


}
