package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.type.Types.blob;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

import okio.ByteString;

public record BlobLiteralExpression(ByteString byteString, Location location) implements Expression {
  @Override
  public Type type() {
    return blob();
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "BlobLiteralExpression{" + byteString + ", " + location() + "}";
  }
}
