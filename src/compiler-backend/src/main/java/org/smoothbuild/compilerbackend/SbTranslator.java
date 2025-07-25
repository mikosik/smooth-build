package org.smoothbuild.compilerbackend;

import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Map.mapOfAll;
import static org.smoothbuild.common.collect.Map.zipToMap;
import static org.smoothbuild.common.collect.Maps.computeIfAbsent;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileErrorMessage;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlistWithShadowing;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.virtualmachine.bytecode.load.BytecodeMethodLoader.BYTECODE_METHOD_NAME;
import static org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader.NATIVE_METHOD_NAME;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.location.FileLocation;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.define.SAnnotatedFunc;
import org.smoothbuild.compilerfrontend.lang.define.SAnnotatedValue;
import org.smoothbuild.compilerfrontend.lang.define.SAnnotation;
import org.smoothbuild.compilerfrontend.lang.define.SBlob;
import org.smoothbuild.compilerfrontend.lang.define.SCall;
import org.smoothbuild.compilerfrontend.lang.define.SCombine;
import org.smoothbuild.compilerfrontend.lang.define.SConstructor;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SExprFunc;
import org.smoothbuild.compilerfrontend.lang.define.SFunc;
import org.smoothbuild.compilerfrontend.lang.define.SInstantiate;
import org.smoothbuild.compilerfrontend.lang.define.SInt;
import org.smoothbuild.compilerfrontend.lang.define.SItem;
import org.smoothbuild.compilerfrontend.lang.define.SLambda;
import org.smoothbuild.compilerfrontend.lang.define.SMonoReference;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprFunc;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprValue;
import org.smoothbuild.compilerfrontend.lang.define.SNamedFunc;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;
import org.smoothbuild.compilerfrontend.lang.define.SOrder;
import org.smoothbuild.compilerfrontend.lang.define.SPolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SPolyReference;
import org.smoothbuild.compilerfrontend.lang.define.SString;
import org.smoothbuild.compilerfrontend.lang.define.SStructSelect;
import org.smoothbuild.compilerfrontend.lang.define.STupleSelect;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FileContentReader;
import org.smoothbuild.virtualmachine.evaluate.execute.BExprAttributes;

public class SbTranslator {
  private final ChainingBytecodeFactory bytecodeF;
  private final TypeSbTranslator typeTranslator;
  private final FileContentReader fileContentReader;
  private final BytecodeLoader bytecodeLoader;
  private final Bindings<SPolyEvaluable> evaluables;
  private final NList<SItem> lexicalEnvironment;
  private final HashMap<CacheKey, BExpr> cache;
  private final HashMap<Hash, String> names;
  private final HashMap<Hash, Location> locations;

  @AssistedInject
  public SbTranslator(
      BytecodeFactory bytecodeFactory,
      FileContentReader fileContentReader,
      BytecodeLoader bytecodeLoader,
      @Assisted Bindings<SPolyEvaluable> evaluables) {
    this(
        new ChainingBytecodeFactory(bytecodeFactory),
        new TypeSbTranslator(new ChainingBytecodeFactory(bytecodeFactory), map()),
        fileContentReader,
        bytecodeLoader,
        evaluables,
        nlist(),
        new HashMap<>(),
        new HashMap<>(),
        new HashMap<>());
  }

  private SbTranslator(
      ChainingBytecodeFactory bytecodeF,
      TypeSbTranslator typeTranslator,
      FileContentReader fileContentReader,
      BytecodeLoader bytecodeLoader,
      Bindings<SPolyEvaluable> evaluables,
      NList<SItem> lexicalEnvironment,
      HashMap<CacheKey, BExpr> cache,
      HashMap<Hash, String> names,
      HashMap<Hash, Location> locations) {
    this.bytecodeF = bytecodeF;
    this.typeTranslator = typeTranslator;
    this.fileContentReader = fileContentReader;
    this.bytecodeLoader = bytecodeLoader;
    this.evaluables = evaluables;
    this.lexicalEnvironment = lexicalEnvironment;
    this.cache = cache;
    this.names = names;
    this.locations = locations;
  }

