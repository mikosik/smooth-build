package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.FunctionDefinition;

@SuppressWarnings("serial")
public class FunctionImplementationException extends Exception {

  public FunctionImplementationException(Class<? extends FunctionDefinition> klass, String message) {
    super(buildMessage(klass, message));
  }

  private static String buildMessage(Class<? extends FunctionDefinition> klass, String message) {
    return "Java class '" + klass.getCanonicalName()
        + "' in not a correct smooth function implementation:\n" + message;
  }
}
