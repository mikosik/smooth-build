package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;

public class ItemNode extends NamedNode implements ReferencableLike {
  private final TypeNode typeNode;
  private final Optional<ExprNode> defaultValue;
  private Optional<ItemSignature> signature;

  public ItemNode(TypeNode typeNode, String name, Optional<ExprNode> defaultValue,
      Location location) {
    super(name, location);
    this.typeNode = typeNode;
    this.defaultValue = defaultValue;
  }

  @Override
  public void setType(Optional<Type> type) {
    super.setType(type);
    signature = type()
        .map(t -> new ItemSignature(t, Optional.of(name()), defaultValue.flatMap(Node::type)));
  }

  public TypeNode typeNode() {
    return typeNode;
  }

  public Optional<ExprNode> defaultValue() {
    return defaultValue;
  }

  public Optional<ItemSignature> itemSignature() {
    return signature;
  }

  @Override
  public Optional<Type> inferredType() {
    return type();
  }
}