  public BExprAttributes bExprAttributes() {
    return new BExprAttributes(mapOfAll(names), mapOfAll(locations));
  }

  private List<BExpr> translateExprs(List<SExpr> exprs) throws SbTranslatorException {
    return exprs.map(this::translateExpr);
  }

  public BExpr translateExpr(SExpr sExpr) throws SbTranslatorException {
    return switch (sExpr) {
      case SBlob sBlob -> saveLocAndReturn(sBlob, translateBlob(sBlob));
      case SCall sCall -> saveLocAndReturn(sCall, translateCall(sCall));
      case SCombine sCombine -> saveLocAndReturn(sCombine, translateCombine(sCombine));
      case SInt sInt -> saveLocAndReturn(sInt, translateInt(sInt));
      case SOrder sOrder -> saveLocAndReturn(sOrder, translateOrder(sOrder));
      case SStructSelect sStructSelect ->
        saveLocAndReturn(sStructSelect, translateStructSelect(sStructSelect));
      case STupleSelect sTupleSelect ->
        saveLocAndReturn(sTupleSelect, translateTupleSelect(sTupleSelect));
      case SString sString -> saveLocAndReturn(sString, translateString(sString));
      case SInstantiate sInstantiate -> translateInstantiate(sInstantiate);
      case SMonoReference sMonoReference -> translateMonoReference(sMonoReference);
      case SLambda sLambda -> translateLambda(sLambda);
    };
  }

  private BBlob translateBlob(SBlob sBlob) throws SbTranslatorException {
    return bytecodeF.blob(sink -> sink.write(sBlob.byteString()));
  }

  private BCall translateCall(SCall sCall) throws SbTranslatorException {
    var bFunction = translateExpr(sCall.callee());
    var bArguments = translateExpr(sCall.args());
    return bytecodeF.call(bFunction, bArguments);
  }

  private BCombine translateCombine(SCombine sCombine) throws SbTranslatorException {
    var bElements = translateExprs(sCombine.elements());
    return bytecodeF.combine(bElements);
  }

  private BInt translateInt(SInt sInt) throws SbTranslatorException {
    return bytecodeF.int_(sInt.bigInteger());
  }

  private BExpr translateInstantiate(SInstantiate sInstantiate) throws SbTranslatorException {
    var keys = sInstantiate.sPolyReference().scheme().typeParams().toList();
    var values = sInstantiate.typeArgs().map(typeTranslator::translate);
    var instantiatedVarMap = zipToMap(keys, values);
    var varMap = typeTranslator.typeVarMap().overrideWith(instantiatedVarMap);
    var newTypeSbTranslator = new TypeSbTranslator(bytecodeF, varMap);
    var sbTranslator = new SbTranslator(
        bytecodeF,
        newTypeSbTranslator,
        fileContentReader,
        bytecodeLoader,
        evaluables,
        lexicalEnvironment,
        cache,
        names,
        locations);
    return sbTranslator.translatePolyReference(sInstantiate.sPolyReference());
  }

  private BExpr translateLambda(SLambda sLambda) throws SbTranslatorException {
    var bLambda = funcBodySbTranslator(sLambda).translateExprFunc(sLambda);
    return saveNalAndReturn(sLambda.fqn().toString(), sLambda, bLambda);
  }

  private BExpr translateMonoReference(SMonoReference sMonoReference) throws SbTranslatorException {
    var id = sMonoReference.referencedId();
    var parts = id.parts();
    if (parts.size() == 1) {
      var name = parts.get(0);
      var itemS = lexicalEnvironment.get(name);
      if (itemS != null) {
        var evaluationType = typeTranslator.translate(itemS.type());
        var index = BigInteger.valueOf(lexicalEnvironment.indexOf(name));
        var bReference = bytecodeF.reference(evaluationType, index);
        return saveNalAndReturn(name.toString(), sMonoReference, bReference);
      }
    }
    throw new SbTranslatorException(compileErrorMessage(
        sMonoReference.location(), "Cannot find " + id.q() + " in lexical environment."));
  }

