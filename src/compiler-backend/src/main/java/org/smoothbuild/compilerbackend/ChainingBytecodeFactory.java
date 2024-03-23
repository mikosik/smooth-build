package org.smoothbuild.compilerbackend;

import java.io.IOException;
import java.math.BigInteger;
import okio.BufferedSink;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.Helpers;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BBlobType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BBoolType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIntType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BStringType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * Wrapper for BytecodeFactory that chains BytecodeExceptions inside SbTranslatorException.
 * This class is thread-safe.
 */
class ChainingBytecodeFactory {
  private final BytecodeFactory bytecodeFactory;

  public ChainingBytecodeFactory(BytecodeFactory bytecodeFactory) {
    this.bytecodeFactory = bytecodeFactory;
  }

  // Expressions

  public BBlob blob(Consumer1<BufferedSink, IOException> writer) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.blob(writer));
  }

  public BBool bool(boolean value) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.bool(value));
  }

  public BCall call(BExpr callableB, BCombine argsB) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.call(callableB, argsB));
  }

  public BCombine combine(List<BExpr> elemBs) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.combine(elemBs));
  }

  public BInt int_(BigInteger value) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.int_(value));
  }

  public BLambda lambda(BFuncType type, BExpr body) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.lambda(type, body));
  }

  public BNativeFunc nativeFunc(
      BFuncType funcType, BBlob jarB, BString classBinaryNameB, BBool isPureB)
      throws SbTranslatorException {
    return invokeTranslatingBytecodeException(
        () -> bytecodeFactory.nativeFunc(funcType, jarB, classBinaryNameB, isPureB));
  }

  public BOrder order(BArrayType arrayType, List<BExpr> elemsB) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.order(arrayType, elemsB));
  }

  public BSelect select(BExpr selectableB, BInt indexB) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.select(selectableB, indexB));
  }

  public BString string(String string) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.string(string));
  }

  public BTuple tuple(List<BValue> items) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.tuple(items));
  }

  public BReference reference(BType evaluationType, BigInteger index) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(
        () -> bytecodeFactory.reference(evaluationType, bytecodeFactory.int_(index)));
  }

  // types

  public BArrayType arrayType(BType elemT) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.arrayType(elemT));
  }

  public BBlobType blobType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::blobType);
  }

  public BBoolType boolType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::boolType);
  }

  public BFuncType funcType(List<BType> paramTs, BType resultT) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.funcType(paramTs, resultT));
  }

  public BFuncType funcType(BTupleType paramTs, BType resultT) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.funcType(paramTs, resultT));
  }

  public BIntType intType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::intType);
  }

  public BStringType stringType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::stringType);
  }

  public BTupleType tupleType(BType... itemTs) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.tupleType(itemTs));
  }

  public BTupleType tupleType(List<BType> itemTs) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.tupleType(itemTs));
  }

  private static <R> R invokeTranslatingBytecodeException(Function0<R, BytecodeException> function0)
      throws SbTranslatorException {
    return Helpers.invokeAndChainBytecodeException(function0, SbTranslatorException::new);
  }
}
