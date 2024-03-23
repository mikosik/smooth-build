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
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
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
import org.smoothbuild.virtualmachine.bytecode.type.CategoryDb;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BCallCategory;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BCombineCategory;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BOrderCategory;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BPickCategory;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BReferenceCategory;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BSelectCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BBlobType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BBoolType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIfCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIntType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BLambdaCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BMapCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BNativeFuncCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BStringType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public abstract class TestingBytecode {

  protected abstract CategoryDb categoryDb();

  protected abstract BytecodeFactory bytecodeF();

  // InstB types

  public BTupleType animalTB() throws BytecodeException {
    return tupleTB(stringTB(), intTB());
  }

  public BArrayType arrayTB() throws BytecodeException {
    return arrayTB(stringTB());
  }

  public BArrayType arrayTB(BType elemT) throws BytecodeException {
    return categoryDb().array(elemT);
  }

  public BBlobType blobTB() throws BytecodeException {
    return categoryDb().blob();
  }

  public BBoolType boolTB() throws BytecodeException {
    return categoryDb().bool();
  }

  public BTupleType fileTB() throws BytecodeException {
    return bytecodeF().fileType();
  }

  public BLambdaCategory lambdaCB() throws BytecodeException {
    return lambdaCB(blobTB(), stringTB(), intTB());
  }

  public BLambdaCategory lambdaCB(BType resultT) throws BytecodeException {
    return categoryDb().lambda(funcTB(resultT));
  }

  public BLambdaCategory lambdaCB(BType param, BType resultT) throws BytecodeException {
    return categoryDb().lambda(funcTB(param, resultT));
  }

  public BLambdaCategory lambdaCB(BType param1, BType param2, BType resultT)
      throws BytecodeException {
    return categoryDb().lambda(funcTB(param1, param2, resultT));
  }

  public BFuncType funcTB() throws BytecodeException {
    return funcTB(blobTB(), stringTB(), intTB());
  }

  public BFuncType funcTB(BType resultT) throws BytecodeException {
    return funcTB(list(), resultT);
  }

  public BFuncType funcTB(BType param1, BType resultT) throws BytecodeException {
    return funcTB(list(param1), resultT);
  }

  public BFuncType funcTB(BType param1, BType param2, BType resultT) throws BytecodeException {
    return funcTB(list(param1, param2), resultT);
  }

  public BFuncType funcTB(List<BType> paramTs, BType resultT) throws BytecodeException {
    return categoryDb().funcT(paramTs, resultT);
  }

  public BIntType intTB() throws BytecodeException {
    return categoryDb().int_();
  }

  public BNativeFuncCategory nativeFuncCB() throws BytecodeException {
    return nativeFuncCB(boolTB(), blobTB());
  }

  public BNativeFuncCategory nativeFuncCB(BType resultT) throws BytecodeException {
    return categoryDb().nativeFunc(funcTB(resultT));
  }

  public BNativeFuncCategory nativeFuncCB(BType param, BType resultT) throws BytecodeException {
    return categoryDb().nativeFunc(funcTB(param, resultT));
  }

  public BNativeFuncCategory nativeFuncCB(BType param1, BType param2, BType resultT)
      throws BytecodeException {
    return categoryDb().nativeFunc(funcTB(param1, param2, resultT));
  }

  public BTupleType personTB() throws BytecodeException {
    return tupleTB(stringTB(), stringTB());
  }

  public BStringType stringTB() throws BytecodeException {
    return categoryDb().string();
  }

  public BTupleType tupleTB(BType... itemTs) throws BytecodeException {
    return categoryDb().tuple(itemTs);
  }

  // OperB categories

  public BCallCategory callCB() throws BytecodeException {
    return callCB(intTB());
  }

  public BCallCategory callCB(BType evaluationType) throws BytecodeException {
    return categoryDb().call(evaluationType);
  }

  public BCombineCategory combineCB(BType... itemTs) throws BytecodeException {
    return categoryDb().combine(tupleTB(itemTs));
  }

  public BIfCategory ifFuncCB() throws BytecodeException {
    return ifFuncCB(intTB());
  }

  public BIfCategory ifFuncCB(BType t) throws BytecodeException {
    return categoryDb().ifFunc(t);
  }

  public BMapCategory mapFuncCB() throws BytecodeException {
    return mapFuncCB(intTB(), boolTB());
  }

  public BMapCategory mapFuncCB(BType r, BType s) throws BytecodeException {
    return categoryDb().mapFunc(r, s);
  }

  public BOrderCategory orderCB() throws BytecodeException {
    return orderCB(intTB());
  }

  public BOrderCategory orderCB(BType elemT) throws BytecodeException {
    return categoryDb().order(arrayTB(elemT));
  }

  public BPickCategory pickCB() throws BytecodeException {
    return pickCB(intTB());
  }

  public BPickCategory pickCB(BType evaluationType) throws BytecodeException {
    return categoryDb().pick(evaluationType);
  }

  public BReferenceCategory varCB() throws BytecodeException {
    return varCB(intTB());
  }

  public BReferenceCategory varCB(BType evaluationType) throws BytecodeException {
    return categoryDb().reference(evaluationType);
  }

  public BSelectCategory selectCB() throws BytecodeException {
    return selectCB(intTB());
  }

  public BSelectCategory selectCB(BType evaluationType) throws BytecodeException {
    return categoryDb().select(evaluationType);
  }

  // ValueB-s

  public BTuple animalB() throws BytecodeException {
    return animalB("rabbit", 7);
  }

  public BTuple animalB(String species, int speed) throws BytecodeException {
    return animalB(stringB(species), intB(speed));
  }

  public BTuple animalB(BString species, BInt speed) throws BytecodeException {
    return tupleB(species, speed);
  }

  public BArray arrayB(BValue... elems) throws BytecodeException {
    return arrayB(elems[0].evaluationType(), elems);
  }

  public BArray arrayB(BType elemT, BValue... elems) throws BytecodeException {
    return bytecodeF().arrayBuilder(arrayTB(elemT)).addAll(list(elems)).build();
  }

  public BBlob blobBJarWithPluginApi(Class<?>... classes) throws BytecodeException {
    return blobBWith(list(classes)
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
    return blobBWith(list(classes));
  }

  private BBlob blobBWith(java.util.List<Class<?>> list) throws BytecodeException {
    try {
      try (var blobBBuilder = bytecodeF().blobBuilder()) {
        Classes.saveBytecodeInJar(blobBBuilder, list);
        return blobBBuilder.build();
      }
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }

  public BBlob blobB() throws BytecodeException {
    return bytecodeF().blob(sink -> sink.writeUtf8("blob data"));
  }

  public BBlob blobB(int data) throws BytecodeException {
    return blobB(Okios.intToByteString(data));
  }

  public BBlob blobB(ByteString bytes) throws BytecodeException {
    return bytecodeF().blob(sink -> sink.write(bytes));
  }

  public BBlobBuilder blobBBuilder() throws BytecodeException {
    return bytecodeF().blobBuilder();
  }

  public BBool boolB() throws BytecodeException {
    return boolB(true);
  }

  public BBool boolB(boolean value) throws BytecodeException {
    return bytecodeF().bool(value);
  }

  public BTuple fileB(Path path) throws BytecodeException {
    return fileB(path, path.toString());
  }

  public BTuple fileB(Path path, String content) throws BytecodeException {
    return fileB(path.toString(), content);
  }

  public BTuple fileB(String path, String content) throws BytecodeException {
    return fileB(path, ByteString.encodeString(content, Constants.CHARSET));
  }

  public BTuple fileB(Path path, ByteString content) throws BytecodeException {
    return fileB(path.toString(), content);
  }

  public BTuple fileB(String path, ByteString content) throws BytecodeException {
    return fileB(path, blobB(content));
  }

  public BTuple fileB(String path, BBlob blob) throws BytecodeException {
    BString string = bytecodeF().string(path);
    return bytecodeF().file(blob, string);
  }

  public BLambda lambdaB() throws BytecodeException {
    return lambdaB(intB());
  }

  public BLambda lambdaB(BExpr body) throws BytecodeException {
    return lambdaB(list(), body);
  }

  public BLambda lambdaB(List<BType> paramTs, BExpr body) throws BytecodeException {
    var funcTB = funcTB(paramTs, body.evaluationType());
    return lambdaB(funcTB, body);
  }

  public BLambda lambdaB(BFuncType type, BExpr body) throws BytecodeException {
    return bytecodeF().lambda(type, body);
  }

  public BLambda idFuncB() throws BytecodeException {
    return lambdaB(list(intTB()), referenceB(intTB(), 0));
  }

  public BLambda returnAbcFuncB() throws BytecodeException {
    return lambdaB(stringB("abc"));
  }

  public BNativeFunc returnAbcNativeFuncB() throws IOException, BytecodeException {
    return returnAbcNativeFuncB(true);
  }

  public BNativeFunc returnAbcNativeFuncB(boolean isPure) throws IOException, BytecodeException {
    return nativeFuncB(funcTB(stringTB()), ReturnAbcFunc.class, isPure);
  }

  public BInt intB() throws BytecodeException {
    return intB(17);
  }

  public BInt intB(int value) throws BytecodeException {
    return intB(BigInteger.valueOf(value));
  }

  public BInt intB(BigInteger value) throws BytecodeException {
    return bytecodeF().int_(value);
  }

  public BNativeFunc returnAbcNativeFunc() throws IOException, BytecodeException {
    var funcTB = funcTB(stringTB());
    return nativeFuncB(funcTB, ReturnAbcFunc.class);
  }

  public static class ReturnAbcFunc {
    public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
      return nativeApi.factory().string("abc");
    }
  }

  public BNativeFunc nativeFuncB(Class<?> clazz) throws IOException, BytecodeException {
    return nativeFuncB(funcTB(), clazz);
  }

  public BNativeFunc nativeFuncB(BFuncType funcType, Class<?> clazz)
      throws IOException, BytecodeException {
    return nativeFuncB(funcType, clazz, true);
  }

  public BNativeFunc nativeFuncB(BFuncType funcType, Class<?> clazz, boolean isPure)
      throws IOException, BytecodeException {
    return nativeFuncB(
        funcType, blobBJarWithPluginApi(clazz), stringB(clazz.getName()), boolB(isPure));
  }

  public BNativeFunc nativeFuncB() throws BytecodeException {
    return nativeFuncB(funcTB());
  }

  public BNativeFunc nativeFuncB(BFuncType funcType) throws BytecodeException {
    return nativeFuncB(funcType, blobB(7), stringB("class binary name"), boolB(true));
  }

  public BNativeFunc nativeFuncB(BFuncType type, BBlob jar, BString classBinaryName)
      throws BytecodeException {
    return nativeFuncB(type, jar, classBinaryName, boolB(true));
  }

  public BNativeFunc nativeFuncB(BFuncType type, BBlob jar, BString classBinaryName, BBool isPure)
      throws BytecodeException {
    return bytecodeF().nativeFunc(type, jar, classBinaryName, isPure);
  }

  public BTuple personB(String firstName, String lastName) throws BytecodeException {
    return tupleB(stringB(firstName), stringB(lastName));
  }

  public BString stringB() throws BytecodeException {
    return bytecodeF().string("abc");
  }

  public BString stringB(String string) throws BytecodeException {
    return bytecodeF().string(string);
  }

  public BTuple tupleB(BValue... items) throws BytecodeException {
    return bytecodeF().tuple(list(items));
  }

  public BArray logArrayWithOneError() throws BytecodeException {
    return arrayB(bytecodeF().errorLog("error message"));
  }

  public BArray logArrayEmpty() throws BytecodeException {
    return arrayB(bytecodeF().storedLogType());
  }

  public BTuple fatalLog() throws BytecodeException {
    return fatalLog("fatal message");
  }

  public BTuple fatalLog(String text) throws BytecodeException {
    return bytecodeF().fatalLog(text);
  }

  public BTuple errorLog() throws BytecodeException {
    return errorLog("error message");
  }

  public BTuple errorLog(String text) throws BytecodeException {
    return bytecodeF().errorLog(text);
  }

  public BTuple warningLog() throws BytecodeException {
    return warningLog("warning message");
  }

  public BTuple warningLog(String text) throws BytecodeException {
    return bytecodeF().warningLog(text);
  }

  public BTuple infoLog() throws BytecodeException {
    return infoLog("info message");
  }

  public BTuple infoLog(String text) throws BytecodeException {
    return bytecodeF().infoLog(text);
  }

  // OperB-s

  public BCall callB() throws BytecodeException {
    return callB(idFuncB(), intB());
  }

  public BCall callB(BExpr func, BExpr... args) throws BytecodeException {
    return callB(func, combineB(args));
  }

  public BCall callB(BExpr func, BCombine args) throws BytecodeException {
    return bytecodeF().call(func, args);
  }

  public BCombine combineB(BExpr... items) throws BytecodeException {
    return bytecodeF().combine(list(items));
  }

  public BIf ifFuncB(BType t) throws BytecodeException {
    return bytecodeF().ifFunc(t);
  }

  public BMap mapFuncB(BType r, BType s) throws BytecodeException {
    return bytecodeF().mapFunc(r, s);
  }

  public BOrder orderB() throws BytecodeException {
    return orderB(intTB());
  }

  public BOrder orderB(BExpr... elems) throws BytecodeException {
    return orderB(elems[0].evaluationType(), elems);
  }

  public BOrder orderB(BType elemT, BExpr... elems) throws BytecodeException {
    var elemList = list(elems);
    return bytecodeF().order(arrayTB(elemT), elemList);
  }

  public BPick pickB() throws BytecodeException {
    return pickB(arrayB(intB()), intB(0));
  }

  public BPick pickB(BExpr array, BExpr index) throws BytecodeException {
    return bytecodeF().pick(array, index);
  }

  public BReference referenceB(int index) throws BytecodeException {
    return referenceB(intTB(), index);
  }

  public BReference referenceB(BType evaluationType, int index) throws BytecodeException {
    return bytecodeF().reference(evaluationType, intB(index));
  }

  public BSelect selectB() throws BytecodeException {
    return bytecodeF().select(tupleB(intB()), intB(0));
  }

  public BSelect selectB(BExpr tuple, BInt index) throws BytecodeException {
    return bytecodeF().select(tuple, index);
  }

  public static BTrace traceB() {
    return new BTrace();
  }

  public static BTrace traceB(BExpr call, BExpr called) {
    return new BTrace(call.hash(), called.hash());
  }

  public static BTrace traceB(BExpr call, BExpr called, BTrace tail) {
    return traceB(call.hash(), called.hash(), tail);
  }

  public static BTrace traceB(Hash call, Hash called, BTrace tail) {
    return new BTrace(call, called, tail);
  }

  public static BTrace traceB(Hash call, Hash called) {
    return new BTrace(call, called);
  }
}
