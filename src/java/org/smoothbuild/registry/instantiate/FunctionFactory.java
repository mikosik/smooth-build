package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.exc.MissingNameException;

public class FunctionFactory {
  private final InstantiatorFactory instantiatorFactory;

  public FunctionFactory(InstantiatorFactory instantiatorFactory) {
    this.instantiatorFactory = instantiatorFactory;
  }

  public Function create(Class<? extends FunctionDefinition> klass)
      throws FunctionImplementationException {
    String name = getFunctionName(klass);
    Instantiator instantiator = instantiatorFactory.create(klass);

    return new Function(name, instantiator);
  }

  private static String getFunctionName(Class<? extends FunctionDefinition> klass)
      throws MissingNameException {
    FunctionName annotation = klass.getAnnotation(FunctionName.class);
    if (annotation == null) {
      throw new MissingNameException(klass);
    }
    return annotation.value();
  }
}
