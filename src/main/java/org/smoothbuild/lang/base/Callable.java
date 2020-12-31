package org.smoothbuild.lang.base;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Callable extends Declared {
  public static final String PARENTHESES = "()";
  private final Type resultType;
  private final ImmutableList<Item> parameters;

  public Callable(Type resultType, String name, ImmutableList<Item> parameters, Location location) {
    super(resultType, name, location);
    this.resultType = requireNonNull(resultType);
    this.parameters = requireNonNull(parameters);
  }

  @Override
  public String extendedName() {
    return name() + PARENTHESES;
  }

  public Type resultType() {
    return resultType;
  }

  public ImmutableList<Item> parameters() {
    return parameters;
  }

  public List<Type> parameterTypes() {
    return map(parameters, Item::type);
  }

  public ImmutableList<ItemSignature> parameterSignatures() {
    return parameters.stream()
        .map(Item::signature)
        .collect(toImmutableList());
  }

  public boolean canBeCalledArgless() {
    return parameters.stream()
        .allMatch(p -> p.defaultValue().isPresent());
  }

  public abstract Expression createCallExpression(ImmutableList<Expression> arguments,
      Location location);
}
