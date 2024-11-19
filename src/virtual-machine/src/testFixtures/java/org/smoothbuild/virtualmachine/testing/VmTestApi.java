package org.smoothbuild.virtualmachine.testing;

import static java.lang.ClassLoader.getSystemClassLoader;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.filesystem.base.FileSystemPart.fileSystemPart;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.log.base.Log.containsFailure;
import static org.smoothbuild.common.testing.TestingByteString.byteString;
import static org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader.NATIVE_METHOD_NAME;

import java.io.IOException;
import java.math.BigInteger;
import okio.ByteString;
import org.smoothbuild.common.Constants;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullFileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.SynchronizedBucket;
import org.smoothbuild.common.filesystem.mem.MemoryBucket;
import org.smoothbuild.common.io.Okios;
import org.smoothbuild.common.reflect.Classes;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.testing.CommonTestApi;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
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
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMethod;
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
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIfKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIntType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BInvokeKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BMapKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BOrderKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BPickKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BReferenceKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSelectKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BStringType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeMethodLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FileContentReader;
import org.smoothbuild.virtualmachine.bytecode.load.JarClassLoaderFactory;
import org.smoothbuild.virtualmachine.bytecode.load.MethodLoader;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCache;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCacheInitializer;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationHashFactory;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.compute.StepEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate;
import org.smoothbuild.virtualmachine.evaluate.execute.BReferenceInliner;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace.Line;
import org.smoothbuild.virtualmachine.evaluate.execute.Job;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.evaluate.step.BOutput;
import org.smoothbuild.virtualmachine.evaluate.step.CombineStep;
import org.smoothbuild.virtualmachine.evaluate.step.InvokeStep;
import org.smoothbuild.virtualmachine.evaluate.step.OrderStep;
import org.smoothbuild.virtualmachine.evaluate.step.PickStep;
import org.smoothbuild.virtualmachine.evaluate.step.SelectStep;
import org.smoothbuild.virtualmachine.evaluate.step.Step;

public interface VmTestApi extends CommonTestApi {
  public static Alias PROJECT = new Alias("t-project");
  public static Path BYTECODE_DB_SHORT_PATH = path(".smooth/bytecode");
  public static FullPath BYTECODE_DB_PATH = PROJECT.append(BYTECODE_DB_SHORT_PATH);
  public static FullPath COMPUTATION_DB_PATH = PROJECT.append(".smooth/computations");
  public static FullPath PROJECT_PATH = fullPath(PROJECT, Path.root());

  public StepEvaluator stepEvaluator();

  public BytecodeFactory bytecodeF();

  public BExprDb exprDb();

  public BKindDb kindDb();

  public default Job job(BExpr expr, BExpr... environment) {
    return job(expr, list(environment));
  }

  public FileSystem<FullPath> filesystem();

  public default FileSystem<Path> projectBucket() {
    return fileSystemPart(filesystem(), PROJECT_PATH);
  }

  public default FileSystem<Path> computationCacheBucket() {
    return fileSystemPart(filesystem(), COMPUTATION_DB_PATH);
  }

  public default Job job(BExpr expr, List<BExpr> list) {
    return new Job(expr, list.map(this::job), new BTrace());
  }

  public default Job job(BExpr expr) {
    return job(expr, list());
  }

  public default void throwExceptionOnFailure(Output<Tuple0> output) {
    if (containsFailure(output.report().logs())) {
      throw new RuntimeException(output.toString());
    }
  }

  public default FileSystem<FullPath> newSynchronizedMemoryFileSystem() {
    return new FullFileSystem(map(PROJECT, new SynchronizedBucket(new MemoryBucket())));
  }

  public default BEvaluate bEvaluate() {
    return bEvaluate(scheduler());
  }

  public default BEvaluate bEvaluate(NativeMethodLoader nativeMethodLoader) {
    return new BEvaluate(
        scheduler(), stepEvaluator(nativeMethodLoader), bytecodeF(), bReferenceInliner());
  }

  public default BEvaluate bEvaluate(Scheduler scheduler) {
    return new BEvaluate(scheduler, stepEvaluator(scheduler), bytecodeF(), bReferenceInliner());
  }

  public default BReferenceInliner bReferenceInliner() {
    return new BReferenceInliner(bytecodeF());
  }

  public default BEvaluate bEvaluate(StepEvaluator stepEvaluator) {
    return new BEvaluate(scheduler(), stepEvaluator, bytecodeF(), bReferenceInliner());
  }

