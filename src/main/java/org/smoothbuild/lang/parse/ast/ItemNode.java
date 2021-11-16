package org.smoothbuild.lang.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.like.EvaluableLike;
import org.smoothbuild.lang.base.type.impl.TypeS;

public class ItemNode extends EvaluableNode implements EvaluableLike {
  private Optional<ItemSignature> signature;

  public ItemNode(TypeNode typeNode, String name, Optional<ExprNode> body, Location location) {
    super(Optional.of(typeNode), name, body, Optional.empty(), location);
  }

  @Override
  public void setType(Optional<TypeS> type) {
    super.setType(type);
    signature = type()
        .map(t -> new ItemSignature(t, Optional.of(name()), body().flatMap(Node::type)));
  }

  public Optional<ItemSignature> itemSignature() {
    return signature;
  }

  @Override
  public Optional<TypeS> inferredType() {
    return type();
  }

  public Item toItem(ModulePath path) {
    checkState(body().isEmpty());
    return new Item(type().get(), path, name(), Optional.empty(), location());
  }
}
