package org.smoothbuild.parse.expr;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.type.StringType;

public class StringLiteralExpression extends Expression {
  private final StringType stringType;
  private final String string;

  public StringLiteralExpression(StringType stringType, String string, Location location) {
    super(location);
    this.stringType = stringType;
    this.string = string;
  }

  public String string() {
    return string;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
