package org.smoothbuild.lang.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Location;

public class FuncNode extends CallableNode {
  public FuncNode(TypeNode type, String name, List<ItemNode> params, ExprNode expr,
      Location location) {
    super(type, name, expr, params, location);
  }
}
