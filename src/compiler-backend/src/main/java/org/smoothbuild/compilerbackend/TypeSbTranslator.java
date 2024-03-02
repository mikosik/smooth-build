package org.smoothbuild.compilerbackend;

import static org.smoothbuild.common.Throwables.unexpectedCaseExc;
import static org.smoothbuild.common.collect.List.listOfAll;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.type.ArrayTS;
import org.smoothbuild.compilerfrontend.lang.type.BlobTS;
import org.smoothbuild.compilerfrontend.lang.type.BoolTS;
import org.smoothbuild.compilerfrontend.lang.type.FuncTS;
import org.smoothbuild.compilerfrontend.lang.type.IntTS;
import org.smoothbuild.compilerfrontend.lang.type.InterfaceTS;
import org.smoothbuild.compilerfrontend.lang.type.StringTS;
import org.smoothbuild.compilerfrontend.lang.type.StructTS;
import org.smoothbuild.compilerfrontend.lang.type.TupleTS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;
import org.smoothbuild.compilerfrontend.lang.type.VarS;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

class TypeSbTranslator {
  private final ChainingBytecodeFactory bytecodeF;
  private final Map<VarS, TypeB> varMap;

  public TypeSbTranslator(ChainingBytecodeFactory bytecodeF, Map<VarS, TypeB> varMap) {
    this.bytecodeF = bytecodeF;
    this.varMap = varMap;
  }

  public Map<VarS, TypeB> varMap() {
    return varMap;
  }

  public TypeB translate(TypeS type) throws SbTranslatorException {
    return switch (type) {
      case ArrayTS arrayTS -> translate(arrayTS);
      case BlobTS blobTS -> bytecodeF.blobT();
      case BoolTS boolTS -> bytecodeF.boolT();
      case FuncTS funcTS -> translate(funcTS);
      case IntTS intTS -> bytecodeF.intT();
      case VarS varS -> translate(varS);
      case StringTS stringTS -> bytecodeF.stringT();
      case StructTS structTS -> translate(structTS);
      case TupleTS tupleTS -> translate(tupleTS);
      case InterfaceTS interfaceTS -> throw unexpectedCaseExc(interfaceTS);
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

  public TupleTB translate(StructTS struct) throws SbTranslatorException {
    return bytecodeF.tupleT(listOfAll(struct.fields()).map(isig -> translate(isig.type())));
  }

  public FuncTB translate(FuncTS func) throws SbTranslatorException {
    return bytecodeF.funcT(translate(func.params()), translate(func.result()));
  }

  public TupleTB translate(TupleTS tuple) throws SbTranslatorException {
    return bytecodeF.tupleT(listOfAll(tuple.elements()).map(this::translate));
  }

  public ArrayTB translate(ArrayTS array) throws SbTranslatorException {
    return bytecodeF.arrayT(translate(array.elem()));
  }
}
