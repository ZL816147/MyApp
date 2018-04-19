package com.create.protocol.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Administrator on 2016/12/18.
 */

public class GetDir {
    // 获取Excel文件夹
    public static String getExcelDir() {
        // SD卡指定文件夹
        String sdcardPath = Environment.getExternalStorageDirectory()
                .toString();
        File dir = new File(sdcardPath + File.separator + "Excel"
                + File.separator + "Person");

        if (dir.exists()) {
            return dir.toString();

        } else {
            dir.mkdirs();
            Log.e("BAG", "保存路径不存在,");
            return dir.toString();
        }
    }
}
