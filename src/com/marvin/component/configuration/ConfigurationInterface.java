/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marvin.component.configuration;

import com.marvin.component.configuration.builder.TreeBuilder;

/**
 *
 * @author cdi305
 */
public interface ConfigurationInterface {
    
    TreeBuilder getConfigTreeBuilder();
    
}