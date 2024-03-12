package org.smoothbuild.virtualmachine.bytecode;

import static okio.Okio.buffer;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.function.Function0.memoizer;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.math.BigInteger;
import okio.BufferedSink;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.PickB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.ReferenceB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
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
public class BytecodeFactory {
  private final ExprDb exprDb;
  private final CategoryDb categoryDb;
  private final Function0<TupleTB, BytecodeException> storedLogTypeMemoizer;
  private final Function0<TupleTB, BytecodeException> fileTypeMemoizer;

  @Inject
  public BytecodeFactory(ExprDb exprDb, CategoryDb categoryDb) {
    this.exprDb = exprDb;
    this.categoryDb = categoryDb;
    this.storedLogTypeMemoizer = memoizer(() -> createStoredLogType(categoryDb));
    this.fileTypeMemoizer = memoizer(() -> createFileType(categoryDb));
  }

  // Objects

  public ArrayBBuilder arrayBuilderWithElements(TypeB elemT) throws BytecodeException {
    return exprDb.newArrayBuilder(categoryDb.array(elemT));
  }

  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return exprDb.newArrayBuilder(type);
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
    return exprDb.newBlobBuilder();
  }

  public BoolB bool(boolean value) throws BytecodeException {
    return exprDb.newBool(value);
  }

  public CallB call(ExprB func, CombineB args) throws BytecodeException {
    return exprDb.newCall(func, args);
  }

  public CombineB combine(List<ExprB> items) throws BytecodeException {
    return exprDb.newCombine(items);
  }

  public TupleB file(BlobB content, StringB path) throws BytecodeException {
    return exprDb.newTuple(list(content, path));
  }

  public LambdaB lambda(FuncTB type, ExprB body) throws BytecodeException {
    return exprDb.newLambda(type, body);
  }

  public IfFuncB ifFunc(TypeB t) throws BytecodeException {
    return exprDb.newIfFunc(t);
  }

  public IntB int_(BigInteger value) throws BytecodeException {
    return exprDb.newInt(value);
  }

  public MapFuncB mapFunc(TypeB r, TypeB s) throws BytecodeException {
    return exprDb.newMapFunc(r, s);
  }

  public NativeFuncB nativeFunc(FuncTB funcTB, BlobB jar, StringB classBinaryName, BoolB isPure)
      throws BytecodeException {
    return exprDb.newNativeFunc(funcTB, jar, classBinaryName, isPure);
  }

  public PickB pick(ExprB pickable, ExprB index) throws BytecodeException {
    return exprDb.newPick(pickable, index);
  }

  public ReferenceB reference(TypeB evaluationType, IntB index) throws BytecodeException {
    return exprDb.newReference(evaluationType, index);
  }

  public SelectB select(ExprB selectable, IntB index) throws BytecodeException {
    return exprDb.newSelect(selectable, index);
  }

  public StringB string(String string) throws BytecodeException {
    return exprDb.newString(string);
  }

  public TupleB tuple(List<ValueB> items) throws BytecodeException {
    return exprDb.newTuple(items);
  }

  public OrderB order(ArrayTB evaluationType, List<ExprB> elems) throws BytecodeException {
    return exprDb.newOrder(evaluationType, elems);
  }

  // Types

  public ArrayTB arrayType(TypeB elementType) throws BytecodeException {
    return categoryDb.array(elementType);
  }

  public BlobTB blobType() throws BytecodeException {
    return categoryDb.blob();
  }

  public BoolTB boolType() throws BytecodeException {
    return categoryDb.bool();
  }

  public FuncTB funcType(List<TypeB> paramTypes, TypeB resultType) throws BytecodeException {
    return categoryDb.funcT(listOfAll(paramTypes), resultType);
  }

  public FuncTB funcType(TupleTB paramTypes, TypeB resultType) throws BytecodeException {
    return categoryDb.funcT(paramTypes, resultType);
  }

  public IntTB intType() throws BytecodeException {
    return categoryDb.int_();
  }

  public TupleTB storedLogType() throws BytecodeException {
    return storedLogTypeMemoizer.apply();
  }

  public StringTB stringType() throws BytecodeException {
    return categoryDb.string();
  }

  public TupleTB tupleType(TypeB... itemTs) throws BytecodeException {
    return categoryDb.tuple(itemTs);
  }

  public TupleTB tupleType(List<TypeB> itemTs) throws BytecodeException {
    return categoryDb.tuple(itemTs);
  }

  // other values and its types

  public TupleTB fileType() throws BytecodeException {
    return fileTypeMemoizer.apply();
  }

  public TupleB fatalLog(String text) throws BytecodeException {
    return storedLog(FATAL, text);
  }

  public TupleB errorLog(String text) throws BytecodeException {
    return storedLog(ERROR, text);
  }

  public TupleB warningLog(String text) throws BytecodeException {
    return storedLog(WARNING, text);
  }

  public TupleB infoLog(String text) throws BytecodeException {
    return storedLog(INFO, text);
  }

  private TupleB storedLog(Level level, String message) throws BytecodeException {
    var messageValue = exprDb.newString(message);
    var levelValue = exprDb.newString(level.name());
    return exprDb.newTuple(list(messageValue, levelValue));
  }

  private static TupleTB createStoredLogType(CategoryDb categoryDb) throws BytecodeException {
    var stringType = categoryDb.string();
    return categoryDb.tuple(stringType, stringType);
  }

  private static TupleTB createFileType(CategoryDb categoryDb) throws BytecodeException {
    return categoryDb.tuple(categoryDb.blob(), categoryDb.string());
  }
}
