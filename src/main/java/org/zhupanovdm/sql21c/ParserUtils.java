package org.zhupanovdm.sql21c;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {

    public static String replaceIncorrectParams(String kld) {
        Matcher matcher = Pattern.compile("@\\d[_\\d\\w]*+").matcher(kld);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String group = matcher.group();
            String s = group.charAt(0) + "_" + group.substring(1);
            matcher.appendReplacement(sb, s);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
