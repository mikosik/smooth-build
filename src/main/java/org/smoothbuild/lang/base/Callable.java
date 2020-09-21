package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Callable extends Evaluable {
  public static final String PARENTHESES = "()";
  private final Signature signature;

  public Callable(Signature signature, Location location) {
    super(signature.type(), signature.name(), location);
    this.signature = checkNotNull(signature);
  }

  @Override
  public String extendedName() {
    return signature().name() + PARENTHESES;
  }

  public Signature signature() {
    return signature;
  }

  public ImmutableList<Item> parameters() {
    return signature.parameters();
  }

  public List<Type> parameterTypes() {
    return signature.parameterTypes();
  }

  public boolean canBeCalledArgless() {
    return signature.parameters().stream()
        .allMatch(ItemSignature::hasDefaultValue);
  }

  public abstract Expression createCallExpression(ImmutableList<Expression> arguments,
      Location location);
}
