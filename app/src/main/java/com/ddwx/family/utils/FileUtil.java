package com.ddwx.family.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ddwx.family.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.ddwx.family.utils.ConstantApi.accessTokenPath;
import static com.ddwx.family.utils.ConstantApi.rootFilePath;

public class FileUtil {

    /**
     * 获取App文档存储路径
     *
     * @param context
     */
    public static void getRootFilePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            ConstantApi.rootFilePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath();
        else
            ConstantApi.rootFilePath = context.getFilesDir().getPath();

    }

    /**
     * 检测File是否存在
     *
     * @return
     */
    public static boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() ? true : false;
    }

    /**
     * 重写文件内容
     *
     * @param fileName
     * @param content
     * @throws IOException
     */

    public static void writeFileData(String fileName, String content) throws IOException {
        makeFileByPath(fileName);
        String filePath = rootFilePath + "/" + fileName;
        File file = new File(filePath);
        FileOutputStream outputStream = new FileOutputStream(file, false);
        outputStream.write(content.getBytes("UTF-8"));
        outputStream.close();
    }

    /**
     * 续写文件内容
     *
     * @param fileName
     * @param content
     * @throws IOException
     */
    public static void writtenFileData(String fileName, String content) throws IOException {
        makeFileByPath(fileName);
        String filePath = rootFilePath + "/" + fileName;
        content = System.currentTimeMillis() + "\r\n" + content + "\r\n";
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Log.e("writeFile: ", filePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(content.getBytes("UTF-8"));
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void makeFileByPath(String fileName) throws IOException {
        File file = null;
        checkFilePath();
        file = new File(rootFilePath + "/" + fileName);
        if (!file.exists())
            file.createNewFile();
    }

    /**
     * 检测文档文件夹是否存在,不存在则生成
     */
    private static void checkFilePath() {
        File file = new File(rootFilePath);
        if (!file.exists()) file.mkdir();
    }

    /**
     * 读取文件内容
     *
     * @param path
     * @return
     */
    public static String readFileContent(String path) {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File file = new File(path);
                byte[] buffer = new byte[32 * 1024];
                FileInputStream fis = new FileInputStream(file);
                int len = 0;
                StringBuffer sb = new StringBuffer("");
                while ((len = fis.read(buffer)) > 0) {
                    sb.append(new String(buffer, 0, len));
                }
                fis.close();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void initBeanByFileContent(String path,UrlType type) throws JSONException {
        String s = readFileContent(path);
        switch (type){
            case ACCRSSTOKEN:
                InitBean.initAccessTokenBean(new JSONObject(s));
                break;
        }
    }
}
