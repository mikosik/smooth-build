package org.smoothbuild.registry;

import java.util.Map;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.registry.exc.FunctionAlreadyRegisteredException;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.instantiate.Function;
import org.smoothbuild.registry.instantiate.FunctionFactory;

import com.google.common.collect.Maps;

public class FunctionsRegistry {
  private final FunctionFactory functionFactory;
  private final Map<String, Function> map = Maps.newHashMap();

  public FunctionsRegistry(FunctionFactory functionFactory) {
    this.functionFactory = functionFactory;
  }

  public void register(Class<? extends FunctionDefinition> klass) throws FunctionImplementationException,
      FunctionAlreadyRegisteredException {
    Function function = functionFactory.create(klass);
    String name = function.name();
    if (containsType(name)) {
      // TODO add more details to the exception: function definition class,
      // builtin/plugin, plugin jar file
      throw new FunctionAlreadyRegisteredException(name);
    } else {
      map.put(name, function);
    }
  }

  public boolean containsType(String name) {
    return map.containsKey(name);
  }

  public Function getType(String name) {
    Function function = map.get(name);
    if (function == null) {
      throw new IllegalArgumentException("Function '" + name + "' doesn't exist.");
    }
    return function;
  }
}
