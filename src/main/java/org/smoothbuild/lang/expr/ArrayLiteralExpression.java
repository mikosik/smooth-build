package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ArrayType;

public class ArrayLiteralExpression extends Expression {
  private final ArrayType arrayType;

  public ArrayLiteralExpression(ArrayType arrayType, List<? extends Expression> elements,
      Location location) {
    super(elements, location);
    this.arrayType = arrayType;
  }

  public ArrayType arrayType() {
    return arrayType;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
