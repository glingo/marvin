/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marvin.component.container;

import app.service.Service;
import app.service.TestServiceA;
import app.service.TestServiceB;
import app.service.TestServiceC;
import com.marvin.component.container.config.Definition;
import com.marvin.component.container.config.Reference;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cdi305
 */
public class ContainerBuilderProgrammaticTest {
    
    public static void main(String[] args) {
        
        // definition du service test.service.a
        Object[] args_A = new Object[]{"Service A", 1};
        Definition def_A = new Definition("app.service.TestServiceA", args_A);
        
        // definition du service test.service.b
        Reference ref_A = new Reference("test.service.a");
        Object[] args_B = new Object[]{ref_A, "Service B", 2};
        Definition def_B = new Definition("app.service.TestServiceB", args_B);
        
         // definition du service test.service.c
        Collection collec = Arrays.asList("Random string", 42);
        Object[] args_C = new Object[]{ref_A, "Service C", 3, collec};
        Definition def_C = new Definition("app.service.TestServiceC", args_C);
        
        ContainerBuilder builder = new ContainerBuilder();
        
        builder.addDefinition("test.service.a", def_A);
        builder.addDefinition("test.service.b", def_B);
        builder.addDefinition("test.service.c", def_C);
        
        builder.build();
        Container container = builder.getContainer();
        
        try {
            
            Service a = container.get("test.service.a", TestServiceA.class);
            Service b = container.get("test.service.b", TestServiceB.class);
            Service c = container.get("test.service.c", TestServiceC.class);
            
            System.out.println(a);
            System.out.println(b);
            System.out.println(c);
            
            a.sayHello();
            b.sayHello();
            c.sayHello();
            
        } catch (ContainerException ex) {
            Logger.getLogger(ContainerBuilderProgrammaticTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}