package org.smoothbuild.function.exc;

import java.lang.reflect.Method;

import org.smoothbuild.lang.type.ExecuteMethod;

@SuppressWarnings("serial")
public class StaticExecuteMethodException extends FunctionImplementationException {

  public StaticExecuteMethodException(Class<?> klass, Method method) {
    super(klass, "Method " + method.getName() + "\nannotated with @"
        + ExecuteMethod.class.getName() + "\nin class " + klass.getCanonicalName()
        + "\nshould be non static");
  }
}
