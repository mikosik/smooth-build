package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.base.STypeNames.interfaceTypeName;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;

/**
 * This class is immutable.
 */
public final class SStructType extends SFieldSetType {
  private final NList<SItemSig> fields;

  public SStructType(String name, NList<SItemSig> fields) {
    super(name, calculateFieldSetVars(fields.list()));
    this.fields = fields;
  }

  public NList<SItemSig> fields() {
    return fields;
  }

  @Override
  public Map<String, SItemSig> fieldSet() {
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
