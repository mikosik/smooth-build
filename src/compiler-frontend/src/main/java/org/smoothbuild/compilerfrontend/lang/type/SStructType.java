package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.interfaceTypeName;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.base.NList;
import org.smoothbuild.compilerfrontend.lang.base.Name;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;

/**
 * This class is immutable.
 */
public final class SStructType extends SInterfaceType {
  private final Id id;
  private final NList<SItemSig> fields;

  public SStructType(Id id, NList<SItemSig> fields) {
    super(id.toString(), fields.map());
    this.id = id;
    this.fields = fields;
  }

  public Id id() {
    return id;
  }

  public NList<SItemSig> fields() {
    return fields;
  }

  @Override
  public Map<Name, SItemSig> fieldSet() {
    return fields.map();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SStructType thatStruct
        && this.name().equals(thatStruct.name())
        && this.fields.equals(thatStruct.fields);
  }

  @Override
  public String toString() {
    return name() + interfaceTypeName(fieldSet());
  }
}
