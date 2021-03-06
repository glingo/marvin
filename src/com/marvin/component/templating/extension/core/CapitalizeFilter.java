package com.marvin.component.templating.extension.core;

import com.marvin.component.templating.extension.Filter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CapitalizeFilter implements Filter {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Object apply(Object input, Map<String, Object> args) {
        if (input == null) {
            return null;
        }
        String value = (String) input;

        if (value.length() == 0) {
            return value;
        }

        StringBuilder result = new StringBuilder();

        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (Character.isWhitespace(c)) {
                result.append(c);
            } else {
                result.append(Character.toTitleCase(c));
                result.append(Arrays.copyOfRange(chars, i + 1, chars.length));
                break;
            }
        }

        return result.toString();
    }

}
