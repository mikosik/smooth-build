package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.common.collect.List.list;

import java.io.IOException;
import java.math.BigInteger;
import okio.ByteString;
import org.smoothbuild.common.Constants;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.io.Okios;
import org.smoothbuild.common.reflect.Classes;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlobBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBlobType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBoolType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCallKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCombineKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIfKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIntType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BInvokeKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BMapKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BOrderKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BPickKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BReferenceKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSelectKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BStringType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public abstract class TestingBytecode {

  protected abstract BKindDb kindDb();

  protected abstract BytecodeFactory bytecodeF();

  // InstB types

  public BTupleType bAnimalType() throws BytecodeException {
    return bTupleType(bStringType(), bIntType());
  }

  public BArrayType bArrayType() throws BytecodeException {
    return bArrayType(bStringType());
  }

  public BArrayType bArrayType(BType elemT) throws BytecodeException {
    return kindDb().array(elemT);
  }

  public BBlobType bBlobType() throws BytecodeException {
    return kindDb().blob();
  }

  public BBoolType bBoolType() throws BytecodeException {
    return kindDb().bool();
  }

  public BTupleType bFileType() throws BytecodeException {
    return bytecodeF().fileType();
  }

  public BLambdaKind bLambdaKind() throws BytecodeException {
    return bLambdaKind(bBlobType(), bStringType(), bIntType());
  }

  public BLambdaKind bLambdaKind(BType resultT) throws BytecodeException {
    return kindDb().lambda(bFuncType(resultT));
  }

  public BLambdaKind bLambdaKind(BType param, BType resultT) throws BytecodeException {
    return kindDb().lambda(bFuncType(param, resultT));
  }

  public BLambdaKind bLambdaKind(BType param1, BType param2, BType resultT)
      throws BytecodeException {
    return kindDb().lambda(bFuncType(param1, param2, resultT));
  }

  public BFuncType bFuncType() throws BytecodeException {
    return bFuncType(bBlobType(), bStringType(), bIntType());
  }

  public BFuncType bFuncType(BType resultT) throws BytecodeException {
    return bFuncType(list(), resultT);
  }

  public BFuncType bFuncType(BType param1, BType resultT) throws BytecodeException {
    return bFuncType(list(param1), resultT);
  }

  public BFuncType bFuncType(BType param1, BType param2, BType resultT) throws BytecodeException {
    return bFuncType(list(param1, param2), resultT);
  }

  public BFuncType bFuncType(List<BType> paramTs, BType resultT) throws BytecodeException {
    return kindDb().funcT(paramTs, resultT);
  }

  public BIntType bIntType() throws BytecodeException {
    return kindDb().int_();
  }

  public BInvokeKind bInvokeKind() throws BytecodeException {
    return bInvokeKind(bIntType());
  }

  public BInvokeKind bInvokeKind(BType evaluationType) throws BytecodeException {
    return kindDb().invoke(evaluationType);
  }

  public BTupleType bPersonType() throws BytecodeException {
    return bTupleType(bStringType(), bStringType());
  }

  public BStringType bStringType() throws BytecodeException {
    return kindDb().string();
  }

  public BTupleType bTupleType(BType... itemTypes) throws BytecodeException {
    return kindDb().tuple(itemTypes);
  }

  // Operation kinds

  public BCallKind bCallKind() throws BytecodeException {
    return bCallKind(bIntType());
  }

  public BCallKind bCallKind(BType evaluationType) throws BytecodeException {
    return kindDb().call(evaluationType);
  }

  public BCombineKind bCombineKind(BType... itemTypes) throws BytecodeException {
    return kindDb().combine(bTupleType(itemTypes));
  }

  public BIfKind bIfKind() throws BytecodeException {
    return bIfKind(bIntType());
  }

  public BIfKind bIfKind(BType evaluationType) throws BytecodeException {
    return kindDb().if_(evaluationType);
  }

  public BMapKind bMapKind() throws BytecodeException {
    return bMapKind(bArrayType(bIntType()));
  }

  public BMapKind bMapKind(BType evaluationType) throws BytecodeException {
    return kindDb().map(evaluationType);
  }

  public BOrderKind bOrderKind() throws BytecodeException {
    return bOrderKind(bIntType());
  }

  public BOrderKind bOrderKind(BType elemT) throws BytecodeException {
    return kindDb().order(bArrayType(elemT));
  }

  public BPickKind bPickKind() throws BytecodeException {
    return bPickKind(bIntType());
  }

  public BPickKind bPickKind(BType evaluationType) throws BytecodeException {
    return kindDb().pick(evaluationType);
  }

  public BReferenceKind bReferenceKind() throws BytecodeException {
    return bReferenceKind(bIntType());
  }

  public BReferenceKind bReferenceKind(BType evaluationType) throws BytecodeException {
    return kindDb().reference(evaluationType);
  }

  public BSelectKind bSelectKind() throws BytecodeException {
    return bSelectKind(bIntType());
  }

  public BSelectKind bSelectKind(BType evaluationType) throws BytecodeException {
    return kindDb().select(evaluationType);
  }

  // ValueB-s

  public BTuple bAnimal() throws BytecodeException {
    return bAnimal("rabbit", 7);
  }

  public BTuple bAnimal(String species, int speed) throws BytecodeException {
    return bAnimal(bString(species), bInt(speed));
  }

  public BTuple bAnimal(BString species, BInt speed) throws BytecodeException {
    return bTuple(species, speed);
  }

  public BArray bArray(BValue... elements) throws BytecodeException {
    return bArray(elements[0].evaluationType(), elements);
  }

  public BArray bArray(BType elementType, BValue... elements) throws BytecodeException {
    return bytecodeF()
        .arrayBuilder(bArrayType(elementType))
        .addAll(list(elements))
        .build();
  }

  public BBlob blobBJarWithPluginApi(Class<?>... classes) throws BytecodeException {
    return bBlobWith(list(classes)
        .append(
            BBlob.class,
            NativeApi.class,
            BExpr.class,
            BString.class,
            BTuple.class,
            BValue.class,
            BytecodeException.class));
  }

  public BBlob blobBJarWithJavaByteCode(Class<?>... classes) throws BytecodeException {
    return bBlobWith(list(classes));
  }

  private BBlob bBlobWith(java.util.List<Class<?>> list) throws BytecodeException {
    try {
      try (var blobBBuilder = bytecodeF().blobBuilder()) {
        Classes.saveBytecodeInJar(blobBBuilder, list);
        return blobBBuilder.build();
      }
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }

  public BBlob bBlob() throws BytecodeException {
    return bytecodeF().blob(sink -> sink.writeUtf8("blob data"));
  }

  public BBlob bBlob(int data) throws BytecodeException {
    return bBlob(Okios.intToByteString(data));
  }

  public BBlob bBlob(ByteString bytes) throws BytecodeException {
    return bytecodeF().blob(sink -> sink.write(bytes));
  }

  public BBlobBuilder bBlobBuilder() throws BytecodeException {
    return bytecodeF().blobBuilder();
  }

  public BBool bBool() throws BytecodeException {
    return bBool(true);
  }

  public BBool bBool(boolean value) throws BytecodeException {
    return bytecodeF().bool(value);
  }

  public BTuple bFile(Path path) throws BytecodeException {
    return bFile(path, path.toString());
  }

  public BTuple bFile(Path path, String content) throws BytecodeException {
    return bFile(path.toString(), content);
  }

  public BTuple bFile(String path, String content) throws BytecodeException {
    return bFile(path, ByteString.encodeString(content, Constants.CHARSET));
  }

  public BTuple bFile(Path path, ByteString content) throws BytecodeException {
    return bFile(path.toString(), content);
  }

  public BTuple bFile(String path, ByteString content) throws BytecodeException {
    return bFile(path, bBlob(content));
  }

  public BTuple bFile(String path, BBlob blob) throws BytecodeException {
    BString string = bytecodeF().string(path);
    return bytecodeF().file(blob, string);
  }

  public BLambda bLambda() throws BytecodeException {
    return bLambda(bInt());
  }

  public BLambda bLambda(BExpr body) throws BytecodeException {
    return bLambda(list(), body);
  }

  public BLambda bLambda(List<BType> paramTs, BExpr body) throws BytecodeException {
    var funcTB = bFuncType(paramTs, body.evaluationType());
    return bLambda(funcTB, body);
  }

  public BLambda bLambda(BFuncType type, BExpr body) throws BytecodeException {
    return bytecodeF().lambda(type, body);
  }

  public BLambda bIntIdFunc() throws BytecodeException {
    return bLambda(list(bIntType()), bReference(bIntType(), 0));
  }

  public BLambda bStringIdFunc() throws BytecodeException {
    return bLambda(list(bStringType()), bReference(bStringType(), 0));
  }

  public BLambda bReturnAbcFunc() throws BytecodeException {
    return bLambda(bString("abc"));
  }

  public BInt bInt() throws BytecodeException {
    return bInt(17);
  }

  public BInt bInt(int value) throws BytecodeException {
    return bInt(BigInteger.valueOf(value));
  }

  public BInt bInt(BigInteger value) throws BytecodeException {
    return bytecodeF().int_(value);
  }

  public BInvoke bReturnAbcInvoke() throws BytecodeException {
    return bReturnAbcInvoke(true);
  }

  public BInvoke bReturnAbcInvoke(boolean isPure) throws BytecodeException {
    return bInvoke(bStringType(), ReturnAbcFunc.class, isPure);
  }

  public static class ReturnAbcFunc {
    public static BValue func(NativeApi nativeApi, BTuple arguments) throws BytecodeException {
      return nativeApi.factory().string("abc");
    }
  }

  public BInvoke bInvoke() throws BytecodeException {
    return bInvoke(bIntType());
  }

  public BInvoke bInvoke(BType evaluationType) throws BytecodeException {
    return bInvoke(evaluationType, bBlob(7), bString("class binary name"), bBool(true), bTuple());
  }

  public BInvoke bInvoke(Class<?> clazz) throws BytecodeException {
    return bInvoke(bIntType(), clazz);
  }

  public BInvoke bInvoke(BType evaluationType, Class<?> clazz) throws BytecodeException {
    return bInvoke(evaluationType, clazz, true);
  }

  public BInvoke bInvoke(BType evaluationType, BExpr jar, BExpr classBinaryName)
      throws BytecodeException {
    return bInvoke(evaluationType, jar, classBinaryName, bBool(true), bTuple());
  }

  public BInvoke bInvoke(BType evaluationType, Class<?> clazz, boolean isPure)
      throws BytecodeException {
    return bInvoke(evaluationType, clazz, isPure, bTuple());
  }

  public BInvoke bInvoke(BType evaluationType, Class<?> clazz, boolean isPure, BTuple arguments)
      throws BytecodeException {
    return bInvoke(
        evaluationType,
        blobBJarWithPluginApi(clazz),
        bString(clazz.getName()),
        bBool(isPure),
        arguments);
  }

  public BInvoke bInvoke(BType evaluationType, BExpr jar, BExpr classBinaryName, BExpr arguments)
      throws BytecodeException {
    return bytecodeF().invoke(evaluationType, jar, classBinaryName, bBool(true), arguments);
  }

  public BInvoke bInvoke(
      BType evaluationType, BExpr jar, BExpr classBinaryName, BExpr isPure, BExpr arguments)
      throws BytecodeException {
    return bytecodeF().invoke(evaluationType, jar, classBinaryName, isPure, arguments);
  }

  public BTuple bPerson(String firstName, String lastName) throws BytecodeException {
    return bTuple(bString(firstName), bString(lastName));
  }

  public BString bString() throws BytecodeException {
    return bytecodeF().string("abc");
  }

  public BString bString(String string) throws BytecodeException {
    return bytecodeF().string(string);
  }

  public BTuple bTuple(BValue... items) throws BytecodeException {
    return bytecodeF().tuple(list(items));
  }

  public BArray bLogArrayWithOneError() throws BytecodeException {
    return bArray(bytecodeF().errorLog("error message"));
  }

  public BArray bLogArrayEmpty() throws BytecodeException {
    return bArray(bytecodeF().storedLogType());
  }

  public BTuple bFatalLog() throws BytecodeException {
    return bFatalLog("fatal message");
  }

  public BTuple bFatalLog(String text) throws BytecodeException {
    return bytecodeF().fatalLog(text);
  }

  public BTuple bErrorLog() throws BytecodeException {
    return bErrorLog("error message");
  }

  public BTuple bErrorLog(String text) throws BytecodeException {
    return bytecodeF().errorLog(text);
  }

  public BTuple bWarningLog() throws BytecodeException {
    return bWarningLog("warning message");
  }

  public BTuple bWarningLog(String text) throws BytecodeException {
    return bytecodeF().warningLog(text);
  }

  public BTuple bInfoLog() throws BytecodeException {
    return bInfoLog("info message");
  }

  public BTuple bInfoLog(String text) throws BytecodeException {
    return bytecodeF().infoLog(text);
  }

  // Operations

  public BCall bCall() throws BytecodeException {
    return bCall(bIntIdFunc(), bInt());
  }

  public BCall bCall(BExpr func, BExpr... args) throws BytecodeException {
    return bCall(func, bCombine(args));
  }

  public BCall bCall(BExpr func, BCombine args) throws BytecodeException {
    return bytecodeF().call(func, args);
  }

  public BCombine bCombine(BExpr... items) throws BytecodeException {
    return bytecodeF().combine(list(items));
  }

  public BIf bIf(BExpr condition, BExpr then_, BExpr else_) throws BytecodeException {
    return bytecodeF().if_(condition, then_, else_);
  }

  public BMap bMap(BExpr array, BExpr mapper) throws BytecodeException {
    return bytecodeF().map(array, mapper);
  }

  public BOrder bOrder() throws BytecodeException {
    return bOrder(bIntType());
  }

  public BOrder bOrder(BExpr... elements) throws BytecodeException {
    return bOrder(elements[0].evaluationType(), elements);
  }

  public BOrder bOrder(BType elementType, BExpr... elements) throws BytecodeException {
    var elemList = list(elements);
    return bytecodeF().order(bArrayType(elementType), elemList);
  }

  public BPick bPick() throws BytecodeException {
    return bPick(bArray(bInt()), bInt(0));
  }

  public BPick bPick(BExpr array, BExpr index) throws BytecodeException {
    return bytecodeF().pick(array, index);
  }

  public BReference bReference(int index) throws BytecodeException {
    return bReference(bIntType(), index);
  }

  public BReference bReference(BType evaluationType, int index) throws BytecodeException {
    return bytecodeF().reference(evaluationType, bInt(index));
  }

  public BSelect bSelect() throws BytecodeException {
    return bytecodeF().select(bTuple(bInt()), bInt(0));
  }

  public BSelect bSelect(BExpr tuple, BInt index) throws BytecodeException {
    return bytecodeF().select(tuple, index);
  }

  public static BTrace bTrace() {
    return new BTrace();
  }

  public static BTrace bTrace(BExpr call, BExpr called) {
    return new BTrace(call.hash(), called.hash());
  }

  public static BTrace bTrace(BExpr call, BExpr called, BTrace tail) {
    return bTrace(call.hash(), called.hash(), tail);
  }

  public static BTrace bTrace(Hash call, Hash called, BTrace tail) {
    return new BTrace(call, called, tail);
  }

  public static BTrace bTrace(Hash call, Hash called) {
    return new BTrace(call, called);
  }
}
