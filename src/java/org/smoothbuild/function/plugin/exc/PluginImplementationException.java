package org.smoothbuild.function.plugin.exc;

@SuppressWarnings("serial")
public class PluginImplementationException extends Exception {
  public PluginImplementationException(String message) {
    super(message);
  }

  public PluginImplementationException(String message, Throwable e) {
    super(message, e);
  }
}
