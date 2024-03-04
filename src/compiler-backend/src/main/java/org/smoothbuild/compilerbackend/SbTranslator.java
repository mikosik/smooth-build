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
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.filesystem.space.FilePath;
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
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.LambdaB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.load.BytecodeLoader;
import org.smoothbuild.virtualmachine.bytecode.load.FilePersister;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class SbTranslator {
  private final ChainingBytecodeFactory bytecodeF;
  private final TypeSbTranslator typeF;
  private final FilePersister filePersister;
  private final BytecodeLoader bytecodeLoader;
  private final ImmutableBindings<NamedEvaluableS> evaluables;
  private final NList<ItemS> lexicalEnvironment;
  private final HashMap<CacheKey, ExprB> cache;
  private final HashMap<Hash, String> nameMapping;
  private final HashMap<Hash, Location> locationMapping;

  @Inject
  public SbTranslator(
      BytecodeF bytecodeF,
      FilePersister filePersister,
      BytecodeLoader bytecodeLoader,
      ImmutableBindings<NamedEvaluableS> evaluables) {
    this(
        new ChainingBytecodeFactory(bytecodeF),
        new TypeSbTranslator(new ChainingBytecodeFactory(bytecodeF), map()),
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
      HashMap<CacheKey, ExprB> cache,
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

  private List<ExprB> translateExprs(List<ExprS> exprs) throws SbTranslatorException {
    return exprs.map(this::translateExpr);
  }

  public ExprB translateExpr(ExprS exprS) throws SbTranslatorException {
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

  private BlobB translateBlob(BlobS blobS) throws SbTranslatorException {
    return bytecodeF.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallB translateCall(CallS callS) throws SbTranslatorException {
    var callableB = translateExpr(callS.callee());
    var argsB = (CombineB) translateExpr(callS.args());
    return bytecodeF.call(callableB, argsB);
  }

  private CombineB translateCombine(CombineS combineS) throws SbTranslatorException {
    var elemBs = translateExprs(combineS.elems());
    return bytecodeF.combine(elemBs);
  }

  private IntB translateInt(IntS intS) throws SbTranslatorException {
    return bytecodeF.int_(intS.bigInteger());
  }

  private ExprB translateInstantiate(InstantiateS instantiateS) throws SbTranslatorException {
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

  private ExprB translatePolymorphic(PolymorphicS polymorphicS) throws SbTranslatorException {
    return switch (polymorphicS) {
      case LambdaS lambdaS -> translateLambda(lambdaS);
      case ReferenceS referenceS -> translateReference(referenceS);
    };
  }

  private ExprB translateLambda(LambdaS lambdaS) throws SbTranslatorException {
    var lambdaB = funcBodySbTranslator(lambdaS).translateExprFunc(lambdaS);
    return saveNalAndReturn("<lambda>", lambdaS, lambdaB);
  }

  private ExprB translateReference(ReferenceS referenceS) throws SbTranslatorException {
    var itemS = lexicalEnvironment.get(referenceS.name());
    if (itemS == null) {
      Maybe<NamedEvaluableS> namedEvaluableS = evaluables.getMaybe(referenceS.name());
      if (namedEvaluableS.isSome()) {
        return switch (namedEvaluableS.get()) {
          case NamedFuncS namedFuncS -> translateNamedFuncWithCache(namedFuncS);
          case NamedValueS namedValueS -> translateNamedValueWithCache(referenceS, namedValueS);
        };
      } else {
        throw new SbTranslatorException(
            "Cannot resolve `" + referenceS.name() + "` at " + referenceS.location() + ".");
      }
    } else {
      var evaluationType = typeF.translate(itemS.type());
      var index = BigInteger.valueOf(lexicalEnvironment.indexOf(referenceS.name()));
      return saveNalAndReturn(referenceS, bytecodeF.var(evaluationType, index));
    }
  }

  private ExprB translateNamedFuncWithCache(NamedFuncS namedFuncS) throws SbTranslatorException {
    var key = new CacheKey(namedFuncS.name(), typeF.varMap());
    return computeIfAbsent(cache, key, k -> translateNamedFunc(namedFuncS));
  }

  private ExprB translateNamedFunc(NamedFuncS namedFuncS) throws SbTranslatorException {
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

  private ExprB translateNamedFuncImpl(NamedFuncS namedFuncS) throws SbTranslatorException {
    return switch (namedFuncS) {
      case AnnotatedFuncS a -> translateAnnotatedFunc(a);
      case NamedExprFuncS e -> translateExprFunc(e);
      case ConstructorS c -> translateConstructor(c);
    };
  }

  private ExprB translateAnnotatedFunc(AnnotatedFuncS annotatedFuncS) throws SbTranslatorException {
    var annotationName = annotatedFuncS.annotation().name();
    return switch (annotationName) {
      case BYTECODE -> fetchFuncBytecode(annotatedFuncS);
      case NATIVE_PURE, NATIVE_IMPURE -> translateNativeFunc(annotatedFuncS);
      default -> throw new SbTranslatorException(
          "Illegal function annotation: " + annotationName + ".");
    };
  }

  private LambdaB translateExprFunc(ExprFuncS exprFuncS) throws SbTranslatorException {
    var funcT = typeF.translate(exprFuncS.schema().type());
    var bodyB = translateExpr(exprFuncS.body());
    return bytecodeF.lambda(funcT, bodyB);
  }

  private NativeFuncB translateNativeFunc(AnnotatedFuncS nativeFuncS) throws SbTranslatorException {
    var funcTB = typeF.translate(nativeFuncS.schema().type());
    var annS = nativeFuncS.annotation();
    var jarB = persistNativeJar(annS.location());
    var classBinaryNameB = bytecodeF.string(annS.path().string());
    var isPureB = bytecodeF.bool(annS.name().equals(NATIVE_PURE));
    return bytecodeF.nativeFunc(funcTB, jarB, classBinaryNameB, isPureB);
  }

  private LambdaB translateConstructor(ConstructorS constructorS) throws SbTranslatorException {
    var funcTB = typeF.translate(constructorS.schema().type());
    var bodyB = bytecodeF.combine(createRefsB(funcTB.params()));
    saveLoc(bodyB, constructorS);
    return bytecodeF.lambda(funcTB, bodyB);
  }

  private List<ExprB> createRefsB(TupleTB paramTs) throws SbTranslatorException {
    return paramTs
        .elements()
        .zipWithIndex()
        .map(tuple -> bytecodeF.var(tuple.element1(), BigInteger.valueOf(tuple.element2())));
  }

  private OrderB translateOrder(OrderS orderS) throws SbTranslatorException {
    var arrayTB = typeF.translate(orderS.evaluationType());
    var elementsB = translateExprs(orderS.elems());
    return bytecodeF.order(arrayTB, elementsB);
  }

  private SelectB translateSelect(SelectS selectS) throws SbTranslatorException {
    var selectableB = translateExpr(selectS.selectable());
    var structTS = (StructTS) selectS.selectable().evaluationType();
    var indexJ = structTS.fields().indexOf(selectS.field());
    var bigInteger = BigInteger.valueOf(indexJ);
    var indexB = bytecodeF.int_(bigInteger);
    saveLoc(indexB, selectS);
    return bytecodeF.select(selectableB, indexB);
  }

  private StringB translateString(StringS stringS) throws SbTranslatorException {
    return bytecodeF.string(stringS.string());
  }

  private ExprB translateNamedValueWithCache(ReferenceS referenceS, NamedValueS namedValueS)
      throws SbTranslatorException {
    var key = new CacheKey(namedValueS.name(), typeF.varMap());
    return computeIfAbsent(
        cache, key, k -> translateNamedValue(referenceS.location(), namedValueS));
  }

  private ExprB translateNamedValue(Location refLocation, NamedValueS namedValueS)
      throws SbTranslatorException {
    return switch (namedValueS) {
      case AnnotatedValueS annotatedValueS -> translateAnnotatedValue(annotatedValueS);
      case NamedExprValueS namedExprValueS -> translateNamedExprValue(refLocation, namedExprValueS);
    };
  }

  private ExprB translateAnnotatedValue(AnnotatedValueS annotatedValueS)
      throws SbTranslatorException {
    var annName = annotatedValueS.annotation().name();
    if (annName.equals(BYTECODE)) {
      return saveNalAndReturn(annotatedValueS, fetchValBytecode(annotatedValueS));
    } else {
      throw new SbTranslatorException("Illegal value annotation: " + q("@" + annName) + ".");
    }
  }

  private ExprB translateNamedExprValue(Location refLocation, NamedExprValueS namedExprValueS)
      throws SbTranslatorException {
    var resultTB = typeF.translate(namedExprValueS.schema().type());
    var funcTB = bytecodeF.funcT(list(), resultTB);
    var funcB = bytecodeF.lambda(funcTB, translateExpr(namedExprValueS.body()));
    saveNal(funcB, namedExprValueS);
    var call = bytecodeF.call(funcB, bytecodeF.combine(list()));
    saveLoc(call, refLocation);
    return call;
  }

  // helpers

  private ExprB fetchValBytecode(AnnotatedValueS annotatedValueS) throws SbTranslatorException {
    var typeB = typeF.translate(annotatedValueS.schema().type());
    return fetchBytecode(annotatedValueS.annotation(), typeB, annotatedValueS.name());
  }

  private ExprB fetchFuncBytecode(AnnotatedFuncS annotatedFuncS) throws SbTranslatorException {
    var typeB = typeF.translate(annotatedFuncS.schema().type());
    return fetchBytecode(annotatedFuncS.annotation(), typeB, annotatedFuncS.name());
  }

  private ExprB fetchBytecode(AnnotationS annotation, TypeB typeB, String name)
      throws SbTranslatorException {
    var varNameToTypeMap = typeF.varMap().mapKeys(VarS::name);
    var jar = persistNativeJar(annotation.location());
    var bytecode = loadBytecode(name, jar, annotation.path().string(), varNameToTypeMap);
    if (bytecode.isLeft()) {
      throw new SbTranslatorException(annotation.location() + ": " + bytecode.left());
    }
    var bytecodeB = bytecode.right();
    if (!bytecodeB.evaluationType().equals(typeB)) {
      throw new SbTranslatorException(annotation.location()
          + ": Bytecode provider returned object of wrong type "
          + bytecodeB.evaluationType().q()
          + " when " + q(name) + " is declared as " + typeB.q() + ".");
    }
    return bytecodeB;
  }

  private Either<String, ExprB> loadBytecode(
      String name, BlobB jar, String path, Map<String, TypeB> varNameToTypeMap)
      throws SbTranslatorException {
    try {
      return bytecodeLoader.load(name, jar, path, varNameToTypeMap);
    } catch (BytecodeException e) {
      throw new SbTranslatorException(e);
    }
  }

  private BlobB persistNativeJar(Location location) throws SbTranslatorException {
    var filePath = filePathOf(location).withExtension("jar");
    try {
      return filePersister.persist(filePath);
    } catch (BytecodeException e) {
      var message = location + ": Error persisting native jar %s.".formatted(filePath.q());
      throw new SbTranslatorException(message, e);
    }
  }

  private static FilePath filePathOf(Location location) throws SbTranslatorException {
    if (location instanceof FileLocation sourceLocation) {
      return sourceLocation.file();
    } else {
      throw new SbTranslatorException(location
          + ": Error loading native jar: Impossible to infer native file name for location "
          + location + ".");
    }
  }

  // helpers for saving names and locations

  private ExprB saveNalAndReturn(Nal nal, ExprB exprB) {
    saveNal(exprB, nal);
    return exprB;
  }

  private ExprB saveNalAndReturn(String name, Located located, ExprB exprB) {
    saveNal(exprB, name, located);
    return exprB;
  }

  private ExprB saveLocAndReturn(Located located, ExprB exprB) {
    saveLoc(exprB, located);
    return exprB;
  }

  private void saveNal(ExprB exprB, Nal nal) {
    saveNal(exprB, nal.name(), nal);
  }

  private void saveNal(ExprB exprB, String name, Located located) {
    nameMapping.put(exprB.hash(), name);
    saveLoc(exprB, located);
  }

  private void saveLoc(ExprB exprB, Located located) {
    saveLoc(exprB, located.location());
  }

  private void saveLoc(ExprB exprB, Location location) {
    locationMapping.put(exprB.hash(), location);
  }

  private static record CacheKey(String name, Map<VarS, TypeB> varMap) {}
}
