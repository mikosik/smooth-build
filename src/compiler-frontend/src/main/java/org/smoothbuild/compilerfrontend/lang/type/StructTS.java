package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.base.TypeNamesS.interfaceTypeName;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.define.ItemSigS;

/**
 * This class is immutable.
 */
public final class StructTS extends FieldSetTS {
  private final NList<ItemSigS> fields;

  public StructTS(String name, NList<ItemSigS> fields) {
    super(name, calculateFieldSetVars(fields.list()));
    this.fields = fields;
  }

  public NList<ItemSigS> fields() {
    return fields;
  }

  @Override
  public Map<String, ItemSigS> fieldSet() {
    return fields.map();
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

  @Override
  public String toString() {
    return name() + interfaceTypeName(fieldSet());
  }
}
