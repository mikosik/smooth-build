package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Item.toItemSignatures;
import static org.smoothbuild.lang.base.type.Types.struct;

import com.google.common.collect.ImmutableList;

public class Struct extends Defined {
  private final ImmutableList<Item> fields;

  public Struct(String name, ImmutableList<Item> fields, Location location) {
    super(struct(name, toItemSignatures(fields)), name, location);
    this.fields = fields;
  }

  public ImmutableList<Item> fields() {
    return fields;
  }
}
