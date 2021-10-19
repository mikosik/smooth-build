package org.smoothbuild.lang.base.type.impl;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.type.api.TypeNames.functionTypeName;
import static org.smoothbuild.lang.base.type.help.FunctionTypeImplHelper.calculateVariables;

import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class FunctionTypeImpl extends AbstractTypeImpl implements FunctionType {
  private final Type result;
  private final ImmutableList<Type> parameters;

  public FunctionTypeImpl(Type result, ImmutableList<Type> parameters) {
    super(functionTypeName(result, parameters), calculateVariables(result, parameters));
    this.result = requireNonNull(result);
    this.parameters = requireNonNull(parameters);
  }

  @Override
  public Type result() {
    return result;
  }

  @Override
  public ImmutableList<Type> parameters() {
    return parameters;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof FunctionTypeImpl that
        && result.equals(that.result)
        && parameters.equals(that.parameters);
  }
}
