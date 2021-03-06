package com.marvin.component.templating.node.expression;

import com.marvin.component.templating.template.EvaluationContext;
import com.marvin.component.templating.template.Template;
import com.marvin.component.templating.extension.Function;
import com.marvin.component.templating.extension.NodeVisitor;
import com.marvin.component.templating.node.ArgumentsNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class  FunctionOrMacroInvocationExpression implements Expression<Object> {

    private final String functionName;

    private final ArgumentsNode args;

    private final int lineNumber;

    public FunctionOrMacroInvocationExpression(String functionName, ArgumentsNode arguments, int lineNumber) {
        this.functionName = functionName;
        this.args = arguments;
        this.lineNumber = lineNumber;
    }

    @Override
    public Object evaluate(Template self, EvaluationContext context) throws Exception {
        Function function = context.getExtensionRegistry().getFunction(functionName);
        if (function != null) {
            return applyFunction(self, context, function, args);
        }
        return self.macro(context, functionName, args, false);
    }

    private Object applyFunction(Template self, EvaluationContext context, Function function, ArgumentsNode args) throws Exception {
        List<Object> arguments = new ArrayList<>();

        Collections.addAll(arguments, args);

        Map<String, Object> namedArguments = args.getArgumentMap(self, context, function);
        return function.execute(namedArguments);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public String getFunctionName() {
        return functionName;
    }

    public ArgumentsNode getArguments() {
        return args;
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

}
