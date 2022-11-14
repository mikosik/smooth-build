package org.smoothbuild.compile.sb;

import static org.smoothbuild.compile.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.computeIfAbsent;
import static org.smoothbuild.util.collect.Maps.mapKeys;
import static org.smoothbuild.util.collect.Maps.mapValues;
import static org.smoothbuild.util.collect.NList.nlist;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.DefFuncB;
import org.smoothbuild.bytecode.expr.inst.IntB;
import org.smoothbuild.bytecode.expr.inst.NatFuncB;
import org.smoothbuild.bytecode.expr.inst.StringB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.ArrayTB;
import org.smoothbuild.bytecode.type.inst.FuncTB;
import org.smoothbuild.bytecode.type.inst.TupleTB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.lang.base.WithLoc;
import org.smoothbuild.compile.lang.define.AnnFuncS;
import org.smoothbuild.compile.lang.define.AnnS;
import org.smoothbuild.compile.lang.define.AnnValS;
import org.smoothbuild.compile.lang.define.BlobS;
import org.smoothbuild.compile.lang.define.CallS;
import org.smoothbuild.compile.lang.define.DefFuncS;
import org.smoothbuild.compile.lang.define.DefValS;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.lang.define.IntS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.MonoizeS;
import org.smoothbuild.compile.lang.define.NamedFuncS;
import org.smoothbuild.compile.lang.define.NamedPolyFuncS;
import org.smoothbuild.compile.lang.define.NamedPolyValS;
import org.smoothbuild.compile.lang.define.OrderS;
import org.smoothbuild.compile.lang.define.ParamRefS;
import org.smoothbuild.compile.lang.define.PolyExprS;
import org.smoothbuild.compile.lang.define.PolyRefS;
import org.smoothbuild.compile.lang.define.SelectS;
import org.smoothbuild.compile.lang.define.StringS;
import org.smoothbuild.compile.lang.define.SyntCtorS;
import org.smoothbuild.compile.lang.define.ValS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TupleTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.load.FileLoader;
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
      case BlobS       blobS       -> translateAndSaveLoc(blobS,     this::translateBlob);
      case CallS       callS       -> translateAndSaveLoc(callS,     this::translateCall);
      case IntS        intS        -> translateAndSaveLoc(intS,      this::translateInt);
      case OrderS      orderS      -> translateAndSaveLoc(orderS,    this::translateOrder);
      case ParamRefS   paramRefS   -> translateAndSaveLoc(paramRefS, this::translateParamRef);
      case SelectS     selectS     -> translateAndSaveLoc(selectS,   this::translateSelect);
      case StringS     stringS     -> translateAndSaveLoc(stringS,   this::translateString);
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
    var varMap = mapValues(monoizeS.varMap(), typeSbTranslator::translate);
    var newTypeSbTranslator = new TypeSbTranslator(bytecodeF, varMap);
    var sbTranslator = new SbTranslator(bytecodeF, newTypeSbTranslator, fileLoader, bytecodeLoader,
        environment, cache, nameMapping, locMapping);
    return sbTranslator.translatePolyExpr(monoizeS.polyExprS());
  }

  public ExprB translatePolyExpr(PolyExprS polyExprS) {
    return switch (polyExprS) {
      case PolyRefS polyRefS -> translatePolyRef(polyRefS);
    };
  }

  private ExprB translatePolyRef(PolyRefS polyRefS) {
    return switch (polyRefS.namedPolyEvaluable()) {
      case NamedPolyFuncS namedPolyFuncS -> translateNamedFunc(namedPolyFuncS.mono());
      case NamedPolyValS namedPolyValS -> translateVal(polyRefS.loc(), namedPolyValS.mono());
    };
  }

  private ExprB translateNamedFunc(NamedFuncS namedFuncS) {
    var key = new CacheKey(namedFuncS.name(), typeSbTranslator.varMap());
    return computeIfAbsent(cache, key, name -> setEnvironmentAndTranslateFunc(namedFuncS));
  }

  private ExprB setEnvironmentAndTranslateFunc(NamedFuncS namedFuncS) {
    var newEnvironment = namedFuncS.params();
    var sbTranslator = new SbTranslator(bytecodeF, typeSbTranslator, fileLoader, bytecodeLoader,
        newEnvironment, cache, nameMapping, locMapping);
    return translateAndSaveNal(namedFuncS, sbTranslator::translateFuncImpl);
  }

  private ExprB translateFuncImpl(NamedFuncS namedFuncS) {
    return switch (namedFuncS) {
      case AnnFuncS n -> translateAnnFunc(n);
      case DefFuncS d -> translateDefFunc(d);
      case SyntCtorS c -> translateSyntCtor(c);
    };
  }

  private ExprB translateAnnFunc(AnnFuncS annFuncS) {
    var annName = annFuncS.ann().name();
    return switch (annName) {
      case BYTECODE -> fetchFuncBytecode(annFuncS);
      case NATIVE_PURE, NATIVE_IMPURE -> translateNatFunc(annFuncS);
      default -> throw new SbTranslatorExc("Illegal function annotation: " + annName + ".");
    };
  }

  private DefFuncB translateDefFunc(DefFuncS defFuncS) {
    return bytecodeF.defFunc(
        translateT(defFuncS.type()),
        translateExpr(defFuncS.body()));
  }

  private NatFuncB translateNatFunc(AnnFuncS natFuncS) {
    var funcTB = translateT(natFuncS.type());
    var annS = natFuncS.ann();
    var jarB = loadNativeJar(annS.loc());
    var classBinaryNameB = bytecodeF.string(annS.path().string());
    var isPureB = bytecodeF.bool(annS.name().equals(NATIVE_PURE));
    return bytecodeF.natFunc(funcTB, jarB, classBinaryNameB, isPureB);
  }

  private DefFuncB translateSyntCtor(SyntCtorS syntCtorS) {
    var funcTB = translateT(syntCtorS.type());
    var bodyB = bytecodeF.combine(createRefsB(funcTB.params()));
    saveLoc(bodyB, syntCtorS);
    return bytecodeF.defFunc(funcTB, bodyB);
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

  private ExprB translateVal(Loc refLoc, ValS valS) {
    var key = new CacheKey(valS.name(), typeSbTranslator.varMap());
    return computeIfAbsent(cache, key, name -> translateValImpl(refLoc, valS));
  }

  private ExprB translateValImpl(Loc refLoc, ValS valS) {
    return switch (valS) {
      case AnnValS annValS -> translateAndSaveNal(annValS, this::translateAnnVal);
      case DefValS defValS -> translateDefVal(refLoc, defValS);
    };
  }

  private ExprB translateAnnVal(AnnValS annValS) {
    var annName = annValS.ann().name();
    if (annName.equals(BYTECODE)) {
      return fetchValBytecode(annValS);
    } else {
      throw new SbTranslatorExc("Illegal value annotation: " + q("@" + annName) + ".");
    }
  }

  private ExprB translateDefVal(Loc refLoc, DefValS defValS) {
    var funcTB = bytecodeF.funcT(translateT(defValS.type()), list());
    var funcB = bytecodeF.defFunc(funcTB, translateExpr(defValS.body()));
    saveNal(funcB, defValS);
    var call = bytecodeF.call(funcB, bytecodeF.combine(list()));
    saveLoc(call, refLoc);
    return call;
  }

  // helpers

  private ExprB fetchValBytecode(AnnValS annValS) {
    var typeB = translateT(annValS.type());
    return fetchBytecode(annValS.ann(), typeB, annValS.name());
  }

  private ExprB fetchFuncBytecode(AnnFuncS annFuncS) {
    var typeB = translateT(annFuncS.type());
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

  private <T extends Nal> ExprB translateAndSaveNal(T nal, Function<T, ExprB> translator) {
    var result = translator.apply(nal);
    saveNal(result, nal);
    return result;
  }

  private <T extends WithLoc> ExprB translateAndSaveLoc(T withLoc, Function<T, ExprB> translator) {
    var exprB = translator.apply(withLoc);
    saveLoc(exprB, withLoc);
    return exprB;
  }

  private void saveNal(ExprB exprB, Nal nal) {
    nameMapping.put(exprB.hash(), nal.name());
    saveLoc(exprB, nal);
  }

  private void saveLoc(ExprB exprB, WithLoc withLoc) {
    saveLoc(exprB, withLoc.loc());
  }

  private void saveLoc(ExprB exprB, Loc loc) {
    locMapping.put(exprB.hash(), loc);
  }

  private static record CacheKey(String name, ImmutableMap<VarS, TypeB> varMap) {}
}
