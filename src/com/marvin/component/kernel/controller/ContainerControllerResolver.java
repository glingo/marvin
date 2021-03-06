package com.marvin.component.kernel.controller;

import com.marvin.component.container.awareness.ContainerAwareInterface;
import com.marvin.component.util.ClassUtils;
import java.lang.reflect.Method;

public abstract class ContainerControllerResolver<T> extends ControllerResolver<T> {

    private final ControllerNameParser parser;
    
    public ContainerControllerResolver(ControllerNameParser parser) {
        assert parser != null : "ControllerNamePArser could not be null";
//        if(parser == null) {
//            String msg = "ControllerNamePArser could not be null";
//            throw new IllegalArgumentException(msg);
//        }
        this.parser = parser;
    }

    @Override
    protected ControllerReference instantiateController(Class controller, Method meth) throws Exception {
        ControllerReference reference = super.instantiateController(controller, meth);
        
        if (reference.getHolder() instanceof ContainerAwareInterface) {
            this.logger.info("This is a ContainerAware, set the container.");
            ((ContainerAwareInterface) reference.getHolder()).setContainer(getContainer());
        }
        
        return reference;
    }
    
    @Override
    protected ControllerReference createController(String name) throws Exception {
        
        if(!name.contains("::")) {
            
            if(name.contains(":")){
                String[] fragments = name.split(":");
                
                switch(fragments.length) {
                    case 3 :    // a:b:c notation
                        name = this.parser.parse(name);
                        break;
                    case 2:     // service:method notation
                        this.logger.info("Resolving a controller from container");
                        Object service = getContainer().get(fragments[0]);
                        Method action = ClassUtils.getMethod(service.getClass(), fragments[1], new Class[]{});
                        return new ControllerReference(service, action);
                }
            }
        }
        
        return super.createController(name);
    }
    

}
