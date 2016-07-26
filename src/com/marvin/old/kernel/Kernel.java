package com.marvin.old.kernel;

import com.marvin.old.kernel.controller.Controller;
import com.marvin.old.event.dispatcher.Dispatcher;
import com.marvin.old.event.Event;
import com.marvin.old.event.EventDispatcher;
import com.marvin.old.dependency.Container;
import com.marvin.old.dependency.ContainerBuilder;
import com.marvin.old.dependency.loader.ContainerLoader;
import com.marvin.old.kernel.bundle.Bundle;
import com.marvin.old.kernel.controller.ControllerResolver;
import com.marvin.old.kernel.event.KernelEvent;
import com.marvin.old.kernel.event.KernelEvents;
import com.marvin.old.locator.FileLocator;
import com.marvin.old.locator.QueuedLocator;
import com.marvin.old.logger.Logger;
import com.marvin.old.routing.Route;
import com.marvin.old.routing.Router;
import com.marvin.old.routing.RouterBuilder;
import com.marvin.old.routing.loader.RouterLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;

import java.net.URL;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Le Kernel va handle un inputstream et repondre sur un outputstream.
 * @author Dr.Who
 */
public abstract class Kernel {
    
    protected static final int thread = 10;
    protected boolean booted = false;
    protected Map<String, Bundle> bundles = new ConcurrentHashMap<>();
    protected ControllerResolver resolver = new ControllerResolver();
    
    /** c'est un service */
    protected Logger logger = new Logger();
    /** c'est un service */
    protected Dispatcher<Event> dispatcher = new EventDispatcher();
    /** c'est un service */
    protected Container container;
    /** c'est un service */
    protected Router router;

    public Kernel() {
        super();
    }

    abstract protected Bundle[] registerBundles();
    
    protected URL getConfigURL(){
        return this.getClass().getResource("config");
    }
    
    public void boot() {
        
        if(this.isBooted()) {
            return;
        }
        
        this.dispatcher.dispatch(KernelEvents.BEFORE_LOAD, new KernelEvent(this));
        logger.log("kernel booting ...");
        
        URL resource = this.getConfigURL();
        QueuedLocator locator = new QueuedLocator(resource.getPath());
        
        this.initializeContainer(locator);
        this.initializeBundles();
        this.initializeRouter(locator);
        
        this.booted = true;
        
        logger.log("kernel booted");
        this.dispatcher.dispatch(KernelEvents.AFTER_LOAD, new KernelEvent(this));
    }
    
    protected void initializeBundles() {
        logger.log("kernel initializeBundles ...");
        Collector<Bundle, ?, ConcurrentMap<String, Bundle>> c = Collectors.toConcurrentMap(Bundle::getName, Bundle::boot);
        this.bundles = Arrays.asList(this.registerBundles()).stream().collect(c);
    }
    
    protected void initializeContainer(FileLocator locator) {
        logger.log("kernel initialize Container ...");
        
        ContainerBuilder builder = new ContainerBuilder();
        ContainerLoader loader = new ContainerLoader(locator, builder);
        
//        loader.load("parameters.xml");
        loader.load("config.xml");
//        loader.load("routing.xml");
        
        this.container = builder.build();
        
        // Inject the kernel as a service
        this.container.set("kernel", this);
        // Inject the container as a service
        this.container.set("container", container);
        // Inject the kernel as a service
        this.container.set("logger", this.logger);
        // Inject an event dispatcher
        this.container.set("event_dispatcher", this.dispatcher);
        // Inject a thread_pool
        this.container.set("thread_pool", Executors.newFixedThreadPool(thread));
    }
    
    protected void initializeRouter(FileLocator locator){
        
        RouterBuilder builder = new RouterBuilder();
        RouterLoader loader = new RouterLoader(locator, builder);
        loader.load("routing.xml");
        
        this.router = builder.build();
        // Inject the router as a service
        container.set("router", this.router);
    }
    
    public void terminate() {
//        this.dispatcher.dispatch(KernelEvents.TERMINATE, new KernelEvent(this));
//        this.pool.shutdown();
    }
    
    
    public void handle(String row, PrintWriter writer) throws Exception {
        // Toute cette partie est la recuperation de la Requette.
        String uri = null;
        Matcher matcher = Pattern.compile("^([A-Z]+) (\\p{Graph}+) ?((HTTP/[0-9\\.]+)?)$").matcher(row);

        if (matcher.find()) {
            String method = matcher.group(1);
            uri = matcher.group(2);
            String protocol = matcher.group(3);
        }
        
        if(uri == null && row.matches("^(\\p{Graph}+)$")) {
            uri = row;
        }
        
        if(uri != null) {
            this.boot();
            
            Route route = this.router.find(row);
            
            if(route == null || route.getController() == null){
                writer.format("Sorry we could find a route for %s\n", row);
                return;
            }
            
            Controller controller = this.resolver.createController(route.getController());
            
            if(controller == null){
                writer.format("No controller set for %s\n", route.getName());
                return;
            }
            
            controller.run();
        }
        
        writer.flush();
        
    }
    
    public void handle(BufferedReader reader, PrintWriter writer) throws Exception {

        String line = reader.readLine();

        while (line != null && !"".equals(line) && !System.lineSeparator().equals(line) && !line.equals("quit")) {
            this.handle(line, writer);
            line = reader.readLine();
        }
        
    }
    
    public void handle(Reader reader, PrintWriter writer) throws Exception{
        BufferedReader buffered = new BufferedReader(reader);
        this.handle(buffered, writer);
    }
    
    public void handle(InputStream in, OutputStream out) throws Exception{
        PrintWriter writer = new PrintWriter(out, true);
        Reader reader = new InputStreamReader(in);
        this.handle(reader, writer);
    }
    
    public void handle(InputStream in, OutputStream out, OutputStream err) throws Exception {
        this.handle(in, out);
        out.flush();
    }
    
    public boolean isBooted() {
        return this.booted;
    }
    
    public Container getContainer(){
        return this.container;
    }
    
}