package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Native;

public class ValueNode extends ReferencableNode {
  public ValueNode(Optional<TypeNode> type, String name, Optional<ExprNode> expr,
      Optional<Native> nativ, Location location) {
    super(type, name, expr, nativ, location);
  }
}
