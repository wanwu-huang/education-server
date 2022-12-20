package org.mentpeak.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;

import org.mentpeak.common.constant.CommonConstant;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.service.FileUploadService;
import org.mentpeak.user.util.QiNiuTokenUtil;
import org.mentpeak.user.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private QiNiuTokenUtil qiNiuTokenUtil;

    @Override
    public Result fileUpload ( MultipartFile file ) {

        Map < String, Object > map = new HashMap <> ( );

        //生成文件编码，截取文件后缀
        String filename = file.getOriginalFilename ( );
        String strSuffix = StrUtil.getSuffix ( filename );
        long size = file.getSize ( );

        String PRRE_ = "";
        // 文件类型(1:附件;2;图片;3:视频)
        Integer fileType = 1;

        // 图片格式校验
        Pattern imgPattern = Pattern.compile ( CommonConstant.IMAGE_SUFFIX );
        Matcher imgMatcher = imgPattern.matcher ( filename.toLowerCase ( ) );
        boolean imgResult = imgMatcher.find ( );
        if ( imgResult ) {
            PRRE_ = CommonConstant.IMAGE_PRRE;
            fileType = CommonConstant.IMAGE_FILETYPE;
        }

        // 附件格式校验
        Pattern filePattern = Pattern.compile ( CommonConstant.FILE_SUFFIX );
        Matcher fileMatcher = filePattern.matcher ( filename.toLowerCase ( ) );
        boolean fileResult = fileMatcher.find ( );
        if ( fileResult ) {
            PRRE_ = CommonConstant.FILE_PRRE;
            fileType = CommonConstant.FILE_FILETYPE;
        }

        // 视频格式校验
        Pattern vedioPattern = Pattern.compile ( CommonConstant.VEDIO_SUFFIX );
        Matcher vedioMatcher = vedioPattern.matcher ( filename.toLowerCase ( ) );
        boolean vedioResult = vedioMatcher.find ( );
        if ( vedioResult ) {
            PRRE_ = CommonConstant.VEDIO_PRRE;
            fileType = CommonConstant.VEDIO_FILETYPE;
        }


        map.put ( "fileName" ,
                  filename );
        map.put ( "fileType" ,
                  fileType );
        // 单位字节
        map.put ( "fileSize" ,
                  size );

        if ( imgResult || fileResult || vedioResult ) {

            // 上传符合以上文件格式的文件
            String new_name = UUID.randomUUID ( )
                                  .toString ( );
            String token = qiNiuTokenUtil.getDefaultDynamicUpToken ( );
            log.debug ( token );
            //	华南服务器
            Configuration config = new Configuration ( Zone.zone2 ( ) );
            UploadManager manager = new UploadManager ( config );
            Response response = null;
            try {
                response = manager.put ( file.getInputStream ( ) ,
                                         PRRE_ + "/" + new_name + "." + strSuffix ,
                                         token ,
                                         null ,
                                         null );
                DefaultPutRet putRet = JSONObject.parseObject ( response.bodyString ( ) ,
                                                                DefaultPutRet.class );
                String url = CommonConstant.COMMON_ARR + putRet.key;

                map.put ( "fileUrl" ,
                          url );

                return Result.data ( map );
            } catch ( QiniuException e ) {
                e.printStackTrace ( );
                return Result.fail ( e.toString ( ) );
            } catch ( IOException e ) {
                e.printStackTrace ( );
                return Result.fail ( e.toString ( ) );
            }

        } else {
            return Result.fail ( "请上传指定格式的文件" );
        }

    }

    @Override
    public Result upload (
            MultipartFile file ,
            String path ) {
        Map < String, Object > map = new HashMap <> ( );

        //生成文件编码，截取文件后缀
        String filename = file.getOriginalFilename ( );
        String strSuffix = StrUtil.getSuffix ( filename );
        long size = file.getSize ( );

        String PRRE_ = "";
        // 文件类型(1:附件;2;图片;3:视频)
        Integer fileType = 1;

        // 图片格式校验
        Pattern imgPattern = Pattern.compile ( CommonConstant.IMAGE_SUFFIX );
        Matcher imgMatcher = imgPattern.matcher ( filename.toLowerCase ( ) );
        boolean imgResult = imgMatcher.find ( );
        if ( imgResult ) {
            PRRE_ = CommonConstant.IMAGE_PRRE;
            fileType = CommonConstant.IMAGE_FILETYPE;
        }

        // 附件格式校验
        Pattern filePattern = Pattern.compile ( CommonConstant.FILE_SUFFIX );
        Matcher fileMatcher = filePattern.matcher ( filename.toLowerCase ( ) );
        boolean fileResult = fileMatcher.find ( );
        if ( fileResult ) {
            PRRE_ = CommonConstant.FILE_PRRE;
            fileType = CommonConstant.FILE_FILETYPE;
        }

        // 视频格式校验
        Pattern vedioPattern = Pattern.compile ( CommonConstant.VEDIO_SUFFIX );
        Matcher vedioMatcher = vedioPattern.matcher ( filename.toLowerCase ( ) );
        boolean vedioResult = vedioMatcher.find ( );
        if ( vedioResult ) {
            PRRE_ = CommonConstant.VEDIO_PRRE;
            fileType = CommonConstant.VEDIO_FILETYPE;
        }


        map.put ( "fileName" ,
                  filename );
        map.put ( "fileType" ,
                  fileType );
        // 单位字节
        map.put ( "fileSize" ,
                  size );

        if ( imgResult || fileResult || vedioResult ) {

            // 上传符合以上文件格式的文件
            String new_name = UUID.randomUUID ( )
                                  .toString ( );
            String token = qiNiuTokenUtil.getDefaultDynamicUpToken ( );
            log.debug ( token );
            //	华南服务器
            Configuration config = new Configuration ( Zone.zone2 ( ) );
            UploadManager manager = new UploadManager ( config );
            Response response = null;
            try {
                response = manager.put ( file.getInputStream ( ) ,
                                         path + PRRE_ + "/" + new_name + "." + strSuffix ,
                                         token ,
                                         null ,
                                         null );
                DefaultPutRet putRet = JSONObject.parseObject ( response.bodyString ( ) ,
                                                                DefaultPutRet.class );
                String url = CommonConstant.COMMON_ARR + putRet.key;

                map.put ( "fileUrl" ,
                          url );

                return Result.data ( map );
            } catch ( QiniuException e ) {
                e.printStackTrace ( );
                return Result.fail ( e.toString ( ) );
            } catch ( IOException e ) {
                e.printStackTrace ( );
                return Result.fail ( e.toString ( ) );
            }

        } else {
            return Result.fail ( "请上传指定格式的文件" );
        }
    }

}
