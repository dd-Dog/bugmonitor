package com.flyscale.bugmonitor.util;

import android.util.Log;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by bian on 2018/12/6.
 */

public class ZipCompressorByAnt {

    private static final String TAG = "ZipCompressorByAnt";
    private File mTargetZipFile;

    public ZipCompressorByAnt(String pathName) {
        mTargetZipFile = new File(pathName);
    }

    public void compress(String srcPathName) {
        File srcdir = new File(srcPathName);
        if (!srcdir.exists()) {
            throw new RuntimeException(srcPathName + "不存在！");
        }

        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(mTargetZipFile);
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
        fileSet.setDir(srcdir);
        //fileSet.setIncludes("**/*.java"); 包括哪些文件或文件夹 eg:zip.setIncludes("*.java");
        //fileSet.setExcludes(...); 排除哪些文件或文件夹
        zip.addFileset(fileSet);

        zip.execute();
    }

    /**
     * 压缩文件列表到某ZIP文件
     *
     * @param zipFilename 要压缩到的ZIP文件
     * @param paths       文件列表，多参数
     * @throws Exception
     */
    public static void compress(String zipFilename, String... paths)
            throws Exception {
        Log.d(TAG, "compress,paths=" + paths);
        compress(new FileOutputStream(zipFilename), paths);
    }

    /**
     * 压缩文件列表到输出流
     *
     * @param os    要压缩到的流
     * @param paths 文件列表，多参数
     * @throws Exception
     */
    private static void compress(OutputStream os, String... paths)
            throws Exception {
        Log.d(TAG, "compress,paths=" + paths);
        ZipOutputStream zos = new ZipOutputStream(os);
        for (String path : paths) {
            if (path.equals(""))
                continue;
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    zipDirectory(zos, file.getPath(), file.getName()
                            + File.separator);
                } else {
                    zipFile(zos, file.getPath(), "");
                }
            }
        }
        zos.close();
    }

    private static void zipDirectory(ZipOutputStream zos, String dirName,
                                     String basePath) throws Exception {
        Log.d(TAG, "zipDirectory,dirName=" + dirName + ",basePath=" + basePath);
        File dir = new File(dirName);
        if (dir.exists()) {
            File files[] = dir.listFiles();
            if (files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        zipDirectory(zos, file.getPath(), basePath
                                + file.getName().substring(
                                file.getName().lastIndexOf(
                                        File.separator) + 1)
                                + File.separator);
                    } else
                        zipFile(zos, file.getPath(), basePath);
                }
            } else {
                ZipEntry ze = new ZipEntry(basePath);
                zos.putNextEntry(ze);
            }
        }
    }

    private static void zipFile(ZipOutputStream zos, String filename,
                                String basePath) throws Exception {
        File file = new File(filename);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(filename);
            ZipEntry ze = new ZipEntry(basePath + file.getName());
            zos.putNextEntry(ze);
            byte[] buffer = new byte[8192];
            int count = 0;
            while ((count = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, count);
            }
            fis.close();
        }
    }
}