  private BExpr translatePolyReference(SPolyReference sPolyReference) throws SbTranslatorException {
    return evaluables
        .find(sPolyReference.referencedId())
        .mapOk(this::translateNamedEvaluable)
        .okOrThrow(
            e -> new SbTranslatorException(compileErrorMessage(sPolyReference.location(), e)));
  }

  private BExpr translateNamedEvaluable(SPolyEvaluable sPolyEvaluable)
      throws SbTranslatorException {
    return switch (sPolyEvaluable.evaluable()) {
      case SNamedFunc sNamedFunc -> translateNamedFuncWithCache(sNamedFunc);
      case SNamedValue sNamedValue -> translateNamedValueWithCache(sNamedValue);
    };
  }

  private BExpr translateNamedFuncWithCache(SNamedFunc sNamedFunc) throws SbTranslatorException {
    var key = new CacheKey(sNamedFunc.fqn(), typeTranslator.typeVarMap());
    return computeIfAbsent(cache, key, k -> translateNamedFunc(sNamedFunc));
  }

  private BExpr translateNamedFunc(SNamedFunc sNamedFunc) throws SbTranslatorException {
    var bFunc = funcBodySbTranslator(sNamedFunc).translateNamedFuncImpl(sNamedFunc);
    return saveNalAndReturn(sNamedFunc, bFunc);
  }

  private SbTranslator funcBodySbTranslator(SFunc sFunc) {
    var newEnvironment = nlistWithShadowing(sFunc.params().list().addAll(lexicalEnvironment));
    return new SbTranslator(
        bytecodeF,
        typeTranslator,
        fileContentReader,
        bytecodeLoader,
        evaluables,
        newEnvironment,
        cache,
        names,
        locations);
  }

  private BExpr translateNamedFuncImpl(SNamedFunc sNamedFunc) throws SbTranslatorException {
    return switch (sNamedFunc) {
      case SAnnotatedFunc a -> translateAnnotatedFunc(a);
      case SNamedExprFunc e -> translateExprFunc(e);
      case SConstructor c -> translateConstructor(c);
    };
  }

  private BExpr translateAnnotatedFunc(SAnnotatedFunc sAnnotatedFunc) throws SbTranslatorException {
    var annotationName = sAnnotatedFunc.annotation().name();
    return switch (annotationName) {
      case BYTECODE -> fetchFuncBytecode(sAnnotatedFunc);
      case NATIVE_PURE, NATIVE_IMPURE -> translateNativeFunc(sAnnotatedFunc);
      default ->
        throw new SbTranslatorException("Illegal function annotation: " + annotationName + ".");
    };
  }

  private BLambda translateExprFunc(SExprFunc sExprFunc) throws SbTranslatorException {
    var funcType = typeTranslator.translate(sExprFunc.type());
    var bBody = translateExpr(sExprFunc.body());
    return bytecodeF.lambda(funcType, bBody);
  }

  private BLambda translateNativeFunc(SAnnotatedFunc sNativeFunc) throws SbTranslatorException {
    var sAnnotation = sNativeFunc.annotation();
    var bJar = readNativeJar(sAnnotation.location());
    var bClassBinaryName = bytecodeF.string(sAnnotation.path().string());
    var bMethodName = bytecodeF.string(NATIVE_METHOD_NAME);
    var bMethodTuple = bytecodeF.method(bJar, bClassBinaryName, bMethodName).tuple();
    var bIsPure = bytecodeF.bool(sAnnotation.name().equals(NATIVE_PURE));
    var bLambdaType = typeTranslator.translate(sNativeFunc.type());
    var bArguments = referencesToAllArguments(bLambdaType);
    var bInvoke = bytecodeF.invoke(bLambdaType.result(), bMethodTuple, bIsPure, bArguments);
    saveNal(bInvoke, sNativeFunc);
    var bLambda = bytecodeF.lambda(bLambdaType, bInvoke);
    saveNal(bLambda, sNativeFunc);
    return bLambda;
  }

