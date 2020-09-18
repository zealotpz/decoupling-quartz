package com.zealotpz.quartz.utils;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * description:
 *
 * @author: zealotpz
 * create: 2020-09-17 15:44
 **/
public class ClassUtil {


    public static Set<String> getClassName(String packageName, boolean isRecursion) {
        Set<String> classNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");

        URL url = loader.getResource(packagePath);
        if (url != null) {
            String protocol = url.getProtocol();
            if (protocol.equals("file")) {
                classNames = getClassNameFromDir(url.getPath(), packageName, isRecursion);
            } else if (protocol.equals("jar")) {
                JarFile jarFile = null;
                try {
                    jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                if(jarFile != null){
//                    getClassNameFromJar(jarFile.entries(), packageName, isRecursion);
//                }
            }
        }
//        else {
//            /*从所有的jar包中查找包名*/
//            classNames = getClassNameFromJars(((URLClassLoader)loader).getURLs(), packageName, isRecursion);
//        }

        return classNames;
    }

    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath    文件路径
     * @param isRecursion 是否遍历子包
     * @return 类的完整名称
     */
    private static Set<String> getClassNameFromDir(String filePath, String packageName, boolean isRecursion) {
        Set<String> className = new HashSet<String>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File childFile : files) {
            if (childFile.isDirectory()) {
                if (isRecursion) {
                    className.addAll(getClassNameFromDir(childFile.getPath(), packageName + "." + childFile.getName(), isRecursion));
                }
            } else {
                String fileName = childFile.getName();
                if (fileName.endsWith(".class") && !fileName.contains("$")) {
                    className.add(packageName + "." + fileName.replace(".class", ""));
                }
            }
        }

        return className;
    }


    public static void main(String[] agrs) {
        Set<String> className = getClassName("com.zealotpz.quartz.job", false);
        className.forEach(s -> {
            System.out.println("------->" + s);
        });

    }

}
