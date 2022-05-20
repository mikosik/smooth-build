package org.smoothbuild.lang.type;

import com.google.common.collect.ImmutableList;

public sealed interface FuncTS extends TypeS
  permits MonoFuncTS, PolyFuncTS {

    public MonoTS res();

    public ImmutableList<MonoTS> params();
}
