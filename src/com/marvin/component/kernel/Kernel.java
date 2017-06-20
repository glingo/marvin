package com.marvin.component.kernel;


import com.marvin.bundle.framework.Application;
import static com.marvin.bundle.framework.Application.create;
import com.marvin.component.container.ContainerBuilder;
import com.marvin.component.container.IContainer;
import com.marvin.component.container.xml.XMLDefinitionReader;
import com.marvin.component.container.extension.ExtensionInterface;

import com.marvin.component.io.IResource;
import com.marvin.component.io.loader.ClassPathResourceLoader;
import com.marvin.component.io.loader.ResourceLoader;
import com.marvin.component.kernel.bundle.Bundle;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.logging.LogManager;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public abstract class Kernel {
    
    protected static final String VERSION = "v0.1";
    
    protected final Logger logger = Logger.getLogger(getClass().getName());
    
    private String rootDir;
    private String environment;
    
    private boolean booted = false;
    private boolean debug  = false;
    
    private Map<String, Bundle> bundles;
    
    private IContainer container;
    private ResourceLoader resourceLoader;
    
    public Kernel() {
        this("dev", true);
    }

    public Kernel(String environment, boolean debug) {
        this.environment = environment;
        this.debug = debug;
    }
    
    abstract protected List<Bundle> registerBundles();
    
    public Map<String, Bundle> getBundles() {
        if(this.bundles == null) {
            initializeBundles();
        }
        return this.bundles;
    }
    
    public Bundle getBundle(String name){
        return getBundles().getOrDefault(name, null);
    }

    public void boot() {
        if (isBooted()) {
            this.logger.warning("Kernel is already booted ...");
            return;
        }
        
        registerLoggingConfiguration();

        initializeBundles();
        
        initializeContainer();

        this.bundles.values().forEach((Bundle bundle) -> {
            bundle.setContainer(container);
            bundle.boot();
        });

        this.booted = true;
    }
    
    protected void registerLoggingConfiguration() {
        try {
            IResource resource = getResourceLoader().load(String.format("./config/logging_%s.properties", getEnvironment()));
            LogManager.getLogManager().readConfiguration(resource.getInputStream());
        } catch(IOException e) {
            this.logger.info("Unable to load any logging configuration.");
        }
    }
    
    protected void initializeBundles() {
        this.bundles = registerBundles().stream()
            .collect(Collectors.toMap(Bundle::getName, Function.identity()));
    }
    
    
    protected void prepareContainer(ContainerBuilder builder) {
        
        // add kernel parameters.
        builder.addParameters(getKernelParameters());
        
        // register extensions
        this.bundles.values().forEach((Bundle bundle) -> {
            ExtensionInterface extension = bundle.getContainerExtension();
            builder.registerExtension(extension);
        });
    }
    
    protected Map<String, Object> getKernelParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("kernel.root_dir", getRootDir());
        
        // Add more parameters here
        
        return parameters;
    }
    
    protected void initializeContainer() {
        ContainerBuilder builder = new ContainerBuilder();
        prepareContainer(builder);
        
        registerContainerConfiguration(builder);

        this.bundles.values().forEach((Bundle bundle) -> {
            bundle.build(builder);
        });
        
        // Inject the kernel as a service.
        builder.set("kernel", this);
        // Inject the container as a service.
        builder.set("container", builder.getContainer());
        builder.set("kernel.resource_loader", getResourceLoader());

        this.container = builder.build();

        // Inject a thread_pool
//        builder.set("thread_pool", Executors.newFixedThreadPool(THREAD));
    }
    
    protected void registerContainerConfiguration(ContainerBuilder builder) {
        // TODO Add config readers
        
        // maybe add more reader here
        // like an yml reader, Configuration class ...
        XMLDefinitionReader reader  = new XMLDefinitionReader(getResourceLoader(), builder);
        String location = String.format("./config/config_%s.xml", getEnvironment());
        reader.read(location);
    }
    
    private ResourceLoader getResourceLoader() {
        if (this.resourceLoader == null) {
            this.resourceLoader = new ClassPathResourceLoader(getClass());
        }
        return this.resourceLoader;
    }
    
    public void terminate() {
        this.logger.info("Terminating kernel ...");
    }

    /* GETTERS and SETTERS */
    
    public boolean isBooted() {
        return this.booted;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public IContainer getContainer() {
        return this.container;
    }
    
    public String getEnvironment(){
        return this.environment;
    }
    
    public String getRootDir() {
        if(this.rootDir == null) {
            this.rootDir = getClass().getPackage().getName().replaceAll("\\.", "/");
        }
        return this.rootDir;
    }
}
