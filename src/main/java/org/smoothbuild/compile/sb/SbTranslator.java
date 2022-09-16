package org.smoothbuild.compile.sb;

import static org.smoothbuild.compile.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.computeIfAbsent;
import static org.smoothbuild.util.collect.Maps.mapKeys;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.ParamRefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.expr.val.DefFuncB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.NatFuncB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.compile.lang.base.ExprInfo;
import org.smoothbuild.compile.lang.base.ExprInfoImpl;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.define.AnnFuncS;
import org.smoothbuild.compile.lang.define.AnnS;
import org.smoothbuild.compile.lang.define.AnnValS;
import org.smoothbuild.compile.lang.define.BlobS;
import org.smoothbuild.compile.lang.define.CallS;
import org.smoothbuild.compile.lang.define.DefFuncS;
import org.smoothbuild.compile.lang.define.DefValS;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.lang.define.FuncS;
import org.smoothbuild.compile.lang.define.IntS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.MonoizeS;
import org.smoothbuild.compile.lang.define.NamedValS;
import org.smoothbuild.compile.lang.define.OrderS;
import org.smoothbuild.compile.lang.define.ParamRefS;
import org.smoothbuild.compile.lang.define.PolyFuncS;
import org.smoothbuild.compile.lang.define.PolyValS;
import org.smoothbuild.compile.lang.define.SelectS;
import org.smoothbuild.compile.lang.define.StringS;
import org.smoothbuild.compile.lang.define.SyntCtorS;
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
  private TypeSbTranslator typeSbTranslator;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;
  private final Deque<NList<ItemS>> callStack;
  private final Map<CacheKey, ExprB> cache;
  private final Map<ExprB, ExprInfo> descriptions;

  @Inject
  public SbTranslator(BytecodeF bytecodeF, FileLoader fileLoader,
      BytecodeLoader bytecodeLoader) {
    this.bytecodeF = bytecodeF;
    this.typeSbTranslator = new TypeSbTranslator(bytecodeF, ImmutableMap.of());
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
    this.callStack = new LinkedList<>();
    this.cache = new HashMap<>();
    this.descriptions = new HashMap<>();
  }

  public ImmutableMap<ExprB, ExprInfo> descriptions() {
    return ImmutableMap.copyOf(descriptions);
  }

  private ImmutableList<ExprB> translateExprs(ImmutableList<ExprS> exprs) {
    return map(exprs, this::translateExpr);
  }

  public ExprB translateExpr(ExprS exprS) {
    return switch (exprS) {
      case BlobS blobS -> translateAndCacheNal(blobS, this::translateBlob);
      case CallS callS -> translateAndCacheNal(callS, this::translateCall);
      case IntS intS -> translateAndCacheNal(intS, this::translateInt);
      case MonoizeS monoizeS -> translateMonoize(monoizeS);
      case FuncS funcS -> translateFunc(funcS, ImmutableMap.of());
      case OrderS orderS -> translateAndCacheNal(orderS, this::translateOrder);
      case ParamRefS paramRefS -> translateAndCacheNal(paramRefS, this::translateParamRef);
      case SelectS selectS -> translateAndCacheNal(selectS, this::translateSelect);
      case StringS stringS -> translateAndCacheNal(stringS, this::translateString);
      case NamedValS namedValS -> translateVal(namedValS, ImmutableMap.of());
    };
  }

  private <T extends ExprS> ExprB translateAndCacheNal(T exprS, Function<T, ExprB> translator) {
    var exprB = translator.apply(exprS);
    descriptions.put(exprB, exprS);
    return exprB;
  }

  private BlobB translateBlob(BlobS blobS) {
    return bytecodeF.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallB translateCall(CallS callS) {
    var callableB = translateExpr(callS.callee());
    var argsB = translateExprs(callS.args());
    var paramTupleT = ((FuncTB) callableB.type()).params();
    var combineB = bytecodeF.combine(paramTupleT, argsB);

    descriptions.put(combineB, new ExprInfoImpl("{}", callS.loc()));
    return bytecodeF.call(translateT(callS.type()), callableB, combineB);
  }

  private IntB translateInt(IntS intS) {
    return bytecodeF.int_(intS.bigInteger());
  }

  private ExprB translateMonoize(MonoizeS monoizeS) {
    var varMap = mapValues(monoizeS.varMap(), typeSbTranslator::translate);
    var oldTypeSbConverter = typeSbTranslator;
    typeSbTranslator = new TypeSbTranslator(bytecodeF, varMap);
    try {
      return switch (monoizeS.refable()) {
        case PolyFuncS polyFuncS -> translateFunc(polyFuncS.mono(), varMap);
        case PolyValS polyValS -> translateVal(polyValS.mono(), varMap);
      };
    } finally {
      typeSbTranslator = oldTypeSbConverter;
    }
  }

  private ExprB translateFunc(FuncS funcS, ImmutableMap<VarS, TypeB> varMap) {
    var key = new CacheKey(funcS.name(), varMap);
    return computeIfAbsent(cache, key, name -> translateFuncImpl(funcS, varMap));
  }

  private ExprB translateFuncImpl(FuncS funcS, ImmutableMap<VarS, TypeB> varMap) {
    try {
      callStack.push(funcS.params());
      var funcB = switch (funcS) {
        case AnnFuncS n -> translateAnnFunc(n, varMap);
        case DefFuncS d -> translateDefFunc(d);
        case SyntCtorS c -> translateSyntCtor(c);
      };
      descriptions.put(funcB, funcS);
      return funcB;
    } finally {
      callStack.pop();
    }
  }

  private ExprB translateAnnFunc(AnnFuncS annFuncS, ImmutableMap<VarS, TypeB> varMap) {
    var annName = annFuncS.ann().name();
    return switch (annName) {
      case BYTECODE -> fetchFuncBytecode(annFuncS, varMap);
      case NATIVE_PURE, NATIVE_IMPURE -> translateNatFunc(annFuncS);
      default -> throw new TranslateSbExc("Illegal function annotation: " + annName + ".");
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
    var funcTS = syntCtorS.type();
    var resTB = translateT(funcTS.res());
    var paramTBs = translateT(funcTS.params());
    var paramRefsB = createParamRefsB(paramTBs);
    var paramsTB = bytecodeF.tupleT(map(paramRefsB, ExprB::type));
    var bodyB = bytecodeF.combine(paramsTB, paramRefsB);
    descriptions.put(bodyB, new ExprInfoImpl("{}", syntCtorS.loc()));
    return bytecodeF.defFunc(resTB, paramTBs, bodyB);
  }

  private ImmutableList<ExprB> createParamRefsB(TupleTB paramTs) {
    Builder<ExprB> builder = ImmutableList.builder();
    for (int i = 0; i < paramTs.size(); i++) {
      var closedT = paramTs.get(i);
      builder.add(bytecodeF.paramRef(closedT, BigInteger.valueOf(i)));
    }
    return builder.build();
  }

  private OrderB translateOrder(OrderS orderS) {
    var arrayTB = translateArrayT(orderS.type());
    var elemsB = translateExprs(orderS.elems());
    return bytecodeF.order(arrayTB, elemsB);
  }

  private ParamRefB translateParamRef(ParamRefS paramRefS) {
    var index = callStack.peek().indexMap().get(paramRefS.paramName());
    return bytecodeF.paramRef(translateT(paramRefS.type()), BigInteger.valueOf(index));
  }

  private SelectB translateSelect(SelectS selectS) {
    var selectableB = translateExpr(selectS.selectable());
    var structTS = (StructTS) selectS.selectable().type();
    var indexJ = structTS.fields().indexMap().get(selectS.field());
    var indexB = bytecodeF.int_(BigInteger.valueOf(indexJ));
    descriptions.put(indexB, selectS);
    return bytecodeF.select(translateT(selectS.type()), selectableB, indexB);
  }

  private StringB translateString(StringS stringS) {
    return bytecodeF.string(stringS.string());
  }

  private ExprB translateVal(NamedValS namedValS, ImmutableMap<VarS, TypeB> varMap) {
    var key = new CacheKey(namedValS.name(), varMap);
    return computeIfAbsent(cache, key, name -> translateValImpl(namedValS, varMap));
  }

  private ExprB translateValImpl(NamedValS namedValS, ImmutableMap<VarS, TypeB> varMap) {
    return switch (namedValS) {
      case AnnValS annValS -> translateAnnVal(annValS, varMap);
      case DefValS defValS -> translateExpr(defValS.body());
    };
  }

  private ExprB translateAnnVal(AnnValS annValS, ImmutableMap<VarS, TypeB> varMap) {
    var annName = annValS.ann().name();
    return switch (annName) {
      case BYTECODE ->  fetchValBytecode(annValS, varMap);
      default -> throw new TranslateSbExc("Illegal value annotation: " + q("@" + annName) + ".");
    };
  }

  // helpers

  private ExprB fetchValBytecode(AnnValS annValS, ImmutableMap<VarS, TypeB> varMap) {
    var typeB = translateT(annValS.type());
    return fetchBytecode(annValS.ann(), typeB, annValS.name(), varMap);
  }

  private ExprB fetchFuncBytecode(AnnFuncS annFuncS, ImmutableMap<VarS, TypeB> varMap) {
    var typeB = translateT(annFuncS.type());
    return fetchBytecode(annFuncS.ann(), typeB, annFuncS.name(), varMap);
  }

  private ExprB fetchBytecode(AnnS ann, TypeB typeB, String name, Map<VarS, TypeB> varMap) {
    var varNameToTypeMap = mapKeys(varMap, VarS::name);
    var jar = loadNativeJar(ann.loc());
    var bytecodeTry = bytecodeLoader.load(name, jar, ann.path().string(), varNameToTypeMap);
    if (!bytecodeTry.isPresent()) {
      throw new TranslateSbExc(ann.loc() + ": " + bytecodeTry.error());
    }
    var bytecodeB = bytecodeTry.result();
    if (!bytecodeB.type().equals(typeB)) {
      throw new TranslateSbExc(ann.loc() + ": Bytecode provider returned object of wrong type "
          + bytecodeB.type().q() + " when " + q(name) + " is declared as " + typeB.q() + ".");
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
      throw new TranslateSbExc(message);
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

  private ArrayTB translateArrayT(ArrayTS arrayTS) {
    return typeSbTranslator.translate(arrayTS);
  }

  private static record CacheKey(String name, ImmutableMap<VarS, TypeB> varMap) {
  }
}
