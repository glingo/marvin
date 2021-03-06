package com.marvin.component.container.compiler.passes;

import com.marvin.component.container.ContainerBuilder;
import java.util.function.Consumer;

public interface CompilerPassInterface extends Consumer<ContainerBuilder> {

    @Override
    public void accept(ContainerBuilder builder);
}
