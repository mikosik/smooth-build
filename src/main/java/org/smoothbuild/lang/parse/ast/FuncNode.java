package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

public class FuncNode extends CallableNode {
  public FuncNode(Optional<TypeNode> type, String name, List<ItemNode> params,
      Optional<ExprNode> expr, Location location) {
    super(type, name, expr, params, location);
  }
}
