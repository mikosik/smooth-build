package org.smoothbuild.compile.frontend.lang.type;

import static org.smoothbuild.compile.frontend.lang.type.VarSetS.varSetS;

import com.google.common.collect.ImmutableMap;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compile.frontend.lang.define.ItemSigS;

public abstract sealed class FieldSetTS extends TypeS permits InterfaceTS, StructTS {
  protected FieldSetTS(String name, VarSetS varSetS) {
    super(name, varSetS);
  }

  public abstract ImmutableMap<String, ItemSigS> fieldSet();

  protected static VarSetS calculateFieldSetVars(List<ItemSigS> fields) {
    return varSetS(fields.map(ItemSigS::type));
  }
}
