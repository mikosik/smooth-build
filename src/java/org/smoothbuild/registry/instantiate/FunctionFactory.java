package org.smoothbuild.registry.instantiate;

import static org.smoothbuild.lang.function.FullyQualifiedName.fullyQualifiedName;

import org.smoothbuild.lang.function.FullyQualifiedName;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Type;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.exc.IllegalFunctionNameException;
import org.smoothbuild.registry.exc.IllegalReturnTypeException;
import org.smoothbuild.registry.exc.MissingNameException;
import org.smoothbuild.registry.exc.StrangeExecuteMethodException;

public class FunctionFactory {
  private final InstantiatorFactory instantiatorFactory;

  public FunctionFactory(InstantiatorFactory instantiatorFactory) {
    this.instantiatorFactory = instantiatorFactory;
  }

  public Function create(Class<? extends FunctionDefinition> klass)
      throws FunctionImplementationException {
    FullyQualifiedName name = getFunctionName(klass);
    Type type = getReturnType(klass);
    Instantiator instantiator = instantiatorFactory.create(klass);

    return new Function(name, type, instantiator);
  }

  private static FullyQualifiedName getFunctionName(Class<? extends FunctionDefinition> klass)
      throws MissingNameException, IllegalFunctionNameException {
    FunctionName annotation = klass.getAnnotation(FunctionName.class);
    if (annotation == null) {
      throw new MissingNameException(klass);
    }
    try {
      return fullyQualifiedName(annotation.value());
    } catch (IllegalArgumentException e) {
      throw new IllegalFunctionNameException(klass, e.getMessage());
    }
  }

  private static Type getReturnType(Class<? extends FunctionDefinition> klass)
      throws IllegalReturnTypeException, StrangeExecuteMethodException {
    Class<?> javaType;
    try {
      javaType = klass.getMethod("execute").getReturnType();
    } catch (NoSuchMethodException | SecurityException e) {
      throw new StrangeExecuteMethodException(klass, e);
    }
    Type type = Type.toType(javaType);
    if (type == null) {
      throw new IllegalReturnTypeException(klass, javaType);
    }
    return type;
  }
}
