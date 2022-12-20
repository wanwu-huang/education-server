package org.mentpeak.common.util;/**
 * @author hzl
 * @create 2021-05-19
 */

import java.util.Properties;

/**
 * @author hzl
 * @data 2021年05月19日13:19
 */
public class FileUtil {

    private static final String TEMP_PATH = "temp";

    /**
     * 获取临时文件路径
     *
     * @return
     */
    public static String getFilePath ( ) {
        Properties properties = System.getProperties ( );
        System.out.println ( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>系统：" + properties.getProperty ( "os.name" ) );
        System.out.println ( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>根路径" + properties.getProperty ( "user.dir" ) );

        String path = properties.getProperty ( "user.dir" );

        if ( properties.getProperty ( "os.name" )
                       .toLowerCase ( )
                       .contains ( "win" ) ) {
            path += "\\";
        } else {
            path += "/";
        }
        path = path.replace ( "\\" ,
                              "/" );
//        path += TEMP_PATH;
        return path;
    }

    public static void main ( String[] args ) {
        String filePath = getFilePath ( );
        System.out.println ( filePath );
    }

}
