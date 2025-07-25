package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.List.listOfAll;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.name.Name;

/**
 * This class is immutable.
 */
public sealed class SInterfaceType extends SType permits SStructType {
  private final Map<Name, SItemSig> fields;

  public SInterfaceType(Map<Name, SItemSig> fields) {
    super(calculateFieldSetVars(listOfAll(fields.values())));
    this.fields = fields;
  }

  public static Set<STypeVar> calculateFieldSetVars(List<SItemSig> fields) {
    return fields.map(SItemSig::type).flatMap(SType::typeVars).toSet();
  }

  public Map<Name, SItemSig> fieldSet() {
    return fields;
  }

  @Override
  public String specifier() {
    return listOfAll(fields.values()).toString("{", ",", "}");
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SInterfaceType that && this.fields.equals(that.fields);
  }
}
