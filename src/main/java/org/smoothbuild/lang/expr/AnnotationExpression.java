package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.type.Types.blobT;
import static org.smoothbuild.lang.base.type.Types.stringT;
import static org.smoothbuild.lang.base.type.Types.structT;
import static org.smoothbuild.util.Lists.list;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;

public record AnnotationExpression(
    StringLiteralExpression path, boolean isPure, Location location)
    implements Expression {
  private static final Type TYPE = structT("Native", list(
      new ItemSignature(stringT(), "path", Optional.empty()),
      new ItemSignature(blobT(), "content", Optional.empty())));

  @Override
  public Type type() {
    return TYPE;
  }
}
