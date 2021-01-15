package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

public class CallableNode extends EvaluableNode implements RefTarget {
  private final List<ItemNode> params;

  public CallableNode(TypeNode typeNode, String name, ExprNode exprNode, List<ItemNode> params,
      Location location) {
    super(typeNode, name, exprNode, location);
    this.params = ImmutableList.copyOf(params);
  }

  public List<ItemNode> params() {
    return params;
  }

  public ImmutableList<ItemSignature> parameterSignatures() {
    return map(params(), itemNode -> itemNode.itemSignature().get());
  }

  @Override
  public Optional<Type> inferredType() {
    return type();
  }
}
