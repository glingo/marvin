package com.marvin.component.templating.extension.core;

import com.marvin.component.templating.extension.Function;
import com.marvin.component.templating.node.operator.OperatorUtils;
import java.util.List;
import java.util.Map;

public class MaxFunction implements Function {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Object execute(Map<String, Object> args) {
        Object min = null;

        int i = 0;

        while (args.containsKey(String.valueOf(i))) {

            Object candidate = args.get(String.valueOf(i));
            i++;

            if (min == null) {
                min = candidate;
                continue;
            }
            if (OperatorUtils.gt(candidate, min)) {
                min = candidate;
            }

        }
        return min;

    }

}
