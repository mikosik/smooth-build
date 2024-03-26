package org.smoothbuild.compilerbackend;

import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Map.mapOfAll;
import static org.smoothbuild.common.collect.Map.zipToMap;
import static org.smoothbuild.common.collect.Maps.computeIfAbsent;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.common.collect.NList.nlistWithShadowing;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compilerfrontend.lang.type.AnnotationNames.NATIVE_PURE;

import jakarta.inject.Inject;
import java.math.BigInteger;
import java.util.HashMap;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.location.FileLocation;
import org.smoothbuild.compilerfrontend.lang.base.location.Located;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
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
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;
import org.smoothbuild.virtualmachine.bytecode.type.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.base.BType;

public class SbTranslator {
  private final ChainingBytecodeFactory bytecodeF;
  private final TypeSbTranslator typeF;
  private final FilePersister filePersister;
  private final BytecodeLoader bytecodeLoader;
  private final ImmutableBindings<SNamedEvaluable> evaluables;
  private final NList<SItem> lexicalEnvironment;
  private final HashMap<CacheKey, BExpr> cache;
  private final HashMap<Hash, String> nameMapping;
  private final HashMap<Hash, Location> locationMapping;

  @Inject
  public SbTranslator(
      BytecodeFactory bytecodeFactory,
      FilePersister filePersister,
      BytecodeLoader bytecodeLoader,
      ImmutableBindings<SNamedEvaluable> evaluables) {
    this(
        new ChainingBytecodeFactory(bytecodeFactory),
        new TypeSbTranslator(new ChainingBytecodeFactory(bytecodeFactory), map()),
        filePersister,
        bytecodeLoader,
        evaluables,
        nlist(),
        new HashMap<>(),
        new HashMap<>(),
        new HashMap<>());
  }

  private SbTranslator(
      ChainingBytecodeFactory bytecodeF,
      TypeSbTranslator typeF,
      FilePersister filePersister,
      BytecodeLoader bytecodeLoader,
      ImmutableBindings<SNamedEvaluable> evaluables,
      NList<SItem> lexicalEnvironment,
      HashMap<CacheKey, BExpr> cache,
      HashMap<Hash, String> nameMapping,
      HashMap<Hash, Location> locationMapping) {
    this.bytecodeF = bytecodeF;
    this.typeF = typeF;
    this.filePersister = filePersister;
    this.bytecodeLoader = bytecodeLoader;
    this.evaluables = evaluables;
    this.lexicalEnvironment = lexicalEnvironment;
    this.cache = cache;
    this.nameMapping = nameMapping;
    this.locationMapping = locationMapping;
  }

  public BsMapping bsMapping() {
    return new BsMapping(mapOfAll(nameMapping), mapOfAll(locationMapping));
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
    var callableB = translateExpr(sCall.callee());
    var argsB = (BCombine) translateExpr(sCall.args());
    return bytecodeF.call(callableB, argsB);
  }

  private BCombine translateCombine(SCombine sCombine) throws SbTranslatorException {
    var elemBs = translateExprs(sCombine.elems());
    return bytecodeF.combine(elemBs);
  }

  private BInt translateInt(SInt sInt) throws SbTranslatorException {
    return bytecodeF.int_(sInt.bigInteger());
  }

  private BExpr translateInstantiate(SInstantiate sInstantiate) throws SbTranslatorException {
    var keys = sInstantiate.sPolymorphic().schema().quantifiedVars().toList();
    var values = sInstantiate.typeArgs().map(typeF::translate);
    var instantiatedVarMap = zipToMap(keys, values);
    var varMap = typeF.varMap().overrideWith(instantiatedVarMap);
    var newTypeSbTranslator = new TypeSbTranslator(bytecodeF, varMap);
    var sbTranslator = new SbTranslator(
        bytecodeF,
        newTypeSbTranslator,
        filePersister,
        bytecodeLoader,
        evaluables,
        lexicalEnvironment,
        cache,
        nameMapping,
        locationMapping);
    return sbTranslator.translatePolymorphic(sInstantiate.sPolymorphic());
  }

