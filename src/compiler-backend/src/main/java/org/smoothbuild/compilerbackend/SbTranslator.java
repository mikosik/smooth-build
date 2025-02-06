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

import com.google.inject.assistedinject.Assisted;
import jakarta.inject.Inject;
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
import org.smoothbuild.common.log.report.BExprAttributes;
import org.smoothbuild.compilerfrontend.lang.bindings.ImmutableBindings;
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
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprFunc;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprValue;
import org.smoothbuild.compilerfrontend.lang.define.SNamedFunc;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;
import org.smoothbuild.compilerfrontend.lang.define.SOrder;
import org.smoothbuild.compilerfrontend.lang.define.SPolymorphic;
import org.smoothbuild.compilerfrontend.lang.define.SReference;
import org.smoothbuild.compilerfrontend.lang.define.SSelect;
import org.smoothbuild.compilerfrontend.lang.define.SString;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
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

public class SbTranslator {
  private final ChainingBytecodeFactory bytecodeF;
  private final TypeSbTranslator typeTranslator;
  private final FileContentReader fileContentReader;
  private final BytecodeLoader bytecodeLoader;
  private final ImmutableBindings<SNamedEvaluable> evaluables;
  private final NList<SItem> lexicalEnvironment;
  private final HashMap<CacheKey, BExpr> cache;
  private final HashMap<Hash, String> names;
  private final HashMap<Hash, Location> locations;

  @Inject
  public SbTranslator(
      BytecodeFactory bytecodeFactory,
      FileContentReader fileContentReader,
      BytecodeLoader bytecodeLoader,
      @Assisted ImmutableBindings<SNamedEvaluable> evaluables) {
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
      ImmutableBindings<SNamedEvaluable> evaluables,
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
      case SSelect sSelect -> saveLocAndReturn(sSelect, translateSelect(sSelect));
      case SString sString -> saveLocAndReturn(sString, translateString(sString));
      case SInstantiate sInstantiate -> translateInstantiate(sInstantiate);
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
    var keys = sInstantiate.sPolymorphic().schema().quantifiedVars().toList();
    var values = sInstantiate.typeArgs().map(typeTranslator::translate);
    var instantiatedVarMap = zipToMap(keys, values);
    var varMap = typeTranslator.varMap().overrideWith(instantiatedVarMap);
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
    return sbTranslator.translatePolymorphic(sInstantiate.sPolymorphic());
  }

  private BExpr translatePolymorphic(SPolymorphic sPolymorphic) throws SbTranslatorException {
    return switch (sPolymorphic) {
      case SLambda sLambda -> translateLambda(sLambda);
      case SReference sReference -> translateReference(sReference);
    };
  }

  private BExpr translateLambda(SLambda sLambda) throws SbTranslatorException {
    var bLambda = funcBodySbTranslator(sLambda).translateExprFunc(sLambda);
    return saveNalAndReturn("<lambda>", sLambda, bLambda);
  }

  private BExpr translateReference(SReference sReference) throws SbTranslatorException {
    var id = sReference.referencedId();
    var parts = id.parts();
    if (parts.size() == 1) {
      var name = parts.get(0);
      var itemS = lexicalEnvironment.get(name);
      if (itemS != null) {
        var evaluationType = typeTranslator.translate(itemS.type());
        var index = BigInteger.valueOf(lexicalEnvironment.indexOf(name));
        var bReference = bytecodeF.reference(evaluationType, index);
        return saveNalAndReturn(name.toString(), sReference, bReference);
      }
    }
    return evaluables
        .find(id)
        .mapOk(this::translateNamedEvaluable)
        .okOrThrow(e -> new SbTranslatorException(compileErrorMessage(sReference.location(), e)));
  }

  private BExpr translateNamedEvaluable(SNamedEvaluable evaluable) throws SbTranslatorException {
    return switch (evaluable) {
      case SNamedFunc sNamedFunc -> translateNamedFuncWithCache(sNamedFunc);
      case SNamedValue sNamedValue -> translateNamedValueWithCache(sNamedValue);
    };
  }

