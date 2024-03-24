package org.smoothbuild.compilerbackend;

import static org.smoothbuild.common.base.Throwables.unexpectedCaseExc;
import static org.smoothbuild.common.collect.List.listOfAll;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SBlobType;
import org.smoothbuild.compilerfrontend.lang.type.SBoolType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SIntType;
import org.smoothbuild.compilerfrontend.lang.type.SInterfaceType;
import org.smoothbuild.compilerfrontend.lang.type.SStringType;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

class TypeSbTranslator {
  private final ChainingBytecodeFactory bytecodeF;
  private final Map<SVar, BType> varMap;

  public TypeSbTranslator(ChainingBytecodeFactory bytecodeF, Map<SVar, BType> varMap) {
    this.bytecodeF = bytecodeF;
    this.varMap = varMap;
  }

  public Map<SVar, BType> varMap() {
    return varMap;
  }

  public BType translate(SType type) throws SbTranslatorException {
    return switch (type) {
      case SArrayType sArrayType -> translate(sArrayType);
      case SBlobType sBlobType -> bytecodeF.blobType();
      case SBoolType sBoolType -> bytecodeF.boolType();
      case SFuncType sFuncType -> translate(sFuncType);
      case SIntType sIntType -> bytecodeF.intType();
      case SVar sVar -> translate(sVar);
      case SStringType sStringType -> bytecodeF.stringType();
      case SStructType sStructType -> translate(sStructType);
      case STupleType sTupleType -> translate(sTupleType);
      case SInterfaceType sInterfaceType -> throw unexpectedCaseExc(sInterfaceType);
    };
  }

  public BType translate(SVar var) {
    BType bType = varMap.get(var);
    if (bType == null) {
      throw new IllegalStateException("Unknown variable " + var.q() + ".");
    } else {
      return bType;
    }
  }

  public BTupleType translate(SStructType struct) throws SbTranslatorException {
    return bytecodeF.tupleType(listOfAll(struct.fields()).map(isig -> translate(isig.type())));
  }

  public BFuncType translate(SFuncType func) throws SbTranslatorException {
    return bytecodeF.funcType(translate(func.params()), translate(func.result()));
  }

  public BTupleType translate(STupleType tuple) throws SbTranslatorException {
    return bytecodeF.tupleType(listOfAll(tuple.elements()).map(this::translate));
  }

  public BArrayType translate(SArrayType array) throws SbTranslatorException {
    return bytecodeF.arrayType(translate(array.elem()));
  }
}
