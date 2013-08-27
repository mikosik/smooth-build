package org.smoothbuild.function.plugin.exc;

@SuppressWarnings("serial")
public class ParamsImplementationException extends PluginImplementationException {

  public ParamsImplementationException(Class<?> klass, String message) {
    super(buildMessage(klass, message));
  }

  public ParamsImplementationException(Class<?> klass, String message, Throwable e) {
    super(buildMessage(klass, message), e);
  }

  private static String buildMessage(Class<?> klass, String message) {
    return "Java class '" + klass.getCanonicalName()
        + "' in not a correct function's parameters implementation:\n" + message;
  }
}
