package org.smoothbuild.compile;

import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.lang.base.type.impl.AnyTS;
import org.smoothbuild.lang.base.type.impl.ArrayTS;
import org.smoothbuild.lang.base.type.impl.BlobTS;
import org.smoothbuild.lang.base.type.impl.BoolTS;
import org.smoothbuild.lang.base.type.impl.ClosedVarTS;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.IntTS;
import org.smoothbuild.lang.base.type.impl.NothingTS;
import org.smoothbuild.lang.base.type.impl.OpenVarTS;
import org.smoothbuild.lang.base.type.impl.StringTS;
import org.smoothbuild.lang.base.type.impl.StructTS;
import org.smoothbuild.lang.base.type.impl.TypeS;

public class TypeSbConv {
  private final BytecodeF bytecodeF;

  @Inject
  public TypeSbConv(BytecodeF bytecodeF) {
    this.bytecodeF = bytecodeF;
  }

  public TypeB convert(TypeS type) {
    return switch (type) {
      case AnyTS any -> throw new RuntimeException("S-Any cannot be converted to H-type.");
      case ArrayTS a -> convert(a);
      case BlobTS blob -> bytecodeF.blobT();
      case BoolTS bool -> bytecodeF.boolT();
      case ClosedVarTS v ->  bytecodeF.cVarT(v.name());
      case IntTS i -> bytecodeF.intT();
      case NothingTS n -> bytecodeF.nothingT();
      case OpenVarTS v ->  bytecodeF.oVarT(v.name());
      case StringTS s -> bytecodeF.stringT();
      case StructTS st -> convert(st);
      case FuncTS f -> convert(f);
    };
  }

  public TupleTB convert(StructTS st) {
    return bytecodeF.tupleT(map(st.fields(), isig -> convert(isig.type())));
  }

  public ArrayTB convert(ArrayTS a) {
    return bytecodeF.arrayT(convert(a.elem()));
  }

  public FuncTB convert(FuncTS funcTS) {
    return bytecodeF.funcT(convert(funcTS.res()), map(funcTS.params(), this::convert));
  }
}
