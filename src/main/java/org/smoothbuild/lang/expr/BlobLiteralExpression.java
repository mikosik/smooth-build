package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.BlobTypeS;

import okio.ByteString;

public record BlobLiteralExpression(BlobTypeS type, ByteString byteString, Location location)
    implements Expression {
}
