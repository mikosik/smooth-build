package org.smoothbuild.lang.like;

import java.util.Optional;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.util.collect.NameableImpl;

public class Param extends NameableImpl {
  private final ItemSigS itemSigS;
  private final Optional<? extends Obj> body;

  public Param(ItemSigS itemSigS, Optional<? extends Obj> body) {
    super(itemSigS.nameO());
    this.itemSigS = itemSigS;
    this.body = body;
  }

  public TypeS type() {
    return itemSigS.type();
  }

  public String typeAndName() {
    return itemSigS.typeAndName();
  }

  public Optional<? extends Obj> body() {
    return body;
  }

  public ItemSigS sig() {
    return itemSigS;
  }
}
