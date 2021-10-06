package org.smoothbuild.lang.base.type;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.lang.base.type.ItemSignature.itemSignature;
import static org.smoothbuild.util.Lists.allMatch;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.smoothbuild.lang.base.type.Sides.Side;

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

  public ImmutableList<Type> parameterTypes() {
    return map(parameters, ItemSignature::type);
  }

  @Override
  public boolean contains(Type that) {
    return this.equals(that)
        || result.contains(that)
        || parameters.stream().anyMatch(p -> p.type().contains(that));
  }

  @Override
  Type mapVariables(BoundsMap boundsMap, Side side, TypeFactory typeFactory) {
    if (isPolytype()) {
      var newResultType = result.mapVariables(boundsMap, side, typeFactory);
      var newParameters = map(parameters,
          i -> itemSignature(i.type().mapVariables(boundsMap, side.reversed(), typeFactory)));
      return createFunctionType(newResultType, newParameters, typeFactory);
    } else {
      return this;
    }
  }

  @Override
  Type strip(TypeFactory typeFactory) {
    var resultTypeStripped = result.strip(typeFactory);
    var parametersStripped = map(parameters, i -> itemSignature(i.type().strip(typeFactory)));
    return createFunctionType(resultTypeStripped, parametersStripped, typeFactory);
  }

  private FunctionType createFunctionType(Type resultType, ImmutableList<ItemSignature> parameters,
      TypeFactory typeFactory) {
    if (isFunctionTypeEqual(resultType, parameters)) {
      return this;
    }
    return typeFactory.function(resultType, parameters);
  }

  private boolean isFunctionTypeEqual(Type resultType,
      ImmutableList<ItemSignature> parameters) {
    return result == resultType && this.parameters.equals(parameters);
  }

  @Override
  protected boolean inequalByConstruction(Type that, Side side,
      BiFunction<Type, Type, Function<Side, Boolean>> f) {
    return that instanceof FunctionType thatFunction
        && f.apply(this.result, thatFunction.result).apply(side)
        && allMatch(
            this.parameterTypes(),
            thatFunction.parameterTypes(),
            (a, b) -> f.apply(a, b).apply(side.reversed()));
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
