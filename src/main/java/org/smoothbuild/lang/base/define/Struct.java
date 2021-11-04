package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.StructSType;

import com.google.common.collect.ImmutableList;

public class Struct extends DefinedType {
  private final ImmutableList<Item> fields;

  public Struct(StructSType type, ModulePath modulePath, String name, ImmutableList<Item> fields,
      Location location) {
    super(type, modulePath, name, location);
    this.fields = fields;
  }

  public ImmutableList<Item> fields() {
    return fields;
  }
}
