/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.marvin.component.templating.node;


import com.marvin.component.templating.template.EvaluationContext;
import com.marvin.component.templating.template.Template;
import com.marvin.component.templating.extension.NodeVisitor;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class BodyNode extends AbstractRenderableNode {

    private final List<RenderableNode> children;

    private static final List<Class<? extends Node>> nodesToRenderInChild = new ArrayList<>();

    static {
        nodesToRenderInChild.add(SetNode.class);
        nodesToRenderInChild.add(ImportNode.class);
    }

    /**
     * When a template extends a parent template there are very few nodes in the
     * child that should actually get rendered such as set and import. All
     * others should be ignored.
     */
    private boolean onlyRenderInheritanceSafeNodes = false;

    public BodyNode(int lineNumber, List<RenderableNode> children) {
        super(lineNumber);
        this.children = children;
    }

    @Override
    public void render(Template self, Writer writer, EvaluationContext context) throws Exception {
        for (RenderableNode child : children) {
            if (onlyRenderInheritanceSafeNodes && context.getHierarchy().getParent() != null) {
                if (!nodesToRenderInChild.contains(child.getClass())) {
                    continue;
                }
            }
            child.render(self, writer, context);
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public List<RenderableNode> getChildren() {
        return children;
    }

    public boolean isOnlyRenderInheritanceSafeNodes() {
        return onlyRenderInheritanceSafeNodes;
    }

    public void setOnlyRenderInheritanceSafeNodes(boolean onlyRenderInheritanceSafeNodes) {
        this.onlyRenderInheritanceSafeNodes = onlyRenderInheritanceSafeNodes;
    }

}
