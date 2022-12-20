package org.mentpeak.dict.cache;


import org.mentpeak.core.cache.constant.CacheConstant;
import org.mentpeak.core.cache.utils.CacheUtil;
import org.mentpeak.core.tool.api.Result;
import org.mentpeak.core.tool.utils.Func;
import org.mentpeak.core.tool.utils.SpringUtil;
import org.mentpeak.core.tool.utils.StringPool;
import org.mentpeak.dict.entity.Dict;
import org.mentpeak.dict.feign.IDictClient;

import java.util.List;

/**
 * DictCache
 *
 * @author lxp
 */
public class DictCache {

    private static IDictClient dictClient;

    static {
        dictClient = SpringUtil.getBean ( IDictClient.class );
    }

    /**
     * 获取字典值
     *
     * @param code    字典编号
     * @param dictKey 字典键
     * @return
     */
    public static String getValue (
            String code ,
            Integer dictKey ) {
        String dictValue = CacheUtil.get ( CacheConstant.DICT_VALUE ,
                                           code + StringPool.UNDERSCORE + dictKey ,
                                           String.class );
        if ( Func.isEmpty ( dictValue ) ) {
            Result < String > result = dictClient.getValue ( code ,
                                                             dictKey );
            if ( result.isSuccess ( ) ) {
                CacheUtil.put ( CacheConstant.DICT_VALUE ,
                                code + StringPool.UNDERSCORE + dictKey ,
                                result.getData ( ) );
                return result.getData ( );
            } else {
                return StringPool.EMPTY;
            }
        } else {
            return dictValue;
        }
    }

    /**
     * 获取字典集合
     *
     * @param code 字典编号
     * @return
     */
    @SuppressWarnings ( "unchecked" )
    public static List < Dict > getList ( String code ) {
        Object dictList = CacheUtil.get ( CacheConstant.DICT_LIST ,
                                          code );
        if ( Func.isEmpty ( dictList ) ) {
            Result < List < Dict > > result = dictClient.getList ( code );
            if ( result.isSuccess ( ) ) {
                CacheUtil.put ( CacheConstant.DICT_LIST ,
                                code ,
                                result.getData ( ) );
                return result.getData ( );
            } else {
                return null;
            }
        } else {
            return ( List < Dict > ) dictList;
        }
    }

}
