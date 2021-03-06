package com.marvin.component.templating.node.expression;

import com.marvin.component.templating.template.EvaluationContext;
import com.marvin.component.templating.template.Template;
import com.marvin.component.templating.extension.NodeVisitor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapExpression implements Expression<Map<?, ?>> {

    // FIXME should keys be of any type?
    private final Map<Expression<?>, Expression<?>> entries;
    private final int lineNumber;

    public MapExpression(int lineNumber) {
        this.entries = Collections.emptyMap();
        this.lineNumber = lineNumber;
    }

    public MapExpression(Map<Expression<?>, Expression<?>> entries, int lineNumber) {
        if (entries == null) {
            this.entries = Collections.emptyMap();
        } else {
            this.entries = entries;
        }
        this.lineNumber = lineNumber;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Map<?, ?> evaluate(Template self, EvaluationContext context) throws Exception {
        Map<Object, Object> returnEntries = new HashMap<>(Long.valueOf(Math.round(Math.ceil(entries.size() / 0.75)))
                .intValue());
        for (Entry<Expression<?>, Expression<?>> entry : entries.entrySet()) {
            Expression<?> keyExpr = entry.getKey();
            Expression<?> valueExpr = entry.getValue();
            Object key = keyExpr == null ? null : keyExpr.evaluate(self, context);
            Object value = valueExpr == null ? null : valueExpr.evaluate(self, context);
            returnEntries.put(key, value);
        }
        return returnEntries;
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

}