  private BCombine referencesToAllArguments(BLambdaType lambdaType) throws SbTranslatorException {
    List<BExpr> argumentReferences = lambdaType
        .params()
        .elements()
        .zipWithIndex()
        .map(t -> bytecodeF.reference(t.element1(), BigInteger.valueOf(t.element2())));
    return bytecodeF.combine(argumentReferences);
  }

  private BLambda translateConstructor(SConstructor sConstructor) throws SbTranslatorException {
    var bFuncType = typeTranslator.translate(sConstructor.type());
    var bBody = bytecodeF.combine(createReferenceB(bFuncType.params()));
    saveLoc(bBody, sConstructor);
    return bytecodeF.lambda(bFuncType, bBody);
  }

  private List<BExpr> createReferenceB(BTupleType sParamTypes) throws SbTranslatorException {
    return sParamTypes
        .elements()
        .zipWithIndex()
        .map(tuple -> bytecodeF.reference(tuple.element1(), BigInteger.valueOf(tuple.element2())));
  }

  private BOrder translateOrder(SOrder sOrder) throws SbTranslatorException {
    var bArrayType = typeTranslator.translate(sOrder.evaluationType());
    var bElements = translateExprs(sOrder.elements());
    return bytecodeF.order(bArrayType, bElements);
  }

  private BSelect translateStructSelect(SStructSelect sStructSelect) throws SbTranslatorException {
    var bSelectable = translateExpr(sStructSelect.selectable());
    var sStructType = (SStructType) sStructSelect.selectable().evaluationType();
    var indexJ = sStructType.fields().indexOf(sStructSelect.field());
    var bigInteger = BigInteger.valueOf(indexJ);
    var bIndex = bytecodeF.int_(bigInteger);
    saveLoc(bIndex, sStructSelect);
    return bytecodeF.select(bSelectable, bIndex);
  }

  private BSelect translateTupleSelect(STupleSelect sTupleSelect) throws SbTranslatorException {
    var bSelectable = translateExpr(sTupleSelect.selectable());
    var bigInteger = sTupleSelect.index();
    var bIndex = bytecodeF.int_(bigInteger);
    saveLoc(bIndex, sTupleSelect);
    return bytecodeF.select(bSelectable, bIndex);
  }

  private BString translateString(SString sString) throws SbTranslatorException {
    return bytecodeF.string(sString.string());
  }

  private BExpr translateNamedValueWithCache(SNamedValue sNamedValue) throws SbTranslatorException {
    var key = new CacheKey(sNamedValue.fqn(), typeTranslator.typeVarMap());
    return computeIfAbsent(cache, key, k -> translateNamedValue(sNamedValue));
  }

  private BExpr translateNamedValue(SNamedValue sNamedValue) throws SbTranslatorException {
    return switch (sNamedValue) {
      case SAnnotatedValue sAnnotatedValue -> translateAnnotatedValue(sAnnotatedValue);
      case SNamedExprValue sNamedExprValue -> translateNamedExprValue(sNamedExprValue);
    };
  }

  private BExpr translateAnnotatedValue(SAnnotatedValue sAnnotatedValue)
      throws SbTranslatorException {
    var annName = sAnnotatedValue.annotation().name();
    if (annName.equals(BYTECODE)) {
      return saveNalAndReturn(sAnnotatedValue, fetchValBytecode(sAnnotatedValue));
    } else {
      throw new SbTranslatorException("Illegal value annotation: " + q("@" + annName) + ".");
    }
  }

  private BExpr translateNamedExprValue(SNamedExprValue sNamedExprValue)
      throws SbTranslatorException {
    return translateExpr(sNamedExprValue.body());
  }

  // helpers

