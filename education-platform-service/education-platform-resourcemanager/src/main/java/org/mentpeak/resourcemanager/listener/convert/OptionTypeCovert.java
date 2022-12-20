package org.mentpeak.resourcemanager.listener.convert;

import com.alibaba.excel.converters.AutoConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * @author lxp
 * @date 2022/06/18 23:16
 **/
public class OptionTypeCovert extends AutoConverter {

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
	public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
									GlobalConfiguration globalConfiguration) throws Exception {

		return "";
	}
}
