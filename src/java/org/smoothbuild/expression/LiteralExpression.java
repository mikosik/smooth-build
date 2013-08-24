package org.smoothbuild.expression;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.function.Type;

public class LiteralExpression implements Expression {
  private final ExpressionId id;
  private final Type type;
  private final Object value;

  public static LiteralExpression stringExpression(ExpressionId id, String value) {
    return new LiteralExpression(id, Type.STRING, value);
  }

  public static LiteralExpression literalExpression(ExpressionId id, Type type, Object value) {
    checkArgument(type.javaType().isAssignableFrom(value.getClass()));
    return new LiteralExpression(id, type, value);
  }

  private LiteralExpression(ExpressionId id, Type type, Object value) {
    this.id = id;
    this.type = type;
    this.value = value;
  }

  @Override
  public ExpressionId id() {
    return id;
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public void calculate() {}

  @Override
  public Object result() {
    return value;
  }
}
