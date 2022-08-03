package org.smoothbuild.compile;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.cnst.FuncTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.BlobTS;
import org.smoothbuild.lang.type.BoolTS;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.IntTS;
import org.smoothbuild.lang.type.StringTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

public class TypeSbConverter {
  private final BytecodeF bytecodeF;
  private final ImmutableMap<VarS, TypeB> varMap;

  public TypeSbConverter(BytecodeF bytecodeF, ImmutableMap<VarS, TypeB> varMap) {
    this.bytecodeF = bytecodeF;
    this.varMap = varMap;
  }

  public TypeB convert(TypeS type) {
    return switch (type) {
      case ArrayTS a -> convert(a);
      case BlobTS blob -> bytecodeF.blobT();
      case BoolTS bool -> bytecodeF.boolT();
      case IntTS i -> bytecodeF.intT();
      case VarS v ->  convert(v);
      case StringTS s -> bytecodeF.stringT();
      case StructTS st -> convert(st);
      case FuncTS f -> convert(f);
    };
  }

  public TypeB convert(VarS var) {
    return requireNonNull(varMap.get(var));
  }

  public TupleTB convert(StructTS struct) {
    return bytecodeF.tupleT(map(struct.fields(), isig -> convert(isig.type())));
  }

  public ArrayTB convert(ArrayTS array) {
    return bytecodeF.arrayT(convert(array.elem()));
  }

  public FuncTB convert(FuncTS func) {
    var res = convert(func.res());
    var params = map(func.params(), this::convert);
    return bytecodeF.funcT(res, params);
  }
}
