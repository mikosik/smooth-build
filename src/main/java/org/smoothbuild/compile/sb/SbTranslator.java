package org.smoothbuild.compile.sb;

import static org.smoothbuild.compile.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.computeIfAbsent;
import static org.smoothbuild.util.collect.Maps.mapKeys;
import static org.smoothbuild.util.collect.Maps.mapValues;
import static org.smoothbuild.util.collect.NList.nlist;
import static org.smoothbuild.util.collect.NList.nlistWithShadowing;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.value.BlobB;
import org.smoothbuild.bytecode.expr.value.DefinedFuncB;
import org.smoothbuild.bytecode.expr.value.IntB;
import org.smoothbuild.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.bytecode.expr.value.StringB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.value.ArrayTB;
import org.smoothbuild.bytecode.type.value.FuncTB;
import org.smoothbuild.bytecode.type.value.TupleTB;
import org.smoothbuild.bytecode.type.value.TypeB;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Located;
import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.lang.define.AnnFuncS;
import org.smoothbuild.compile.lang.define.AnnS;
import org.smoothbuild.compile.lang.define.AnnValueS;
import org.smoothbuild.compile.lang.define.AnonFuncS;
import org.smoothbuild.compile.lang.define.BlobS;
import org.smoothbuild.compile.lang.define.CallS;
import org.smoothbuild.compile.lang.define.ConstructorS;
import org.smoothbuild.compile.lang.define.DefFuncS;
import org.smoothbuild.compile.lang.define.DefValueS;
import org.smoothbuild.compile.lang.define.EvaluableRefS;
import org.smoothbuild.compile.lang.define.ExprFuncS;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.lang.define.FuncS;
import org.smoothbuild.compile.lang.define.IntS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.MonoizableS;
import org.smoothbuild.compile.lang.define.MonoizeS;
import org.smoothbuild.compile.lang.define.NamedFuncS;
import org.smoothbuild.compile.lang.define.NamedValueS;
import org.smoothbuild.compile.lang.define.OrderS;
import org.smoothbuild.compile.lang.define.ParamRefS;
import org.smoothbuild.compile.lang.define.SelectS;
import org.smoothbuild.compile.lang.define.StringS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TupleTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.util.collect.Maps;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class SbTranslator {
  private final BytecodeF bytecodeF;
  private final TypeSbTranslator typeSbTranslator;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;
  private final NList<ItemS> environment;
  private final Map<CacheKey, ExprB> cache;
  private final Map<Hash, String> nameMapping;
  private final Map<Hash, Loc> locMapping;

  @Inject
  public SbTranslator(BytecodeF bytecodeF, FileLoader fileLoader, BytecodeLoader bytecodeLoader) {
    this(bytecodeF, new TypeSbTranslator(bytecodeF, ImmutableMap.of()), fileLoader, bytecodeLoader,
        nlist(), new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  public SbTranslator(BytecodeF bytecodeF, TypeSbTranslator typeSbTranslator, FileLoader fileLoader,
      BytecodeLoader bytecodeLoader, NList<ItemS> environment, Map<CacheKey, ExprB> cache,
      Map<Hash, String> nameMapping, Map<Hash, Loc> locMapping) {
    this.bytecodeF = bytecodeF;
    this.typeSbTranslator = typeSbTranslator;
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
    this.environment = environment;
    this.cache = cache;
    this.nameMapping = nameMapping;
    this.locMapping = locMapping;
  }

  public BsMapping bsMapping() {
    return new BsMapping(nameMapping, locMapping);
  }

  private ImmutableList<ExprB> translateExprs(ImmutableList<ExprS> exprs) {
    return map(exprs, this::translateExpr);
  }

  public ExprB translateExpr(ExprS exprS) {
    // @formatter:off
    return switch (exprS) {
      case BlobS       blobS       -> saveLocAndReturn(blobS,     translateBlob(blobS));
      case CallS       callS       -> saveLocAndReturn(callS,     translateCall(callS));
      case IntS        intS        -> saveLocAndReturn(intS,      translateInt(intS));
      case OrderS      orderS      -> saveLocAndReturn(orderS,    translateOrder(orderS));
      case ParamRefS   paramRefS   -> saveLocAndReturn(paramRefS, translateParamRef(paramRefS));
      case SelectS     selectS     -> saveLocAndReturn(selectS,   translateSelect(selectS));
      case StringS     stringS     -> saveLocAndReturn(stringS,   translateString(stringS));
      case MonoizeS    monoizeS    -> translateMonoize(monoizeS);
    };
    // @formatter:on
  }

  private BlobB translateBlob(BlobS blobS) {
    return bytecodeF.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallB translateCall(CallS callS) {
    var callableB = translateExpr(callS.callee());
    var argsB = translateExprs(callS.args());
    var combineB = bytecodeF.combine(argsB);
    saveLoc(combineB, callS);
    return bytecodeF.call(callableB, combineB);
  }

  private IntB translateInt(IntS intS) {
    return bytecodeF.int_(intS.bigInteger());
  }

  private ExprB translateMonoize(MonoizeS monoizeS) {
    var monoizedVarMap = mapValues(monoizeS.varMap(), typeSbTranslator::translate);
    var varMap = Maps.concat(monoizedVarMap, typeSbTranslator.varMap());
    var newTypeSbTranslator = new TypeSbTranslator(bytecodeF, varMap);
    var sbTranslator = new SbTranslator(bytecodeF, newTypeSbTranslator, fileLoader, bytecodeLoader,
        environment, cache, nameMapping, locMapping);
    return sbTranslator.translateMonoizable(monoizeS.monoizableS());
  }

  public ExprB translateMonoizable(MonoizableS monoizableS) {
    return switch (monoizableS) {
      case AnonFuncS anonFuncS -> translateAnonFunc(anonFuncS);
      case EvaluableRefS evaluableRefS -> translateEvaluableRef(evaluableRefS);
    };
  }

  private ExprB translateAnonFunc(AnonFuncS anonFuncS) {
    var funcB = funcBodySbTranslator(anonFuncS)
        .translateAnonFuncImpl(anonFuncS);
    return saveNalAndReturn("<anonymous>", anonFuncS, funcB);
  }

  private ExprB translateAnonFuncImpl(AnonFuncS anonFuncS) {
    return bytecodeF.closurize(translateExprFunc(anonFuncS));
  }

  private ExprB translateEvaluableRef(EvaluableRefS evaluableRefS) {
    return switch (evaluableRefS.namedEvaluable()) {
      case NamedFuncS namedFuncS -> translateNamedFuncWithCache(namedFuncS);
      case NamedValueS namedValS -> translateNamedValueWithCache(evaluableRefS.loc(), namedValS);
    };
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
    return new SbTranslator(bytecodeF, typeSbTranslator, fileLoader, bytecodeLoader,
        newEnvironment, cache, nameMapping, locMapping);
  }

  private ExprB translateNamedFuncImpl(NamedFuncS namedFuncS) {
    return switch (namedFuncS) {
      case AnnFuncS n -> translateAnnFunc(n);
      case DefFuncS d -> translateExprFunc(d);
      case ConstructorS c -> translateConstructor(c);
    };
  }

  private ExprB translateAnnFunc(AnnFuncS annFuncS) {
    var annName = annFuncS.ann().name();
    return switch (annName) {
      case BYTECODE -> fetchFuncBytecode(annFuncS);
      case NATIVE_PURE, NATIVE_IMPURE -> translateNativeFunc(annFuncS);
      default -> throw new SbTranslatorExc("Illegal function annotation: " + annName + ".");
    };
  }

  private DefinedFuncB translateExprFunc(ExprFuncS exprFuncS) {
    return bytecodeF.definedFunc(
        translateT(exprFuncS.schema().type()),
        translateExpr(exprFuncS.body()));
  }

  private NativeFuncB translateNativeFunc(AnnFuncS nativeFuncS) {
    var funcTB = translateT(nativeFuncS.schema().type());
    var annS = nativeFuncS.ann();
    var jarB = loadNativeJar(annS.loc());
    var classBinaryNameB = bytecodeF.string(annS.path().string());
    var isPureB = bytecodeF.bool(annS.name().equals(NATIVE_PURE));
    return bytecodeF.nativeFunc(funcTB, jarB, classBinaryNameB, isPureB);
  }

  private DefinedFuncB translateConstructor(ConstructorS constructorS) {
    var funcTB = translateT(constructorS.schema().type());
    var bodyB = bytecodeF.combine(createRefsB(funcTB.params()));
    saveLoc(bodyB, constructorS);
    return bytecodeF.definedFunc(funcTB, bodyB);
  }

  private ImmutableList<ExprB> createRefsB(TupleTB paramTs) {
    Builder<ExprB> builder = ImmutableList.builder();
    for (int i = 0; i < paramTs.size(); i++) {
      var closedT = paramTs.get(i);
      builder.add(bytecodeF.ref(closedT, BigInteger.valueOf(i)));
    }
    return builder.build();
  }

  private OrderB translateOrder(OrderS orderS) {
    var arrayTB = translateT(orderS.evalT());
    var elemsB = translateExprs(orderS.elems());
    return bytecodeF.order(arrayTB, elemsB);
  }

  private RefB translateParamRef(ParamRefS paramRefS) {
    var index = environment.indexOf(paramRefS.paramName());
    if (index == null) {
      throw new SbTranslatorExc("Reference to unknown parameter `" + paramRefS.paramName()
          + "` at " + paramRefS.loc() + ".");
    }
    return bytecodeF.ref(translateT(paramRefS.evalT()), BigInteger.valueOf(index));
  }

  private SelectB translateSelect(SelectS selectS) {
    var selectableB = translateExpr(selectS.selectable());
    var structTS = (StructTS) selectS.selectable().evalT();
    var indexJ = structTS.fields().indexOf(selectS.field());
    var bigInteger = BigInteger.valueOf(indexJ);
    var indexB = bytecodeF.int_(bigInteger);
    saveLoc(indexB, selectS);
    return bytecodeF.select(selectableB, indexB);
  }

  private StringB translateString(StringS stringS) {
    return bytecodeF.string(stringS.string());
  }

  private ExprB translateNamedValueWithCache(Loc refLoc, NamedValueS namedValueS) {
    var key = new CacheKey(namedValueS.name(), typeSbTranslator.varMap());
    return computeIfAbsent(cache, key, name -> translateNamedValue(refLoc, namedValueS));
  }

  private ExprB translateNamedValue(Loc refLoc, NamedValueS namedValueS) {
    return switch (namedValueS) {
      case AnnValueS annValueS -> saveNalAndReturn(annValueS, translateAnnValue(annValueS));
      case DefValueS defValueS -> translateDefValue(refLoc, defValueS);
    };
  }

  private ExprB translateAnnValue(AnnValueS annValueS) {
    var annName = annValueS.ann().name();
    if (annName.equals(BYTECODE)) {
      return fetchValBytecode(annValueS);
    } else {
      throw new SbTranslatorExc("Illegal value annotation: " + q("@" + annName) + ".");
    }
  }

  private ExprB translateDefValue(Loc refLoc, DefValueS defValueS) {
    var funcTB = bytecodeF.funcT(list(), translateT(defValueS.schema().type()));
    var funcB = bytecodeF.definedFunc(funcTB, translateExpr(defValueS.body()));
    saveNal(funcB, defValueS);
    var call = bytecodeF.call(funcB, bytecodeF.combine(list()));
    saveLoc(call, refLoc);
    return call;
  }

  // helpers

  private ExprB fetchValBytecode(AnnValueS annValueS) {
    var typeB = translateT(annValueS.schema().type());
    return fetchBytecode(annValueS.ann(), typeB, annValueS.name());
  }

  private ExprB fetchFuncBytecode(AnnFuncS annFuncS) {
    var typeB = translateT(annFuncS.schema().type());
    return fetchBytecode(annFuncS.ann(), typeB, annFuncS.name());
  }

  private ExprB fetchBytecode(AnnS ann, TypeB typeB, String name) {
    var varNameToTypeMap = mapKeys(typeSbTranslator.varMap(), VarS::name);
    var jar = loadNativeJar(ann.loc());
    var bytecodeTry = bytecodeLoader.load(name, jar, ann.path().string(), varNameToTypeMap);
    if (!bytecodeTry.isPresent()) {
      throw new SbTranslatorExc(ann.loc() + ": " + bytecodeTry.error());
    }
    var bytecodeB = bytecodeTry.result();
    if (!bytecodeB.evalT().equals(typeB)) {
      throw new SbTranslatorExc(ann.loc() + ": Bytecode provider returned object of wrong type "
          + bytecodeB.evalT().q() + " when " + q(name) + " is declared as " + typeB.q() + ".");
    }
    return bytecodeB;
  }

  private BlobB loadNativeJar(Loc loc) {
    var filePath = loc.file().withExtension("jar");
    try {
      return fileLoader.load(filePath);
    } catch (FileNotFoundException e) {
      String message = loc + ": Error loading native jar: File %s doesn't exist."
          .formatted(filePath.q());
      throw new SbTranslatorExc(message);
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
    saveLoc(exprB, located.loc());
  }

  private void saveLoc(ExprB exprB, Loc loc) {
    locMapping.put(exprB.hash(), loc);
  }

  private static record CacheKey(String name, ImmutableMap<VarS, TypeB> varMap) {}
}
