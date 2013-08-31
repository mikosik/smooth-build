package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

import org.smoothbuild.plugin.ExecuteMethod;

@SuppressWarnings("serial")
public class MissingNameException extends FunctionImplementationException {
  public MissingNameException(Method method) {
    super(method, "It should be annotated with @" + ExecuteMethod.class.getCanonicalName() + ".");
  }
}
