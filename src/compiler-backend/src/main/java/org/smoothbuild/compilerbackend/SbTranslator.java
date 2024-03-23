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
import org.smoothbuild.compilerfrontend.lang.define.AnnotatedFuncS;
import org.smoothbuild.compilerfrontend.lang.define.AnnotatedValueS;
import org.smoothbuild.compilerfrontend.lang.define.AnnotationS;
import org.smoothbuild.compilerfrontend.lang.define.BlobS;
import org.smoothbuild.compilerfrontend.lang.define.CallS;
import org.smoothbuild.compilerfrontend.lang.define.CombineS;
import org.smoothbuild.compilerfrontend.lang.define.ConstructorS;
import org.smoothbuild.compilerfrontend.lang.define.ExprFuncS;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.compilerfrontend.lang.define.FuncS;
import org.smoothbuild.compilerfrontend.lang.define.InstantiateS;
import org.smoothbuild.compilerfrontend.lang.define.IntS;
import org.smoothbuild.compilerfrontend.lang.define.ItemS;
import org.smoothbuild.compilerfrontend.lang.define.LambdaS;
import org.smoothbuild.compilerfrontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compilerfrontend.lang.define.NamedExprFuncS;
import org.smoothbuild.compilerfrontend.lang.define.NamedExprValueS;
import org.smoothbuild.compilerfrontend.lang.define.NamedFuncS;
import org.smoothbuild.compilerfrontend.lang.define.NamedValueS;
import org.smoothbuild.compilerfrontend.lang.define.OrderS;
import org.smoothbuild.compilerfrontend.lang.define.PolymorphicS;
import org.smoothbuild.compilerfrontend.lang.define.ReferenceS;
import org.smoothbuild.compilerfrontend.lang.define.SelectS;
import org.smoothbuild.compilerfrontend.lang.define.StringS;
import org.smoothbuild.compilerfrontend.lang.type.StructTS;
import org.smoothbuild.compilerfrontend.lang.type.VarS;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class SbTranslator {
  private final ChainingBytecodeFactory bytecodeF;
  private final TypeSbTranslator typeF;
  private final FilePersister filePersister;
  private final BytecodeLoader bytecodeLoader;
  private final ImmutableBindings<NamedEvaluableS> evaluables;
  private final NList<ItemS> lexicalEnvironment;
  private final HashMap<CacheKey, BExpr> cache;
  private final HashMap<Hash, String> nameMapping;
  private final HashMap<Hash, Location> locationMapping;

  @Inject
  public SbTranslator(
      BytecodeFactory bytecodeFactory,
      FilePersister filePersister,
      BytecodeLoader bytecodeLoader,
      ImmutableBindings<NamedEvaluableS> evaluables) {
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
      ImmutableBindings<NamedEvaluableS> evaluables,
      NList<ItemS> lexicalEnvironment,
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

  private List<BExpr> translateExprs(List<ExprS> exprs) throws SbTranslatorException {
    return exprs.map(this::translateExpr);
  }

  public BExpr translateExpr(ExprS exprS) throws SbTranslatorException {
    return switch (exprS) {
      case BlobS blobS -> saveLocAndReturn(blobS, translateBlob(blobS));
      case CallS callS -> saveLocAndReturn(callS, translateCall(callS));
      case CombineS combineS -> saveLocAndReturn(combineS, translateCombine(combineS));
      case IntS intS -> saveLocAndReturn(intS, translateInt(intS));
      case OrderS orderS -> saveLocAndReturn(orderS, translateOrder(orderS));
      case SelectS selectS -> saveLocAndReturn(selectS, translateSelect(selectS));
      case StringS stringS -> saveLocAndReturn(stringS, translateString(stringS));
      case InstantiateS instantiateS -> translateInstantiate(instantiateS);
    };
  }

  private BBlob translateBlob(BlobS blobS) throws SbTranslatorException {
    return bytecodeF.blob(sink -> sink.write(blobS.byteString()));
  }

  private BCall translateCall(CallS callS) throws SbTranslatorException {
    var callableB = translateExpr(callS.callee());
    var argsB = (BCombine) translateExpr(callS.args());
    return bytecodeF.call(callableB, argsB);
  }

  private BCombine translateCombine(CombineS combineS) throws SbTranslatorException {
    var elemBs = translateExprs(combineS.elems());
    return bytecodeF.combine(elemBs);
  }

  private BInt translateInt(IntS intS) throws SbTranslatorException {
    return bytecodeF.int_(intS.bigInteger());
  }

  private BExpr translateInstantiate(InstantiateS instantiateS) throws SbTranslatorException {
    var keys = instantiateS.polymorphicS().schema().quantifiedVars().toList();
    var values = instantiateS.typeArgs().map(typeF::translate);
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
    return sbTranslator.translatePolymorphic(instantiateS.polymorphicS());
  }

  private BExpr translatePolymorphic(PolymorphicS polymorphicS) throws SbTranslatorException {
    return switch (polymorphicS) {
      case LambdaS lambdaS -> translateLambda(lambdaS);
      case ReferenceS referenceS -> translateReference(referenceS);
    };
  }

  private BExpr translateLambda(LambdaS lambdaS) throws SbTranslatorException {
    var lambdaB = funcBodySbTranslator(lambdaS).translateExprFunc(lambdaS);
    return saveNalAndReturn("<lambda>", lambdaS, lambdaB);
  }

  private BExpr translateReference(ReferenceS referenceS) throws SbTranslatorException {
    var itemS = lexicalEnvironment.get(referenceS.referencedName());
    if (itemS == null) {
      Maybe<NamedEvaluableS> namedEvaluableS = evaluables.getMaybe(referenceS.referencedName());
      if (namedEvaluableS.isSome()) {
        return switch (namedEvaluableS.get()) {
          case NamedFuncS namedFuncS -> translateNamedFuncWithCache(namedFuncS);
          case NamedValueS namedValueS -> translateNamedValueWithCache(referenceS, namedValueS);
        };
      } else {
        throw new SbTranslatorException("Cannot resolve `" + referenceS.referencedName() + "` at "
            + referenceS.location() + ".");
      }
    } else {
      var evaluationType = typeF.translate(itemS.type());
      var index = BigInteger.valueOf(lexicalEnvironment.indexOf(referenceS.referencedName()));
      return saveNalAndReturn(
          referenceS.referencedName(), referenceS, bytecodeF.reference(evaluationType, index));
    }
  }

  private BExpr translateNamedFuncWithCache(NamedFuncS namedFuncS) throws SbTranslatorException {
    var key = new CacheKey(namedFuncS.name(), typeF.varMap());
    return computeIfAbsent(cache, key, k -> translateNamedFunc(namedFuncS));
  }

  private BExpr translateNamedFunc(NamedFuncS namedFuncS) throws SbTranslatorException {
    var funcB = funcBodySbTranslator(namedFuncS).translateNamedFuncImpl(namedFuncS);
    return saveNalAndReturn(namedFuncS, funcB);
  }

  private SbTranslator funcBodySbTranslator(FuncS funcS) {
    var newEnvironment = nlistWithShadowing(funcS.params().list().appendAll(lexicalEnvironment));
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

  private BExpr translateNamedFuncImpl(NamedFuncS namedFuncS) throws SbTranslatorException {
    return switch (namedFuncS) {
      case AnnotatedFuncS a -> translateAnnotatedFunc(a);
      case NamedExprFuncS e -> translateExprFunc(e);
      case ConstructorS c -> translateConstructor(c);
    };
  }

  private BExpr translateAnnotatedFunc(AnnotatedFuncS annotatedFuncS) throws SbTranslatorException {
    var annotationName = annotatedFuncS.annotation().name();
    return switch (annotationName) {
      case BYTECODE -> fetchFuncBytecode(annotatedFuncS);
      case NATIVE_PURE, NATIVE_IMPURE -> translateNativeFunc(annotatedFuncS);
      default -> throw new SbTranslatorException(
          "Illegal function annotation: " + annotationName + ".");
    };
  }

  private BLambda translateExprFunc(ExprFuncS exprFuncS) throws SbTranslatorException {
    var funcT = typeF.translate(exprFuncS.schema().type());
    var bodyB = translateExpr(exprFuncS.body());
    return bytecodeF.lambda(funcT, bodyB);
  }

  private BNativeFunc translateNativeFunc(AnnotatedFuncS nativeFuncS) throws SbTranslatorException {
    var funcTB = typeF.translate(nativeFuncS.schema().type());
    var annS = nativeFuncS.annotation();
    var jarB = persistNativeJar(annS.location());
    var classBinaryNameB = bytecodeF.string(annS.path().string());
    var isPureB = bytecodeF.bool(annS.name().equals(NATIVE_PURE));
    return bytecodeF.nativeFunc(funcTB, jarB, classBinaryNameB, isPureB);
  }

  private BLambda translateConstructor(ConstructorS constructorS) throws SbTranslatorException {
    var funcTB = typeF.translate(constructorS.schema().type());
    var bodyB = bytecodeF.combine(createReferenceB(funcTB.params()));
    saveLoc(bodyB, constructorS);
    return bytecodeF.lambda(funcTB, bodyB);
  }

  private List<BExpr> createReferenceB(BTupleType paramTs) throws SbTranslatorException {
    return paramTs
        .elements()
        .zipWithIndex()
        .map(tuple -> bytecodeF.reference(tuple.element1(), BigInteger.valueOf(tuple.element2())));
  }

  private BOrder translateOrder(OrderS orderS) throws SbTranslatorException {
    var arrayTB = typeF.translate(orderS.evaluationType());
    var elementsB = translateExprs(orderS.elems());
    return bytecodeF.order(arrayTB, elementsB);
  }

  private BSelect translateSelect(SelectS selectS) throws SbTranslatorException {
    var selectableB = translateExpr(selectS.selectable());
    var structTS = (StructTS) selectS.selectable().evaluationType();
    var indexJ = structTS.fields().indexOf(selectS.field());
    var bigInteger = BigInteger.valueOf(indexJ);
    var indexB = bytecodeF.int_(bigInteger);
    saveLoc(indexB, selectS);
    return bytecodeF.select(selectableB, indexB);
  }

  private BString translateString(StringS stringS) throws SbTranslatorException {
    return bytecodeF.string(stringS.string());
  }

  private BExpr translateNamedValueWithCache(ReferenceS referenceS, NamedValueS namedValueS)
      throws SbTranslatorException {
    var key = new CacheKey(namedValueS.name(), typeF.varMap());
    return computeIfAbsent(
        cache, key, k -> translateNamedValue(referenceS.location(), namedValueS));
  }

  private BExpr translateNamedValue(Location refLocation, NamedValueS namedValueS)
      throws SbTranslatorException {
    return switch (namedValueS) {
      case AnnotatedValueS annotatedValueS -> translateAnnotatedValue(annotatedValueS);
      case NamedExprValueS namedExprValueS -> translateNamedExprValue(refLocation, namedExprValueS);
    };
  }

  private BExpr translateAnnotatedValue(AnnotatedValueS annotatedValueS)
      throws SbTranslatorException {
    var annName = annotatedValueS.annotation().name();
    if (annName.equals(BYTECODE)) {
      return saveNalAndReturn(annotatedValueS, fetchValBytecode(annotatedValueS));
    } else {
      throw new SbTranslatorException("Illegal value annotation: " + q("@" + annName) + ".");
    }
  }

  private BExpr translateNamedExprValue(Location refLocation, NamedExprValueS namedExprValueS)
      throws SbTranslatorException {
    var bResultType = typeF.translate(namedExprValueS.schema().type());
    var bFuncType = bytecodeF.funcType(list(), bResultType);
    var bFunc = bytecodeF.lambda(bFuncType, translateExpr(namedExprValueS.body()));
    saveNal(bFunc, namedExprValueS);
    var bCall = bytecodeF.call(bFunc, bytecodeF.combine(list()));
    saveLoc(bCall, refLocation);
    return bCall;
  }

  // helpers

  private BExpr fetchValBytecode(AnnotatedValueS annotatedValueS) throws SbTranslatorException {
    var bType = typeF.translate(annotatedValueS.schema().type());
    return fetchBytecode(annotatedValueS.annotation(), bType, annotatedValueS.name());
  }

  private BExpr fetchFuncBytecode(AnnotatedFuncS annotatedFuncS) throws SbTranslatorException {
    var bType = typeF.translate(annotatedFuncS.schema().type());
    return fetchBytecode(annotatedFuncS.annotation(), bType, annotatedFuncS.name());
  }

  private BExpr fetchBytecode(AnnotationS annotation, BType bType, String name)
      throws SbTranslatorException {
    var varNameToTypeMap = typeF.varMap().mapKeys(VarS::name);
    var jar = persistNativeJar(annotation.location());
    var bytecode = loadBytecode(name, jar, annotation.path().string(), varNameToTypeMap);
    if (bytecode.isLeft()) {
      throw new SbTranslatorException(annotation.location() + ": " + bytecode.left());
    }
    var bExpr = bytecode.right();
    if (!bExpr.evaluationType().equals(bType)) {
      throw new SbTranslatorException(annotation.location()
          + ": Bytecode provider returned object of wrong type "
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

  private static record CacheKey(String name, Map<VarS, BType> varMap) {}
}
