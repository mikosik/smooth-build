package org.smoothbuild.compilerbackend;

import static org.smoothbuild.common.base.Throwables.unexpectedCaseExc;
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
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

class TypeSbTranslator {
  private final ChainingBytecodeFactory bytecodeF;
  private final Map<VarS, BType> varMap;

  public TypeSbTranslator(ChainingBytecodeFactory bytecodeF, Map<VarS, BType> varMap) {
    this.bytecodeF = bytecodeF;
    this.varMap = varMap;
  }

  public Map<VarS, BType> varMap() {
    return varMap;
  }

  public BType translate(TypeS type) throws SbTranslatorException {
    return switch (type) {
      case ArrayTS arrayTS -> translate(arrayTS);
      case BlobTS blobTS -> bytecodeF.blobType();
      case BoolTS boolTS -> bytecodeF.boolType();
      case FuncTS funcTS -> translate(funcTS);
      case IntTS intTS -> bytecodeF.intType();
      case VarS varS -> translate(varS);
      case StringTS stringTS -> bytecodeF.stringType();
      case StructTS structTS -> translate(structTS);
      case TupleTS tupleTS -> translate(tupleTS);
      case InterfaceTS interfaceTS -> throw unexpectedCaseExc(interfaceTS);
    };
  }

  public BType translate(VarS var) {
    BType bType = varMap.get(var);
    if (bType == null) {
      throw new IllegalStateException("Unknown variable " + var.q() + ".");
    } else {
      return bType;
    }
  }

  public BTupleType translate(StructTS struct) throws SbTranslatorException {
    return bytecodeF.tupleType(listOfAll(struct.fields()).map(isig -> translate(isig.type())));
  }

  public BFuncType translate(FuncTS func) throws SbTranslatorException {
    return bytecodeF.funcType(translate(func.params()), translate(func.result()));
  }

  public BTupleType translate(TupleTS tuple) throws SbTranslatorException {
    return bytecodeF.tupleType(listOfAll(tuple.elements()).map(this::translate));
  }

  public BArrayType translate(ArrayTS array) throws SbTranslatorException {
    return bytecodeF.arrayType(translate(array.elem()));
  }
}
