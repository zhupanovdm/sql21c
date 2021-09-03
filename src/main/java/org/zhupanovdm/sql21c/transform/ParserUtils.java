package org.zhupanovdm.sql21c.transform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {

    public static String replaceIncorrectParams(String input) {
        Matcher matcher = Pattern.compile("@\\d[_\\d\\w]*+").matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String group = matcher.group();
            String s = group.charAt(0) + "_" + group.substring(1);
            matcher.appendReplacement(sb, s);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String toEntityName(String input) {
        if (input.charAt(0) == '[') {
            return input.replace("[", "").replace("]", "");
        }
        return input;
    }

    public static String toDboName(String input) {
        return '[' + input + ']';
    }

}
