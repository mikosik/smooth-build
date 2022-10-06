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
import org.smoothbuild.compile.lang.type.TupleTS;
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
    return requireNonNull(varMap.get(var));
  }

  public TupleTB translate(StructTS struct) {
    return bytecodeF.tupleT(map(struct.fields(), isig -> translate(isig.type())));
  }

  public FuncTB translate(FuncTS func) {
    return bytecodeF.funcT(translate(func.res()), translate(func.params()));
  }

  public TupleTB translate(TupleTS tuple) {
    return bytecodeF.tupleT(map(tuple.items(), this::translate));
  }

  public ArrayTB translate(ArrayTS array) {
    return bytecodeF.arrayT(translate(array.elem()));
  }
}
