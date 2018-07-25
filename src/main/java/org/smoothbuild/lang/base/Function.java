package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.expr.EvaluatorTypeChooser.fixedTypeChooser;

import org.smoothbuild.lang.expr.EvaluatorTypeChooser;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.Named;

import com.google.common.collect.ImmutableList;

public abstract class Function implements Named {
  private final Signature signature;
  private final Location location;

  public Function(Signature signature, Location location) {
    this.signature = checkNotNull(signature);
    this.location = checkNotNull(location);
  }

  public Signature signature() {
    return signature;
  }

  @Override
  public Location location() {
    return location;
  }

  public Type type() {
    return signature.type();
  }

  @Override
  public String name() {
    return signature.name();
  }

  public ImmutableList<Parameter> parameters() {
    return signature.parameters();
  }

  public Expression createCallExpression(Location location) {
    if (!type().isConcrete()) {
      throw new UnsupportedOperationException(
          "Cannot create call expression for generic function `" + name()
              + "` without providing actual type for generic result type.");
    }
    return createCallExpression(type(), fixedTypeChooser((ConcreteType) type()), location);
  }

  public abstract Expression createCallExpression(Type type,
      EvaluatorTypeChooser evaluatorTypeChooser, Location location);
}
