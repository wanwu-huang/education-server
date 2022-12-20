package org.mentpeak.test.strategy.scoring;

/**
 * 颜色
 * @author demain_lee
 * @since 2022-08-10
 */
public enum ColorEnum {


    GREEN("#3ACE0A"),
    ORANGE("#FB8500"),
    YELLOW("#FFC31E"),
    RED("#E5162E"),
    BLUE("#027AFF"),
    ORANGE2("#F3BFA3"),
    BLUE2("#B7D9FF"),
    THREECOLOR("#E5162E"),
    TWOCOLOR("#FB8500"),
    ONECOLOR("#FFC31E"),
    ZEROCOLOR("#3ACE0A"),

    BLACK("#333333")
    ;

    private final String value;

    ColorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
