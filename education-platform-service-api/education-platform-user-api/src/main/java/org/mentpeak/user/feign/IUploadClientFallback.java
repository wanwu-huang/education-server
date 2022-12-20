package org.mentpeak.user.feign;

import org.mentpeak.core.tool.api.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户远程调用失败，降级
 */
@Component
public class IUploadClientFallback implements IUploadClient {

    @Override
    public Result < String > fileUpload ( MultipartFile file ) {
        return Result.fail ( "上传文件失败" );
    }
}
