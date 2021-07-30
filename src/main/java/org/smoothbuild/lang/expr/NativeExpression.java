package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.util.Lists.list;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;

public record NativeExpression(StringLiteralExpression path, boolean isPure, Location location)
    implements Expression {
  private static final Type TYPE = struct("Native", list(
      new ItemSignature(string(), "path", Optional.empty()),
      new ItemSignature(blob(), "content", Optional.empty())));

  @Override
  public Type type() {
    return TYPE;
  }
}
