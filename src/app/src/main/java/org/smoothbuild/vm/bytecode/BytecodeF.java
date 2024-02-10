package org.smoothbuild.vm.bytecode;

import static okio.Okio.buffer;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.function.Function0.memoize;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.math.BigInteger;
import okio.BufferedSink;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.exc.BytecodeDbException;
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

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class BytecodeF {
  private final BytecodeDb bytecodeDb;
  private final CategoryDb categoryDb;
  private final Function0<TupleTB, BytecodeException> messageTypeSupplier;
  private final Function0<TupleTB, BytecodeException> fileTypeSupplier;

  @Inject
  public BytecodeF(BytecodeDb bytecodeDb, CategoryDb categoryDb) {
    this.bytecodeDb = bytecodeDb;
    this.categoryDb = categoryDb;
    this.messageTypeSupplier = memoize(() -> createMessageT(categoryDb));
    this.fileTypeSupplier = memoize(() -> createFileT(categoryDb));
  }

  // Objects

  public ArrayBBuilder arrayBuilderWithElems(TypeB elemT) throws BytecodeException {
    return bytecodeDb.arrayBuilder(categoryDb.array(elemT));
  }

  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return bytecodeDb.arrayBuilder(type);
  }

  public BlobB blob(Consumer1<BufferedSink, IOException> writer) throws BytecodeException {
    try (BlobBBuilder builder = blobBuilder()) {
      try (var bufferedSink = buffer(builder)) {
        writer.accept(bufferedSink);
      }
      return builder.build();
    } catch (IOException e) {
      throw new BytecodeDbException(e);
    }
  }

  public BlobBBuilder blobBuilder() throws BytecodeException {
    return bytecodeDb.blobBuilder();
  }

  public BoolB bool(boolean value) throws BytecodeException {
    return bytecodeDb.bool(value);
  }

  public CallB call(ExprB func, CombineB args) throws BytecodeException {
    return bytecodeDb.call(func, args);
  }

  public CombineB combine(List<ExprB> items) throws BytecodeException {
    return bytecodeDb.combine(items);
  }

  public TupleB file(BlobB content, StringB path) throws BytecodeException {
    return bytecodeDb.tuple(list(content, path));
  }

  public LambdaB lambda(FuncTB type, ExprB body) throws BytecodeException {
    return bytecodeDb.lambda(type, body);
  }

  public IfFuncB ifFunc(TypeB t) throws BytecodeException {
    return bytecodeDb.ifFunc(t);
  }

  public IntB int_(BigInteger value) throws BytecodeException {
    return bytecodeDb.int_(value);
  }

  public MapFuncB mapFunc(TypeB r, TypeB s) throws BytecodeException {
    return bytecodeDb.mapFunc(r, s);
  }

  public NativeFuncB nativeFunc(FuncTB funcTB, BlobB jar, StringB classBinaryName, BoolB isPure)
      throws BytecodeException {
    return bytecodeDb.nativeFunc(funcTB, jar, classBinaryName, isPure);
  }

  public PickB pick(ExprB pickable, ExprB index) throws BytecodeException {
    return bytecodeDb.pick(pickable, index);
  }

  public VarB var(TypeB evaluationT, BigInteger value) throws BytecodeException {
    return bytecodeDb.varB(evaluationT, bytecodeDb.int_(value));
  }

  public SelectB select(ExprB selectable, IntB index) throws BytecodeException {
    return bytecodeDb.select(selectable, index);
  }

  public StringB string(String string) throws BytecodeException {
    return bytecodeDb.string(string);
  }

  public TupleB tuple(List<ValueB> items) throws BytecodeException {
    return bytecodeDb.tuple(items);
  }

  public OrderB order(ArrayTB evaluationT, List<ExprB> elems) throws BytecodeException {
    return bytecodeDb.order(evaluationT, elems);
  }

  // Types

  public ArrayTB arrayT(TypeB elemT) throws BytecodeException {
    return categoryDb.array(elemT);
  }

  public BlobTB blobT() throws BytecodeException {
    return categoryDb.blob();
  }

  public BoolTB boolT() throws BytecodeException {
    return categoryDb.bool();
  }

  public FuncTB funcT(List<TypeB> paramTs, TypeB resultT) throws BytecodeException {
    return categoryDb.funcT(listOfAll(paramTs), resultT);
  }

  public FuncTB funcT(TupleTB paramTs, TypeB resultT) throws BytecodeException {
    return categoryDb.funcT(paramTs, resultT);
  }

  public IntTB intT() throws BytecodeException {
    return categoryDb.int_();
  }

  public TupleTB messageT() throws BytecodeException {
    return messageTypeSupplier.apply();
  }

  public StringTB stringT() throws BytecodeException {
    return categoryDb.string();
  }

  public TupleTB tupleT(TypeB... itemTs) throws BytecodeException {
    return categoryDb.tuple(itemTs);
  }

  public TupleTB tupleT(List<TypeB> itemTs) throws BytecodeException {
    return categoryDb.tuple(itemTs);
  }

  // other values and its types

  public TupleTB fileT() throws BytecodeException {
    return fileTypeSupplier.apply();
  }

  public TupleB fatalMessage(String text) throws BytecodeException {
    return message(FATAL, text);
  }

  public TupleB errorMessage(String text) throws BytecodeException {
    return message(ERROR, text);
  }

  public TupleB warningMessage(String text) throws BytecodeException {
    return message(WARNING, text);
  }

  public TupleB infoMessage(String text) throws BytecodeException {
    return message(INFO, text);
  }

  private TupleB message(Level level, String text) throws BytecodeException {
    ValueB textValue = bytecodeDb.string(text);
    ValueB severityValue = bytecodeDb.string(level.name());
    return bytecodeDb.tuple(list(textValue, severityValue));
  }

  private static TupleTB createMessageT(CategoryDb categoryDb) throws BytecodeException {
    var stringT = categoryDb.string();
    return categoryDb.tuple(stringT, stringT);
  }

  private static TupleTB createFileT(CategoryDb categoryDb) throws BytecodeException {
    return categoryDb.tuple(categoryDb.blob(), categoryDb.string());
  }
}
