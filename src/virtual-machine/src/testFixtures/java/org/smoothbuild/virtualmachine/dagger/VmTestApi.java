package org.smoothbuild.virtualmachine.dagger;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.testing.TestingByteString.byteString;
import static org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader.NATIVE_METHOD_NAME;

import java.io.IOException;
import java.math.BigInteger;
import okio.ByteString;
import org.smoothbuild.common.Constants;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.dagger.CommonTestApi;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.io.Okios;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.reflect.Classes;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlobBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateVariant;
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
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayGetKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBlobType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBoolType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCallKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCreateArrayKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCreateTupleKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCreateVariantKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BFoldKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIfKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIntType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BInvokeKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BMapKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BRefKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BStringType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSwitchKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleGetKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BVariantType;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeMethodLoader;
import org.smoothbuild.virtualmachine.bytecode.load.JarClassLoaderFactory;
import org.smoothbuild.virtualmachine.bytecode.load.MethodLoader;
import org.smoothbuild.virtualmachine.evaluate.BEvaluateTask;
import org.smoothbuild.virtualmachine.evaluate.base.BExprAttributes;
import org.smoothbuild.virtualmachine.evaluate.job.Job;
import org.smoothbuild.virtualmachine.evaluate.job.JobContext;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public interface VmTestApi extends CommonTestApi {
  public VmTestComponent provide();

  public default BEvaluateTask bEvaluateTask() {
    return bEvaluateTask(new BExprAttributes());
  }

  public default BEvaluateTask bEvaluateTask(BExprAttributes bExprAttributes) {
    return provide()
        .vmComponentBuilder()
        .bExprAttributes(bExprAttributes)
        .build()
        .bEvaluateTask();
  }

  public default Job job(BExpr expr, BExpr... environment) {
    return job(expr, list(environment));
  }

  public default Job job(BExpr expr) {
    return job(expr, list());
  }

  public default Job job(BExpr expr, List<BExpr> environment) {
    var jobContext = jobContext();
    return jobContext.newJob(
        expr, environment.map(e -> jobContext.newJob(e, list(), new Trace())), new Trace());
  }

  public default JobContext jobContext() {
    return jobContext(new BExprAttributes());
  }

  private JobContext jobContext(BExprAttributes bExprAttributes) {
    return provide()
        .vmComponentBuilder()
        .bExprAttributes(bExprAttributes)
        .build()
        .jobContext();
  }

  public default FullPath moduleFullPath() {
    return provide().projectPath().append("module.smooth");
  }

  public default BOutput bOutput(BValue value) throws BytecodeException {
    return bOutput(value, bLogArrayEmpty());
  }

  public default BOutput bOutput(BValue value, BArray messages) throws BytecodeException {
    return BOutput.bOutput(value, messages);
  }

  public default BytecodeLoader bytecodeLoader(ClassLoader systemClassLoader) {
    return new BytecodeLoader(bytecodeMethodLoader(systemClassLoader), provide().bytecodeFactory());
  }

  public default BytecodeMethodLoader bytecodeMethodLoader(ClassLoader classLoader) {
    var bytecodeFactory = provide().bytecodeFactory();
    var jarClassLoaderFactory = new JarClassLoaderFactory(bytecodeFactory, classLoader);
    var methodLoader = new MethodLoader(jarClassLoaderFactory);
    return new BytecodeMethodLoader(methodLoader);
  }

  public default BTupleType bAnimalType() throws BytecodeException {
    return bTupleType(bStringType(), bIntType());
  }

  public default BArrayType bArrayType() throws BytecodeException {
    return bStringArrayType();
  }

  public default BArrayType bBoolArrayType() throws BytecodeException {
    return bArrayType(bBoolType());
  }

  public default BArrayType bBlobArrayType() throws BytecodeException {
    return bArrayType(bBlobType());
  }

  public default BArrayType bIntArrayType() throws BytecodeException {
    return bArrayType(bIntType());
  }

  public default BArrayType bStringArrayType() throws BytecodeException {
    return bArrayType(bStringType());
  }

  public default BArrayType bFileArrayType() throws BytecodeException {
    return bArrayType(bFileType());
  }

  public default BArrayType bArrayType(BType elemT) throws BytecodeException {
    return provide().kindDb().array(elemT);
  }

  public default BBlobType bBlobType() throws BytecodeException {
    return provide().kindDb().blob();
  }

  public default BBoolType bBoolType() throws BytecodeException {
    return provide().kindDb().bool();
  }

  public default BTupleType bFileType() throws BytecodeException {
    return provide().bytecodeFactory().fileType();
  }

  public default BVariantType bVariantType() throws BytecodeException {
    return bVariantType(bStringType(), bIntType());
  }

  public default BVariantType bVariantType(BType... alternatives) throws BytecodeException {
    return provide().kindDb().variant(alternatives);
  }

  public default BVariantType bVariantType(List<BType> alternatives) throws BytecodeException {
    return provide().kindDb().variant(alternatives);
  }

  public default BLambdaType bLambdaType() throws BytecodeException {
    return bLambdaType(bBlobType(), bStringType(), bIntType());
  }

  public default BLambdaType bStringLambdaType() throws BytecodeException {
    return bLambdaType(bStringType());
  }

  public default BLambdaType bIntLambdaType() throws BytecodeException {
    return bLambdaType(bIntType());
  }

  public default BLambdaType bLambdaType(BType resultT) throws BytecodeException {
    return bLambdaType(list(), resultT);
  }

  public default BLambdaType bIntIntLambdaType() throws BytecodeException {
    return bLambdaType(bIntType(), bIntType());
  }

  public default BLambdaType bLambdaType(BType param1, BType resultT) throws BytecodeException {
    return bLambdaType(list(param1), resultT);
  }

  public default BLambdaType bLambdaType(BType param1, BType param2, BType resultT)
      throws BytecodeException {
    return bLambdaType(list(param1, param2), resultT);
  }

  public default BLambdaType bLambdaType(List<BType> paramTs, BType resultT)
      throws BytecodeException {
    return provide().kindDb().lambda(paramTs, resultT);
  }

  public default BIntType bIntType() throws BytecodeException {
    return provide().kindDb().int_();
  }

  public default BInvokeKind bInvokeKind() throws BytecodeException {
    return bInvokeKind(bIntType());
  }

  public default BInvokeKind bInvokeKind(BType evaluationType) throws BytecodeException {
    return provide().kindDb().invoke(evaluationType);
  }

  public default BTupleType bMethodType() throws BytecodeException {
    return provide().kindDb().method();
  }

  public default BTupleType bPersonType() throws BytecodeException {
    return bTupleType(bStringType(), bStringType());
  }

  public default BStringType bStringType() throws BytecodeException {
    return provide().kindDb().string();
  }

  public default BTupleType bTupleType(List<BType> itemTypes) throws BytecodeException {
    return provide().kindDb().tuple(itemTypes);
  }

  public default BTupleType bTupleType(BType... itemTypes) throws BytecodeException {
    return provide().kindDb().tuple(itemTypes);
  }

  public default BCallKind bCallKind() throws BytecodeException {
    return bCallKind(bIntType());
  }

  public default BCallKind bCallKind(BType evaluationType) throws BytecodeException {
    return provide().kindDb().call(evaluationType);
  }

  public default BCreateVariantKind bCreateVariantKind(BVariantType evaluationType)
      throws BytecodeException {
    return provide().kindDb().createVariant(evaluationType);
  }

  public default BSwitchKind bSwitchKind(BType evaluationType) throws BytecodeException {
    return provide().kindDb().switch_(evaluationType);
  }

  public default BCreateTupleKind bCreateTupleKind(BType... itemTypes) throws BytecodeException {
    return provide().kindDb().createTuple(bTupleType(itemTypes));
  }

  public default BFoldKind bFoldKind(BType evaluationType) throws BytecodeException {
    return provide().kindDb().fold(evaluationType);
  }

  public default BIfKind bIfKind() throws BytecodeException {
    return bIfKind(bIntType());
  }

  public default BIfKind bIfKind(BType evaluationType) throws BytecodeException {
    return provide().kindDb().if_(evaluationType);
  }

  public default BMapKind bMapKind() throws BytecodeException {
    return bMapKind(bIntArrayType());
  }

  public default BMapKind bMapKind(BArrayType evaluationType) throws BytecodeException {
    return provide().kindDb().map(evaluationType);
  }

  public default BCreateArrayKind bCreateArrayKind() throws BytecodeException {
    return bCreateArrayKind(bIntType());
  }

  public default BCreateArrayKind bCreateArrayKind(BType elemT) throws BytecodeException {
    return provide().kindDb().createArray(bArrayType(elemT));
  }

  public default BArrayGetKind bArrayGetKind() throws BytecodeException {
    return bArrayGetKind(bIntType());
  }

  public default BArrayGetKind bArrayGetKind(BType evaluationType) throws BytecodeException {
    return provide().kindDb().arrayGet(evaluationType);
  }

  public default BRefKind bRefKind() throws BytecodeException {
    return bRefKind(bIntType());
  }

  public default BRefKind bRefKind(BType evaluationType) throws BytecodeException {
    return provide().kindDb().ref(evaluationType);
  }

  public default BTupleGetKind bTupleGetKind() throws BytecodeException {
    return bTupleGetKind(bIntType());
  }

  public default BTupleGetKind bTupleGetKind(BType evaluationType) throws BytecodeException {
    return provide().kindDb().tupleGet(evaluationType);
  }

  public default BTuple bAnimal() throws BytecodeException {
    return bAnimal("rabbit", 7);
  }

  public default BTuple bAnimal(String species, int speed) throws BytecodeException {
    return bAnimal(bString(species), bInt(speed));
  }

  public default BTuple bAnimal(BString species, BInt speed) throws BytecodeException {
    return bTuple(species, speed);
  }

  public default BArray bArray(BValue... elements) throws BytecodeException {
    return bArray(elements[0].evaluationType(), elements);
  }

  public default BArrayBuilder newBArrayBuilder(BArrayType type) {
    return provide().exprDb().newArrayBuilder(type);
  }

  public default BArray bArray(BType elementType, BValue... elements) throws BytecodeException {
    return provide()
        .bytecodeFactory()
        .arrayBuilder(bArrayType(elementType))
        .addAll(list(elements))
        .build();
  }

  public default BBlob blobBJarWithPluginApi(Class<?>... classes) throws IOException {
    return bBlobWith(list(classes)
        .add(
            BBlob.class,
            NativeApi.class,
            BExpr.class,
            BString.class,
            BTuple.class,
            BValue.class,
            BytecodeException.class));
  }

  public default BBlob blobBJarWithJavaByteCode(Class<?>... classes) throws IOException {
    return bBlobWith(list(classes));
  }

  private BBlob bBlobWith(List<Class<?>> list) throws IOException {
    try (var blobBBuilder = provide().bytecodeFactory().blobBuilder()) {
      Classes.saveBytecodeInJar(blobBBuilder, list);
      return blobBBuilder.build();
    }
  }

  public default BBlob bBlob() throws IOException {
    return bBlob("blob data");
  }

  public default BBlob bBlob(String string) throws IOException {
    return bBlob(byteString(string));
  }

  public default BBlob bBlob(int data) throws IOException {
    return bBlob(Okios.intToByteString(data));
  }

  public default BBlob bBlob(ByteString bytes) throws IOException {
    return provide().bytecodeFactory().blob(sink -> sink.write(bytes));
  }

  public default BBlobBuilder bBlobBuilder() throws BytecodeException {
    return provide().bytecodeFactory().blobBuilder();
  }

  public default BBool bBool() throws BytecodeException {
    return bBool(true);
  }

  public default BBool bBool(boolean value) throws BytecodeException {
    return provide().bytecodeFactory().bool(value);
  }

  public default BVariant bVariant() throws BytecodeException {
    var type = bVariantType(bStringType(), bIntType());
    return bVariant(type, bInt(0), bString("7"));
  }

  public default BVariant bVariant(BVariantType type, int index, BValue choice)
      throws BytecodeException {
    return bVariant(type, bInt(index), choice);
  }

  public default BVariant bVariant(BVariantType type, BInt index, BValue choice)
      throws BytecodeException {
    return provide().bytecodeFactory().variant(type, index, choice);
  }

  public default BCreateVariant bCreateVariant() throws BytecodeException {
    var type = bVariantType(bStringType(), bIntType());
    return bCreateVariant(type, bInt(0), bString("7"));
  }

  public default BCreateVariant bCreateVariant(BVariantType type, int index, BExpr choice)
      throws BytecodeException {
    return bCreateVariant(type, bInt(index), choice);
  }

  public default BCreateVariant bCreateVariant(BVariantType type, BInt index, BExpr choice)
      throws BytecodeException {
    return provide().bytecodeFactory().createVariant(type, index, choice);
  }

  public default BTuple bFile(Path path) throws IOException {
    return bFile(path, path.toString());
  }

  public default BTuple bFile(Path path, String content) throws IOException {
    return bFile(path.toString(), content);
  }

  public default BTuple bFile(String path, String content) throws IOException {
    return bFile(path, ByteString.encodeString(content, Constants.CHARSET));
  }

  public default BTuple bFile(Path path, ByteString content) throws IOException {
    return bFile(path.toString(), content);
  }

  public default BTuple bFile(String path, ByteString content) throws IOException {
    return bFile(path, bBlob(content));
  }

  public default BTuple bFile(String path, BBlob blob) throws BytecodeException {
    BString string = provide().bytecodeFactory().string(path);
    return provide().bytecodeFactory().file(blob, string);
  }

  public default BLambda bLambda() throws BytecodeException {
    return bLambda(bInt());
  }

  public default BLambda bLambda(BExpr body) throws BytecodeException {
    return bLambda(list(), body);
  }

  public default BLambda bLambda(List<BType> paramTypes, BExpr body) throws BytecodeException {
    var lambdaType = bLambdaType(paramTypes, body.evaluationType());
    return bLambda(lambdaType, body);
  }

  public default BLambda bLambda(BLambdaType type, BExpr body) throws BytecodeException {
    return provide().bytecodeFactory().lambda(type, body);
  }

  public default BLambda bIntIdLambda() throws BytecodeException {
    return bLambda(list(bIntType()), bRef(bIntType(), 1));
  }

  public default BLambda bStringIdLambda() throws BytecodeException {
    return bLambda(list(bStringType()), bRef(bStringType(), 1));
  }

  public default BLambda bs2iLambda() throws BytecodeException {
    return bLambda(list(bStringType()), bInt(7));
  }

  public default BLambda bi2sLambda() throws BytecodeException {
    return bLambda(list(bIntType()), bString("a"));
  }

  public default BLambda bi2iLambda() throws BytecodeException {
    return bi2iLambda(7);
  }

  public default BLambda bi2iLambda(int value) throws BytecodeException {
    return bLambda(list(bIntType()), bInt(value));
  }

  public default BLambda bii2iLambda() throws BytecodeException {
    return bii2iLambda(7);
  }

  public default BLambda bii2iLambda(int value) throws BytecodeException {
    return bLambda(list(bIntType(), bIntType()), bInt(value));
  }

  public default BLambda bReturnAbcLambda() throws BytecodeException {
    return bLambda(bString("abc"));
  }

  public default BInt bInt() throws BytecodeException {
    return bInt(17);
  }

  public default BInt bInt(int value) throws BytecodeException {
    return bInt(BigInteger.valueOf(value));
  }

  public default BInt bInt(BigInteger value) throws BytecodeException {
    return provide().bytecodeFactory().int_(value);
  }

  public default BInvoke bReturnAbcInvoke() throws IOException {
    return bReturnAbcInvoke(true);
  }

  public default BInvoke bReturnAbcInvoke(boolean isPure) throws IOException {
    return bInvoke(bStringType(), ReturnAbcFunc.class, isPure);
  }

  public static class ReturnAbcFunc {
    public static BValue func(NativeApi nativeApi, BTuple arguments) throws BytecodeException {
      return nativeApi.factory().string("abc");
    }
  }

  public default BInvoke bInvoke() throws IOException {
    return bInvoke(bIntType());
  }

  public default BInvoke bInvoke(BType evaluationType) throws IOException {
    var bMethodTuple = bMethodTuple(bBlob(7));
    return bInvoke(evaluationType, bMethodTuple, bBool(true), bTuple());
  }

  public default BInvoke bInvoke(Class<?> clazz) throws IOException {
    return bInvoke(bIntType(), clazz);
  }

  public default BInvoke bInvoke(BType evaluationType, Class<?> clazz) throws IOException {
    return bInvoke(evaluationType, clazz, true);
  }

  public default BInvoke bInvoke(BType evaluationType, BExpr method) throws BytecodeException {
    return bInvoke(evaluationType, method, bBool(true), bTuple());
  }

  public default BInvoke bInvoke(BType evaluationType, Class<?> clazz, boolean isPure)
      throws IOException {
    return bInvoke(evaluationType, clazz, isPure, bTuple());
  }

  public default BInvoke bInvoke(
      BType evaluationType, Class<?> clazz, boolean isPure, BExpr arguments) throws IOException {
    var bMethodTuple = bMethodTuple(clazz);
    return bInvoke(evaluationType, bMethodTuple, bBool(isPure), arguments);
  }

  public default BInvoke bInvoke(BType evaluationType, BExpr method, BExpr arguments)
      throws BytecodeException {
    return provide().bytecodeFactory().invoke(evaluationType, method, bBool(true), arguments);
  }

  public default BInvoke bInvoke(BType evaluationType, BExpr method, BExpr isPure, BExpr arguments)
      throws BytecodeException {
    return provide().bytecodeFactory().invoke(evaluationType, method, isPure, arguments);
  }

  public default BTuple bMethodTuple() throws IOException {
    var jar = bBlob();
    var classBinaryName = bString();
    return bMethodTuple(jar, classBinaryName);
  }

  public default BTuple bMethodTuple(Class<?> clazz) throws IOException {
    return bMethodTuple(clazz, NATIVE_METHOD_NAME);
  }

  public default BTuple bMethodTuple(Class<?> clazz, String methodName) throws IOException {
    return bMethodTuple(blobBJarWithPluginApi(clazz), clazz.getName(), methodName);
  }

  public default BTuple bMethodTuple(BBlob jar) throws BytecodeException {
    return bMethodTuple(jar, "classBinaryName", NATIVE_METHOD_NAME);
  }

  public default BTuple bMethodTuple(BBlob jar, String classBinaryName, String methodName)
      throws BytecodeException {
    return bMethod(jar, bString(classBinaryName), bString(methodName)).tuple();
  }

  public default BTuple bMethodTuple(String classBinaryName) throws IOException {
    return bMethodTuple(bBlob(), bString(classBinaryName));
  }

  public default BTuple bMethodTuple(BBlob jar, BString classBinaryName) throws BytecodeException {
    return bMethod(jar, classBinaryName).tuple();
  }

  public default BMethod bMethod(Class<?> clazz) throws IOException {
    return bMethod(clazz, NATIVE_METHOD_NAME);
  }

  public default BMethod bMethod(Class<?> clazz, String methodName) throws IOException {
    return new BMethod(bMethodTuple(clazz, methodName));
  }

  public default BMethod bMethod(BBlob jar, String classBinaryName) throws BytecodeException {
    return bMethod(jar, classBinaryName, NATIVE_METHOD_NAME);
  }

  public default BMethod bMethod(BBlob jar, String classBinaryName, String methodName)
      throws BytecodeException {
    return bMethod(jar, bString(classBinaryName), bString(methodName));
  }

  public default BMethod bMethod(BBlob jar, BString classBinaryName) throws BytecodeException {
    return bMethod(jar, classBinaryName, bString(NATIVE_METHOD_NAME));
  }

  public default BMethod bMethod(BBlob jar, BString classBinaryName, BString methodName)
      throws BytecodeException {
    return provide().bytecodeFactory().method(jar, classBinaryName, methodName);
  }

  public default BTuple bPerson(String firstName, String lastName) throws BytecodeException {
    return bTuple(bString(firstName), bString(lastName));
  }

  public default BString bString() throws BytecodeException {
    return provide().bytecodeFactory().string("abc");
  }

  public default BString bString(String string) throws BytecodeException {
    return provide().bytecodeFactory().string(string);
  }

  public default BTuple bTuple(BValue... items) throws BytecodeException {
    return bTuple(list(items));
  }

  public default BTuple bTuple(List<BValue> list) throws BytecodeException {
    return provide().bytecodeFactory().tuple(list);
  }

  public default BArray bLogArrayWithOneError() throws BytecodeException {
    return bArray(bErrorLog("error message"));
  }

  public default BArray bLogArrayEmpty() throws BytecodeException {
    return bArray(provide().bytecodeFactory().storedLogType());
  }

  public default BTuple bFatalLog() throws BytecodeException {
    return bFatalLog("fatal message");
  }

  public default BTuple bFatalLog(String text) throws BytecodeException {
    return provide().bytecodeFactory().fatalLog(text);
  }

  public default BTuple bErrorLog() throws BytecodeException {
    return bErrorLog("error message");
  }

  public default BTuple bErrorLog(String text) throws BytecodeException {
    return provide().bytecodeFactory().errorLog(text);
  }

  public default BTuple bWarningLog() throws BytecodeException {
    return bWarningLog("warning message");
  }

  public default BTuple bWarningLog(String text) throws BytecodeException {
    return provide().bytecodeFactory().warningLog(text);
  }

  public default BTuple bInfoLog() throws BytecodeException {
    return bInfoLog("info message");
  }

  public default BTuple bInfoLog(String text) throws BytecodeException {
    return provide().bytecodeFactory().infoLog(text);
  }

  // Operations

  public default BCall bCall() throws BytecodeException {
    return bCall(bIntIdLambda(), bInt());
  }

  public default BCall bCall(BExpr lambda, BExpr... arguments) throws BytecodeException {
    return provide().bytecodeFactory().call(lambda, bCreateTuple(arguments));
  }

  public default BCall bCallWithArguments(BExpr lambda, BExpr arguments) throws BytecodeException {
    return provide().bytecodeFactory().call(lambda, arguments);
  }

  public default BSwitch bSwitch(BExpr choice, BCreateTuple handlers) throws BytecodeException {
    return provide().bytecodeFactory().switch_(choice, handlers);
  }

  public default BCreateTuple bCreateTuple(BExpr... items) throws BytecodeException {
    return provide().bytecodeFactory().createTuple(list(items));
  }

  public default BFold bFold(BExpr array, BExpr initial, BExpr folder) throws BytecodeException {
    return provide().bytecodeFactory().fold(array, initial, folder);
  }

  public default BIf bIf(BExpr condition, BExpr then_, BExpr else_) throws BytecodeException {
    return provide().bytecodeFactory().if_(condition, then_, else_);
  }

  public default BMap bMap(BExpr array, BExpr mapper) throws BytecodeException {
    return provide().bytecodeFactory().map(array, mapper);
  }

  public default BCreateArray bCreateArray() throws BytecodeException {
    return bCreateArray(bIntType());
  }

  public default BCreateArray bCreateArray(BExpr... elements) throws BytecodeException {
    return bCreateArray(elements[0].evaluationType(), elements);
  }

  public default BCreateArray bCreateArray(BType elementType, BExpr... elements)
      throws BytecodeException {
    var elemList = list(elements);
    return provide().bytecodeFactory().createArray(bArrayType(elementType), elemList);
  }

  public default BArrayGet bArrayGet() throws BytecodeException {
    return bArrayGet(bArray(bInt()), bInt(0));
  }

  public default BArrayGet bArrayGet(BExpr array, int index) throws BytecodeException {
    return provide().bytecodeFactory().arrayGet(array, bInt(index));
  }

  public default BArrayGet bArrayGet(BExpr array, BExpr index) throws BytecodeException {
    return provide().bytecodeFactory().arrayGet(array, index);
  }

  public default BRef bRef(int index) throws BytecodeException {
    return bRef(bIntType(), index);
  }

  public default BRef bRef(BType evaluationType, int index) throws BytecodeException {
    return provide().bytecodeFactory().ref(evaluationType, bInt(index));
  }

  public default BTupleGet bTupleGet() throws BytecodeException {
    return bTupleGet(bTuple(bInt()), 0);
  }

  public default BTupleGet bTupleGet(BExpr tuple, int index) throws BytecodeException {
    return provide().bytecodeFactory().tupleGet(tuple, bInt(index));
  }

  public default BTupleGet bTupleGet(BExpr tuple, BInt index) throws BytecodeException {
    return provide().bytecodeFactory().tupleGet(tuple, index);
  }
}