  public default StepEvaluator stepEvaluator(NativeMethodLoader nativeMethodLoader) {
    return new StepEvaluator(
        computationHashFactory(),
        () -> container(nativeMethodLoader),
        computationCache(),
        scheduler(),
        bytecodeF());
  }

  public default StepEvaluator stepEvaluator(Scheduler scheduler) {
    return new StepEvaluator(
        computationHashFactory(), this::container, computationCache(), scheduler, bytecodeF());
  }

  public default ComputationHashFactory computationHashFactory() {
    return new ComputationHashFactory(Hash.of(123));
  }

  public default NativeApi nativeApi() {
    return container();
  }

  public default Container container() {
    return container(nativeMethodLoader());
  }

  public default Container container(NativeMethodLoader nativeMethodLoader) {
    return new Container(
        filesystem(), PROJECT_PATH, fileContentReader(), bytecodeF(), nativeMethodLoader);
  }

  public default ComputationCache computationCache() {
    var computationCache = new ComputationCache(computationCacheBucket(), exprDb(), bytecodeF());
    throwExceptionOnFailure(new ComputationCacheInitializer(computationCache).execute());
    return computationCache;
  }

  public default FullPath moduleFullPath() {
    return moduleFullPath("module.smooth");
  }

  public default FullPath moduleFullPath(String path) {
    return fullPath(PROJECT, path(path));
  }

  public default Step task() throws BytecodeException {
    return orderTask();
  }

  public default InvokeStep invokeTask() throws BytecodeException {
    return invokeTask(bInvoke(), bTrace());
  }

  public default InvokeStep invokeTask(BInvoke invoke) {
    return invokeTask(invoke, null);
  }

  public default InvokeStep invokeTask(BInvoke invoke, BTrace trace) {
    return new InvokeStep(invoke, trace);
  }

  public default CombineStep combineTask() throws BytecodeException {
    return combineTask(bCombine(), bTrace());
  }

  public default CombineStep combineTask(BCombine combine, BTrace trace) {
    return new CombineStep(combine, trace);
  }

  public default SelectStep selectTask() throws BytecodeException {
    return selectTask(bSelect(), bTrace());
  }

  public default SelectStep selectTask(BSelect select, BTrace trace) {
    return new SelectStep(select, trace);
  }

  public default PickStep pickTask() throws BytecodeException {
    return pickTask(bPick(), bTrace());
  }

  public default PickStep pickTask(BPick pick, BTrace trace) {
    return new PickStep(pick, trace);
  }

  public default OrderStep orderTask() throws BytecodeException {
    return orderTask(bOrder(), bTrace());
  }

  public default OrderStep orderTask(BOrder order, BTrace trace) {
    return new OrderStep(order, trace);
  }

  public default BOutput output(BValue value) throws BytecodeException {
    return output(value, bLogArrayEmpty());
  }

  public default BOutput output(BValue value, BArray messages) {
    return new BOutput(value, messages);
  }

  public default NativeMethodLoader nativeMethodLoader() {
    return new NativeMethodLoader(methodLoader());
  }

  public default FileContentReader fileContentReader() {
    return mock(FileContentReader.class);
  }

  public default BytecodeLoader bytecodeLoader() {
    return new BytecodeLoader(bytecodeMethodLoader(), bytecodeF());
  }

  public default BytecodeMethodLoader bytecodeMethodLoader() {
    return new BytecodeMethodLoader(methodLoader());
  }

  private MethodLoader methodLoader() {
    return new MethodLoader(jarClassLoaderFactory());
  }

  private JarClassLoaderFactory jarClassLoaderFactory() {
    return new JarClassLoaderFactory(bytecodeF(), getSystemClassLoader());
  }

  // InstB types

  public default BTupleType bAnimalType() throws BytecodeException {
    return bTupleType(bStringType(), bIntType());
  }

  public default BArrayType bArrayType() throws BytecodeException {
    return bArrayType(bStringType());
  }

  public default BArrayType bArrayType(BType elemT) throws BytecodeException {
    return kindDb().array(elemT);
  }

  public default BBlobType bBlobType() throws BytecodeException {
    return kindDb().blob();
  }

  public default BBoolType bBoolType() throws BytecodeException {
    return kindDb().bool();
  }

  public default BTupleType bFileType() throws BytecodeException {
    return bytecodeF().fileType();
  }

  public default BLambdaType bLambdaType() throws BytecodeException {
    return bLambdaType(bBlobType(), bStringType(), bIntType());
  }

  public default BLambdaType bLambdaType(BType resultT) throws BytecodeException {
    return bLambdaType(list(), resultT);
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
    return kindDb().lambda(paramTs, resultT);
  }

