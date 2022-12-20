package org.mentpeak.test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mentpeak.test.base.BaseOption;
import org.mentpeak.test.jumplogic.IdsJumpLogic;

/**
 * @author lxp
 * @date 2022/06/16 17:27
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class LogicOption extends BaseOption {
	private static final long serialVersionUID = 942120274634213677L;
	private IdsJumpLogic jumpLogic;
}
