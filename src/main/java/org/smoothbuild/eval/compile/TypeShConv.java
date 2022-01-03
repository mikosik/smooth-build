package org.smoothbuild.eval.compile;

import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.bytecode.ByteCodeFactory;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.lang.base.type.impl.AnyTS;
import org.smoothbuild.lang.base.type.impl.ArrayTS;
import org.smoothbuild.lang.base.type.impl.BlobTS;
import org.smoothbuild.lang.base.type.impl.BoolTS;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.IntTS;
import org.smoothbuild.lang.base.type.impl.NothingTS;
import org.smoothbuild.lang.base.type.impl.StringTS;
import org.smoothbuild.lang.base.type.impl.StructTS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.VarTS;

public class TypeShConv {
  private final ByteCodeFactory byteCodeFactory;

  @Inject
  public TypeShConv(ByteCodeFactory byteCodeFactory) {
    this.byteCodeFactory = byteCodeFactory;
  }

  public TypeB convert(TypeS type) {
    return switch (type) {
      case AnyTS any -> throw new RuntimeException("S-Any cannot be converted to H-type.");
      case ArrayTS a -> convert(a);
      case BlobTS blob -> byteCodeFactory.blobT();
      case BoolTS bool -> byteCodeFactory.boolT();
      case IntTS i -> byteCodeFactory.intT();
      case NothingTS n -> byteCodeFactory.nothingT();
      case StringTS s -> byteCodeFactory.stringT();
      case StructTS st -> convert(st);
      case VarTS v ->  byteCodeFactory.varT(v.name());
      case FuncTS f -> convert(f);
    };
  }

  public TupleTB convert(StructTS st) {
    return byteCodeFactory.tupleT(map(st.fields(), isig -> convert(isig.type())));
  }

  public ArrayTB convert(ArrayTS a) {
    return byteCodeFactory.arrayT(convert(a.elem()));
  }

  public FuncTB convert(FuncTS funcTS) {
    return byteCodeFactory.funcT(convert(funcTS.res()), map(funcTS.params(), this::convert));
  }
}
