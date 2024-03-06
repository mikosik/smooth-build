package org.smoothbuild.compilerbackend;

import java.io.IOException;
import java.math.BigInteger;
import okio.BufferedSink;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.Helpers;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.ReferenceB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BoolB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.LambdaB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.BlobTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.BoolTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.IntTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.StringTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

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

  public BlobB blob(Consumer1<BufferedSink, IOException> writer) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.blob(writer));
  }

  public BoolB bool(boolean value) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.bool(value));
  }

  public CallB call(ExprB callableB, CombineB argsB) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.call(callableB, argsB));
  }

  public CombineB combine(List<ExprB> elemBs) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.combine(elemBs));
  }

  public IntB int_(BigInteger value) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.int_(value));
  }

  public LambdaB lambda(FuncTB type, ExprB body) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.lambda(type, body));
  }

  public NativeFuncB nativeFunc(FuncTB funcTB, BlobB jarB, StringB classBinaryNameB, BoolB isPureB)
      throws SbTranslatorException {
    return invokeTranslatingBytecodeException(
        () -> bytecodeFactory.nativeFunc(funcTB, jarB, classBinaryNameB, isPureB));
  }

  public OrderB order(ArrayTB arrayTB, List<ExprB> elemsB) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.order(arrayTB, elemsB));
  }

  public SelectB select(ExprB selectableB, IntB indexB) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.select(selectableB, indexB));
  }

  public StringB string(String string) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.string(string));
  }

  public TupleB tuple(List<ValueB> items) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.tuple(items));
  }

  public ReferenceB reference(TypeB evaluationType, BigInteger index) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(
        () -> bytecodeFactory.reference(evaluationType, bytecodeFactory.int_(index)));
  }

  // types

  public ArrayTB arrayType(TypeB elemT) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.arrayType(elemT));
  }

  public BlobTB blobType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::blobType);
  }

  public BoolTB boolType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::boolType);
  }

  public FuncTB funcType(List<TypeB> paramTs, TypeB resultT) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.funcType(paramTs, resultT));
  }

  public FuncTB funcType(TupleTB paramTs, TypeB resultT) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.funcType(paramTs, resultT));
  }

  public IntTB intType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::intType);
  }

  public StringTB stringType() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeFactory::stringType);
  }

  public TupleTB tupleType(TypeB... itemTs) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.tupleType(itemTs));
  }

  public TupleTB tupleType(List<TypeB> itemTs) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeFactory.tupleType(itemTs));
  }

  private static <R> R invokeTranslatingBytecodeException(Function0<R, BytecodeException> function0)
      throws SbTranslatorException {
    return Helpers.invokeAndChainBytecodeException(function0, SbTranslatorException::new);
  }
}
