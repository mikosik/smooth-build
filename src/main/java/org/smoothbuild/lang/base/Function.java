package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.base.Location.unknownLocation;

import java.util.List;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.object.type.Type;
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

  public List<Type> parameterTypes() {
    return signature.parameterTypes();
  }

  public boolean canBeCalledArgless() {
    return signature.parameters().stream()
        .allMatch(ParameterInfo::hasDefaultValue);
  }

  public Expression createAgrlessCallExpression() {
    ImmutableList<Expression> defaultArguments = signature
        .parameters()
        .stream()
        .map(Parameter::defaultValueExpression)
        .collect(toImmutableList());
    return createCallExpression(defaultArguments, unknownLocation());
  }

  public abstract Expression createCallExpression(List<? extends Expression> arguments,
      Location location);
}
