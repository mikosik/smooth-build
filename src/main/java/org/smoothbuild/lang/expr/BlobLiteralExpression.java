package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.api.BlobType;

import okio.ByteString;

public record BlobLiteralExpression(BlobType type, ByteString byteString, Location location)
    implements Expression {
}
