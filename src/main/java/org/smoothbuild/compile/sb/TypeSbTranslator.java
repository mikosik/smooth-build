package org.smoothbuild.compile.sb;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.BlobTS;
import org.smoothbuild.compile.lang.type.BoolTS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.IntTS;
import org.smoothbuild.compile.lang.type.StringTS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.collect.ImmutableMap;

public class TypeSbTranslator {
  private final BytecodeF bytecodeF;
  private final ImmutableMap<VarS, TypeB> varMap;

  public TypeSbTranslator(BytecodeF bytecodeF, ImmutableMap<VarS, TypeB> varMap) {
    this.bytecodeF = bytecodeF;
    this.varMap = varMap;
  }

  public TypeB translate(TypeS type) {
    return switch (type) {
      case ArrayTS a -> translate(a);
      case BlobTS blob -> bytecodeF.blobT();
      case BoolTS bool -> bytecodeF.boolT();
      case IntTS i -> bytecodeF.intT();
      case VarS v ->  translate(v);
      case StringTS s -> bytecodeF.stringT();
      case StructTS st -> translate(st);
      case FuncTS f -> translate(f);
    };
  }

  public TypeB translate(VarS var) {
    return requireNonNull(varMap.get(var));
  }

  public TupleTB translate(StructTS struct) {
    return bytecodeF.tupleT(map(struct.fields(), isig -> translate(isig.type())));
  }

  public ArrayTB translate(ArrayTS array) {
    return bytecodeF.arrayT(translate(array.elem()));
  }

  public FuncTB translate(FuncTS func) {
    var res = translate(func.res());
    var params = map(func.params(), this::translate);
    return bytecodeF.funcT(res, params);
  }
}
