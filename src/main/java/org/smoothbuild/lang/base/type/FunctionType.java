package org.smoothbuild.lang.base.type;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.lang.base.type.Bounds.unbounded;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.Collection;
import java.util.List;

import org.smoothbuild.util.Sets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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
        list(result),
        parameterTypes,
        calculateVariables(result, parameterTypes));
    this.result = requireNonNull(result);
    this.parameters = requireNonNull(parameters);
  }

  private static TypeConstructor createTypeConstructor(ImmutableList<Type> parameters) {
    return new TypeConstructor("()", 1, parameters.size(),
        (covar, contravar) -> new FunctionType(covar.get(0), toItemSignatures(contravar)));
  }

  private static ImmutableSet<Variable> calculateVariables(Type resultType,
      ImmutableList<Type> parameters) {
    return concat(resultType, parameters).stream()
        .map(Type::variables)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
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

  public Type inferResultType(List<Type> argumentTypes) {
    var boundedVariables = inferVariableBoundsInCall(resultType(), parameterTypes(), argumentTypes);
    return resultType().mapVariables(boundedVariables, LOWER);
  }

  public static BoundsMap inferVariableBoundsInCall(Type resultTypes,
      List<Type> parameterTypes, List<Type> argumentTypes) {
    var boundedVariables = inferVariableBounds(parameterTypes, argumentTypes, LOWER);
    var resultVariables = Sets.map(resultTypes.variables(), v -> new Bounded(v, unbounded()));
    return boundedVariables.mergeWith(resultVariables);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof FunctionType that
        && result.equals(that.result)
        && parameters.equals(that.parameters);
  }

  private static String createName(Type resultType, ImmutableList<ItemSignature> parameters) {
    String parametersString = parameters
        .stream()
        .map(ItemSignature::toString)
        .collect(joining(", "));
    return resultType.name() + "(" + parametersString + ")";
  }
}
