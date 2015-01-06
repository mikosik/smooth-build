package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

public class DuplicatedAnnotationException extends NativeFunctionImplementationException {
  public DuplicatedAnnotationException(Method method, Class<?> annotationClass) {
    super(method, "One of its parameters is annotated twice as " + annotationClass.getName());
  }
}
