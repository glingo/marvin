package com.marvin.component.configuration.builder;

import com.marvin.component.configuration.builder.definition.ArrayNodeDefinition;
import com.marvin.component.configuration.builder.definition.BooleanNodeDefinition;
import com.marvin.component.configuration.builder.definition.EnumNodeDefinition;
import com.marvin.component.configuration.builder.definition.FloatNodeDefinition;
import com.marvin.component.configuration.builder.definition.IntegerNodeDefinition;
import com.marvin.component.configuration.builder.definition.NodeDefinition;
import com.marvin.component.configuration.builder.definition.ParentNodeDefinitionInterface;
import com.marvin.component.configuration.builder.definition.ScalarNodeDefinition;
import com.marvin.component.configuration.builder.definition.VariableNodeDefinition;
import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 *
 * @author cdi305
 */
public class NodeBuilder implements NodeParentInterface {
    
    protected NodeParentInterface parent;
    protected HashMap<String, Class> definitionMapping;

    public NodeBuilder() {
        HashMap<String, Class> definitions = new HashMap();
        definitions.put("variable", VariableNodeDefinition.class);
        definitions.put("scalar", ScalarNodeDefinition.class);
        definitions.put("boolean", BooleanNodeDefinition.class);
        definitions.put("integer", IntegerNodeDefinition.class);
        definitions.put("float", FloatNodeDefinition.class);
        definitions.put("array", ArrayNodeDefinition.class);
        definitions.put("enum", EnumNodeDefinition.class);
        this.definitionMapping = definitions;
    }
    
    public NodeDefinition node(String name, String type) throws Exception {
        Class<NodeDefinition> cl = this.definitionMapping.getOrDefault(type, null);
        Constructor<NodeDefinition> ctr = cl.getConstructor(new Class[]{String.class});
        NodeDefinition node = ctr.newInstance(name);
        this.append(node);
        return node;
    }
    
    public NodeDefinition variableNode(String name) throws Exception {
        return this.node(name, "variable");
    }
     
    public NodeDefinition enumNode(String name) throws Exception {
        return this.node(name, "enum");
    }
    
    public NodeDefinition floatNode(String name) throws Exception {
        return this.node(name, "float");
    }
    
    public NodeDefinition integerNode(String name) throws Exception {
        return this.node(name, "integer");
    }
    
    public NodeDefinition booleanNode(String name) throws Exception {
        return this.node(name, "boolean");
    }
    
    public NodeDefinition scalarNode(String name) throws Exception {
        return this.node(name, "scalar");
    }
    
    public NodeDefinition arrayNode(String name) throws Exception {
        return this.node(name, "array");
    }

    public void setParent(NodeParentInterface parent) {
        this.parent = parent;
    }
    
    public NodeParentInterface end(){
        return this.parent;
    }
    
    public NodeBuilder append(NodeDefinition definition) throws CloneNotSupportedException {

        if(definition instanceof ParentNodeDefinitionInterface) {
            NodeBuilder builder = (NodeBuilder) this.clone();
            definition.setBuilder(builder);
        }

        if(this.parent != null) {
            this.parent.append(definition);
            definition.setParent(this);
        }
        
        return this;
    }

//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//        NodeBuilder builder = (NodeBuilder) super.clone();
//        builder.setParent(null);
//        return builder;
//    }

}
