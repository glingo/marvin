package com.marvin.component.templating.node;

import com.marvin.component.templating.template.EvaluationContext;
import com.marvin.component.templating.template.Template;
import com.marvin.component.templating.extension.NodeVisitor;
import com.marvin.component.templating.node.expression.Expression;

/**
 * The right hand side to the test expression.
 *
 * @author Mitchell
 *
 */
public class TestInvocationExpression implements Expression<Object> {

    private final String testName;

    private final ArgumentsNode args;

    private final int lineNumber;

    @Override
    public Object evaluate(Template self, EvaluationContext context) throws Exception {
        throw new UnsupportedOperationException();
    }

    public TestInvocationExpression(int lineNumber, String testName, ArgumentsNode args) {
        this.testName = testName;
        this.args = args;
        this.lineNumber = lineNumber;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public ArgumentsNode getArgs() {
        return args;
    }

    public String getTestName() {
        return testName;
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }
}
