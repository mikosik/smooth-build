package org.smoothbuild.virtualmachine.bytecode;

import static okio.Okio.buffer;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.function.Function0.memoizer;
import static org.smoothbuild.common.log.Level.ERROR;
import static org.smoothbuild.common.log.Level.FATAL;
import static org.smoothbuild.common.log.Level.INFO;
import static org.smoothbuild.common.log.Level.WARNING;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.math.BigInteger;
import okio.BufferedSink;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.PickB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.VarB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobBBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BoolB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IfFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.LambdaB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.MapFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryDb;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.BlobTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.BoolTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.IntTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.StringTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class BytecodeF {
  private final ExprDb exprDb;
  private final CategoryDb categoryDb;
  private final Function0<TupleTB, BytecodeException> messageTypeMemoizer;
  private final Function0<TupleTB, BytecodeException> fileTypeMemoizer;

  @Inject
  public BytecodeF(ExprDb exprDb, CategoryDb categoryDb) {
    this.exprDb = exprDb;
    this.categoryDb = categoryDb;
    this.messageTypeMemoizer = memoizer(() -> createMessageT(categoryDb));
    this.fileTypeMemoizer = memoizer(() -> createFileT(categoryDb));
  }

  // Objects

  public ArrayBBuilder arrayBuilderWithElements(TypeB elemT) throws BytecodeException {
    return exprDb.arrayBuilder(categoryDb.array(elemT));
  }

  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return exprDb.arrayBuilder(type);
  }

  public BlobB blob(Consumer1<BufferedSink, IOException> writer) throws BytecodeException {
    try (BlobBBuilder builder = blobBuilder()) {
      try (var bufferedSink = buffer(builder)) {
        writer.accept(bufferedSink);
      }
      return builder.build();
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }

  public BlobBBuilder blobBuilder() throws BytecodeException {
    return exprDb.blobBuilder();
  }

  public BoolB bool(boolean value) throws BytecodeException {
    return exprDb.bool(value);
  }

  public CallB call(ExprB func, CombineB args) throws BytecodeException {
    return exprDb.call(func, args);
  }

  public CombineB combine(List<ExprB> items) throws BytecodeException {
    return exprDb.combine(items);
  }

  public TupleB file(BlobB content, StringB path) throws BytecodeException {
    return exprDb.tuple(list(content, path));
  }

  public LambdaB lambda(FuncTB type, ExprB body) throws BytecodeException {
    return exprDb.lambda(type, body);
  }

  public IfFuncB ifFunc(TypeB t) throws BytecodeException {
    return exprDb.ifFunc(t);
  }

  public IntB int_(BigInteger value) throws BytecodeException {
    return exprDb.int_(value);
  }

  public MapFuncB mapFunc(TypeB r, TypeB s) throws BytecodeException {
    return exprDb.mapFunc(r, s);
  }

  public NativeFuncB nativeFunc(FuncTB funcTB, BlobB jar, StringB classBinaryName, BoolB isPure)
      throws BytecodeException {
    return exprDb.nativeFunc(funcTB, jar, classBinaryName, isPure);
  }

  public PickB pick(ExprB pickable, ExprB index) throws BytecodeException {
    return exprDb.pick(pickable, index);
  }

  public VarB var(TypeB evaluationType, BigInteger index) throws BytecodeException {
    return exprDb.varB(evaluationType, exprDb.int_(index));
  }

  public SelectB select(ExprB selectable, IntB index) throws BytecodeException {
    return exprDb.select(selectable, index);
  }

  public StringB string(String string) throws BytecodeException {
    return exprDb.string(string);
  }

  public TupleB tuple(List<ValueB> items) throws BytecodeException {
    return exprDb.tuple(items);
  }

  public OrderB order(ArrayTB evaluationType, List<ExprB> elems) throws BytecodeException {
    return exprDb.order(evaluationType, elems);
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
    return messageTypeMemoizer.apply();
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
    return fileTypeMemoizer.apply();
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
    ValueB textValue = exprDb.string(text);
    ValueB severityValue = exprDb.string(level.name());
    return exprDb.tuple(list(textValue, severityValue));
  }

  private static TupleTB createMessageT(CategoryDb categoryDb) throws BytecodeException {
    var stringT = categoryDb.string();
    return categoryDb.tuple(stringT, stringT);
  }

  private static TupleTB createFileT(CategoryDb categoryDb) throws BytecodeException {
    return categoryDb.tuple(categoryDb.blob(), categoryDb.string());
  }
}