  private BExpr fetchValBytecode(SAnnotatedValue sAnnotatedValue) throws SbTranslatorException {
    var bType = typeTranslator.translate(sAnnotatedValue.type());
    return fetchBytecode(sAnnotatedValue.annotation(), bType, sAnnotatedValue.fqn());
  }

  private BExpr fetchFuncBytecode(SAnnotatedFunc sAnnotatedFunc) throws SbTranslatorException {
    var bType = typeTranslator.translate(sAnnotatedFunc.type());
    return fetchBytecode(sAnnotatedFunc.annotation(), bType, sAnnotatedFunc.fqn());
  }

  private BExpr fetchBytecode(SAnnotation annotation, BType bType, Id id)
      throws SbTranslatorException {
    var varNameToTypeMap = typeTranslator.typeVarMap().mapKeys(var -> var.name().toString());
    var jar = readNativeJar(annotation.location());
    var bytecode = loadBytecode(id, jar, annotation.path().string(), varNameToTypeMap);
    if (bytecode.isErr()) {
      throw new SbTranslatorException(annotation.location() + ": " + bytecode.err());
    }
    var bExpr = bytecode.ok();
    if (!bExpr.evaluationType().equals(bType)) {
      throw new SbTranslatorException(annotation.location()
          + ": Bytecode provider returned expression of wrong type "
          + bExpr.evaluationType().q()
          + " when " + id.q() + " is declared as " + bType.q() + ".");
    }
    return bExpr;
  }

  private Result<BExpr> loadBytecode(
      Id id, BBlob jar, String classBinaryName, Map<String, BType> varNameToTypeMap)
      throws SbTranslatorException {
    try {
      var bClassBinaryName = bytecodeF.string(classBinaryName);
      var bMethodName = bytecodeF.string(BYTECODE_METHOD_NAME);
      var bMethod = bytecodeF.method(jar, bClassBinaryName, bMethodName);
      return bytecodeLoader.load(id.toString(), bMethod, varNameToTypeMap);
    } catch (IOException e) {
      throw new SbTranslatorException(e);
    }
  }

  private BBlob readNativeJar(Location location) throws SbTranslatorException {
    var fullPath = fullPathOf(location).withExtension("jar");
    try {
      return fileContentReader.read(fullPath);
    } catch (IOException e) {
      var message = location + ": Error loading native jar %s.".formatted(fullPath.q());
      throw new SbTranslatorException(message, e);
    }
  }

  private static FullPath fullPathOf(Location location) throws SbTranslatorException {
    if (location instanceof FileLocation sourceLocation) {
      return sourceLocation.path();
    } else {
      throw new SbTranslatorException(location
          + ": Error loading native jar: Impossible to infer native file name for location "
          + location + ".");
    }
  }

  // helpers for saving names and locations

  private BExpr saveNalAndReturn(SNamedEvaluable sNamedEvaluable, BExpr bExpr) {
    saveNal(bExpr, sNamedEvaluable);
    return bExpr;
  }

  private BExpr saveNalAndReturn(String name, HasLocation hasLocation, BExpr bExpr) {
    saveNal(bExpr, name, hasLocation);
    return bExpr;
  }

  private BExpr saveLocAndReturn(HasLocation hasLocation, BExpr bExpr) {
    saveLoc(bExpr, hasLocation);
    return bExpr;
  }

  private void saveNal(BExpr bExpr, SNamedEvaluable sNamedEvaluable) {
    saveNal(bExpr, sNamedEvaluable.fqn().toString(), sNamedEvaluable);
  }

  private void saveNal(BExpr bExpr, String name, HasLocation hasLocation) {
    names.put(bExpr.hash(), name);
    saveLoc(bExpr, hasLocation);
  }

  private void saveLoc(BExpr bExpr, HasLocation hasLocation) {
    saveLoc(bExpr, hasLocation.location());
  }

  private void saveLoc(BExpr bExpr, Location location) {
    locations.put(bExpr.hash(), location);
  }

  private static record CacheKey(Id id, Map<STypeVar, BType> varMap) {}
}
