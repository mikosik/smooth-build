package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Item.toItemSignatures;
import static org.smoothbuild.lang.base.type.Types.structT;

import com.google.common.collect.ImmutableList;

public class Struct extends DefinedType {
  private final ImmutableList<Item> fields;

  public Struct(ModulePath modulePath, String name, ImmutableList<Item> fields, Location location) {
    super(structT(name, toItemSignatures(fields)), modulePath, name, location);
    this.fields = fields;
  }

  public ImmutableList<Item> fields() {
    return fields;
  }
}
