package org.smoothbuild.function.exc;

import org.smoothbuild.function.Type;

import com.google.common.base.Joiner;

@SuppressWarnings("serial")
public class IllegalReturnTypeException extends FunctionImplementationException {

  public IllegalReturnTypeException(Class<?> klass, Class<?> returnType) {
    super(klass, "Return type of execute() method is illegal '" + returnType.getCanonicalName()
        + "'.\n Only following types are allowed: " + allowedTypes() + ".");
  }

  private static String allowedTypes() {
    return "{" + Joiner.on(", ").join(Type.allJavaTypes()) + "}";
  }
}
