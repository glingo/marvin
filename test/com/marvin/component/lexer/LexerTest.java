/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marvin.component.lexer;

import com.marvin.component.resource.ResourceService;
import com.marvin.component.resource.loader.ClasspathResourceLoader;
import com.marvin.component.resource.loader.FileResourceLoader;
import com.marvin.component.resource.reference.ResourceReference;
import com.marvin.component.templating.lexer.Lexer;
import com.marvin.component.templating.lexer.Syntax;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cdi305
 */
public class LexerTest {
    
    public static void main(String[] args) {
        ResourceService resourceService = ResourceService.builder()
                .with(ResourceReference.FILE, FileResourceLoader.instance())
                .with(ResourceReference.CLASSPATH, new ClasspathResourceLoader(ClassLoader.getSystemClassLoader()))
                .build();
        
        try (InputStream is = resourceService.load("com/marvin/config/view")) {
            Syntax syntax = new Syntax();
            Lexer lexer = new Lexer(syntax, null, null);
            InputStreamReader isr = new InputStreamReader(is);
            Reader reader = new BufferedReader(isr);
            
            lexer.tokenize(reader, "com/marvin/config/view");
            
            System.out.println(lexer.getTokens());
            
        } catch (Exception ex) {
            Logger.getLogger(LexerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
