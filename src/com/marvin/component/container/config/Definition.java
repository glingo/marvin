package com.marvin.component.container.config;

/**
 *
 * @author cdi305
 */
public class Definition {
    
    protected String scope;
    
    protected String className;
    
    protected String factoryMethodName;
    
    protected String parentName;
    
    protected String description;
    
    protected Boolean deprecated;
    
    protected String[] aliases;
    
    protected Object[] arguments;

    public Definition() {}
    
    public Definition(String className, Object[] arguments) {
        this.className = className;
        this.arguments = arguments;
    }
    
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFactoryMethodName() {
        return factoryMethodName;
    }

    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
    
}