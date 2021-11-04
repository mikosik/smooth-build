package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.BlobSType;

import okio.ByteString;

public record BlobLiteralExpression(BlobSType type, ByteString byteString, Location location)
    implements Expression {
}
