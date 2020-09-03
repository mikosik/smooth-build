package org.smoothbuild.lang.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.Type;

public class ItemNode extends NamedNode implements RefTarget {
  private final int index;
  private final TypeNode typeNode;
  private final Optional<ExprNode> defaultValue;
  private Optional<Item> itemInfo;

  public ItemNode(int index, TypeNode typeNode, String name, Optional<ExprNode> defaultValue,
      Location location) {
    super(name, location);
    this.index = index;
    this.typeNode = typeNode;
    this.defaultValue = defaultValue;
  }

  public int index() {
    return index;
  }

  public TypeNode typeNode() {
    return typeNode;
  }

  public Optional<ExprNode> defaultValue() {
    return defaultValue;
  }

  public Optional<Item> itemInfo() {
    return itemInfo;
  }

  public void setItemInfo(Optional<Item> itemInfo) {
    this.itemInfo = itemInfo;
  }

  @Override
  public Optional<Type> inferredType() {
    return type();
  }
}
