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
import java.io.IOException;
import java.math.BigInteger;
import okio.BufferedSink;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlobBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructVariant;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFold;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMethod;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BRef;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTupleGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BVariant;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBlobType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBoolType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIntType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BStringType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BVariantType;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@PerCommand
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

  public BBlob blob(Consumer1<BufferedSink, IOException> writer) throws IOException {
    try (BBlobBuilder builder = blobBuilder()) {
      try (var bufferedSink = buffer(builder)) {
        writer.accept(bufferedSink);
      }
      return builder.build();
    }
  }

  public BBlobBuilder blobBuilder() throws BytecodeException {
    return exprDb.newBlobBuilder();
  }

  public BBool bool(boolean value) throws BytecodeException {
    return exprDb.newBool(value);
  }

  public BCall call(BExpr lambda, BExpr arguments) throws BytecodeException {
    return exprDb.newCall(lambda, arguments);
  }

  public BVariant variant(BVariantType type, BInt index, BValue choice) throws BytecodeException {
    return exprDb.newVariant(type, index, choice);
  }

  public BConstructVariant constructVariant(BVariantType type, BInt index, BExpr choice)
      throws BytecodeException {
    return exprDb.newConstructVariant(type, index, choice);
  }

  public BConstructTuple constructTuple(List<BExpr> items) throws BytecodeException {
    return exprDb.newConstructTuple(items);
  }

  public BTuple file(BBlob content, BString path) throws BytecodeException {
    return exprDb.newTuple(list(content, path));
  }

  public BFold fold(BExpr array, BExpr initial, BExpr folder) throws BytecodeException {
    return exprDb.newFold(array, initial, folder);
  }

  public BIf if_(BExpr condition, BExpr then_, BExpr else_) throws BytecodeException {
    return exprDb.newIf(condition, then_, else_);
  }

  public BInt int_(BigInteger value) throws BytecodeException {
    return exprDb.newInt(value);
  }

  public BInvoke invoke(BType evaluationType, BExpr method, BExpr isPure, BExpr arguments)
      throws BytecodeException {
    return exprDb.newInvoke(evaluationType, method, isPure, arguments);
  }

  public BLambda lambda(BLambdaType type, BExpr body) throws BytecodeException {
    return exprDb.newLambda(type, body);
  }

  public BMap map(BExpr array, BExpr mapper) throws BytecodeException {
    return exprDb.newMap(array, mapper);
  }

  public BMethod method(BBlob jar, BString classBinaryName, BString methodName)
      throws BytecodeException {
    return new BMethod(tuple(list(jar, classBinaryName, methodName)));
  }

  public BArrayGet arrayGet(BExpr array, BExpr index) throws BytecodeException {
    return exprDb.newArrayGet(array, index);
  }

  public BRef ref(BType evaluationType, BInt index) throws BytecodeException {
    return exprDb.newRef(evaluationType, index);
  }

  public BTupleGet tupleGet(BExpr tupleExpr, BInt index) throws BytecodeException {
    return exprDb.newTupleGet(tupleExpr, index);
  }

  public BString string(String string) throws BytecodeException {
    return exprDb.newString(string);
  }

  public BSwitch switch_(BExpr variant, BConstructTuple handlers) throws BytecodeException {
    return exprDb.newSwitch(variant, handlers);
  }

  public BTuple tuple(List<BValue> items) throws BytecodeException {
    return exprDb.newTuple(items);
  }

  public BConstructArray constructArray(BArrayType evaluationType, List<BExpr> elements)
      throws BytecodeException {
    return exprDb.newConstructArray(evaluationType, elements);
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

  public BLambdaType lambdaType(List<BType> paramTypes, BType resultType) throws BytecodeException {
    return kindDb.lambda(listOfAll(paramTypes), resultType);
  }

  public BLambdaType lambdaType(BTupleType paramTypes, BType resultType) throws BytecodeException {
    return kindDb.lambda(paramTypes, resultType);
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
    return exprDb.newTuple(list(levelValue, messageValue));
  }

  private static BTupleType createStoredLogType(BKindDb kindDb) throws BytecodeException {
    var stringType = kindDb.string();
    return kindDb.tuple(stringType, stringType);
  }

  private static BTupleType createFileType(BKindDb kindDb) throws BytecodeException {
    return kindDb.tuple(kindDb.blob(), kindDb.string());
  }
}
