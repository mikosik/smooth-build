package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.exc.MissingNameException;

public class FunctionTypeFactory {
  private final InstantiatorFactory instantiatorFactory;

  public FunctionTypeFactory(InstantiatorFactory instantiatorFactory) {
    this.instantiatorFactory = instantiatorFactory;
  }

  public FunctionType create(Class<? extends Function> klass)
      throws FunctionImplementationException {
    String name = getFunctionName(klass);
    Instantiator instantiator = instantiatorFactory.create(klass);

    return new FunctionType(name, instantiator);
  }

  private static String getFunctionName(Class<? extends Function> klass)
      throws MissingNameException {
    FunctionName annotation = klass.getAnnotation(FunctionName.class);
    if (annotation == null) {
      throw new MissingNameException(klass);
    }
    return annotation.value();
  }
}
