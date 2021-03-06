/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2014 by Mitchell Bösecke
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.marvin.component.templating.node;

import com.marvin.component.templating.template.EvaluationContext;
import com.marvin.component.templating.template.Template;
import com.marvin.component.templating.extension.NodeVisitor;
import java.io.IOException;
import java.io.Writer;


public class FlushNode extends AbstractRenderableNode {

    public FlushNode(int lineNumber) {
        super(lineNumber);
    }

    @Override
    public void render(Template self, Writer writer, EvaluationContext context) throws IOException {
        writer.flush();
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}
