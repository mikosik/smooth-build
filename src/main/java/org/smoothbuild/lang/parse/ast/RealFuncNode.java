package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

public class RealFuncNode extends FunctionNode {
  public RealFuncNode(Optional<TypeNode> type, String name, List<ItemNode> params,
      Optional<ExprNode> expr, Optional<NativeNode> nativ, Location location) {
    super(type, name, expr, params, nativ, location);
  }
}
