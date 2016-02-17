package test;

import java.math.BigDecimal;

import static org.apache.commons.lang.StringUtils.*;

/**
 * User: Frank Tang <br/>
 * Date: 16/2/9<br/>
 * Time: 下午8:14<br/>
 * Email: lovefree103@gmail.com<br/>
 * <br/>
 * This helper class can encode number x(1<=x<=10E) to string which contains 6 chars using method <b>encode</b>.<br/>
 * e.g. <br/>
 *    1 => 9GFq89 <br/>
 *    2 => Kaoq8Z <br/>
 *    3 => XaARpR <br/>
 *    4 => R53ulX <br/>
 *    5 => 98qzy3 <br/>
 *    6 => dkhLY8 <br/>
 *    7 => bl84y8 <br/>
 *    8 => KMfvok <br/>
 *    9 => dBIjfj <br/>
 * You can also decode this string to number again using method <b>decode</b>.Enjoy):
 */
public class NumberCodecHelper {

    static final char[] KEY_ARRAY = "ZKyk8dbj12oxuVpWaBhTIrNfRX0PJLcngz/MFt6wH4iS39O_QvEmsG7DCqU5lYeA".toCharArray();

    static final int LOW_6_BIT = 0b111111;

    static final int[] DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 34, 26, 8, 9, 44, 41, 59, 38, 54, 4, 45, -1, -1, -1, -1, -1, -1, -1, 63, 17, 56, 55, 50, 36, 53, 40, 20, 28, 1, 29, 35, 22, 46, 27, 48, 24, 43, 19, 58, 13, 15, 25, 61, 0, -1, -1, -1, -1, 47, -1, 16, 6, 30, 5, 62, 23, 32, 18, 42, 7, 3, 60, 51, 31, 10, 14, 57, 21, 52, 37, 12, 49, 39, 11, 2, 33, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    public static void main(String[] args) {

        for (int i = 1; i < 10; i++) {
            String code = encode(i);
            System.out.println(String.format("%2d => %s",i,code));
            if( i != decode(code) )
                System.out.println(String.format("can not decode value:'%s' to %2d", code, i));
        }

    }

    public static String encode(long val) {

        if (val <= 0)
            throw new IllegalArgumentException("value must be greater than 0");

        ++val;

        double p = Math.log(val);
        int intVal = (int) p;
        BigDecimal dec = BigDecimal.valueOf(p - intVal);
        String decString = dec.toPlainString();
        String dec9IntString = substring(decString, 2, 11);
        dec9IntString = rightPad(dec9IntString, 9, '0');

        int decInt = Integer.parseInt(dec9IntString);
        if (decString.length() > 11 && decString.charAt(11) > '4')
            decInt++;

        StringBuilder encodeString = new StringBuilder(6);
        int first6Bits = decInt & LOW_6_BIT;
        encodeString.append(KEY_ARRAY[first6Bits]);
        for (int i = 1; i < 5; i++) {
            decInt >>>= 6;
            int sixBits = decInt & LOW_6_BIT;
            encodeString.append(KEY_ARRAY[first6Bits ^ sixBits]);
        }

        encodeString.append(KEY_ARRAY[intVal ^ first6Bits]);

        return encodeString.toString();
    }

    public static long decode(String val) {
        if (isBlank(val) || val.length() != 6)
            throw new IllegalArgumentException("invalid value:" + val);

        int first6Bits = DECODE_TABLE[val.charAt(0)];
        int decNum = first6Bits;
        for (int i = 1; i < 5; i++) {
            char c = val.charAt(i);
            int sixBits = DECODE_TABLE[c] ^ first6Bits;
            decNum |= sixBits << (i * 6);
        }

        StringBuilder decBuf = new StringBuilder();
        int intVal = DECODE_TABLE[val.charAt(5)] ^ first6Bits;
        decBuf.append(intVal)
                .append(".")
                .append(leftPad(decNum + "", 9, '0'));
        double p = Math.pow(Math.E, Double.parseDouble(decBuf.toString()));
        return Math.round(p) - 1;
    }

}
