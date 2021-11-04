package org.smoothbuild.lang.base.type.impl;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.type.api.TypeNames.functionTypeName;
import static org.smoothbuild.lang.base.type.help.FunctionTypeImplHelper.calculateVariables;

import org.smoothbuild.lang.base.type.api.FunctionType;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class FunctionSType extends TypeS implements FunctionType {
  private final TypeS result;
  private final ImmutableList<TypeS> parameters;

  public FunctionSType(TypeS result, ImmutableList<TypeS> parameters) {
    super(functionTypeName(result, parameters), calculateVariables(result, parameters));
    this.result = requireNonNull(result);
    this.parameters = requireNonNull(parameters);
  }

  @Override
  public TypeS result() {
    return result;
  }

  @Override
  public ImmutableList<TypeS> parameters() {
    return parameters;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof FunctionSType that
        && result.equals(that.result)
        && parameters.equals(that.parameters);
  }
}
