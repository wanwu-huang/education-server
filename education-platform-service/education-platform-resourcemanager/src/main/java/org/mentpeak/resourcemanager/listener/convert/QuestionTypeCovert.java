package org.mentpeak.resourcemanager.listener.convert;

import com.alibaba.excel.converters.AutoConverter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * @author lxp
 * @date 2022/06/18 22:48
 **/
public class QuestionTypeCovert implements Converter<Long> {

	/**
	 * Convert excel objects to Java objects
	 *
	 * @param cellData            Excel cell data.NotNull.
	 * @param contentProperty     Content property.Nullable.
	 * @param globalConfiguration Global configuration.NotNull.
	 * @return Data to put into a Java object
	 * @throws Exception Exception.
	 */
	@Override
	public Long convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
							   GlobalConfiguration globalConfiguration) throws Exception {
		//TODO: 获取题型ID
		return 0L;
	}
}
