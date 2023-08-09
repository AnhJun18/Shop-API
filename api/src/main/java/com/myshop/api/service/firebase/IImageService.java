package com.myshop.api.service.firebase;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public interface IImageService {

    String getImageUrl(String name);

    String save(FilePart file) throws IOException;

    public String saveBase64String(String base64String) throws IOException ;

    String save(BufferedImage bufferedImage, String originalFileName) throws IOException;

    void delete(String name) throws IOException;

    default String getExtension(String originalFileName) {
        return StringUtils.getFilenameExtension(originalFileName);
    }

    default String _generateRandomFileName() {
        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();
        // Định dạng ngày thành chuỗi ddMMYYYY
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddMMYYYY"));
        // Tạo một UUID ngẫu nhiên
        UUID uuid = UUID.randomUUID();
        // Kết hợp ngày và UUID thành tên tệp
        String randomFileName = formattedDate + "_" + uuid.toString();
        // Trả về tên tệp ngẫu nhiên
        return randomFileName;
    }

    default byte[] getByteArrays(BufferedImage bufferedImage, String format) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {

            ImageIO.write(bufferedImage, format, baos);

            baos.flush();

            return baos.toByteArray();

        } catch (IOException e) {
            throw e;
        } finally {
            baos.close();
        }
    }


    default byte[] convertFilePartToByteArray(FilePart file) {
     return    DataBufferUtils.join(file.content()).map(dataBuffer -> dataBuffer.asByteBuffer().array())
             .toProcessor().block();

    }

}