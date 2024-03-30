package org.smoothbuild.compilerbackend;

import java.io.IOException;
import java.math.BigInteger;
import okio.BufferedSink;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.Helpers;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMethod;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBlobType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBoolType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIntType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BStringType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

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

  public BCall call(BExpr lambda, BExpr arguments) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.call(lambda, arguments));
  }

  public BCombine combine(List<BExpr> elements) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.combine(elements));
  }

  public BInvoke invoke(BType evaluationType, BExpr method, BExpr isPure, BExpr arguments)
      throws SbTranslatorException {
    return invokeTranslatingBytecodeException(
        () -> bytecodeFactory.invoke(evaluationType, method, isPure, arguments));
  }

  public BInt int_(BigInteger value) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.int_(value));
  }

  public BLambda lambda(BLambdaType type, BExpr body) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.lambda(type, body));
  }

  public BMethod method(BBlob jar, BString classBinaryName) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.method(jar, classBinaryName));
  }

  public BOrder order(BArrayType arrayType, List<BExpr> elements) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.order(arrayType, elements));
  }

  public BSelect select(BExpr selectable, BInt index) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.select(selectable, index));
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

  public BArrayType arrayType(BType elementType) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.arrayType(elementType));
  }

  public BBlobType blobType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::blobType);
  }

  public BBoolType boolType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::boolType);
  }

  public BLambdaType funcType(List<BType> paramTypes, BType resultType)
      throws SbTranslatorException {
    return invokeTranslatingBytecodeException(
        () -> bytecodeFactory.lambdaType(paramTypes, resultType));
  }

  public BLambdaType funcType(BTupleType paramTypes, BType resultType)
      throws SbTranslatorException {
    return invokeTranslatingBytecodeException(
        () -> bytecodeFactory.lambdaType(paramTypes, resultType));
  }

  public BIntType intType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::intType);
  }

  public BStringType stringType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::stringType);
  }

  public BTupleType tupleType(BType... itemTypes) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.tupleType(itemTypes));
  }

  public BTupleType tupleType(List<BType> itemTypes) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.tupleType(itemTypes));
  }

  private static <R> R invokeTranslatingBytecodeException(Function0<R, BytecodeException> function0)
      throws SbTranslatorException {
    return Helpers.invokeAndChainBytecodeException(function0, SbTranslatorException::new);
  }
}
