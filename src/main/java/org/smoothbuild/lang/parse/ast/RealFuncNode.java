package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

public final class RealFuncNode extends FunctionNode {
  public RealFuncNode(Optional<TypeNode> type, String name, List<ItemNode> params,
      Optional<ExprNode> expr, Optional<AnnotationNode> annotation, Location location) {
    super(type, name, expr, params, annotation, location);
  }
}
