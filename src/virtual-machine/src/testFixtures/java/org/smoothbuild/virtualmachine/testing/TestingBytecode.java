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
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.PickB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.ReferenceB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
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
import org.smoothbuild.virtualmachine.bytecode.type.oper.CallCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CombineCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.OrderCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.PickCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.ReferenceCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.SelectCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.BlobTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.BoolTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.IfFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.IntTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.LambdaCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.MapFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.StringTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;
import org.smoothbuild.virtualmachine.evaluate.execute.TraceB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public abstract class TestingBytecode {

  protected abstract CategoryDb categoryDb();

  protected abstract BytecodeFactory bytecodeF();

  // InstB types

  public TupleTB animalTB() throws BytecodeException {
    return tupleTB(stringTB(), intTB());
  }

  public ArrayTB arrayTB() throws BytecodeException {
    return arrayTB(stringTB());
  }

  public ArrayTB arrayTB(TypeB elemT) throws BytecodeException {
    return categoryDb().array(elemT);
  }

  public BlobTB blobTB() throws BytecodeException {
    return categoryDb().blob();
  }

  public BoolTB boolTB() throws BytecodeException {
    return categoryDb().bool();
  }

  public TupleTB fileTB() throws BytecodeException {
    return bytecodeF().fileType();
  }

  public LambdaCB lambdaCB() throws BytecodeException {
    return lambdaCB(blobTB(), stringTB(), intTB());
  }

  public LambdaCB lambdaCB(TypeB resultT) throws BytecodeException {
    return categoryDb().lambda(funcTB(resultT));
  }

  public LambdaCB lambdaCB(TypeB param, TypeB resultT) throws BytecodeException {
    return categoryDb().lambda(funcTB(param, resultT));
  }

  public LambdaCB lambdaCB(TypeB param1, TypeB param2, TypeB resultT) throws BytecodeException {
    return categoryDb().lambda(funcTB(param1, param2, resultT));
  }

  public FuncTB funcTB() throws BytecodeException {
    return funcTB(blobTB(), stringTB(), intTB());
  }

  public FuncTB funcTB(TypeB resultT) throws BytecodeException {
    return funcTB(list(), resultT);
  }

  public FuncTB funcTB(TypeB param1, TypeB resultT) throws BytecodeException {
    return funcTB(list(param1), resultT);
  }

  public FuncTB funcTB(TypeB param1, TypeB param2, TypeB resultT) throws BytecodeException {
    return funcTB(list(param1, param2), resultT);
  }

  public FuncTB funcTB(List<TypeB> paramTs, TypeB resultT) throws BytecodeException {
    return categoryDb().funcT(paramTs, resultT);
  }

  public IntTB intTB() throws BytecodeException {
    return categoryDb().int_();
  }

  public NativeFuncCB nativeFuncCB() throws BytecodeException {
    return nativeFuncCB(boolTB(), blobTB());
  }

  public NativeFuncCB nativeFuncCB(TypeB resultT) throws BytecodeException {
    return categoryDb().nativeFunc(funcTB(resultT));
  }

  public NativeFuncCB nativeFuncCB(TypeB param, TypeB resultT) throws BytecodeException {
    return categoryDb().nativeFunc(funcTB(param, resultT));
  }

  public NativeFuncCB nativeFuncCB(TypeB param1, TypeB param2, TypeB resultT)
      throws BytecodeException {
    return categoryDb().nativeFunc(funcTB(param1, param2, resultT));
  }

  public TupleTB personTB() throws BytecodeException {
    return tupleTB(stringTB(), stringTB());
  }

  public StringTB stringTB() throws BytecodeException {
    return categoryDb().string();
  }

  public TupleTB tupleTB(TypeB... itemTs) throws BytecodeException {
    return categoryDb().tuple(itemTs);
  }

  // OperB categories

  public CallCB callCB() throws BytecodeException {
    return callCB(intTB());
  }

  public CallCB callCB(TypeB evaluationType) throws BytecodeException {
    return categoryDb().call(evaluationType);
  }

  public CombineCB combineCB(TypeB... itemTs) throws BytecodeException {
    return categoryDb().combine(tupleTB(itemTs));
  }

  public IfFuncCB ifFuncCB() throws BytecodeException {
    return ifFuncCB(intTB());
  }

  public IfFuncCB ifFuncCB(TypeB t) throws BytecodeException {
    return categoryDb().ifFunc(t);
  }

  public MapFuncCB mapFuncCB() throws BytecodeException {
    return mapFuncCB(intTB(), boolTB());
  }

  public MapFuncCB mapFuncCB(TypeB r, TypeB s) throws BytecodeException {
    return categoryDb().mapFunc(r, s);
  }

  public OrderCB orderCB() throws BytecodeException {
    return orderCB(intTB());
  }

  public OrderCB orderCB(TypeB elemT) throws BytecodeException {
    return categoryDb().order(arrayTB(elemT));
  }

  public PickCB pickCB() throws BytecodeException {
    return pickCB(intTB());
  }

  public PickCB pickCB(TypeB evaluationType) throws BytecodeException {
    return categoryDb().pick(evaluationType);
  }

  public ReferenceCB varCB() throws BytecodeException {
    return varCB(intTB());
  }

  public ReferenceCB varCB(TypeB evaluationType) throws BytecodeException {
    return categoryDb().reference(evaluationType);
  }

  public SelectCB selectCB() throws BytecodeException {
    return selectCB(intTB());
  }

  public SelectCB selectCB(TypeB evaluationType) throws BytecodeException {
    return categoryDb().select(evaluationType);
  }

  // ValueB-s

  public TupleB animalB() throws BytecodeException {
    return animalB("rabbit", 7);
  }

  public TupleB animalB(String species, int speed) throws BytecodeException {
    return animalB(stringB(species), intB(speed));
  }

  public TupleB animalB(StringB species, IntB speed) throws BytecodeException {
    return tupleB(species, speed);
  }

  public ArrayB arrayB(ValueB... elems) throws BytecodeException {
    return arrayB(elems[0].evaluationType(), elems);
  }

  public ArrayB arrayB(TypeB elemT, ValueB... elems) throws BytecodeException {
    return bytecodeF().arrayBuilder(arrayTB(elemT)).addAll(list(elems)).build();
  }

  public BlobB blobBJarWithPluginApi(Class<?>... classes) throws BytecodeException {
    return blobBWith(list(classes)
        .append(
            BlobB.class,
            NativeApi.class,
            ExprB.class,
            StringB.class,
            TupleB.class,
            ValueB.class,
            BytecodeException.class));
  }

  public BlobB blobBJarWithJavaByteCode(Class<?>... classes) throws BytecodeException {
    return blobBWith(list(classes));
  }

  private BlobB blobBWith(java.util.List<Class<?>> list) throws BytecodeException {
    try {
      try (var blobBBuilder = bytecodeF().blobBuilder()) {
        Classes.saveBytecodeInJar(blobBBuilder, list);
        return blobBBuilder.build();
      }
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }

  public BlobB blobB() throws BytecodeException {
    return bytecodeF().blob(sink -> sink.writeUtf8("blob data"));
  }

  public BlobB blobB(int data) throws BytecodeException {
    return blobB(Okios.intToByteString(data));
  }

  public BlobB blobB(ByteString bytes) throws BytecodeException {
    return bytecodeF().blob(sink -> sink.write(bytes));
  }

  public BlobBBuilder blobBBuilder() throws BytecodeException {
    return bytecodeF().blobBuilder();
  }

  public BoolB boolB() throws BytecodeException {
    return boolB(true);
  }

  public BoolB boolB(boolean value) throws BytecodeException {
    return bytecodeF().bool(value);
  }

  public TupleB fileB(Path path) throws BytecodeException {
    return fileB(path, path.toString());
  }

  public TupleB fileB(Path path, String content) throws BytecodeException {
    return fileB(path.toString(), content);
  }

  public TupleB fileB(String path, String content) throws BytecodeException {
    return fileB(path, ByteString.encodeString(content, Constants.CHARSET));
  }

  public TupleB fileB(Path path, ByteString content) throws BytecodeException {
    return fileB(path.toString(), content);
  }

  public TupleB fileB(String path, ByteString content) throws BytecodeException {
    return fileB(path, blobB(content));
  }

  public TupleB fileB(String path, BlobB blob) throws BytecodeException {
    StringB string = bytecodeF().string(path);
    return bytecodeF().file(blob, string);
  }

  public LambdaB lambdaB() throws BytecodeException {
    return lambdaB(intB());
  }

  public LambdaB lambdaB(ExprB body) throws BytecodeException {
    return lambdaB(list(), body);
  }

  public LambdaB lambdaB(List<TypeB> paramTs, ExprB body) throws BytecodeException {
    var funcTB = funcTB(paramTs, body.evaluationType());
    return lambdaB(funcTB, body);
  }

  public LambdaB lambdaB(FuncTB type, ExprB body) throws BytecodeException {
    return bytecodeF().lambda(type, body);
  }

  public LambdaB idFuncB() throws BytecodeException {
    return lambdaB(list(intTB()), referenceB(intTB(), 0));
  }

  public LambdaB returnAbcFuncB() throws BytecodeException {
    return lambdaB(stringB("abc"));
  }

  public NativeFuncB returnAbcNativeFuncB() throws IOException, BytecodeException {
    return returnAbcNativeFuncB(true);
  }

  public NativeFuncB returnAbcNativeFuncB(boolean isPure) throws IOException, BytecodeException {
    return nativeFuncB(funcTB(stringTB()), ReturnAbcFunc.class, isPure);
  }

  public IntB intB() throws BytecodeException {
    return intB(17);
  }

  public IntB intB(int value) throws BytecodeException {
    return intB(BigInteger.valueOf(value));
  }

  public IntB intB(BigInteger value) throws BytecodeException {
    return bytecodeF().int_(value);
  }

  public NativeFuncB returnAbcNativeFunc() throws IOException, BytecodeException {
    var funcTB = funcTB(stringTB());
    return nativeFuncB(funcTB, ReturnAbcFunc.class);
  }

  public static class ReturnAbcFunc {
    public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
      return nativeApi.factory().string("abc");
    }
  }

  public NativeFuncB nativeFuncB(Class<?> clazz) throws IOException, BytecodeException {
    return nativeFuncB(funcTB(), clazz);
  }

  public NativeFuncB nativeFuncB(FuncTB funcTB, Class<?> clazz)
      throws IOException, BytecodeException {
    return nativeFuncB(funcTB, clazz, true);
  }

  public NativeFuncB nativeFuncB(FuncTB funcTB, Class<?> clazz, boolean isPure)
      throws IOException, BytecodeException {
    return nativeFuncB(
        funcTB, blobBJarWithPluginApi(clazz), stringB(clazz.getName()), boolB(isPure));
  }

  public NativeFuncB nativeFuncB() throws BytecodeException {
    return nativeFuncB(funcTB());
  }

  public NativeFuncB nativeFuncB(FuncTB funcTB) throws BytecodeException {
    return nativeFuncB(funcTB, blobB(7), stringB("class binary name"), boolB(true));
  }

  public NativeFuncB nativeFuncB(FuncTB type, BlobB jar, StringB classBinaryName)
      throws BytecodeException {
    return nativeFuncB(type, jar, classBinaryName, boolB(true));
  }

  public NativeFuncB nativeFuncB(FuncTB type, BlobB jar, StringB classBinaryName, BoolB isPure)
      throws BytecodeException {
    return bytecodeF().nativeFunc(type, jar, classBinaryName, isPure);
  }

  public TupleB personB(String firstName, String lastName) throws BytecodeException {
    return tupleB(stringB(firstName), stringB(lastName));
  }

  public StringB stringB() throws BytecodeException {
    return bytecodeF().string("abc");
  }

  public StringB stringB(String string) throws BytecodeException {
    return bytecodeF().string(string);
  }

  public TupleB tupleB(ValueB... items) throws BytecodeException {
    return bytecodeF().tuple(list(items));
  }

  public ArrayB logArrayWithOneError() throws BytecodeException {
    return arrayB(bytecodeF().errorLog("error message"));
  }

  public ArrayB logArrayEmpty() throws BytecodeException {
    return arrayB(bytecodeF().storedLogType());
  }

  public TupleB fatalLog() throws BytecodeException {
    return fatalLog("fatal message");
  }

  public TupleB fatalLog(String text) throws BytecodeException {
    return bytecodeF().fatalLog(text);
  }

  public TupleB errorLog() throws BytecodeException {
    return errorLog("error message");
  }

  public TupleB errorLog(String text) throws BytecodeException {
    return bytecodeF().errorLog(text);
  }

  public TupleB warningLog() throws BytecodeException {
    return warningLog("warning message");
  }

  public TupleB warningLog(String text) throws BytecodeException {
    return bytecodeF().warningLog(text);
  }

  public TupleB infoLog() throws BytecodeException {
    return infoLog("info message");
  }

  public TupleB infoLog(String text) throws BytecodeException {
    return bytecodeF().infoLog(text);
  }

  // OperB-s

  public CallB callB() throws BytecodeException {
    return callB(idFuncB(), intB());
  }

  public CallB callB(ExprB func, ExprB... args) throws BytecodeException {
    return callB(func, combineB(args));
  }

  public CallB callB(ExprB func, CombineB args) throws BytecodeException {
    return bytecodeF().call(func, args);
  }

  public CombineB combineB(ExprB... items) throws BytecodeException {
    return bytecodeF().combine(list(items));
  }

  public IfFuncB ifFuncB(TypeB t) throws BytecodeException {
    return bytecodeF().ifFunc(t);
  }

  public MapFuncB mapFuncB(TypeB r, TypeB s) throws BytecodeException {
    return bytecodeF().mapFunc(r, s);
  }

  public OrderB orderB() throws BytecodeException {
    return orderB(intTB());
  }

  public OrderB orderB(ExprB... elems) throws BytecodeException {
    return orderB(elems[0].evaluationType(), elems);
  }

  public OrderB orderB(TypeB elemT, ExprB... elems) throws BytecodeException {
    var elemList = list(elems);
    return bytecodeF().order(arrayTB(elemT), elemList);
  }

  public PickB pickB() throws BytecodeException {
    return pickB(arrayB(intB()), intB(0));
  }

  public PickB pickB(ExprB array, ExprB index) throws BytecodeException {
    return bytecodeF().pick(array, index);
  }

  public ReferenceB referenceB(int index) throws BytecodeException {
    return referenceB(intTB(), index);
  }

  public ReferenceB referenceB(TypeB evaluationType, int index) throws BytecodeException {
    return bytecodeF().reference(evaluationType, intB(index));
  }

  public SelectB selectB() throws BytecodeException {
    return bytecodeF().select(tupleB(intB()), intB(0));
  }

  public SelectB selectB(ExprB tuple, IntB index) throws BytecodeException {
    return bytecodeF().select(tuple, index);
  }

  public static TraceB traceB() {
    return new TraceB();
  }

  public static TraceB traceB(ExprB call, ExprB called) {
    return new TraceB(call.hash(), called.hash());
  }

  public static TraceB traceB(ExprB call, ExprB called, TraceB tail) {
    return traceB(call.hash(), called.hash(), tail);
  }

  public static TraceB traceB(Hash call, Hash called, TraceB tail) {
    return new TraceB(call, called, tail);
  }

  public static TraceB traceB(Hash call, Hash called) {
    return new TraceB(call, called);
  }
}
