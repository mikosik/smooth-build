package org.smoothbuild.registry.instantiate;

import java.util.Map;

import org.smoothbuild.lang.function.FullyQualifiedName;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.Type;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.type.Path;

import com.google.common.collect.ImmutableMap;

public class Function {
  private final FullyQualifiedName name;
  private final Type type;
  private final Instantiator instantiator;

  public Function(FullyQualifiedName name, Type type, Instantiator instantiator) {
    this.name = name;
    this.type = type;
    this.instantiator = instantiator;
  }

  public FullyQualifiedName name() {
    return name;
  }

  public Type type() {
    return type;
  }

  // TODO add tests for execute once it is fixed/simplified
  public Object execute(Path resultDir, ImmutableMap<String, Expression> arguments)
      throws FunctionException {
    FunctionDefinition functionDefinition = instantiator.newInstance(resultDir);
    Params params = functionDefinition.params();
    for (Map.Entry<String, Expression> entry : arguments.entrySet()) {
      String argName = entry.getKey();
      Object argValue = entry.getValue().result();
      Param<?> param = params.param(argName);
      setParamValue(param, argValue);
    }

    return functionDefinition.execute();
  }

  private static <T> void setParamValue(Param<T> param, Object argValue) {
    @SuppressWarnings("unchecked")
    T castValue = (T) argValue;
    param.set(castValue);
  }
}
