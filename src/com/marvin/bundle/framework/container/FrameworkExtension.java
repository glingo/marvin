package com.marvin.bundle.framework.container;

import com.marvin.bundle.framework.FrameworkBundle;

import com.marvin.component.configuration.ConfigurationInterface;
import com.marvin.component.container.ContainerBuilder;
import com.marvin.component.container.extension.Extension;
import com.marvin.component.container.xml.XMLDefinitionReader;
import com.marvin.component.io.loader.ClassPathResourceLoader;
import com.marvin.component.io.loader.ResourceLoader;
import com.marvin.component.io.xml.XMLReader;

import java.util.Map;

public class FrameworkExtension extends Extension {

    @Override
    public void load(Map<String, Object> configs, ContainerBuilder builder) {
        ResourceLoader loader = new ClassPathResourceLoader(FrameworkBundle.class);
        XMLReader reader = new XMLDefinitionReader(loader, builder);

        reader.read("resources/config/services.xml");
//            reader.read("resources/config/templating.xml");
//            reader.read("resources/config/routing.xml");
//            reader.read("resources/config/web.xml");
//            reader.read("resources/config/shell.xml");
//            reader.read("resources/config/server.xml");

        ConfigurationInterface configuration = getConfiguration();
        Map<String, Object> config = processConfiguration(configuration, configs);

        registerRouterConfiguration(config.get("router"), builder);
        registerTemplatingConfiguration(config.get("templating"), builder);

//            HashMap<String, Definition> taggedDefinitions = builder.findTaggedDefinitions("event_subscriber");
//            for (Map.Entry<String, Definition> entrySet : taggedDefinitions.entrySet()) {
//                String id = entrySet.getKey();
//                Definition definition = entrySet.getValue();
//                
//            }
    }

    private void registerRouterConfiguration(Object config, ContainerBuilder builder) {
        if(!(config instanceof Map)) {
            return;
        }
        
        Map<String, Object> conf = (Map<String, Object>) config;
        builder.addParameter("router.resource", conf.get("resource"));
    }

    private void registerTemplatingConfiguration(Object config, ContainerBuilder builder) {
        if(!(config instanceof Map)) {
            return;
        }
        
        Map<String, Object> conf = (Map<String, Object>) config;
        builder.addParameter("templating.resource", conf.getOrDefault("resource", "/resources/view/"));
    }
}
