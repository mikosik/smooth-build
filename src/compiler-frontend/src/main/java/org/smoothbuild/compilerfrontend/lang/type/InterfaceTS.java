package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.mapOfAll;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.base.TypeNamesS;
import org.smoothbuild.compilerfrontend.lang.define.ItemSigS;

/**
 * This class is immutable.
 */
public final class InterfaceTS extends FieldSetTS {
  private final Map<String, ItemSigS> fields;

  public InterfaceTS(Map<String, ItemSigS> fields) {
    super(TypeNamesS.interfaceTypeName(fields), calculateFieldSetVars(listOfAll(fields.values())));
    this.fields = mapOfAll(fields);
  }

  @Override
  public Map<String, ItemSigS> fieldSet() {
    return fields;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof InterfaceTS that && this.fields.equals(that.fields);
  }
}
