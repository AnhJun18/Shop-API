package com.myshop.common.utils;

import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

public class ImageUtils {
  public static ByteArrayOutputStream createThumbnail(MultipartFile orginalFile, Integer width){
    ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream();
    try{
      BufferedImage img = ImageIO.read(orginalFile.getInputStream());
      BufferedImage thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, width, Scalr.OP_ANTIALIAS);
      ImageIO.write(thumbImg, orginalFile.getContentType().split("/")[1] , thumbOutput);
    }catch (Exception e){
      e.printStackTrace();
    }
    return thumbOutput;
  }

  public static ByteArrayOutputStream createThumbnail(Path initialFile, String fileType, Integer width){
    ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream();
    try{
      File file = new File(initialFile.toUri());
      InputStream targetStream = new FileInputStream(file);
      BufferedImage img = ImageIO.read(targetStream);
      BufferedImage thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, width, Scalr.OP_ANTIALIAS);
      ImageIO.write(thumbImg, fileType , thumbOutput);
    }catch (Exception e){
      e.printStackTrace();
    }
    return thumbOutput;
  }

}
