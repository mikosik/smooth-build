package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Native;

public class FuncNode extends CallableNode {
  public FuncNode(Optional<TypeNode> type, String name, List<ItemNode> params,
      Optional<ExprNode> expr, Optional<Native> nativ, Location location) {
    super(type, name, expr, params, nativ, location);
  }
}
