package org.smoothbuild.lang.base.type.impl;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.Lists.map;

import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.ItemSignature;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeNames;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class FunctionTypeImpl extends AbstractType implements FunctionType {
  private final Type result;
  private final ImmutableList<ItemSignature> parameters;

  public FunctionTypeImpl(Type result, ImmutableList<ItemSignature> parameters) {
    this(result, parameters, map(parameters, ItemSignature::type));
  }

  private FunctionTypeImpl(
      Type result, ImmutableList<ItemSignature> parameters, ImmutableList<Type> parameterTypes) {
    super(TypeNames.functionTypeName(result, parameters),
        FunctionType.calculateVariables(result, parameterTypes));
    this.result = requireNonNull(result);
    this.parameters = requireNonNull(parameters);
  }

  @Override
  public Type resultType() {
    return result;
  }

  @Override
  public ImmutableList<ItemSignature> parameters() {
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
