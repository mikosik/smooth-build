package org.smoothbuild.function.plugin.exc;

@SuppressWarnings("serial")
public class FunctionClassImplementationException extends PluginImplementationException {
  public FunctionClassImplementationException(Class<?> klass, String message) {
    super(buildMessage(klass, message));
  }

  public FunctionClassImplementationException(Class<?> klass, String message, Throwable e) {
    super(buildMessage(klass, message), e);
  }

  private static String buildMessage(Class<?> klass, String message) {
    return "Java class '" + klass.getCanonicalName()
        + "' has no correct Smooth function implementations:\n" + message;
  }
}
