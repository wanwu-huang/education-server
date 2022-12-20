package org.mentpeak.user.feign;


import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.feign.IUploadClient;
import org.mentpeak.user.service.FileUploadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

/**
 * 地区服务Feign实现类
 *
 * @author mp
 */
@RestController
@AllArgsConstructor
public class uploadClient implements IUploadClient {

    FileUploadService fileUploadService;


    @Override
    @GetMapping ( FILE_UPLOAD )
    public Result < String > fileUpload ( MultipartFile file ) {
        return fileUploadService.fileUpload ( file );
    }
}