  private BExpr translatePolymorphic(SPolymorphic sPolymorphic) throws SbTranslatorException {
    return switch (sPolymorphic) {
      case SLambda sLambda -> translateLambda(sLambda);
      case SReference sReference -> translateReference(sReference);
    };
  }

  private BExpr translateLambda(SLambda sLambda) throws SbTranslatorException {
    var lambdaB = funcBodySbTranslator(sLambda).translateExprFunc(sLambda);
    return saveNalAndReturn("<lambda>", sLambda, lambdaB);
  }

  private BExpr translateReference(SReference sReference) throws SbTranslatorException {
    var itemS = lexicalEnvironment.get(sReference.referencedName());
    if (itemS == null) {
      Maybe<SNamedEvaluable> namedEvaluableS = evaluables.getMaybe(sReference.referencedName());
      if (namedEvaluableS.isSome()) {
        return switch (namedEvaluableS.get()) {
          case SNamedFunc sNamedFunc -> translateNamedFuncWithCache(sNamedFunc);
          case SNamedValue sNamedValue -> translateNamedValueWithCache(sReference, sNamedValue);
        };
      } else {
        throw new SbTranslatorException("Cannot resolve `" + sReference.referencedName() + "` at "
            + sReference.location() + ".");
      }
    } else {
      var evaluationType = typeF.translate(itemS.type());
      var index = BigInteger.valueOf(lexicalEnvironment.indexOf(sReference.referencedName()));
      return saveNalAndReturn(
          sReference.referencedName(), sReference, bytecodeF.reference(evaluationType, index));
    }
  }

  private BExpr translateNamedFuncWithCache(SNamedFunc sNamedFunc) throws SbTranslatorException {
    var key = new CacheKey(sNamedFunc.name(), typeF.varMap());
    return computeIfAbsent(cache, key, k -> translateNamedFunc(sNamedFunc));
  }

  private BExpr translateNamedFunc(SNamedFunc sNamedFunc) throws SbTranslatorException {
    var funcB = funcBodySbTranslator(sNamedFunc).translateNamedFuncImpl(sNamedFunc);
    return saveNalAndReturn(sNamedFunc, funcB);
  }

  private SbTranslator funcBodySbTranslator(SFunc sFunc) {
    var newEnvironment = nlistWithShadowing(sFunc.params().list().appendAll(lexicalEnvironment));
    return new SbTranslator(
        bytecodeF,
        typeF,
        filePersister,
        bytecodeLoader,
        evaluables,
        newEnvironment,
        cache,
        nameMapping,
        locationMapping);
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
    var funcT = typeF.translate(sExprFunc.schema().type());
    var bodyB = translateExpr(sExprFunc.body());
    return bytecodeF.lambda(funcT, bodyB);
  }

  private BNativeFunc translateNativeFunc(SAnnotatedFunc nativeFuncS) throws SbTranslatorException {
    var funcTB = typeF.translate(nativeFuncS.schema().type());
    var annS = nativeFuncS.annotation();
    var jarB = persistNativeJar(annS.location());
    var classBinaryNameB = bytecodeF.string(annS.path().string());
    var isPureB = bytecodeF.bool(annS.name().equals(NATIVE_PURE));
    return bytecodeF.nativeFunc(funcTB, jarB, classBinaryNameB, isPureB);
  }

