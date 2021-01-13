package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class FunctionType extends Type {
  private final Type result;
  private final ImmutableList<ItemSignature> parameters;

  public FunctionType(Type result, ImmutableList<ItemSignature> parameters) {
    this(result, parameters, map(parameters, ItemSignature::type));
  }

  private FunctionType(
      Type result, ImmutableList<ItemSignature> parameters, ImmutableList<Type> parameterTypes) {
    super(
        createName(result, parameters),
        createTypeConstructor(parameterTypes),
        ImmutableList.of(result),
        parameterTypes,
        createIsPolytype(result, parameterTypes));
    this.result = requireNonNull(result);
    this.parameters = requireNonNull(parameters);
  }

  private static TypeConstructor createTypeConstructor(ImmutableList<Type> parameters) {
    return new TypeConstructor("()", 1, parameters.size(),
        (covar, contravar) -> new FunctionType(covar.get(0), toItemSignatures(contravar)));
  }

  private static boolean createIsPolytype(Type resultType, ImmutableList<Type> parameters) {
    return resultType.isPolytype() || parameters.stream().anyMatch(Type::isPolytype);
  }

  public Type resultType() {
    return result;
  }

  public ImmutableList<ItemSignature> parameters() {
    return parameters;
  }

  public List<Type> parameterTypes() {
    return map(parameters, ItemSignature::type);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object instanceof FunctionType that) {
      return result.equals(that.result)
          && parameters.equals(that.parameters);
    }
    return false;
  }

  private static String createName(Type resultType, ImmutableList<ItemSignature> parameters) {
    String parametersString = parameters
        .stream()
        .map(ItemSignature::toString)
        .collect(joining(", "));
    return resultType.name() + "(" + parametersString + ")";
  }
}
