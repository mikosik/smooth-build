package org.smoothbuild.vm.bytecode;

import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.math.BigInteger;

import org.smoothbuild.out.log.Level;
import org.smoothbuild.util.io.DataWriter;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.exc.BytecodeDbExc;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.oper.PickB;
import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.expr.oper.VarB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.BlobBBuilder;
import org.smoothbuild.vm.bytecode.expr.value.BoolB;
import org.smoothbuild.vm.bytecode.expr.value.IfFuncB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.LambdaB;
import org.smoothbuild.vm.bytecode.expr.value.MapFuncB;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.type.CategoryDb;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.BlobTB;
import org.smoothbuild.vm.bytecode.type.value.BoolTB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.IntTB;
import org.smoothbuild.vm.bytecode.type.value.StringTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import com.google.common.collect.ImmutableList;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class BytecodeF {
  private final BytecodeDb bytecodeDb;
  private final CategoryDb categoryDb;
  private final TupleTB messageT;
  private final TupleTB fileT;

  @Inject
  public BytecodeF(BytecodeDb bytecodeDb, CategoryDb categoryDb) {
    this.bytecodeDb = bytecodeDb;
    this.categoryDb = categoryDb;
    this.messageT = createMessageT(categoryDb);
    this.fileT = createFileT(categoryDb);
  }

  // Objects

  public ArrayBBuilder arrayBuilderWithElems(TypeB elemT) {
    return bytecodeDb.arrayBuilder(categoryDb.array(elemT));
  }

  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return bytecodeDb.arrayBuilder(type);
  }

  public BlobB blob(DataWriter dataWriter) {
    try (BlobBBuilder builder = blobBuilder()) {
      builder.write(dataWriter);
      return builder.build();
    } catch (IOException e) {
      throw new BytecodeDbExc(e);
    }
  }

  public BlobBBuilder blobBuilder() {
    return bytecodeDb.blobBuilder();
  }

  public BoolB bool(boolean value) {
    return bytecodeDb.bool(value);
  }

  public CallB call(ExprB func, CombineB args) {
    return bytecodeDb.call(func, args);
  }

  public CombineB combine(ImmutableList<ExprB> items) {
    return bytecodeDb.combine(items);
  }

  public TupleB file(BlobB content, StringB path) {
    return bytecodeDb.tuple(list(content, path));
  }

  public LambdaB lambda(FuncTB type, ExprB body) {
    return bytecodeDb.lambda(type, body);
  }

  public IfFuncB ifFunc(TypeB t) {
    return bytecodeDb.ifFunc(t);
  }

  public IntB int_(BigInteger value) {
    return bytecodeDb.int_(value);
  }

  public MapFuncB mapFunc(TypeB r, TypeB s) {
    return bytecodeDb.mapFunc(r, s);
  }

  public NativeFuncB nativeFunc(FuncTB funcTB, BlobB jar, StringB classBinaryName, BoolB isPure) {
    return bytecodeDb.nativeFunc(funcTB, jar, classBinaryName, isPure);
  }

  public PickB pick(ExprB pickable, ExprB index) {
    return bytecodeDb.pick(pickable, index);
  }

  public VarB var(TypeB evaluationT, BigInteger value) {
    return bytecodeDb.varB(evaluationT, bytecodeDb.int_(value));
  }

  public SelectB select(ExprB selectable, IntB index) {
    return bytecodeDb.select(selectable, index);
  }

  public StringB string(String string) {
    return bytecodeDb.string(string);
  }

  public TupleB tuple(ImmutableList<ValueB> items) {
    return bytecodeDb.tuple(items);
  }

  public OrderB order(ArrayTB evaluationT, ImmutableList<ExprB> elems) {
    return bytecodeDb.order(evaluationT, elems);
  }

  // Types

  public ArrayTB arrayT(TypeB elemT) {
    return categoryDb.array(elemT);
  }

  public BlobTB blobT() {
    return categoryDb.blob();
  }

  public BoolTB boolT() {
    return categoryDb.bool();
  }

  public FuncTB funcT(ImmutableList<TypeB> paramTs, TypeB resultT) {
    return categoryDb.funcT(paramTs, resultT);
  }

  public FuncTB funcT(TupleTB paramTs, TypeB resultT) {
    return categoryDb.funcT(paramTs, resultT);
  }

  public IntTB intT() {
    return categoryDb.int_();
  }

  public TupleTB messageT() {
    return messageT;
  }

  public StringTB stringT() {
    return categoryDb.string();
  }

  public TupleTB tupleT(TypeB... itemTs) {
    return categoryDb.tuple(itemTs);
  }

  public TupleTB tupleT(ImmutableList<TypeB> itemTs) {
    return categoryDb.tuple(itemTs);
  }

  // other values and its types

  public TupleTB fileT() {
    return fileT;
  }

  public TupleB fatalMessage(String text) {
    return message(FATAL, text);
  }

  public TupleB errorMessage(String text) {
    return message(ERROR, text);
  }

  public TupleB warningMessage(String text) {
    return message(WARNING, text);
  }

  public TupleB infoMessage(String text) {
    return message(INFO, text);
  }

  private TupleB message(Level level, String text) {
    ValueB textValue = bytecodeDb.string(text);
    ValueB severityValue = bytecodeDb.string(level.name());
    return bytecodeDb.tuple(list(textValue, severityValue));
  }

  private static TupleTB createMessageT(CategoryDb categoryDb) {
    var stringT = categoryDb.string();
    return categoryDb.tuple(stringT, stringT);
  }

  private static TupleTB createFileT(CategoryDb categoryDb) {
    return categoryDb.tuple(categoryDb.blob(), categoryDb.string());
  }
}
