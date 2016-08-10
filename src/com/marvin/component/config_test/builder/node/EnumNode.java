package com.marvin.component.config_test.builder.node;

import com.marvin.component.config_test.builder.NodeParentInterface;

public class EnumNode extends ScalarNode {

    protected Object[] values;
    
    public EnumNode(String name) {
        super(name);
    }
    
//    public EnumNode(String name, NodeParentInterface parent, Object[] values) {
//        super(name, parent);
//        this.values = values;
//    }
    
    public EnumNode(String name, Object[] values) {
        super(name);
        this.values = values;
    }
    
}
