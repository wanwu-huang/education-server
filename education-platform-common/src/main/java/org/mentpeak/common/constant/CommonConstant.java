package org.mentpeak.common.constant;


import org.mentpeak.core.launch.constant.AppConstant;

/**
 * 通用常量
 *
 * @author mp
 */
public interface CommonConstant {

    /**
     * 动态路由
     */
    String CONFIG_DATA_ID_DYNAMIC_ROUTES = "education-dynamic-routes.yaml";
    public static final String CONFIG_GROUP = "DEFAULT_GROUP";
    public static final long CONFIG_TIMEOUT_MS = 5000;


    /**
     * 微服务之间传递的唯一标识
     */
    String MIC_TRACE_ID = "mic-trace-id";

    /**
     * 日志链路追踪id日志标志
     */
    String LOG_TRACE_ID = "traceId";

    /**
     * nacos dev 地址
     */
    String NACOS_DEV_ADDR = "127.0.0.1:8848";

    /**
     * nacos 绑定命名空间
     */
    String NACOS_DEV_NAMESPACE = "d863f837-88ed-403b-a100-0c8bf5bdf8e6";
//    String NACOS_DEV_NAMESPACE = "";
    /**
     * nacos prod 地址
     */
    String NACOS_PROD_ADDR = "39.105.104.8:8848";

    /**
     * nacos test 地址
     */
    String NACOS_TEST_ADDR = "39.105.104.8:8848";

    /**
     * sentinel dev 地址
     */
    String SENTINEL_DEV_ADDR = "127.0.0.1:8858";

    /**
     * sentinel prod 地址
     */
    String SENTINEL_PROD_ADDR = "127.0.0.1:8858";

    /**
     * sentinel test 地址
     */
    String SENTINEL_TEST_ADDR = "127.0.0.1:8858";

    /**
     * sword 系统名
     */
    String SWORD_NAME = "sword";

    /**
     * saber 系统名
     */
    String SABER_NAME = "saber";

    /**
     * 顶级父节点id
     */
    Integer TOP_PARENT_ID = 0;

    /**
     * 顶级父节点名称
     */
    String TOP_PARENT_NAME = "顶级";
    /**
     * 验证码前缀
     */
    String CAPTCHA_KEY = "education.captcha.";
    /**
     * 默认头像地址
     */
    String DEFAULT_HEAD_URL = "http://mipac.file.mentpeak.com/headImage/4d8ff260-ea7f-4db8-bb36-a674a4af51ad.png";

    /**
     * 公共域名
     */
    String COMMON_ARR = "http://gkxy-youth-assessment.mentpeak.com/";

    /**
     * 学员角色
     * （如数据库变动 根据实际情况修改）
     */
    String STUDENT_ROLE_ID = "3";

    /**
     * 动态获取nacos地址
     *
     * @param profile 环境变量
     * @return addr
     */
    static String nacosAddr(String profile) {
        switch (profile) {
            case (AppConstant.PROD_CODE):
                return NACOS_PROD_ADDR;
            case (AppConstant.TEST_CODE):
                return NACOS_TEST_ADDR;
            default:
                return NACOS_DEV_ADDR;
        }
    }

    /**
     * 动态获取sentinel地址
     *
     * @param profile 环境变量
     * @return addr
     */
    static String sentinelAddr(String profile) {
        switch (profile) {
            case (AppConstant.PROD_CODE):
                return SENTINEL_PROD_ADDR;
            case (AppConstant.TEST_CODE):
                return SENTINEL_TEST_ADDR;
            default:
                return SENTINEL_DEV_ADDR;
        }
    }


    /**
     * 图片文件前缀
     */
    String IMAGE_PRRE = "images";

    /**
     * 附件（文档以及压缩文件前缀）
     */
    String FILE_PRRE = "files";

    /**
     * 视频文件前缀
     */
    String VEDIO_PRRE = "vedio";

    /**
     * 图片文件后缀
     */
    String IMAGE_SUFFIX = ".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png)$";

    /**
     * 附件（文档 压缩文件）后缀
     */
    String FILE_SUFFIX = ".+(.TXT|.txt|.DOC|.doc|.DOCX|.docx|.XLS|.xls|.XLSX|.xlsx|.PPT|.ppt|.PPTX|.pptx|.PDF|.pdf|.ZIP|.zip|.RAR|.rar)$";

    /**
     * 视频文件后缀
     */
    String VEDIO_SUFFIX = ".+(.MP4|.mp4|.AVI|.avi|.MOV|.mov|.3GP|.3gp|.FLV|.flv)$";

    /**
     * 文件类型(1:附件;2;图片;3:视频)
     */
    Integer FILE_FILETYPE = 1;
    Integer IMAGE_FILETYPE = 2;
    Integer VEDIO_FILETYPE = 3;


    /**
     * 字典表 grade
     */
    String DICT_GRADE = "grade";

    /**
     * 字典表 intervene
     */
    String DICT_INTERVENE = "intervene";

    /**
     * 字典表 sex
     */
    String SEX = "sex";


    /**
     * 字典表 risklevel
     */
    String DICT_RISKLEVEL = "risklevel";


    /**
     * 密码校验
     */
    String PASS_WORD_PATTERN = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$";

    /**
     * 手机号校验
     */
    String PHONE_PATTERN = "^1[3456789][0-9]{9}";

    // 问卷试题前缀
    String QUESTIONNAIRE = "QUESTIONNAIRE:";

    // 测谎
    String POLYGRAPH = "polygraph:";

    // 心理状况
    String PSYCHOLOGICAL_CONDITION = "psychological_condition:";

    // 问卷过期时间
    long PAPER_EXPIRE = 1L;

    // 默认密码
    String DEFAULT_PASSWORD = "000000";

    /**
     * 文件格式
     */
    String FILE_XLS = "xls";
    String FILE_XLSX = "xlsx";

    // 教师首次登录
    String TEACHER_FIRST = "teacher:first:";

}
