package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;

public class FieldReadExpression extends Expression {
  private final Field field;

  public FieldReadExpression(Field field, Expression expression, Location location) {
    super(ImmutableList.of(expression), location);
    this.field = field;
  }

  public Field field() {
    return field;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
