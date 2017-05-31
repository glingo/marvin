package com.marvin.component.mvc.controller.argument;

import com.marvin.component.mvc.controller.ControllerReference;
import java.util.List;

public interface ArgumentResolverInterface {
    
    List<Object> getArguments(Object request, Object response, ControllerReference controller);
    
}
