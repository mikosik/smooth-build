package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

public class CallableNode extends ReferencableNode {
  private final List<ItemNode> params;

  public CallableNode(Optional<TypeNode> typeNode, String name, Optional<ExprNode> exprNode,
      List<ItemNode> params, Location location) {
    super(typeNode, name, exprNode, location);
    this.params = ImmutableList.copyOf(params);
  }

  public List<ItemNode> params() {
    return params;
  }

  public ImmutableList<ItemSignature> parameterSignatures() {
    return map(params(), paramNode -> paramNode.itemSignature().get());
  }

  public Optional<ImmutableList<ItemSignature>> optParameterSignatures() {
    var signatures = map(params(), ItemNode::itemSignature);
    if (signatures.stream().anyMatch(Optional::isEmpty)) {
      return Optional.empty();
    } else {
      return Optional.of(map(signatures, Optional::get));
    }
  }

  public Optional<Type> resultType() {
    return type().map(f -> ((FunctionType) f).resultType());
  }
}
