package com.system.assessment.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

    /**
     * 保存文件到指定路径
     *
     * @param sourceFile   源文件（File 类型）
     * @param targetDir    目标目录（如 /static/template）
     * @param targetName   目标文件名（如 template.docx）
     * @throws IOException 如果发生 IO 错误
     */
    /**
     * 使用流保存 MultipartFile 到指定路径
     *
     * @param multipartFile MultipartFile 文件
     * @param targetPath    保存的目标路径（包括文件名）
     * @throws IOException 如果发生 IO 错误
     */
    public static void saveMultipartFileWithStream(MultipartFile multipartFile, String targetPath) throws IOException {
        // 从 ClassPathResource 获取 InputStream
        ClassPathResource classPathResource = new ClassPathResource(targetPath);
        try (InputStream output = classPathResource.getInputStream();
             InputStream inputStream = multipartFile.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(targetPath)) {

            // 保存 multipartFile 内容到新文件
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
    /**
     * 从 ClassPath（如 /static/template）读取文件并转化为 File 对象
     *
     * @param resourcePath ClassPath 内的文件路径（如 "static/template/template.docx"）
     * @return 转换后的 File 对象
     * @throws IOException 如果发生 IO 错误
     */
    public static File getFileFromResource(String resourcePath) throws IOException {
        // 加载资源文件
        ClassPathResource classPathResource = new ClassPathResource(resourcePath);

        // 创建临时文件存储资源内容
        File tempFile = File.createTempFile("temp", ".docx");
        tempFile.deleteOnExit(); // JVM 退出时自动删除

        // 将资源内容写入临时文件
        try (InputStream inputStream = classPathResource.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }
}
