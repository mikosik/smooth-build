package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

import org.smoothbuild.lang.plugin.Name;

public class MissingNameAnnotationException extends FunctionImplementationException {
  public MissingNameAnnotationException(Method method) {
    super(method, "One of its parameter doesn't have " + Name.class.getSimpleName()
        + " annotation.");
  }
}
