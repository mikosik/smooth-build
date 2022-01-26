package org.smoothbuild.eval.compile;

import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.bytecode.ByteCodeF;
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
  private final ByteCodeF byteCodeF;

  @Inject
  public TypeSbConv(ByteCodeF byteCodeF) {
    this.byteCodeF = byteCodeF;
  }

  public TypeB convert(TypeS type) {
    return switch (type) {
      case AnyTS any -> throw new RuntimeException("S-Any cannot be converted to H-type.");
      case ArrayTS a -> convert(a);
      case BlobTS blob -> byteCodeF.blobT();
      case BoolTS bool -> byteCodeF.boolT();
      case ClosedVarTS v ->  byteCodeF.cVarT(v.name());
      case IntTS i -> byteCodeF.intT();
      case NothingTS n -> byteCodeF.nothingT();
      case OpenVarTS v ->  byteCodeF.oVarT(v.name());
      case StringTS s -> byteCodeF.stringT();
      case StructTS st -> convert(st);
      case FuncTS f -> convert(f);
    };
  }

  public TupleTB convert(StructTS st) {
    return byteCodeF.tupleT(map(st.fields(), isig -> convert(isig.type())));
  }

  public ArrayTB convert(ArrayTS a) {
    return byteCodeF.arrayT(convert(a.elem()));
  }

  public FuncTB convert(FuncTS funcTS) {
    return byteCodeF.funcT(convert(funcTS.res()), map(funcTS.params(), this::convert));
  }
}
