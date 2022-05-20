package org.smoothbuild.lang.type;

import com.google.common.collect.ImmutableList;

public sealed abstract class ComposedTS extends MonoTS permits ArrayTS, MonoFuncTS {
  protected ComposedTS(String name, VarSetS vars) {
    super(name, vars);
  }

  public abstract ImmutableList<MonoTS> covars();
  public abstract ImmutableList<MonoTS> contravars();
}
