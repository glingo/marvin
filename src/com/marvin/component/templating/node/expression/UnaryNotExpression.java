package com.marvin.component.templating.node.expression;

import com.marvin.component.templating.template.EvaluationContext;
import com.marvin.component.templating.template.Template;

public class UnaryNotExpression extends UnaryExpression {

    @Override
    public Object evaluate(Template self, EvaluationContext context) throws Exception {
        Boolean result = (Boolean) getChildExpression().evaluate(self, context);
        return !result;
    }

}
