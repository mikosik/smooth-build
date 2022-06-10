package org.smoothbuild.lang.type;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public final class StructTS extends MonoTS {
  private final NList<ItemSigS> fields;

  public StructTS(String name, NList<ItemSigS> fields) {
    super(name);
    this.fields = fields;
  }

  public NList<ItemSigS> fields() {
    return fields;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof StructTS thatStruct
        && this.name().equals(thatStruct.name())
        && this.fields.equals(thatStruct.fields);
  }
}
