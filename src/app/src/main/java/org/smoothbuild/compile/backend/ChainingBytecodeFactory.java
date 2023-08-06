package org.smoothbuild.compile.backend;

import java.io.IOException;
import java.math.BigInteger;
import okio.BufferedSink;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.Helpers;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.VarB;
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
  private final BytecodeF bytecodeF;

  public ChainingBytecodeFactory(BytecodeF bytecodeF) {
    this.bytecodeF = bytecodeF;
  }

  // Expressions

  public BlobB blob(Consumer1<BufferedSink, IOException> writer) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.blob(writer));
  }

  public BoolB bool(boolean value) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.bool(value));
  }

  public CallB call(ExprB callableB, CombineB argsB) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.call(callableB, argsB));
  }

  public CombineB combine(List<ExprB> elemBs) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.combine(elemBs));
  }

  public IntB int_(BigInteger value) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.int_(value));
  }

  public LambdaB lambda(FuncTB type, ExprB body) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.lambda(type, body));
  }

  public NativeFuncB nativeFunc(FuncTB funcTB, BlobB jarB, StringB classBinaryNameB, BoolB isPureB)
      throws SbTranslatorException {
    return invokeTranslatingBytecodeException(
        () -> bytecodeF.nativeFunc(funcTB, jarB, classBinaryNameB, isPureB));
  }

  public OrderB order(ArrayTB arrayTB, List<ExprB> elemsB) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.order(arrayTB, elemsB));
  }

  public SelectB select(ExprB selectableB, IntB indexB) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.select(selectableB, indexB));
  }

  public StringB string(String string) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.string(string));
  }

  public TupleB tuple(List<ValueB> items) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.tuple(items));
  }

  public VarB var(TypeB evaluationT, BigInteger index) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.var(evaluationT, index));
  }

  // types

  public ArrayTB arrayT(TypeB elemT) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.arrayT(elemT));
  }

  public BlobTB blobT() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeF::blobT);
  }

  public BoolTB boolT() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeF::boolT);
  }

  public FuncTB funcT(List<TypeB> paramTs, TypeB resultT) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.funcT(paramTs, resultT));
  }

  public FuncTB funcT(TupleTB paramTs, TypeB resultT) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.funcT(paramTs, resultT));
  }

  public IntTB intT() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeF::intT);
  }

  public StringTB stringT() throws SbTranslatorException {
    return invokeTranslatingBytecodeException(bytecodeF::stringT);
  }

  public TupleTB tupleT(TypeB... itemTs) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.tupleT(itemTs));
  }

  public TupleTB tupleT(List<TypeB> itemTs) throws SbTranslatorException {
    return invokeTranslatingBytecodeException(() -> bytecodeF.tupleT(itemTs));
  }

  private static <R> R invokeTranslatingBytecodeException(Function0<R, BytecodeException> function0)
      throws SbTranslatorException {
    return Helpers.invokeAndChainBytecodeException(function0, SbTranslatorException::new);
  }
}
