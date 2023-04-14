package org.smoothbuild.compile.sb;

import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.computeIfAbsent;
import static org.smoothbuild.util.collect.Maps.mapKeys;
import static org.smoothbuild.util.collect.Maps.override;
import static org.smoothbuild.util.collect.Maps.zip;
import static org.smoothbuild.util.collect.NList.nlist;
import static org.smoothbuild.util.collect.NList.nlistWithShadowing;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.compile.fs.lang.base.Nal;
import org.smoothbuild.compile.fs.lang.base.location.FileLocation;
import org.smoothbuild.compile.fs.lang.base.location.Located;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.define.AnnotatedFuncS;
import org.smoothbuild.compile.fs.lang.define.AnnotatedValueS;
import org.smoothbuild.compile.fs.lang.define.AnnotationS;
import org.smoothbuild.compile.fs.lang.define.BlobS;
import org.smoothbuild.compile.fs.lang.define.CallS;
import org.smoothbuild.compile.fs.lang.define.CombineS;
import org.smoothbuild.compile.fs.lang.define.ConstructorS;
import org.smoothbuild.compile.fs.lang.define.ExprFuncS;
import org.smoothbuild.compile.fs.lang.define.ExprS;
import org.smoothbuild.compile.fs.lang.define.FuncS;
import org.smoothbuild.compile.fs.lang.define.InstantiateS;
import org.smoothbuild.compile.fs.lang.define.IntS;
import org.smoothbuild.compile.fs.lang.define.ItemS;
import org.smoothbuild.compile.fs.lang.define.LambdaS;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.fs.lang.define.NamedExprFuncS;
import org.smoothbuild.compile.fs.lang.define.NamedExprValueS;
import org.smoothbuild.compile.fs.lang.define.NamedFuncS;
import org.smoothbuild.compile.fs.lang.define.NamedValueS;
import org.smoothbuild.compile.fs.lang.define.OrderS;
import org.smoothbuild.compile.fs.lang.define.PolymorphicS;
import org.smoothbuild.compile.fs.lang.define.ReferenceS;
import org.smoothbuild.compile.fs.lang.define.SelectS;
import org.smoothbuild.compile.fs.lang.define.StringS;
import org.smoothbuild.compile.fs.lang.type.AnnotationNames;
import org.smoothbuild.compile.fs.lang.type.ArrayTS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.StructTS;
import org.smoothbuild.compile.fs.lang.type.TupleTS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.ExprFuncB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

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
    this(bytecodeF,
        new TypeSbTranslator(bytecodeF, ImmutableMap.of()),
        fileLoader,
        bytecodeLoader,
        evaluables,
        nlist(),
        new HashMap<>(),
        new HashMap<>(),
        new HashMap<>());
  }

  public SbTranslator(
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

  private ImmutableList<ExprB> translateExprs(ImmutableList<ExprS> exprs) {
    return map(exprs, this::translateExpr);
  }

  public ExprB translateExpr(ExprS exprS) {
    // @formatter:off
    return switch (exprS) {
      case BlobS        blobS        -> saveLocAndReturn(blobS,     translateBlob(blobS));
      case CallS        callS        -> saveLocAndReturn(callS,     translateCall(callS));
      case CombineS     combineS     -> saveLocAndReturn(combineS,  translateCombine(combineS));
      case IntS         intS         -> saveLocAndReturn(intS,      translateInt(intS));
      case OrderS       orderS       -> saveLocAndReturn(orderS,    translateOrder(orderS));
      case SelectS      selectS      -> saveLocAndReturn(selectS,   translateSelect(selectS));
      case StringS      stringS      -> saveLocAndReturn(stringS,   translateString(stringS));
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

  public ExprB translatePolymorphic(PolymorphicS polymorphicS) {
    return switch (polymorphicS) {
      case LambdaS lambdaS -> translateLambda(lambdaS);
      case ReferenceS referenceS -> translateReference(referenceS);
    };
  }

  private ExprB translateLambda(LambdaS lambdaS) {
    var funcB = funcBodySbTranslator(lambdaS)
        .translateLambdaImpl(lambdaS);
    return saveLocAndReturn(lambdaS, funcB);
  }

  private ExprB translateLambdaImpl(LambdaS lambdaS) {
    var exprFuncB = translateExprFunc(lambdaS);
    saveNal(exprFuncB, "<lambda>", lambdaS);
    return bytecodeF.closurize(exprFuncB);
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
        throw new SbTranslatorExc(
            "Cannot resolve `" + referenceS.name() + "` at " + referenceS.location() + ".");
      }
    } else {
      var index = environment.indexOf(referenceS.name());
      return saveNalAndReturn(
          referenceS, bytecodeF.reference(translateT(itemS.type()), BigInteger.valueOf(index)));
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
      case AnnotationNames.BYTECODE -> fetchFuncBytecode(annotatedFuncS);
      case AnnotationNames.NATIVE_PURE, AnnotationNames.NATIVE_IMPURE -> translateNativeFunc(annotatedFuncS);
      default -> throw new SbTranslatorExc("Illegal function annotation: " + annotationName + ".");
    };
  }

  private ExprFuncB translateExprFunc(ExprFuncS exprFuncS) {
    return bytecodeF.exprFunc(
        translateT(exprFuncS.schema().type()),
        translateExpr(exprFuncS.body()));
  }

  private NativeFuncB translateNativeFunc(AnnotatedFuncS nativeFuncS) {
    var funcTB = translateT(nativeFuncS.schema().type());
    var annS = nativeFuncS.annotation();
    var jarB = loadNativeJar(annS.location());
    var classBinaryNameB = bytecodeF.string(annS.path().string());
    var isPureB = bytecodeF.bool(annS.name().equals(AnnotationNames.NATIVE_PURE));
    return bytecodeF.nativeFunc(funcTB, jarB, classBinaryNameB, isPureB);
  }

  private ExprFuncB translateConstructor(ConstructorS constructorS) {
    var funcTB = translateT(constructorS.schema().type());
    var bodyB = bytecodeF.combine(createRefsB(funcTB.params()));
    saveLoc(bodyB, constructorS);
    return bytecodeF.exprFunc(funcTB, bodyB);
  }

  private ImmutableList<ExprB> createRefsB(TupleTB paramTs) {
    Builder<ExprB> builder = ImmutableList.builder();
    for (int i = 0; i < paramTs.size(); i++) {
      var closedT = paramTs.get(i);
      builder.add(bytecodeF.reference(closedT, BigInteger.valueOf(i)));
    }
    return builder.build();
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
    if (annName.equals(AnnotationNames.BYTECODE)) {
      return saveNalAndReturn(annotatedValueS, fetchValBytecode(annotatedValueS));
    } else {
      throw new SbTranslatorExc("Illegal value annotation: " + q("@" + annName) + ".");
    }
  }

  private ExprB translateNamedExprValue(Location refLocation, NamedExprValueS namedExprValueS) {
    var funcTB = bytecodeF.funcT(list(), translateT(namedExprValueS.schema().type()));
    var funcB = bytecodeF.exprFunc(funcTB, translateExpr(namedExprValueS.body()));
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
    var bytecodeTry = bytecodeLoader.load(name, jar, annotation.path().string(), varNameToTypeMap);
    if (!bytecodeTry.isPresent()) {
      throw new SbTranslatorExc(annotation.location() + ": " + bytecodeTry.error());
    }
    var bytecodeB = bytecodeTry.result();
    if (!bytecodeB.evaluationT().equals(typeB)) {
      throw new SbTranslatorExc(annotation.location()
          + ": Bytecode provider returned object of wrong type " + bytecodeB.evaluationT().q()
          + " when " + q(name) + " is declared as " + typeB.q() + ".");
    }
    return bytecodeB;
  }

  private BlobB loadNativeJar(Location location) {
    var filePath = filePathOf(location).withExtension("jar");
    try {
      return fileLoader.load(filePath);
    } catch (FileNotFoundException e) {
      var message = location + ": Error loading native jar: File %s doesn't exist."
          .formatted(filePath.q());
      throw new SbTranslatorExc(message);
    }
  }

  private static FilePath filePathOf(Location location) {
    if (location instanceof FileLocation sourceLocation) {
      return sourceLocation.file();
    } else {
      throw new SbTranslatorExc(location
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
