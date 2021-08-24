package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.type.Types.blobT;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

import okio.ByteString;

public record BlobLiteralExpression(ByteString byteString, Location location)
    implements Expression {
  @Override
  public Type type() {
    return blobT();
  }

  @Override
  public String toString() {
    return "BlobLiteralExpression{" + byteString + ", " + location() + "}";
  }
}
