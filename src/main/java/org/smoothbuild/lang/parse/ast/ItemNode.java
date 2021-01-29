package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.like.ItemLike;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;

public class ItemNode extends ReferencableNode implements ReferencableLike, ItemLike {
  private Optional<ItemSignature> signature;

  public ItemNode(TypeNode typeNode, String name, Optional<ExprNode> expr, Location location) {
    super(Optional.of(typeNode), name, expr, Optional.empty(), location);
  }

  @Override
  public void setType(Optional<Type> type) {
    super.setType(type);
    signature = type()
        .map(t -> new ItemSignature(t, Optional.of(name()), expr().flatMap(Node::type)));
  }

  @Override
  public boolean hasDefaultValue() {
    return expr().isPresent();
  }

  public Optional<ItemSignature> itemSignature() {
    return signature;
  }

  @Override
  public Optional<Type> inferredType() {
    return type();
  }
}
