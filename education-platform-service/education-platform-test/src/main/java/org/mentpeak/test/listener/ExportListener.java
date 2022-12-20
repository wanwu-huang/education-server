package org.mentpeak.test.listener;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class ExportListener {

    private static final String CHARACTER_UTF_8 = "UTF-8";

    private static final String CONTENT_TYPE = "application/vnd.ms-excel";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    public static ServletOutputStream getServletOutputStream(HttpServletResponse response,String fileName) throws IOException {
        response.setContentType(CONTENT_TYPE);
        //设置字符集为utf-8
        response.setCharacterEncoding(CHARACTER_UTF_8);
        response.setHeader(CONTENT_DISPOSITION,"attachment;filename*=utf-8''" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
        return response.getOutputStream();
    }

}
