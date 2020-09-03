package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Location;

public class FieldReadExpression extends Expression {
  private final Field field;

  public FieldReadExpression(Field field, List<? extends Expression> arguments, Location location) {
    super(arguments, location);
    this.field = field;
  }

  public Field field() {
    return field;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
