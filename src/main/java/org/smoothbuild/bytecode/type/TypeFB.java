package org.smoothbuild.bytecode.type;

import java.util.List;

import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.cnst.FuncTB;
import org.smoothbuild.bytecode.type.cnst.NothingTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;

public interface TypeFB {
  public NothingTB nothing();

  public ArrayTB array(TypeB elemType);

  public FuncTB func(TypeB resT, List<? extends TypeB> paramTs);

  public TupleTB tuple(List<? extends TypeB> items);
}