  private BLambda translateConstructor(SConstructor sConstructor) throws SbTranslatorException {
    var funcTB = typeF.translate(sConstructor.schema().type());
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
    var arrayTB = typeF.translate(sOrder.evaluationType());
    var elementsB = translateExprs(sOrder.elems());
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

  private BExpr translateNamedValueWithCache(SReference sReference, SNamedValue sNamedValue)
      throws SbTranslatorException {
    var key = new CacheKey(sNamedValue.name(), typeF.varMap());
    return computeIfAbsent(
        cache, key, k -> translateNamedValue(sReference.location(), sNamedValue));
  }

  private BExpr translateNamedValue(Location refLocation, SNamedValue sNamedValue)
      throws SbTranslatorException {
    return switch (sNamedValue) {
      case SAnnotatedValue sAnnotatedValue -> translateAnnotatedValue(sAnnotatedValue);
      case SNamedExprValue sNamedExprValue -> translateNamedExprValue(refLocation, sNamedExprValue);
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

  private BExpr translateNamedExprValue(Location refLocation, SNamedExprValue sNamedExprValue)
      throws SbTranslatorException {
    var bResultType = typeF.translate(sNamedExprValue.schema().type());
    var bFuncType = bytecodeF.funcType(list(), bResultType);
    var bFunc = bytecodeF.lambda(bFuncType, translateExpr(sNamedExprValue.body()));
    saveNal(bFunc, sNamedExprValue);
    var bCall = bytecodeF.call(bFunc, bytecodeF.combine(list()));
    saveLoc(bCall, refLocation);
    return bCall;
  }

  // helpers

  private BExpr fetchValBytecode(SAnnotatedValue sAnnotatedValue) throws SbTranslatorException {
    var bType = typeF.translate(sAnnotatedValue.schema().type());
    return fetchBytecode(sAnnotatedValue.annotation(), bType, sAnnotatedValue.name());
  }

  private BExpr fetchFuncBytecode(SAnnotatedFunc sAnnotatedFunc) throws SbTranslatorException {
    var bType = typeF.translate(sAnnotatedFunc.schema().type());
    return fetchBytecode(sAnnotatedFunc.annotation(), bType, sAnnotatedFunc.name());
  }

  private BExpr fetchBytecode(SAnnotation annotation, BType bType, String name)
      throws SbTranslatorException {
    var varNameToTypeMap = typeF.varMap().mapKeys(SVar::name);
    var jar = persistNativeJar(annotation.location());
    var bytecode = loadBytecode(name, jar, annotation.path().string(), varNameToTypeMap);
    if (bytecode.isLeft()) {
      throw new SbTranslatorException(annotation.location() + ": " + bytecode.left());
    }
    var bExpr = bytecode.right();
    if (!bExpr.evaluationType().equals(bType)) {
      throw new SbTranslatorException(annotation.location()
          + ": Bytecode provider returned expression of wrong type "
          + bExpr.evaluationType().q()
          + " when " + q(name) + " is declared as " + bType.q() + ".");
    }
    return bExpr;
  }

  private Either<String, BExpr> loadBytecode(
      String name, BBlob jar, String path, Map<String, BType> varNameToTypeMap)
      throws SbTranslatorException {
    try {
      return bytecodeLoader.load(name, jar, path, varNameToTypeMap);
    } catch (BytecodeException e) {
      throw new SbTranslatorException(e);
    }
  }

  private BBlob persistNativeJar(Location location) throws SbTranslatorException {
    var fullPath = fullPathOf(location).withExtension("jar");
    try {
      return filePersister.persist(fullPath);
    } catch (BytecodeException e) {
      var message = location + ": Error persisting native jar %s.".formatted(fullPath.q());
      throw new SbTranslatorException(message, e);
    }
  }

  private static FullPath fullPathOf(Location location) throws SbTranslatorException {
    if (location instanceof FileLocation sourceLocation) {
      return sourceLocation.file();
    } else {
      throw new SbTranslatorException(location
          + ": Error loading native jar: Impossible to infer native file name for location "
          + location + ".");
    }
  }

  // helpers for saving names and locations

  private BExpr saveNalAndReturn(Nal nal, BExpr bExpr) {
    saveNal(bExpr, nal);
    return bExpr;
  }

  private BExpr saveNalAndReturn(String name, Located located, BExpr bExpr) {
    saveNal(bExpr, name, located);
    return bExpr;
  }

  private BExpr saveLocAndReturn(Located located, BExpr bExpr) {
    saveLoc(bExpr, located);
    return bExpr;
  }

  private void saveNal(BExpr bExpr, Nal nal) {
    saveNal(bExpr, nal.name(), nal);
  }

  private void saveNal(BExpr bExpr, String name, Located located) {
    nameMapping.put(bExpr.hash(), name);
    saveLoc(bExpr, located);
  }

  private void saveLoc(BExpr bExpr, Located located) {
    saveLoc(bExpr, located.location());
  }

  private void saveLoc(BExpr bExpr, Location location) {
    locationMapping.put(bExpr.hash(), location);
  }

  private static record CacheKey(String name, Map<SVar, BType> varMap) {}
}
