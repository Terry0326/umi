package com.ugoodtech.umi.core.service.impl;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

import com.ugoodtech.umi.core.domain.Resource;
import com.ugoodtech.umi.core.exception.ResourceNotFoundException;
import com.ugoodtech.umi.core.repository.ResourceRepository;
import com.ugoodtech.umi.core.service.ResourceService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    @Value("${photos.basedir}")
    private String basePath;
    @Value("${photos.url}")
    private String urlPrefix;

    @Override
    public Resource save(String filename, String contentType, byte[] bytes) throws IOException {
        //decide the attachment's relativePath
        String relativePath = saveFile(filename, bytes);
        //
        Resource attachment = new Resource();
        attachment.setCompleted(true);
        attachment.setName(filename);
        if (contentType != null) {
            attachment.setMimeType(contentType);
        } else {
            Path filePath = Paths.get(basePath, relativePath);
            attachment.setMimeType(Files.probeContentType(filePath));
        }
        attachment.setPath(relativePath);
        attachment.setSize(bytes.length);
        attachment.setCreationTime(new Date());
        //todo:
        attachment.setType(Resource.ResourceType.image);
        return this.resourceRepository.save(attachment);
    }

    @Override
    public Resource save(Long possessorId, Resource.PossessorType possessorType, String filename, String contentType,
                         byte[] bytes) throws IOException {
        return save(possessorId, possessorType, filename, contentType, bytes, false);
    }

    public Resource save(Long possessorId, Resource.PossessorType possessorType, String filename, String contentType,
                         byte[] bytes, boolean snapshot) throws IOException {
        //decide the attachment's relativePath
        String relativePath = saveFile(filename, bytes);
        //
        Resource attachment = new Resource();
        attachment.setCompleted(true);
        attachment.setName(filename);
        if (contentType != null) {
            attachment.setMimeType(contentType);
        } else {
            Path filePath = Paths.get(basePath, relativePath);
            attachment.setMimeType(Files.probeContentType(filePath));
        }
        attachment.setPath(relativePath);
        attachment.setSize(bytes.length);
        attachment.setCreationTime(new Date());
        attachment.setPossessorId(possessorId);
        attachment.setPossessorType(possessorType);
        attachment.setType(Resource.ResourceType.image);
        attachment.setSnapshot(snapshot);
        return this.resourceRepository.save(attachment);
    }

    private String saveFile(String filename, byte[] bytes) throws IOException {
        String suffixPath = this.getSuffixPath();
        Path dir = Paths.get(basePath, suffixPath);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (FileAlreadyExistsException x) {
                //
            }
        }

        Path filePath = Paths.get(basePath, suffixPath, filename);
        Random rand = new Random();
        StringBuilder randomName=new StringBuilder(filename);
        String path = suffixPath + File.separator + randomName;
        while (Files.exists(filePath)) {
            int randomNum = rand.nextInt();
            randomName.append(randomName+ "_" + Math.abs(randomNum));
            path = suffixPath + File.separator + randomName;
            filePath = Paths.get(basePath, path);
        }
        //
        Files.write(filePath, bytes, StandardOpenOption.CREATE);
        return path;
    }

    @Override
    public void delete(Long resourceId) {
        Resource attachment = this.resourceRepository.findOne(resourceId);
        if (attachment != null) {
            attachment.setDeleted(true);
            this.resourceRepository.save(attachment);
        }
    }

    @Override
    public void readResourceBody(Long attachmentId, OutputStream os) throws IOException {
        Resource attachment = this.resourceRepository.findOne(attachmentId);
        Path path = Paths.get(basePath, attachment.getPath());
        InputStream inputStream = Files.newInputStream(path);
        IOUtils.copy(inputStream, os);
        inputStream.close();
    }

    @Override
    public Resource getResource(Long resourceId) throws IOException {
        return this.resourceRepository.findOne(resourceId);
    }

    @Override
    public byte[] getPhotoThumbnailBytes(Long photoId, int width, int height) throws IOException, ResourceNotFoundException {
        String photoFilePath = basePath + File.separator + (width + "_" + height) + File.separator + photoId + ".jpg";
        System.out.println(photoFilePath);
        File photoFile = new File(photoFilePath);
        if (photoFile.exists()) {
            return FileUtils.readFileToByteArray(photoFile);
        } else {
            File originalPhotoFile = new File(basePath + File.separator + photoId);
            if (originalPhotoFile.exists()) {
                //计算原图宽度
                BufferedImage buff = ImageIO.read(new FileInputStream(originalPhotoFile));
                BufferedImage bufferedImage;
                int originalWidth = buff.getWidth();
                int originalHeight = buff.getHeight();
                File parent = photoFile.getParentFile();
                if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory \'" + parent + "\' could not be created");
                }
                //如果实际宽度大于需求宽度才缩放
                if (originalWidth > width) {
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    // 设置精确到小数点后2位
                    numberFormat.setMaximumFractionDigits(2);
                    String percent = numberFormat.format((float) width / (float) originalWidth);
                    double percentDouble = new Double(percent);
                    //按百分比缩放
                    bufferedImage = Thumbnails.of(originalPhotoFile)
                            .scale(percentDouble)
                            .outputQuality(1.0)
                            .outputFormat("jpg").asBufferedImage();
                    //如果缩放完高度大于需求高度才剪裁
                    if (bufferedImage.getHeight() > height) {
                        //按缩放后宽度与需求高度剪裁
                        Thumbnails.of(bufferedImage)
                                .scale(1.0)
                                .outputQuality(1.0)
                                .sourceRegion(0, 0, bufferedImage.getWidth(), height).toFile(photoFile);
                    } else {
                        //返回缩放后图
                        Thumbnails.of(bufferedImage)
                                .scale(1.0)
                                .outputQuality(1.0)
                                .toFile(photoFile);
                    }
                    return FileUtils.readFileToByteArray(photoFile);
                } else {
                    if (originalHeight > height) {
                        bufferedImage = Thumbnails.of(originalPhotoFile)
                                .scale(1)
                                .outputQuality(1.0)
                                .outputFormat("jpg").asBufferedImage();
                        Thumbnails.of(bufferedImage)
                                .scale(1.0)
                                .outputQuality(1.0)
                                .sourceRegion(0, 0, originalWidth, height).toFile(photoFile);
                        return FileUtils.readFileToByteArray(photoFile);

                    } else {
                        //返回原图
                        return FileUtils.readFileToByteArray(originalPhotoFile);
                    }
                }
            } else {
                logger.error("photo not found with id:" + photoId);
                throw new ResourceNotFoundException();
            }
        }
    }

    @Override
    public byte[] getResourceBytes(Long resourceId) {
        Resource attachment = this.resourceRepository.findOne(resourceId);
        Path path = Paths.get(basePath, attachment.getPath());
        try {
            return FileUtils.readFileToByteArray(path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void zipAndOutput(Long[] fileIds, ServletOutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        ZipOutputStream zos = new ZipOutputStream(outputStream);

        Set<String> filenameSet = new HashSet<>();
        for (Long fildId : fileIds) {
            Resource attachment = this.resourceRepository.findOne(fildId);
            if (attachment.getType().equals(Resource.ResourceType.video)) {
                continue;//ignore video batch download.
            }
            if (filenameSet.contains(attachment.getName())) {
                logger.warn("duplicate file " + attachment.getName() + ", ignored in zip.");
                continue;
            } else {
                filenameSet.add(attachment.getName());
            }
            Path path = Paths.get(basePath, attachment.getPath());
            FileInputStream fis = FileUtils.openInputStream(path.toFile());
//

            zos.putNextEntry(new ZipEntry(attachment.getName()));
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }
        zos.close();
    }

    @Override
    public void delete(Long[] fileIds) {
        for (Long fileId : fileIds) {
            Resource resource = resourceRepository.findOne(fileId);
            if (resource != null) {
                resource.setDeleted(true);
                resourceRepository.save(resource);
            }
        }
    }


    private String getSuffixPath() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = format.format(new Date());
        return date.substring(0, 6) + File.separator + date.substring(6);
    }

    Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);
}
