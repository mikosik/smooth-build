package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.mapOfAll;
import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.interfaceTypeName;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;

/**
 * This class is immutable.
 */
public sealed class SInterfaceType extends SType permits SStructType {
  private final Map<Id, SItemSig> fields;

  public SInterfaceType(Map<Id, SItemSig> fields) {
    this(interfaceTypeName(fields), fields);
  }

  protected SInterfaceType(String name, Map<Id, SItemSig> fields) {
    super(name, calculateFieldSetVars(listOfAll(fields.values())));
    this.fields = mapOfAll(fields);
  }

  public static SVarSet calculateFieldSetVars(List<SItemSig> fields) {
    return varSetS(fields.map(SItemSig::type));
  }

  public Map<Id, SItemSig> fieldSet() {
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
