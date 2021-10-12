package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.Optionals;

import com.google.common.collect.ImmutableList;

public class FunctionNode extends ReferencableNode {
  private final ImmutableList<ItemNode> params;

  public FunctionNode(Optional<TypeNode> typeNode, String name, Optional<ExprNode> body,
      List<ItemNode> params, Optional<NativeNode> nativ, Location location) {
    super(typeNode, name, body, nativ, location);
    this.params = ImmutableList.copyOf(params);
  }

  public ImmutableList<ItemNode> params() {
    return params;
  }

  public Optional<ImmutableList<Type>> optParameterTypes() {
    return Optionals.pullUp(map(params(), ItemNode::type));
  }

  public Optional<Type> resultType() {
    return type().map(f -> ((FunctionType) f).result());
  }
}
