package org.mentpeak.user.controller;


import org.mentpeak.core.tool.api.Result;
import org.mentpeak.user.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping ( "/fileUploadController" )
@Api ( value = "图片上传", tags = "图片上传接口" )
@Slf4j
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;


    /**
     * 上传文件
     * 包含 图片 文件 视频 等 ..
     */
    @PostMapping ( "/fileUpload" )
    @ApiOperation ( value = "上传文件", notes = "传入file", position = 1 )
    public Result < String > fileUpload ( @RequestParam ( value = "file", required = true ) MultipartFile file ) {
        try {
            return fileUploadService.fileUpload ( file );
        } catch ( Exception e ) {
            e.printStackTrace ( );
            log.error ( e.toString ( ) );
            return Result.fail ( "上传失败" );
        }
    }

    /**
     * 上传文件
     * 包含 图片 文件 视频 等 ..
     */
    @PostMapping ( "/upload" )
    @ApiOperation ( value = "上传文件", notes = "传入file", position = 1 )
    public Result < String > upload (
            @RequestParam ( value = "file", required = true ) MultipartFile file ,
            String path ) {
        try {
            return fileUploadService.upload ( file ,
                                              path );
        } catch ( Exception e ) {
            e.printStackTrace ( );
            log.error ( e.toString ( ) );
            return Result.fail ( "上传失败" );
        }
    }

}