  private BExpr translateNamedFuncWithCache(SNamedFunc sNamedFunc) throws SbTranslatorException {
    var key = new CacheKey(sNamedFunc.fqn(), typeTranslator.varMap());
    return computeIfAbsent(cache, key, k -> translateNamedFunc(sNamedFunc));
  }

  private BExpr translateNamedFunc(SNamedFunc sNamedFunc) throws SbTranslatorException {
    var funcB = funcBodySbTranslator(sNamedFunc).translateNamedFuncImpl(sNamedFunc);
    return saveNalAndReturn(sNamedFunc, funcB);
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
      default -> throw new SbTranslatorException(
          "Illegal function annotation: " + annotationName + ".");
    };
  }

  private BLambda translateExprFunc(SExprFunc sExprFunc) throws SbTranslatorException {
    var funcT = typeTranslator.translate(sExprFunc.schema().type());
    var bodyB = translateExpr(sExprFunc.body());
    return bytecodeF.lambda(funcT, bodyB);
  }

  private BLambda translateNativeFunc(SAnnotatedFunc sNativeFunc) throws SbTranslatorException {
    var sAnnotation = sNativeFunc.annotation();
    var bJar = readNativeJar(sAnnotation.location());
    var bClassBinaryName = bytecodeF.string(sAnnotation.path().string());
    var bMethodName = bytecodeF.string(NATIVE_METHOD_NAME);
    var bMethodTuple = bytecodeF.method(bJar, bClassBinaryName, bMethodName).tuple();
    var bIsPure = bytecodeF.bool(sAnnotation.name().equals(NATIVE_PURE));
    var bLambdaType = typeTranslator.translate(sNativeFunc.schema().type());
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
    var funcTB = typeTranslator.translate(sConstructor.schema().type());
    var bodyB = bytecodeF.combine(createReferenceB(funcTB.params()));
    saveLoc(bodyB, sConstructor);
    return bytecodeF.lambda(funcTB, bodyB);
  }

  private List<BExpr> createReferenceB(BTupleType paramTs) throws SbTranslatorException {
    return paramTs
        .elements()
        .zipWithIndex()
        .map(tuple -> bytecodeF.reference(tuple.element1(), BigInteger.valueOf(tuple.element2())));
  }

  private BOrder translateOrder(SOrder sOrder) throws SbTranslatorException {
    var arrayTB = typeTranslator.translate(sOrder.evaluationType());
    var elementsB = translateExprs(sOrder.elements());
    return bytecodeF.order(arrayTB, elementsB);
  }

  private BSelect translateSelect(SSelect sSelect) throws SbTranslatorException {
    var selectableB = translateExpr(sSelect.selectable());
    var structTS = (SStructType) sSelect.selectable().evaluationType();
    var indexJ = structTS.fields().indexOf(sSelect.field());
    var bigInteger = BigInteger.valueOf(indexJ);
    var indexB = bytecodeF.int_(bigInteger);
    saveLoc(indexB, sSelect);
    return bytecodeF.select(selectableB, indexB);
  }

  private BString translateString(SString sString) throws SbTranslatorException {
    return bytecodeF.string(sString.string());
  }

  private BExpr translateNamedValueWithCache(SNamedValue sNamedValue) throws SbTranslatorException {
    var key = new CacheKey(sNamedValue.fqn(), typeTranslator.varMap());
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
    var bType = typeTranslator.translate(sAnnotatedValue.schema().type());
    return fetchBytecode(sAnnotatedValue.annotation(), bType, sAnnotatedValue.fqn());
  }

  private BExpr fetchFuncBytecode(SAnnotatedFunc sAnnotatedFunc) throws SbTranslatorException {
    var bType = typeTranslator.translate(sAnnotatedFunc.schema().type());
    return fetchBytecode(sAnnotatedFunc.annotation(), bType, sAnnotatedFunc.fqn());
  }

  private BExpr fetchBytecode(SAnnotation annotation, BType bType, Id id)
      throws SbTranslatorException {
    var varNameToTypeMap = typeTranslator.varMap().mapKeys(SType::specifier);
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

  private static record CacheKey(Id id, Map<SVar, BType> varMap) {}
}
