package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.name.TokenNames.interfaceTypeName;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.name.Name;

/**
 * This class is immutable.
 */
public final class SStructType extends SInterfaceType {
  private final Fqn fqn;
  private final NList<SItemSig> fields;

  public SStructType(Fqn fqn, NList<SItemSig> fields) {
    super(fqn.toString(), fields.map());
    this.fqn = fqn;
    this.fields = fields;
  }

  public Fqn fqn() {
    return fqn;
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
