package org.smoothbuild.registry;

import java.util.Map;

import org.smoothbuild.lang.function.Function;
import org.smoothbuild.registry.exc.FunctionAlreadyRegisteredException;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.instantiate.FunctionType;
import org.smoothbuild.registry.instantiate.FunctionTypeFactory;

import com.google.common.collect.Maps;

public class FunctionTypesRegistry {
  private final FunctionTypeFactory functionTypeFactory;
  private final Map<String, FunctionType> map = Maps.newHashMap();

  public FunctionTypesRegistry(FunctionTypeFactory functionTypeFactory) {
    this.functionTypeFactory = functionTypeFactory;
  }

  public void register(Class<? extends Function> klass) throws FunctionImplementationException,
      FunctionAlreadyRegisteredException {
    FunctionType functionType = functionTypeFactory.create(klass);
    String name = functionType.name();
    if (containsType(name)) {
      // TODO add more details to the exception: function definition class,
      // builtin/plugin, plugin jar file
      throw new FunctionAlreadyRegisteredException(name);
    } else {
      map.put(name, functionType);
    }
  }

  public boolean containsType(String name) {
    return map.containsKey(name);
  }

  public FunctionType getType(String name) {
    FunctionType functionType = map.get(name);
    if (functionType == null) {
      throw new IllegalArgumentException("Function '" + name + "' doesn't exist.");
    }
    return functionType;
  }
}
