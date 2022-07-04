package org.smoothbuild.parse;

import java.util.Optional;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.util.collect.NameableImpl;

public class Param extends NameableImpl {
  private final ItemSigS itemSigS;
  private final Optional<? extends TypeS> bodyT;

  public Param(ItemSigS itemSigS, Optional<? extends TypeS> bodyT) {
    super(itemSigS.nameO());
    this.itemSigS = itemSigS;
    this.bodyT = bodyT;
  }

  public Optional<? extends TypeS> bodyT() {
    return bodyT;
  }

  public ItemSigS sig() {
    return itemSigS;
  }
}
