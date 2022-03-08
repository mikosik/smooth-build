package org.smoothbuild.compile;

import static org.smoothbuild.bytecode.type.val.VarSetB.toVarSetB;
import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.VarB;
import org.smoothbuild.lang.type.impl.AnyTS;
import org.smoothbuild.lang.type.impl.ArrayTS;
import org.smoothbuild.lang.type.impl.BlobTS;
import org.smoothbuild.lang.type.impl.BoolTS;
import org.smoothbuild.lang.type.impl.FuncTS;
import org.smoothbuild.lang.type.impl.IntTS;
import org.smoothbuild.lang.type.impl.NothingTS;
import org.smoothbuild.lang.type.impl.StringTS;
import org.smoothbuild.lang.type.impl.StructTS;
import org.smoothbuild.lang.type.impl.TypeS;
import org.smoothbuild.lang.type.impl.VarS;

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
      case IntTS i -> bytecodeF.intT();
      case NothingTS n -> bytecodeF.nothingT();
      case VarS v ->  convert(v);
      case StringTS s -> bytecodeF.stringT();
      case StructTS st -> convert(st);
      case FuncTS f -> convert(f);
    };
  }

  public VarB convert(VarS var) {
    return bytecodeF.varT(var.name());
  }

  public TupleTB convert(StructTS struct) {
    return bytecodeF.tupleT(map(struct.fields(), isig -> convert(isig.type())));
  }

  public ArrayTB convert(ArrayTS array) {
    return bytecodeF.arrayT(convert(array.elem()));
  }

  public FuncTB convert(FuncTS func) {
    var tParams = func.tParams().stream()
        .map(this::convert)
        .collect(toVarSetB());
    var params = map(func.params(), this::convert);
    var res = convert(func.res());
    return bytecodeF.funcT(tParams, res, params);
  }
}
