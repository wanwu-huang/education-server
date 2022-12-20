package org.mentpeak.user.feign;

import org.mentpeak.core.launch.constant.AppConstant;
import org.mentpeak.core.tool.api.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient (
        value = AppConstant.APPLICATION_USER_NAME,
        fallback = IUploadClientFallback.class
)
public interface IUploadClient {

    String API_PREFIX = "/fileUploadController";
    String FILE_UPLOAD = API_PREFIX + "/fileUpload";

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    @GetMapping ( FILE_UPLOAD )
    Result < String > fileUpload ( @RequestParam ( "file" ) MultipartFile file );
}
