package org.smoothbuild.bytecode.type.val;

public sealed interface CallableTB permits FuncTB, MethodTB {
  public TypeB res();

  public TupleTB params();
}
