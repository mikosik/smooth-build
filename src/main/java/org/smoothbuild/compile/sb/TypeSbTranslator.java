package org.smoothbuild.compile.sb;

import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.BlobTS;
import org.smoothbuild.compile.lang.type.BoolTS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.IntTS;
import org.smoothbuild.compile.lang.type.StringTS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TupleTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import com.google.common.collect.ImmutableMap;

public class TypeSbTranslator {
  private final BytecodeF bytecodeF;
  private final ImmutableMap<VarS, TypeB> varMap;

  public TypeSbTranslator(BytecodeF bytecodeF, ImmutableMap<VarS, TypeB> varMap) {
    this.bytecodeF = bytecodeF;
    this.varMap = varMap;
  }

  public ImmutableMap<VarS, TypeB> varMap() {
    return varMap;
  }

  public TypeB translate(TypeS type) {
    return switch (type) {
      case ArrayTS arrayTS -> translate(arrayTS);
      case BlobTS blobTS -> bytecodeF.blobT();
      case BoolTS boolTS -> bytecodeF.boolT();
      case FuncTS funcTS -> translate(funcTS);
      case IntTS intTS -> bytecodeF.intT();
      case VarS varS ->  translate(varS);
      case StringTS stringTS -> bytecodeF.stringT();
      case StructTS structTS -> translate(structTS);
      case TupleTS tupleTS -> translate(tupleTS);
    };
  }

  public TypeB translate(VarS var) {
    TypeB typeB = varMap.get(var);
    if (typeB == null) {
      throw new IllegalStateException("Unknown variable " + var.q() + ".");
    } else {
      return typeB;
    }
  }

  public TupleTB translate(StructTS struct) {
    return bytecodeF.tupleT(map(struct.fields(), isig -> translate(isig.type())));
  }

  public FuncTB translate(FuncTS func) {
    return bytecodeF.funcT(translate(func.params()), translate(func.res()));
  }

  public TupleTB translate(TupleTS tuple) {
    return bytecodeF.tupleT(map(tuple.items(), this::translate));
  }

  public ArrayTB translate(ArrayTS array) {
    return bytecodeF.arrayT(translate(array.elem()));
  }
}
