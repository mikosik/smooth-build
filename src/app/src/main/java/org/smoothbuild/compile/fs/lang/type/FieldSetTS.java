package org.smoothbuild.compile.fs.lang.type;

import static org.smoothbuild.common.collect.Lists.map;
import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;

import org.smoothbuild.compile.fs.lang.define.ItemSigS;

import com.google.common.collect.ImmutableMap;

public abstract sealed class FieldSetTS
    extends TypeS
    permits InterfaceTS, StructTS {
  protected FieldSetTS(String name, VarSetS varSetS) {
    super(name, varSetS);
  }

  public abstract ImmutableMap<String, ItemSigS> fieldSet();

  protected static VarSetS calculateFieldSetVars(Iterable<ItemSigS> fields) {
    return varSetS(map(fields, ItemSigS::type));
  }
}
