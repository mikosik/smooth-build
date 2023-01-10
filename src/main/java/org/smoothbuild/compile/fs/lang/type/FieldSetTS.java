package org.smoothbuild.compile.fs.lang.type;

import org.smoothbuild.compile.fs.lang.define.ItemSigS;

import com.google.common.collect.ImmutableMap;

public abstract sealed class FieldSetTS
    extends TypeS
    permits InterfaceTS, StructTS {
  protected FieldSetTS(String name) {
    super(name);
  }

  public abstract ImmutableMap<String, ItemSigS> fieldSet();
}
