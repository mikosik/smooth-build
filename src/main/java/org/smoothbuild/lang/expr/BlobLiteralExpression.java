package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Location;

import okio.ByteString;

public class BlobLiteralExpression extends Expression {
  private final ByteString byteString;

  public BlobLiteralExpression(ByteString byteString, Location location) {
    super(location);
    this.byteString = byteString;
  }

  public ByteString byteString() {
    return byteString;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
