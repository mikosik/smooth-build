package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.define.Item.toItemSignatures;

import java.util.Optional;

import org.smoothbuild.lang.base.like.CallableLike;
import org.smoothbuild.lang.base.like.ItemLike;
import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Callable extends Referencable implements CallableLike {
  public static final String PARENTHESES = "()";
  private final Type resultType;
  private final ImmutableList<Item> parameters;

  public Callable(Type resultType, String name, ImmutableList<Item> parameters, Location location) {
    super(functionType(resultType, parameters), name, location);
    this.resultType = requireNonNull(resultType);
    this.parameters = requireNonNull(parameters);
  }

  private static FunctionType functionType(Type resultType, ImmutableList<Item> parameters) {
    return new FunctionType(resultType, toItemSignatures(parameters));
  }

  @Override
  public FunctionType type() {
    return (FunctionType) super.type();
  }

  @Override
  public String extendedName() {
    return name() + PARENTHESES;
  }

  public Type resultType() {
    return resultType;
  }

  @Override
  public Optional<Type> inferredResultType() {
    return Optional.of(resultType);
  }

  public ImmutableList<Item> parameters() {
    return parameters;
  }

  @Override
  public ImmutableList<? extends ItemLike> parameterLikes() {
    return parameters;
  }

  @Override
  public ImmutableList<ItemSignature> parameterSignatures() {
    return type().parameters();
  }

  public boolean canBeCalledArgless() {
    return parameters.stream()
        .allMatch(p -> p.defaultValue().isPresent());
  }

  public abstract Expression createCallExpression(ImmutableList<Expression> arguments,
      Location location);
}
