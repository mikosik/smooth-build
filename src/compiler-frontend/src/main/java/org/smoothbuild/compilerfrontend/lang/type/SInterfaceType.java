package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.mapOfAll;
import static org.smoothbuild.compilerfrontend.lang.name.TokenNames.interfaceTypeName;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.name.Name;

/**
 * This class is immutable.
 */
public sealed class SInterfaceType extends SType permits SStructType {
  private final Map<Name, SItemSig> fields;

  public SInterfaceType(Map<Name, SItemSig> fields) {
    this(interfaceTypeName(fields), fields);
  }

  protected SInterfaceType(String name, Map<Name, SItemSig> fields) {
    super(name, calculateFieldSetVars(listOfAll(fields.values())));
    this.fields = mapOfAll(fields);
  }

  public static SVarSet calculateFieldSetVars(List<SItemSig> fields) {
    return varSetS(fields.map(SItemSig::type));
  }

  public Map<Name, SItemSig> fieldSet() {
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
