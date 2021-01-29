package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

public class ValueNode extends ReferencableNode {
  public ValueNode(
      Optional<TypeNode> type, String name, Optional<ExprNode> expr, Optional<String> implementedBy,
      Location location) {
    super(type, name, expr, implementedBy, location);
  }
}
