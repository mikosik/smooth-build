package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.ItemSignature;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.Type;

public class ItemNode extends NamedNode implements RefTarget {
  private final TypeNode typeNode;
  private final Optional<ExprNode> defaultValue;
  private Optional<ItemSignature> itemInfo;

  public ItemNode(TypeNode typeNode, String name, Optional<ExprNode> defaultValue,
      Location location) {
    super(name, location);
    this.typeNode = typeNode;
    this.defaultValue = defaultValue;
  }

  public TypeNode typeNode() {
    return typeNode;
  }

  public Optional<ExprNode> defaultValue() {
    return defaultValue;
  }

  public Optional<ItemSignature> itemInfo() {
    return itemInfo;
  }

  public void setItemInfo(Optional<ItemSignature> itemInfo) {
    this.itemInfo = itemInfo;
  }

  @Override
  public Optional<Type> inferredType() {
    return type();
  }
}
