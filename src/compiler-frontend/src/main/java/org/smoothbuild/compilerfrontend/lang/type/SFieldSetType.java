package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;

public abstract sealed class SFieldSetType extends SType permits SInterfaceType, SStructType {
  protected SFieldSetType(String name, SVarSet sVarSet) {
    super(name, sVarSet);
  }

  public abstract Map<String, SItemSig> fieldSet();

  protected static SVarSet calculateFieldSetVars(List<SItemSig> fields) {
    return varSetS(fields.map(SItemSig::type));
  }
}