  public default BIntType bIntType() throws BytecodeException {
    return kindDb().int_();
  }

  public default BInvokeKind bInvokeKind() throws BytecodeException {
    return bInvokeKind(bIntType());
  }

  public default BInvokeKind bInvokeKind(BType evaluationType) throws BytecodeException {
    return kindDb().invoke(evaluationType);
  }

  public default BTupleType bMethodType() throws BytecodeException {
    return kindDb().method();
  }

  public default BTupleType bPersonType() throws BytecodeException {
    return bTupleType(bStringType(), bStringType());
  }

  public default BStringType bStringType() throws BytecodeException {
    return kindDb().string();
  }

  public default BTupleType bTupleType(List<BType> itemTypes) throws BytecodeException {
    return kindDb().tuple(itemTypes);
  }

  public default BTupleType bTupleType(BType... itemTypes) throws BytecodeException {
    return kindDb().tuple(itemTypes);
  }

  // Operation kinds

  public default BCallKind bCallKind() throws BytecodeException {
    return bCallKind(bIntType());
  }

  public default BCallKind bCallKind(BType evaluationType) throws BytecodeException {
    return kindDb().call(evaluationType);
  }

  public default BCombineKind bCombineKind(BType... itemTypes) throws BytecodeException {
    return kindDb().combine(bTupleType(itemTypes));
  }

  public default BIfKind bIfKind() throws BytecodeException {
    return bIfKind(bIntType());
  }

  public default BIfKind bIfKind(BType evaluationType) throws BytecodeException {
    return kindDb().if_(evaluationType);
  }

  public default BMapKind bMapKind() throws BytecodeException {
    return bMapKind(bArrayType(bIntType()));
  }

  public default BMapKind bMapKind(BType evaluationType) throws BytecodeException {
    return kindDb().map(evaluationType);
  }

  public default BOrderKind bOrderKind() throws BytecodeException {
    return bOrderKind(bIntType());
  }

  public default BOrderKind bOrderKind(BType elemT) throws BytecodeException {
    return kindDb().order(bArrayType(elemT));
  }

  public default BPickKind bPickKind() throws BytecodeException {
    return bPickKind(bIntType());
  }

  public default BPickKind bPickKind(BType evaluationType) throws BytecodeException {
    return kindDb().pick(evaluationType);
  }

  public default BReferenceKind bReferenceKind() throws BytecodeException {
    return bReferenceKind(bIntType());
  }

  public default BReferenceKind bReferenceKind(BType evaluationType) throws BytecodeException {
    return kindDb().reference(evaluationType);
  }

  public default BSelectKind bSelectKind() throws BytecodeException {
    return bSelectKind(bIntType());
  }

  public default BSelectKind bSelectKind(BType evaluationType) throws BytecodeException {
    return kindDb().select(evaluationType);
  }

  // ValueB-s

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

  public default BArray bArray(BType elementType, BValue... elements) throws BytecodeException {
    return bytecodeF()
        .arrayBuilder(bArrayType(elementType))
        .addAll(list(elements))
        .build();
  }

