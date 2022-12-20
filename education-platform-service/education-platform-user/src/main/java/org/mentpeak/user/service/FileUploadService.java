package org.mentpeak.user.service;

import org.mentpeak.core.tool.api.Result;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {


    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    Result fileUpload ( MultipartFile file );

    Result upload (
            MultipartFile file ,
            String path );
}
