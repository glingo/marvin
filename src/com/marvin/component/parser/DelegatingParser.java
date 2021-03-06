package com.marvin.component.parser;

public class DelegatingParser {
    
    ParserResolver resolver;

    public DelegatingParser(ParserResolver resolver) {
        this.resolver = resolver;
    }

    public Object parse(Object type, Object parsed) {
        return resolver.resolve(type).parse(parsed);
    }
    
}