  public default BBlob blobBJarWithPluginApi(Class<?>... classes) throws BytecodeException {
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

  public default BBlob blobBJarWithJavaByteCode(Class<?>... classes) throws BytecodeException {
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

  public default BBlob bBlob() throws BytecodeException {
    return bBlob("blob data");
  }

  public default BBlob bBlob(String string) throws BytecodeException {
    return bBlob(byteString(string));
  }

  public default BBlob bBlob(int data) throws BytecodeException {
    return bBlob(Okios.intToByteString(data));
  }

  public default BBlob bBlob(ByteString bytes) throws BytecodeException {
    return bytecodeF().blob(sink -> sink.write(bytes));
  }

  public default BBlobBuilder bBlobBuilder() throws BytecodeException {
    return bytecodeF().blobBuilder();
  }

  public default BBool bBool() throws BytecodeException {
    return bBool(true);
  }

  public default BBool bBool(boolean value) throws BytecodeException {
    return bytecodeF().bool(value);
  }

  public default BTuple bFile(Path path) throws BytecodeException {
    return bFile(path, path.toString());
  }

  public default BTuple bFile(Path path, String content) throws BytecodeException {
    return bFile(path.toString(), content);
  }

  public default BTuple bFile(String path, String content) throws BytecodeException {
    return bFile(path, ByteString.encodeString(content, Constants.CHARSET));
  }

  public default BTuple bFile(Path path, ByteString content) throws BytecodeException {
    return bFile(path.toString(), content);
  }

  public default BTuple bFile(String path, ByteString content) throws BytecodeException {
    return bFile(path, bBlob(content));
  }

  public default BTuple bFile(String path, BBlob blob) throws BytecodeException {
    BString string = bytecodeF().string(path);
    return bytecodeF().file(blob, string);
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
    return bytecodeF().lambda(type, body);
  }

  public default BLambda bIntIdLambda() throws BytecodeException {
    return bLambda(list(bIntType()), bReference(bIntType(), 0));
  }

  public default BLambda bStringIdLambda() throws BytecodeException {
    return bLambda(list(bStringType()), bReference(bStringType(), 0));
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
    return bytecodeF().int_(value);
  }

  public default BInvoke bReturnAbcInvoke() throws BytecodeException {
    return bReturnAbcInvoke(true);
  }

  public default BInvoke bReturnAbcInvoke(boolean isPure) throws BytecodeException {
    return bInvoke(bStringType(), ReturnAbcFunc.class, isPure);
  }

  public static class ReturnAbcFunc {
    public static BValue func(NativeApi nativeApi, BTuple arguments) throws BytecodeException {
      return nativeApi.factory().string("abc");
    }
  }

  public default BInvoke bInvoke() throws BytecodeException {
    return bInvoke(bIntType());
  }

  public default BInvoke bInvoke(BType evaluationType) throws BytecodeException {
    var bMethodTuple = bMethodTuple(bBlob(7));
    return bInvoke(evaluationType, bMethodTuple, bBool(true), bTuple());
  }

  public default BInvoke bInvoke(Class<?> clazz) throws BytecodeException {
    return bInvoke(bIntType(), clazz);
  }

  public default BInvoke bInvoke(BType evaluationType, Class<?> clazz) throws BytecodeException {
    return bInvoke(evaluationType, clazz, true);
  }

  public default BInvoke bInvoke(BType evaluationType, BExpr method) throws BytecodeException {
    return bInvoke(evaluationType, method, bBool(true), bTuple());
  }

  public default BInvoke bInvoke(BType evaluationType, Class<?> clazz, boolean isPure)
      throws BytecodeException {
    return bInvoke(evaluationType, clazz, isPure, bTuple());
  }

  public default BInvoke bInvoke(
      BType evaluationType, Class<?> clazz, boolean isPure, BTuple arguments)
      throws BytecodeException {
    var bMethodTuple = bMethodTuple(clazz);
    return bInvoke(evaluationType, bMethodTuple, bBool(isPure), arguments);
  }

  public default BInvoke bInvoke(BType evaluationType, BExpr method, BExpr arguments)
      throws BytecodeException {
    return bytecodeF().invoke(evaluationType, method, bBool(true), arguments);
  }

  public default BInvoke bInvoke(BType evaluationType, BExpr method, BExpr isPure, BExpr arguments)
      throws BytecodeException {
    return bytecodeF().invoke(evaluationType, method, isPure, arguments);
  }

  public default BTuple bMethodTuple() throws BytecodeException {
    var jar = bBlob();
    var classBinaryName = bString();
    return bMethodTuple(jar, classBinaryName);
  }

  public default BTuple bMethodTuple(Class<?> clazz) throws BytecodeException {
    return bMethodTuple(clazz, NATIVE_METHOD_NAME);
  }

  public default BTuple bMethodTuple(Class<?> clazz, String methodName) throws BytecodeException {
    return bMethodTuple(blobBJarWithPluginApi(clazz), clazz.getName(), methodName);
  }

  public default BTuple bMethodTuple(BBlob jar) throws BytecodeException {
    return bMethodTuple(jar, "classBinaryName", NATIVE_METHOD_NAME);
  }

  public default BTuple bMethodTuple(BBlob jar, String classBinaryName, String methodName)
      throws BytecodeException {
    return bMethod(jar, bString(classBinaryName), bString(methodName)).tuple();
  }

  public default BTuple bMethodTuple(String classBinaryName) throws BytecodeException {
    return bMethodTuple(bBlob(), bString(classBinaryName));
  }

  public default BTuple bMethodTuple(BBlob jar, BString classBinaryName) throws BytecodeException {
    return bMethod(jar, classBinaryName).tuple();
  }

  public default BMethod bMethod(Class<?> clazz) throws BytecodeException {
    return bMethod(clazz, NATIVE_METHOD_NAME);
  }

  public default BMethod bMethod(Class<?> clazz, String methodName) throws BytecodeException {
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
    return bytecodeF().method(jar, classBinaryName, methodName);
  }

  public default BTuple bPerson(String firstName, String lastName) throws BytecodeException {
    return bTuple(bString(firstName), bString(lastName));
  }

  public default BString bString() throws BytecodeException {
    return bytecodeF().string("abc");
  }

  public default BString bString(String string) throws BytecodeException {
    return bytecodeF().string(string);
  }

  public default BTuple bTuple(BValue... items) throws BytecodeException {
    return bytecodeF().tuple(list(items));
  }

  public default BArray bLogArrayWithOneError() throws BytecodeException {
    return bArray(bytecodeF().errorLog("error message"));
  }

  public default BArray bLogArrayEmpty() throws BytecodeException {
    return bArray(bytecodeF().storedLogType());
  }

  public default BTuple bFatalLog() throws BytecodeException {
    return bFatalLog("fatal message");
  }

  public default BTuple bFatalLog(String text) throws BytecodeException {
    return bytecodeF().fatalLog(text);
  }

  public default BTuple bErrorLog() throws BytecodeException {
    return bErrorLog("error message");
  }

  public default BTuple bErrorLog(String text) throws BytecodeException {
    return bytecodeF().errorLog(text);
  }

  public default BTuple bWarningLog() throws BytecodeException {
    return bWarningLog("warning message");
  }

  public default BTuple bWarningLog(String text) throws BytecodeException {
    return bytecodeF().warningLog(text);
  }

  public default BTuple bInfoLog() throws BytecodeException {
    return bInfoLog("info message");
  }

  public default BTuple bInfoLog(String text) throws BytecodeException {
    return bytecodeF().infoLog(text);
  }

  // Operations

  public default BCall bCall() throws BytecodeException {
    return bCall(bIntIdLambda(), bInt());
  }

  public default BCall bCall(BExpr lambda, BExpr... arguments) throws BytecodeException {
    return bytecodeF().call(lambda, bCombine(arguments));
  }

  public default BCall bCallWithArguments(BExpr lambda, BExpr arguments) throws BytecodeException {
    return bytecodeF().call(lambda, arguments);
  }

  public default BCombine bCombine(BExpr... items) throws BytecodeException {
    return bytecodeF().combine(list(items));
  }

  public default BIf bIf(BExpr condition, BExpr then_, BExpr else_) throws BytecodeException {
    return bytecodeF().if_(condition, then_, else_);
  }

  public default BMap bMap(BExpr array, BExpr mapper) throws BytecodeException {
    return bytecodeF().map(array, mapper);
  }

  public default BOrder bOrder() throws BytecodeException {
    return bOrder(bIntType());
  }

  public default BOrder bOrder(BExpr... elements) throws BytecodeException {
    return bOrder(elements[0].evaluationType(), elements);
  }

  public default BOrder bOrder(BType elementType, BExpr... elements) throws BytecodeException {
    var elemList = list(elements);
    return bytecodeF().order(bArrayType(elementType), elemList);
  }

  public default BPick bPick() throws BytecodeException {
    return bPick(bArray(bInt()), bInt(0));
  }

  public default BPick bPick(BExpr array, int index) throws BytecodeException {
    return bytecodeF().pick(array, bInt(index));
  }

  public default BPick bPick(BExpr array, BExpr index) throws BytecodeException {
    return bytecodeF().pick(array, index);
  }

  public default BReference bReference(int index) throws BytecodeException {
    return bReference(bIntType(), index);
  }

  public default BReference bReference(BType evaluationType, int index) throws BytecodeException {
    return bytecodeF().reference(evaluationType, bInt(index));
  }

  public default BSelect bSelect() throws BytecodeException {
    return bSelect(bTuple(bInt()), 0);
  }

  public default BSelect bSelect(BExpr tuple, int index) throws BytecodeException {
    return bytecodeF().select(tuple, bInt(index));
  }

  public default BSelect bSelect(BExpr tuple, BInt index) throws BytecodeException {
    return bytecodeF().select(tuple, index);
  }

  public default BTrace bTrace() {
    return new BTrace();
  }

  public default BTrace bTrace(BExpr call, BExpr called) {
    return bTrace(call.hash(), called.hash(), (Line) null);
  }

  public default BTrace bTrace(BExpr call, BExpr called, BTrace next) {
    return BTrace.bTrace(call.hash(), called.hash(), next);
  }

  public default BTrace bTrace(Hash call, Hash called, BTrace next) {
    return BTrace.bTrace(call, called, next);
  }

  public default BTrace bTrace(Hash call, Hash called) {
    return bTrace(call, called, (Line) null);
  }

  private BTrace bTrace(Hash call, Hash called, Line next) {
    return new BTrace(new Line(call, called, next));
  }
}
