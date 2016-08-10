/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marvin.component.configuration.builder;

import com.marvin.component.configuration.builder.definition.NodeDefinition;
import com.marvin.component.configuration.builder.node.NodeInterface;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cdi305
 */
public class TreeBuilder extends NodeBuilder {
    
    protected NodeInterface tree;
    protected NodeDefinition root;
    protected NodeBuilder builder;
    
    public NodeDefinition root(String name) throws Exception {
        return this.root(name, "array", null);
    }
    
    public NodeDefinition root(String name, String type, NodeBuilder builder) throws Exception {
        
        if(builder == null) {
            builder = new NodeBuilder();
        }
        
        this.builder = builder;
        
        this.root = this.builder.node(name, type);
        
        this.root.setParent(this);
        
        return this.root;
    }
    
    public NodeInterface buildTree() throws Exception {
        
        if(this.root == null) {
            throw new Exception("The configuration tree has no root node.");
        }
        
        if(this.tree != null) {
            return this.tree;
        }
        
        this.tree = this.root.getNode(true);
        return this.tree;
    }

}
