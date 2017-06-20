package com.marvin.component.resolver;

@FunctionalInterface
public interface Resolver<I, O> {
    O resolve(I object) throws Exception;
}
