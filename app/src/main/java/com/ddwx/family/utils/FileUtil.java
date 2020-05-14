package com.ddwx.family.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtil {


    public static void writeFileData(Context context, String fileName, String content) throws IOException {
        makeFilePath(context, fileName);
        String filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/" + fileName;
        File file = new File(filePath);
        FileOutputStream outputStream = new FileOutputStream(file,false);
        outputStream.write(content.getBytes("UTF-8"));
        outputStream.close();
    }

    /**
     * 续写文件内容
     *
     * @param context
     * @param fileName
     * @param content
     * @throws IOException
     */
    public static void writtenFileData(Context context, String fileName, String content) throws IOException {
        makeFilePath(context, fileName);
        String filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/" + fileName;
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

    private static File makeFilePath(Context context, String fileName) throws IOException {
        File file = null;
        checkFilePath(context);
        file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath() + "/" + fileName);
        if (!file.exists())
            file.createNewFile();
        return file;
    }


    /**
     * 检测文件夹是否存在,不存在则生成
     *
     * @param context
     * @throws IOException
     */
    private static void checkFilePath(Context context) throws IOException {
        File file;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());
        else
            file = new File(context.getFilesDir().getPath());
        if (!file.exists()) file.mkdir();
    }

    /**
     * 【读取文件内容】
     **/
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
}
