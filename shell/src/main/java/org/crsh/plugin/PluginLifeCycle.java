package org.crsh.plugin;

import org.crsh.vfs.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The base class for managing the CRaSH life cycle.
 */
public abstract class PluginLifeCycle {

  /** . */
  protected final Logger log = Logger.getLogger(getClass().getName());

  /** . */
  private PluginContext context;

  /** . */
  private Properties config;

  public Properties getConfig() {
    return config;
  }

  public void setConfig(Properties config) {
    this.config = config;
  }

  public PluginContext getContext() {
    return context;
  }

  protected final void start(PluginContext context) throws IllegalStateException {
    log.info("binhnt: PluginLifeCycle.start: start with context");
    if (this.context != null) {
      throw new IllegalStateException("Already started");
    }
    
    // Get properties from system properties
    Properties config = new Properties();

    // Load properties from configuration file
    Resource res = context.loadResource("crash.properties", ResourceKind.CONFIG);
    if (res != null) {
      try {
        config.load(new ByteArrayInputStream(res.getContent()));
        log.log(Level.FINE, "Loaded properties from " + config);
      } catch (IOException e) {
        log.log(Level.WARNING, "Could not configure from crash.properties", e);
      }
    } else {
      log.log(Level.FINE, "Could not find crash.properties file");
    }

    // Override default properties from external config
    if (this.config != null) {
      config.putAll(this.config);
    }

    // Override default properties from command line
    for (PropertyDescriptor<?> desc : PropertyDescriptor.ALL.values()) {
      configureProperty(context, config, desc);
    }

    log.info("binhnt: PluginLifeCycle.start: Override default properties from plugin defined properties");
    // Override default properties from plugin defined properties.
    for (final CRaSHPlugin<?> plugin : context.manager.getPlugins()) {
      Iterable<PropertyDescriptor<?>> capabilities = plugin.getConfigurationCapabilities();
      Iterator<PropertyDescriptor<?>> i = capabilities.iterator();
      if (i.hasNext()) {
        while (i.hasNext()) {
          PropertyDescriptor<?> descriptor = i.next();
          log.fine("Adding plugin " + plugin + " property " + descriptor.getName());
          configureProperty(context, config, descriptor);
        }
      } else {
        log.fine("Plugin " + plugin + " does not declare any configuration property");
      }
      log.info("binhnt: PluginLifeCycle.start: End");
    }

    //
    context.start();

    //
    this.context = context;
  }

  public final void stop() throws IllegalStateException {
    if (context == null) {
      throw new IllegalStateException("Not started");
    }
    PluginContext context = this.context;
    this.context = null;
    context.stop();
  }

  private void configureProperty(PluginContext context, Properties props, PropertyDescriptor<?> desc) {
    String key = "crash." + desc.name;
    String value = props.getProperty(key);
    if (value != null) {
      try {
        if (context.getProperty(desc) == null) {
          log.log(Level.INFO, "Configuring property " + desc.name + "=" + value + " from properties");
          context.setProperty(desc, value);
        }
      }
      catch (IllegalArgumentException e) {
        log.log(Level.SEVERE, "Could not configure property", e);
      }
    }
  }
}
