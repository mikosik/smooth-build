package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;
import java.util.function.BiFunction;

import com.google.common.collect.ImmutableList;

public class TypeConstructor {
  private final String name;
  private final int covariantSize;
  private final int contravariantSize;
  private final BiFunction<ImmutableList<Type>, ImmutableList<Type>, Type> factoryMethod;

  public TypeConstructor(String name) {
    this(name, 0, 0, (covariants, contravariants) -> {
      throw new UnsupportedOperationException(
          "Type " + name + " doesn't have type constructor");
    });
  }

  public TypeConstructor(String name, int covariantSize, int contravariantSize,
      BiFunction<ImmutableList<Type>, ImmutableList<Type>, Type> factoryMethod) {
    this.name = name;
    this.covariantSize = covariantSize;
    this.contravariantSize = contravariantSize;
    this.factoryMethod = factoryMethod;
  }

  public Type construct(ImmutableList<Type> covariants, ImmutableList<Type> contravariants) {
    checkArgument(covariants.size() == covariantSize);
    checkArgument(contravariants.size() == contravariantSize);
    return factoryMethod.apply(covariants, contravariants);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof TypeConstructor that
        && name.equals(that.name)
        && contravariantSize == that.contravariantSize
        && covariantSize == that.covariantSize;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, covariantSize, contravariantSize);
  }
}
