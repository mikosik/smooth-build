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
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArrayBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlobBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.BKindDb;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BBlobType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BBoolType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIntType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BStringType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class BytecodeFactory {
  private final BExprDb exprDb;
  private final BKindDb kindDb;
  private final Function0<BTupleType, BytecodeException> storedLogTypeMemoizer;
  private final Function0<BTupleType, BytecodeException> fileTypeMemoizer;

  @Inject
  public BytecodeFactory(BExprDb exprDb, BKindDb kindDb) {
    this.exprDb = exprDb;
    this.kindDb = kindDb;
    this.storedLogTypeMemoizer = memoizer(() -> createStoredLogType(kindDb));
    this.fileTypeMemoizer = memoizer(() -> createFileType(kindDb));
  }

  // Objects

  public BArrayBuilder arrayBuilderWithElements(BType elementType) throws BytecodeException {
    return exprDb.newArrayBuilder(kindDb.array(elementType));
  }

  public BArrayBuilder arrayBuilder(BArrayType type) {
    return exprDb.newArrayBuilder(type);
  }

  public BBlob blob(Consumer1<BufferedSink, IOException> writer) throws BytecodeException {
    try (BBlobBuilder builder = blobBuilder()) {
      try (var bufferedSink = buffer(builder)) {
        writer.accept(bufferedSink);
      }
      return builder.build();
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }

  public BBlobBuilder blobBuilder() throws BytecodeException {
    return exprDb.newBlobBuilder();
  }

  public BBool bool(boolean value) throws BytecodeException {
    return exprDb.newBool(value);
  }

  public BCall call(BExpr func, BCombine args) throws BytecodeException {
    return exprDb.newCall(func, args);
  }

  public BCombine combine(List<BExpr> items) throws BytecodeException {
    return exprDb.newCombine(items);
  }

  public BTuple file(BBlob content, BString path) throws BytecodeException {
    return exprDb.newTuple(list(content, path));
  }

  public BLambda lambda(BFuncType type, BExpr body) throws BytecodeException {
    return exprDb.newLambda(type, body);
  }

  public BIf ifFunc(BType t) throws BytecodeException {
    return exprDb.newIfFunc(t);
  }

  public BInt int_(BigInteger value) throws BytecodeException {
    return exprDb.newInt(value);
  }

  public BMap mapFunc(BType r, BType s) throws BytecodeException {
    return exprDb.newMapFunc(r, s);
  }

  public BNativeFunc nativeFunc(
      BFuncType funcType, BBlob jar, BString classBinaryName, BBool isPure)
      throws BytecodeException {
    return exprDb.newNativeFunc(funcType, jar, classBinaryName, isPure);
  }

  public BPick pick(BExpr pickable, BExpr index) throws BytecodeException {
    return exprDb.newPick(pickable, index);
  }

  public BReference reference(BType evaluationType, BInt index) throws BytecodeException {
    return exprDb.newReference(evaluationType, index);
  }

  public BSelect select(BExpr selectable, BInt index) throws BytecodeException {
    return exprDb.newSelect(selectable, index);
  }

  public BString string(String string) throws BytecodeException {
    return exprDb.newString(string);
  }

  public BTuple tuple(List<BValue> items) throws BytecodeException {
    return exprDb.newTuple(items);
  }

  public BOrder order(BArrayType evaluationType, List<BExpr> elements) throws BytecodeException {
    return exprDb.newOrder(evaluationType, elements);
  }

  // Types

  public BArrayType arrayType(BType elementType) throws BytecodeException {
    return kindDb.array(elementType);
  }

  public BBlobType blobType() throws BytecodeException {
    return kindDb.blob();
  }

  public BBoolType boolType() throws BytecodeException {
    return kindDb.bool();
  }

  public BFuncType funcType(List<BType> paramTypes, BType resultType) throws BytecodeException {
    return kindDb.funcT(listOfAll(paramTypes), resultType);
  }

  public BFuncType funcType(BTupleType paramTypes, BType resultType) throws BytecodeException {
    return kindDb.funcT(paramTypes, resultType);
  }

  public BIntType intType() throws BytecodeException {
    return kindDb.int_();
  }

  public BTupleType storedLogType() throws BytecodeException {
    return storedLogTypeMemoizer.apply();
  }

  public BStringType stringType() throws BytecodeException {
    return kindDb.string();
  }

  public BTupleType tupleType(BType... itemTs) throws BytecodeException {
    return kindDb.tuple(itemTs);
  }

  public BTupleType tupleType(List<BType> itemTs) throws BytecodeException {
    return kindDb.tuple(itemTs);
  }

  // other values and its types

  public BTupleType fileType() throws BytecodeException {
    return fileTypeMemoizer.apply();
  }

  public BTuple fatalLog(String text) throws BytecodeException {
    return storedLog(FATAL, text);
  }

  public BTuple errorLog(String text) throws BytecodeException {
    return storedLog(ERROR, text);
  }

  public BTuple warningLog(String text) throws BytecodeException {
    return storedLog(WARNING, text);
  }

  public BTuple infoLog(String text) throws BytecodeException {
    return storedLog(INFO, text);
  }

  private BTuple storedLog(Level level, String message) throws BytecodeException {
    var messageValue = exprDb.newString(message);
    var levelValue = exprDb.newString(level.name());
    return exprDb.newTuple(list(messageValue, levelValue));
  }

  private static BTupleType createStoredLogType(BKindDb kindDb) throws BytecodeException {
    var stringType = kindDb.string();
    return kindDb.tuple(stringType, stringType);
  }

  private static BTupleType createFileType(BKindDb kindDb) throws BytecodeException {
    return kindDb.tuple(kindDb.blob(), kindDb.string());
  }
}
