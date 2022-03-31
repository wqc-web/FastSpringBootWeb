package com.zhongzhou.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName IdentityUtil
 * @Description 身份证号码工具类
 * @Date 2020/4/3 9:32
 * @Author wj
 * <p>
 * 身份证号码的格式：610821-20061222-612-X 由18位数字组成：前6位为地址码，第7至14位为出生日期码，第15至17位为顺序码，
 * 第18位为校验码。检验码分别是0-10共11个数字，当检验码为“10”时，为了保证公民身份证号码18位，所以用“X”表示。虽然校验码为“X”不能更换，但若需全用数字表示，只需将18位公民身份号码转换成15位居民身份证号码，去掉第7至8位和最后1位3个数码。
 * 当今的身份证号码有15位和18位之分。1985年我国实行居民身份证制度，当时签发的身份证号码是15位的，1999年签发的身份证由于年份的扩展（由两位变为四位）和末尾加了效验码，就成了18位。
 * （1）前1、2位数字表示：所在省份的代码；
 * （2）第3、4位数字表示：所在城市的代码；
 * （3）第5、6位数字表示：所在区县的代码；
 * （4）第7~14位数字表示：出生年、月、日；
 * （5）第15、16位数字表示：所在地的派出所的代码；
 * （6）第17位数字表示性别：奇数表示男性，偶数表示女性
 * （7）第18位数字是校检码：根据一定算法生成
 * <p>
 * 手机号码
 * 中国电信号段 133、149、153、173、177、180、181、189、199
 * 中国联通号段 130、131、132、145、155、156、166、175、176、185、186
 * 中国移动号段 134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188、198
 * 其他号段
 * 14号段以前为上网卡专属号段，如中国联通的是145，中国移动的是147等等。
 * 虚拟运营商
 * 电信：1700、1701、1702
 * 移动：1703、1705、1706
 * 联通：1704、1707、1708、1709、171
 * 卫星通信：1349
 */
@Slf4j
public class ValidateUtil implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5712360045128369849L;
    /**
     * 15
     */
    private static final Integer FIFTEEN = 15;
    /**
     * 18
     */
    private static final Integer EIGHTEEN = 18;
    /**
     * 身份证有效
     */
    public static final String VALIDITY = "该身份证有效！";
    /**
     * 位数不足
     */
    public static final String LACKDIGITS = "身份证号码长度应该为15位或18位。";
    /**
     * 最后一位应为数字
     */
    public static final String LASTOFNUMBER = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
    /**
     * 出生日期无效
     */
    public static final String INVALIDBIRTH = "身份证出生日期无效。";
    /**
     * 生日不在有效范围
     */
    public static final String INVALIDSCOPE = "身份证生日不在有效范围。";
    /**
     * 月份无效
     */
    public static final String INVALIDMONTH = "身份证月份无效";
    /**
     * 日期无效
     */
    public static final String INVALIDDAY = "身份证日期无效";
    /**
     * 身份证地区编码错误
     */
    public static final String CODINGERROR = "身份证地区编码错误。";
    /**
     * 身份证校验码无效
     */
    public static final String INVALIDCALIBRATION = "身份证校验码无效，不是合法的身份证号码";
    /**
     * 数字正则
     */
    public static final String NUMBER_REG = "[0-9]*";
    /**
     * 日期正则
     */
    public static final String DATE_REG = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))?$";
    /**
     * 手机号码正则
     */
    public static final String PHONE_REG = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";

    /**
     * 验证手机号码是否正确
     *
     * @param phone 手机号码
     * @return boolean true：正确，false：错误
     */
    public static boolean phoneValidate(String phone) {
        if (phone.length() != 11) {
            log.warn("手机号应为11位数");
            return false;
        } else {
            Pattern p = Pattern.compile(PHONE_REG);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            if (!isMatch) {
                log.warn("请填入正确的手机号");
            }
            return isMatch;
        }
    }

    /**
     * 检验身份证号码是否符合规范
     *
     * @param identity 身份证号码
     * @return boolean true：正确，false：错误
     */
    public static boolean identityValidate(String identity) {
        String Ai = "";
        // 判断号码的长度 15位或18位
        if (identity.length() != FIFTEEN && identity.length() != EIGHTEEN) {
            log.warn(LACKDIGITS);
            return false;
        }
        // 18位身份证前17位位数字，如果是15位的身份证则所有号码都为数字
        if (identity.length() == EIGHTEEN) {
            Ai = identity.substring(0, 17);
        } else if (identity.length() == FIFTEEN) {
            Ai = identity.substring(0, 6) + "19" + identity.substring(6, 15);
        }
        if (!isNumeric(Ai)) {
            log.warn(LASTOFNUMBER);
            return false;
        }

        // 判断出生年月是否有效
        // 年份
        String strYear = Ai.substring(6, 10);
        // 月份
        String strMonth = Ai.substring(10, 12);
        // 日期
        String strDay = Ai.substring(12, 14);
        if (!isDate(strYear + "-" + strMonth + "-" + strDay)) {
            log.warn(INVALIDBIRTH);
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                log.warn(INVALIDSCOPE);
                return false;
            }
        } catch (NumberFormatException | ParseException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            log.warn(INVALIDMONTH);
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            log.warn(INVALIDDAY);
            return false;
        }

        // 判断地区码是否有效
        Hashtable<String, String> areacode = GetAreaCode();
        // 如果身份证前两位的地区码不在Hashtable，则地区码有误
        if (areacode.get(Ai.substring(0, 2)) == null) {
            log.warn(CODINGERROR);
            return false;
        }
        if (!isVarifyCode(Ai, identity)) {
            log.warn(INVALIDCALIBRATION);
            return false;
        }
        log.info(VALIDITY);
        return true;
    }

    /**
     * 判断第18位校验码是否正确 第18位校验码的计算方式：
     * 1. 对前17位数字本体码加权求和 公式为：S = Sum(Ai * Wi), i =
     * 0, ... , 16 其中Ai表示第i个位置上的身份证号码数字值，Wi表示第i位置上的加权因子，其各位对应的值依次为： 7 9 10 5 8 4
     * 2 1 6 3 7 9 10 5 8 4 2
     * 2. 用11对计算结果取模 Y = mod(S, 11)
     * 3. 根据模的值得到对应的校验码
     * 对应关系为： Y值： 0 1 2 3 4 5 6 7 8 9 10 校验码： 1 0 X 9 8 7 6 5 4 3 2
     */
    private static boolean isVarifyCode(String Ai, String identity) {
        String[] VarifyCode = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum = sum + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = sum % 11;
        String strVerifyCode = VarifyCode[modValue];
        Ai = Ai + strVerifyCode;
        if (identity.length() == EIGHTEEN) {
            if (!Ai.equals(identity)) {
                return false;

            }
        }
        return true;
    }

    /**
     * 判断字符串是否为数字,0-9重复0次或者多次
     *
     * @param strnum
     * @return true, 符合; false, 不符合。
     */
    private static boolean isNumeric(String strnum) {
        Pattern pattern = Pattern.compile(NUMBER_REG);
        Matcher isNum = pattern.matcher(strnum);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 功能：判断字符串出生日期是否符合正则表达式：包括年月日，闰年、平年和每月31天、30天和闰月的28天或者29天
     *
     * @param strDate
     * @return true, 符合; false, 不符合。
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern.compile(DATE_REG);
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将所有地址编码保存在一个Hashtable中
     *
     * @return Hashtable 对象
     */
    private static Hashtable<String, String> GetAreaCode() {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }
}