package org.smoothbuild.lang.like.common;

import java.util.Optional;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.util.collect.NameableImpl;

public class ParamC extends NameableImpl {
  private final ItemSigS itemSigS;
  private final Optional<? extends ObjC> body;

  public ParamC(ItemSigS itemSigS, Optional<? extends ObjC> body) {
    super(itemSigS.nameO());
    this.itemSigS = itemSigS;
    this.body = body;
  }

  public MonoTS type() {
    return itemSigS.type();
  }

  public String typeAndName() {
    return itemSigS.typeAndName();
  }

  public Optional<? extends ObjC> body() {
    return body;
  }

  public ItemSigS sig() {
    return itemSigS;
  }
}
