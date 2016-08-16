package com.marvin.bundle.framework.container;

import com.marvin.component.configuration.ConfigurationInterface;
import com.marvin.component.container.ContainerBuilder;
import com.marvin.component.container.extension.Extension;
import com.marvin.component.container.xml.XMLDefinitionReader;
import com.marvin.component.io.loader.ClassPathResourceLoader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cdi305
 */
public class FrameworkExtension extends Extension {

    @Override
    public void load(HashMap<String, Object> configs, ContainerBuilder builder) {
        try {
            
            ClassPathResourceLoader loader = new ClassPathResourceLoader(this.getClass());
            XMLDefinitionReader reader = new XMLDefinitionReader(builder, loader);
            
            reader.read("../resources/config/services.xml");
        
            ConfigurationInterface configuration = this.getConfiguration();
            HashMap<String, Object> conf = this.processConfiguration(configuration, configs);
            
            System.out.println(conf);
        } catch (Exception ex) {
            Logger.getLogger(FrameworkExtension.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}