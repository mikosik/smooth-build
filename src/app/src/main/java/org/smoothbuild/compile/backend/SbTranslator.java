package org.smoothbuild.compile.backend;

import static org.smoothbuild.common.Strings.q;
import static org.smoothbuild.common.collect.Iterables.intIterable;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Lists.concat;
import static org.smoothbuild.common.collect.Lists.map;
import static org.smoothbuild.common.collect.Maps.computeIfAbsent;
import static org.smoothbuild.common.collect.Maps.mapKeys;
import static org.smoothbuild.common.collect.Maps.override;
import static org.smoothbuild.common.collect.Maps.zip;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.common.collect.NList.nlistWithShadowing;
import static org.smoothbuild.compile.frontend.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compile.frontend.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compile.frontend.lang.type.AnnotationNames.NATIVE_PURE;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jakarta.inject.Inject;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compile.frontend.lang.base.Nal;
import org.smoothbuild.compile.frontend.lang.base.location.FileLocation;
import org.smoothbuild.compile.frontend.lang.base.location.Located;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.define.AnnotatedFuncS;
import org.smoothbuild.compile.frontend.lang.define.AnnotatedValueS;
import org.smoothbuild.compile.frontend.lang.define.AnnotationS;
import org.smoothbuild.compile.frontend.lang.define.BlobS;
import org.smoothbuild.compile.frontend.lang.define.CallS;
import org.smoothbuild.compile.frontend.lang.define.CombineS;
import org.smoothbuild.compile.frontend.lang.define.ConstructorS;
import org.smoothbuild.compile.frontend.lang.define.ExprFuncS;
import org.smoothbuild.compile.frontend.lang.define.ExprS;
import org.smoothbuild.compile.frontend.lang.define.FuncS;
import org.smoothbuild.compile.frontend.lang.define.InstantiateS;
import org.smoothbuild.compile.frontend.lang.define.IntS;
import org.smoothbuild.compile.frontend.lang.define.ItemS;
import org.smoothbuild.compile.frontend.lang.define.LambdaS;
import org.smoothbuild.compile.frontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.frontend.lang.define.NamedExprFuncS;
import org.smoothbuild.compile.frontend.lang.define.NamedExprValueS;
import org.smoothbuild.compile.frontend.lang.define.NamedFuncS;
import org.smoothbuild.compile.frontend.lang.define.NamedValueS;
import org.smoothbuild.compile.frontend.lang.define.OrderS;
import org.smoothbuild.compile.frontend.lang.define.PolymorphicS;
import org.smoothbuild.compile.frontend.lang.define.ReferenceS;
import org.smoothbuild.compile.frontend.lang.define.SelectS;
import org.smoothbuild.compile.frontend.lang.define.StringS;
import org.smoothbuild.compile.frontend.lang.type.ArrayTS;
import org.smoothbuild.compile.frontend.lang.type.FuncTS;
import org.smoothbuild.compile.frontend.lang.type.StructTS;
import org.smoothbuild.compile.frontend.lang.type.TupleTS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;
import org.smoothbuild.compile.frontend.lang.type.VarS;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.LambdaB;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.load.BytecodeLoader;
import org.smoothbuild.vm.bytecode.load.FileLoader;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class SbTranslator {
  private final BytecodeF bytecodeF;
  private final TypeSbTranslator typeSbTranslator;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;
  private final ImmutableBindings<NamedEvaluableS> evaluables;
  private final NList<ItemS> environment;
  private final Map<CacheKey, ExprB> cache;
  private final Map<Hash, String> nameMapping;
  private final Map<Hash, Location> locationMapping;

  @Inject
  public SbTranslator(
      BytecodeF bytecodeF,
      FileLoader fileLoader,
      BytecodeLoader bytecodeLoader,
      ImmutableBindings<NamedEvaluableS> evaluables) {
    this(
        bytecodeF,
        new TypeSbTranslator(bytecodeF, ImmutableMap.of()),
        fileLoader,
        bytecodeLoader,
        evaluables,
        nlist(),
        new HashMap<>(),
        new HashMap<>(),
        new HashMap<>());
  }

  private SbTranslator(
      BytecodeF bytecodeF,
      TypeSbTranslator typeSbTranslator,
      FileLoader fileLoader,
      BytecodeLoader bytecodeLoader,
      ImmutableBindings<NamedEvaluableS> evaluables,
      NList<ItemS> environment,
      Map<CacheKey, ExprB> cache,
      Map<Hash, String> nameMapping,
      Map<Hash, Location> locationMapping) {
    this.bytecodeF = bytecodeF;
    this.typeSbTranslator = typeSbTranslator;
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
    this.evaluables = evaluables;
    this.environment = environment;
    this.cache = cache;
    this.nameMapping = nameMapping;
    this.locationMapping = locationMapping;
  }

  public BsMapping bsMapping() {
    return new BsMapping(nameMapping, locationMapping);
  }

  private List<ExprB> translateExprs(ImmutableList<ExprS> exprs) {
    return list(exprs).map(this::translateExpr);
  }

  public ExprB translateExpr(ExprS exprS) {
    // @formatter:off
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
    // @formatter:on
  }

  private BlobB translateBlob(BlobS blobS) {
    return bytecodeF.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallB translateCall(CallS callS) {
    var callableB = translateExpr(callS.callee());
    var argsB = (CombineB) translateExpr(callS.args());
    return bytecodeF.call(callableB, argsB);
  }

  private CombineB translateCombine(CombineS combineS) {
    var elemBs = translateExprs(combineS.elems());
    return bytecodeF.combine(elemBs);
  }

  private IntB translateInt(IntS intS) {
    return bytecodeF.int_(intS.bigInteger());
  }

  private ExprB translateInstantiate(InstantiateS instantiateS) {
    var keys = instantiateS.polymorphicS().schema().quantifiedVars().asList();
    var values = map(instantiateS.typeArgs(), typeSbTranslator::translate);
    var instantiatedVarMap = zip(keys, values);
    var varMap = override(instantiatedVarMap, typeSbTranslator.varMap());
    var newTypeSbTranslator = new TypeSbTranslator(bytecodeF, varMap);
    var sbTranslator = new SbTranslator(
        bytecodeF,
        newTypeSbTranslator,
        fileLoader,
        bytecodeLoader,
        evaluables,
        environment,
        cache,
        nameMapping,
        locationMapping);
    return sbTranslator.translatePolymorphic(instantiateS.polymorphicS());
  }

  private ExprB translatePolymorphic(PolymorphicS polymorphicS) {
    return switch (polymorphicS) {
      case LambdaS lambdaS -> translateLambda(lambdaS);
      case ReferenceS referenceS -> translateReference(referenceS);
    };
  }

  private ExprB translateLambda(LambdaS lambdaS) {
    var lambdaB = funcBodySbTranslator(lambdaS).translateExprFunc(lambdaS);
    return saveNalAndReturn("<lambda>", lambdaS, lambdaB);
  }

  private ExprB translateReference(ReferenceS referenceS) {
    var itemS = environment.get(referenceS.name());
    if (itemS == null) {
      Optional<NamedEvaluableS> namedEvaluableS = evaluables.getOptional(referenceS.name());
      if (namedEvaluableS.isPresent()) {
        return switch (namedEvaluableS.get()) {
          case NamedFuncS namedFuncS -> translateNamedFuncWithCache(namedFuncS);
          case NamedValueS namedValueS -> translateNamedValueWithCache(referenceS, namedValueS);
        };
      } else {
        throw new SbTranslatorException(
            "Cannot resolve `" + referenceS.name() + "` at " + referenceS.location() + ".");
      }
    } else {
      var index = environment.indexOf(referenceS.name());
      return saveNalAndReturn(
          referenceS, bytecodeF.var(translateT(itemS.type()), BigInteger.valueOf(index)));
    }
  }

  private ExprB translateNamedFuncWithCache(NamedFuncS namedFuncS) {
    var key = new CacheKey(namedFuncS.name(), typeSbTranslator.varMap());
    return computeIfAbsent(cache, key, name -> translateNamedFunc(namedFuncS));
  }

  private ExprB translateNamedFunc(NamedFuncS namedFuncS) {
    var funcB = funcBodySbTranslator(namedFuncS).translateNamedFuncImpl(namedFuncS);
    return saveNalAndReturn(namedFuncS, funcB);
  }

  private SbTranslator funcBodySbTranslator(FuncS funcS) {
    var newEnvironment = nlistWithShadowing(concat(funcS.params(), environment));
    return new SbTranslator(
        bytecodeF,
        typeSbTranslator,
        fileLoader,
        bytecodeLoader,
        evaluables,
        newEnvironment,
        cache,
        nameMapping,
        locationMapping);
  }

  private ExprB translateNamedFuncImpl(NamedFuncS namedFuncS) {
    return switch (namedFuncS) {
      case AnnotatedFuncS a -> translateAnnotatedFunc(a);
      case NamedExprFuncS e -> translateExprFunc(e);
      case ConstructorS c -> translateConstructor(c);
    };
  }

  private ExprB translateAnnotatedFunc(AnnotatedFuncS annotatedFuncS) {
    var annotationName = annotatedFuncS.annotation().name();
    return switch (annotationName) {
      case BYTECODE -> fetchFuncBytecode(annotatedFuncS);
      case NATIVE_PURE, NATIVE_IMPURE -> translateNativeFunc(annotatedFuncS);
      default -> throw new SbTranslatorException(
          "Illegal function annotation: " + annotationName + ".");
    };
  }

  private LambdaB translateExprFunc(ExprFuncS exprFuncS) {
    return bytecodeF.lambda(translateT(exprFuncS.schema().type()), translateExpr(exprFuncS.body()));
  }

  private NativeFuncB translateNativeFunc(AnnotatedFuncS nativeFuncS) {
    var funcTB = translateT(nativeFuncS.schema().type());
    var annS = nativeFuncS.annotation();
    var jarB = loadNativeJar(annS.location());
    var classBinaryNameB = bytecodeF.string(annS.path().string());
    var isPureB = bytecodeF.bool(annS.name().equals(NATIVE_PURE));
    return bytecodeF.nativeFunc(funcTB, jarB, classBinaryNameB, isPureB);
  }

  private LambdaB translateConstructor(ConstructorS constructorS) {
    var funcTB = translateT(constructorS.schema().type());
    var bodyB = bytecodeF.combine(createRefsB(funcTB.params()));
    saveLoc(bodyB, constructorS);
    return bytecodeF.lambda(funcTB, bodyB);
  }

  private List<ExprB> createRefsB(TupleTB paramTs) {
    return paramTs
        .elements()
        .zip(intIterable(0), (typeB, i) -> bytecodeF.var(typeB, BigInteger.valueOf(i)));
  }

  private OrderB translateOrder(OrderS orderS) {
    var arrayTB = translateT(orderS.evaluationT());
    var elemsB = translateExprs(orderS.elems());
    return bytecodeF.order(arrayTB, elemsB);
  }

  private SelectB translateSelect(SelectS selectS) {
    var selectableB = translateExpr(selectS.selectable());
    var structTS = (StructTS) selectS.selectable().evaluationT();
    var indexJ = structTS.fields().indexOf(selectS.field());
    var bigInteger = BigInteger.valueOf(indexJ);
    var indexB = bytecodeF.int_(bigInteger);
    saveLoc(indexB, selectS);
    return bytecodeF.select(selectableB, indexB);
  }

  private StringB translateString(StringS stringS) {
    return bytecodeF.string(stringS.string());
  }

  private ExprB translateNamedValueWithCache(ReferenceS referenceS, NamedValueS namedValueS) {
    var key = new CacheKey(namedValueS.name(), typeSbTranslator.varMap());
    return computeIfAbsent(
        cache, key, name -> translateNamedValue(referenceS.location(), namedValueS));
  }

  private ExprB translateNamedValue(Location refLocation, NamedValueS namedValueS) {
    return switch (namedValueS) {
      case AnnotatedValueS annotatedValueS -> translateAnnotatedValue(annotatedValueS);
      case NamedExprValueS namedExprValueS -> translateNamedExprValue(refLocation, namedExprValueS);
    };
  }

  private ExprB translateAnnotatedValue(AnnotatedValueS annotatedValueS) {
    var annName = annotatedValueS.annotation().name();
    if (annName.equals(BYTECODE)) {
      return saveNalAndReturn(annotatedValueS, fetchValBytecode(annotatedValueS));
    } else {
      throw new SbTranslatorException("Illegal value annotation: " + q("@" + annName) + ".");
    }
  }

  private ExprB translateNamedExprValue(Location refLocation, NamedExprValueS namedExprValueS) {
    var funcTB = bytecodeF.funcT(list(), translateT(namedExprValueS.schema().type()));
    var funcB = bytecodeF.lambda(funcTB, translateExpr(namedExprValueS.body()));
    saveNal(funcB, namedExprValueS);
    var call = bytecodeF.call(funcB, bytecodeF.combine(list()));
    saveLoc(call, refLocation);
    return call;
  }

  // helpers

  private ExprB fetchValBytecode(AnnotatedValueS annotatedValueS) {
    var typeB = translateT(annotatedValueS.schema().type());
    return fetchBytecode(annotatedValueS.annotation(), typeB, annotatedValueS.name());
  }

  private ExprB fetchFuncBytecode(AnnotatedFuncS annotatedFuncS) {
    var typeB = translateT(annotatedFuncS.schema().type());
    return fetchBytecode(annotatedFuncS.annotation(), typeB, annotatedFuncS.name());
  }

  private ExprB fetchBytecode(AnnotationS annotation, TypeB typeB, String name) {
    var varNameToTypeMap = mapKeys(typeSbTranslator.varMap(), VarS::name);
    var jar = loadNativeJar(annotation.location());
    var bytecode = bytecodeLoader.load(name, jar, annotation.path().string(), varNameToTypeMap);
    if (!bytecode.isRight()) {
      throw new SbTranslatorException(annotation.location() + ": " + bytecode.getLeft());
    }
    var bytecodeB = bytecode.get();
    if (!bytecodeB.evaluationT().equals(typeB)) {
      throw new SbTranslatorException(annotation.location()
          + ": Bytecode provider returned object of wrong type "
          + bytecodeB.evaluationT().q()
          + " when " + q(name) + " is declared as " + typeB.q() + ".");
    }
    return bytecodeB;
  }

  private BlobB loadNativeJar(Location location) {
    var filePath = filePathOf(location).withExtension("jar");
    try {
      return fileLoader.load(filePath);
    } catch (FileNotFoundException e) {
      var message =
          location + ": Error loading native jar: File %s doesn't exist.".formatted(filePath.q());
      throw new SbTranslatorException(message);
    }
  }

  private static FilePath filePathOf(Location location) {
    if (location instanceof FileLocation sourceLocation) {
      return sourceLocation.file();
    } else {
      throw new SbTranslatorException(location
          + ": Error loading native jar: Impossible to infer native file name for location "
          + location + ".");
    }
  }

  private TypeB translateT(TypeS typeS) {
    return typeSbTranslator.translate(typeS);
  }

  private FuncTB translateT(FuncTS funcTS) {
    return typeSbTranslator.translate(funcTS);
  }

  private TupleTB translateT(TupleTS tupleTS) {
    return typeSbTranslator.translate(tupleTS);
  }

  private ArrayTB translateT(ArrayTS arrayTS) {
    return typeSbTranslator.translate(arrayTS);
  }

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

  private static record CacheKey(String name, ImmutableMap<VarS, TypeB> varMap) {}
}
