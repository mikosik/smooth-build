package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.mapOfAll;
import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.interfaceTypeName;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;

/**
 * This class is immutable.
 */
public final class SInterfaceType extends SFieldSetType {
  private final Map<String, SItemSig> fields;

  public SInterfaceType(Map<String, SItemSig> fields) {
    super(interfaceTypeName(fields), calculateFieldSetVars(listOfAll(fields.values())));
    this.fields = mapOfAll(fields);
  }

  @Override
  public Map<String, SItemSig> fieldSet() {
    return fields;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SInterfaceType that && this.fields.equals(that.fields);
  }
}
