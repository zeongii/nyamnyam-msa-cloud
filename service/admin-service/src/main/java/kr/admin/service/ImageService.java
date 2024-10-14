package kr.admin.service;


import kr.admin.component.ImageModel;
import org.springframework.web.multipart.MultipartFile;


public interface ImageService {

    ImageModel insertReceipt(MultipartFile file);

}