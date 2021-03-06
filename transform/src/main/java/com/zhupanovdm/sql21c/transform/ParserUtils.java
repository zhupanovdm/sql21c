package com.zhupanovdm.sql21c.transform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {
    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("\\s*\\[\\s*'?(.*?)'?\\s*]\\s*");
    private static final Pattern INCORRECT_PARAM_PATTERN = Pattern.compile("@\\d[_\\d\\w]*+");

    public static String fixIncorrectParams(String input) {
        Matcher matcher = INCORRECT_PARAM_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String group = matcher.group();
            matcher.appendReplacement(sb, group.charAt(0) + "_" + group.substring(1));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String toEntityName(String input) {
        Matcher matcher = TABLE_NAME_PATTERN.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return input;
    }

    public static String toDboName(String input) {
        if (input.contains(".")) {
            return '[' + input + ']';
        }
        return input;
    }
}
