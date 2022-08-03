package org.smoothbuild.compile;

import static org.smoothbuild.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.lang.type.AnnotationNames.NATIVE_PURE;
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
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.BlobB;
import org.smoothbuild.bytecode.obj.cnst.FuncB;
import org.smoothbuild.bytecode.obj.cnst.IntB;
import org.smoothbuild.bytecode.obj.cnst.MethodB;
import org.smoothbuild.bytecode.obj.cnst.StringB;
import org.smoothbuild.bytecode.obj.expr.CallB;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.cnst.FuncTB;
import org.smoothbuild.bytecode.type.cnst.MethodTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.base.NalImpl;
import org.smoothbuild.lang.define.AnnFuncS;
import org.smoothbuild.lang.define.AnnS;
import org.smoothbuild.lang.define.AnnValS;
import org.smoothbuild.lang.define.BlobS;
import org.smoothbuild.lang.define.CallS;
import org.smoothbuild.lang.define.DefFuncS;
import org.smoothbuild.lang.define.DefValS;
import org.smoothbuild.lang.define.ExprS;
import org.smoothbuild.lang.define.FuncS;
import org.smoothbuild.lang.define.IntS;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.MonoizeS;
import org.smoothbuild.lang.define.OrderS;
import org.smoothbuild.lang.define.ParamRefS;
import org.smoothbuild.lang.define.PolyFuncS;
import org.smoothbuild.lang.define.PolyValS;
import org.smoothbuild.lang.define.SelectS;
import org.smoothbuild.lang.define.StringS;
import org.smoothbuild.lang.define.SyntCtorS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class SbConverter {
  private final BytecodeF bytecodeF;
  private TypeSbConverter typeSbConverter;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;
  private final Deque<NList<ItemS>> callStack;
  private final Map<CacheKey, ObjB> cache;
  private final Map<ObjB, Nal> nals;

  @Inject
  public SbConverter(BytecodeF bytecodeF, FileLoader fileLoader,
      BytecodeLoader bytecodeLoader) {
    this.bytecodeF = bytecodeF;
    this.typeSbConverter = new TypeSbConverter(bytecodeF, ImmutableMap.of());
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
    this.callStack = new LinkedList<>();
    this.cache = new HashMap<>();
    this.nals = new HashMap<>();
  }

  public ImmutableMap<ObjB, Nal> nals() {
    return ImmutableMap.copyOf(nals);
  }

  private ImmutableList<ObjB> convertExprs(ImmutableList<ExprS> exprs) {
    return map(exprs, this::convertExpr);
  }

  public ObjB convertExpr(ExprS exprS) {
    return switch (exprS) {
      case BlobS blobS -> convertAndCacheNal(blobS, this::convertBlob);
      case CallS callS -> convertAndCacheNal(callS, this::convertCall);
      case IntS intS -> convertAndCacheNal(intS, this::convertInt);
      case MonoizeS monoizeS -> convertMonoize(monoizeS);
      case FuncS funcS -> convertFunc(funcS, ImmutableMap.of());
      case OrderS orderS -> convertAndCacheNal(orderS, this::convertOrder);
      case ParamRefS paramRefS -> convertAndCacheNal(paramRefS, this::convertParamRef);
      case SelectS selectS -> convertAndCacheNal(selectS, this::convertSelect);
      case StringS stringS -> convertAndCacheNal(stringS, this::convertString);
      case ValS valS -> convertVal(valS, ImmutableMap.of());
    };
  }

  private <T extends ExprS> ObjB convertAndCacheNal(T exprS, Function<T, ObjB> mapping) {
    var objB = mapping.apply(exprS);
    nals.put(objB, exprS);
    return objB;
  }

  private BlobB convertBlob(BlobS blobS) {
    return bytecodeF.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallB convertCall(CallS callS) {
    var callableB = convertExpr(callS.callee());
    var argsB = convertExprs(callS.args());
    var paramTupleT = ((FuncTB) callableB.type()).paramsTuple();
    var combineB = bytecodeF.combine(paramTupleT, argsB);

    nals.put(combineB, new NalImpl("{}", callS.loc()));
    return bytecodeF.call(convertT(callS.type()), callableB, combineB);
  }

  private IntB convertInt(IntS intS) {
    return bytecodeF.int_(intS.bigInteger());
  }

  private ObjB convertMonoize(MonoizeS monoizeS) {
    var varMap = mapValues(monoizeS.varMap(), typeSbConverter::convert);
    var oldTypeSbConverter = typeSbConverter;
    typeSbConverter = new TypeSbConverter(bytecodeF, varMap);
    try {
      return switch (monoizeS.refable()) {
        case PolyFuncS polyFuncS -> convertFunc(polyFuncS.mono(), varMap);
        case PolyValS polyValS -> convertVal(polyValS.mono(), varMap);
      };
    } finally {
      typeSbConverter = oldTypeSbConverter;
    }
  }

  private ObjB convertFunc(FuncS funcS, ImmutableMap<VarS, TypeB> varMap) {
    var key = new CacheKey(funcS.name(), varMap);
    return computeIfAbsent(cache, key, name -> convertFuncImpl(funcS, varMap));
  }

  private ObjB convertFuncImpl(FuncS funcS, ImmutableMap<VarS, TypeB> varMap) {
    try {
      callStack.push(funcS.params());
      var funcB = switch (funcS) {
        case AnnFuncS n -> convertAnnFunc(n, varMap);
        case DefFuncS d -> convertDefFunc(d);
        case SyntCtorS c -> convertSyntCtor(c);
      };
      nals.put(funcB, funcS);
      return funcB;
    } finally {
      callStack.pop();
    }
  }

  private ObjB convertAnnFunc(AnnFuncS annFuncS, ImmutableMap<VarS, TypeB> varMap) {
    var annName = annFuncS.ann().name();
    return switch (annName) {
      case BYTECODE -> fetchFuncBytecode(annFuncS, varMap);
      case NATIVE_PURE, NATIVE_IMPURE -> convertNatFunc(annFuncS);
      default -> throw new ConvertSbExc("Illegal function annotation: " + annName + ".");
    };
  }

  private FuncB convertDefFunc(DefFuncS defFuncS) {
    var funcTB = convertFuncT(defFuncS.type());
    var body = convertExpr(defFuncS.body());
    return bytecodeF.func(funcTB, body);
  }

  private FuncB convertNatFunc(AnnFuncS natFuncS) {
    var funcTB = convertFuncT(natFuncS.type());
    var methodTB = bytecodeF.methodT(funcTB.res(), funcTB.params());
    var methodB = createMethodB(natFuncS.ann(), methodTB);
    var paramRefsB = createParamRefsB(funcTB.params());
    var paramsTB = bytecodeF.tupleT(map(paramRefsB, ObjB::type));
    var argsB = bytecodeF.combine(paramsTB, paramRefsB);
    var bodyB = bytecodeF.invoke(funcTB.res(), methodB, argsB);
    nals.put(bodyB, natFuncS);
    return bytecodeF.func(funcTB, bodyB);
  }

  private MethodB createMethodB(AnnS annS, MethodTB methodTB) {
    var jarB = loadNativeJar(annS.loc());
    var classBinaryNameB = bytecodeF.string(annS.path().string());
    var isPureB = bytecodeF.bool(annS.name().equals(NATIVE_PURE));
    return bytecodeF.method(methodTB, jarB, classBinaryNameB, isPureB);
  }

  private FuncB convertSyntCtor(SyntCtorS syntCtorS) {
    var funcTB = convertFuncT(syntCtorS.type());
    var paramRefsB = createParamRefsB(funcTB.params());
    var paramsTB = bytecodeF.tupleT(map(paramRefsB, ObjB::type));
    var bodyB = bytecodeF.combine(paramsTB, paramRefsB);
    nals.put(bodyB, new NalImpl("{}", syntCtorS.loc()));
    return bytecodeF.func(funcTB, bodyB);
  }

  private ImmutableList<ObjB> createParamRefsB(ImmutableList<TypeB> paramTs) {
    Builder<ObjB> builder = ImmutableList.builder();
    for (int i = 0; i < paramTs.size(); i++) {
      var closedT = paramTs.get(i);
      builder.add(bytecodeF.paramRef(closedT, BigInteger.valueOf(i)));
    }
    return builder.build();
  }

  private OrderB convertOrder(OrderS orderS) {
    var arrayTB = convertArrayT(orderS.type());
    var elemsB = convertExprs(orderS.elems());
    return bytecodeF.order(arrayTB, elemsB);
  }

  private ParamRefB convertParamRef(ParamRefS paramRefS) {
    var index = callStack.peek().indexMap().get(paramRefS.paramName());
    return bytecodeF.paramRef(convertT(paramRefS.type()), BigInteger.valueOf(index));
  }

  private SelectB convertSelect(SelectS selectS) {
    var selectableB = convertExpr(selectS.selectable());
    var structTS = (StructTS) selectS.selectable().type();
    var indexJ = structTS.fields().indexMap().get(selectS.field());
    var indexB = bytecodeF.int_(BigInteger.valueOf(indexJ));
    nals.put(indexB, selectS);
    return bytecodeF.select(convertT(selectS.type()), selectableB, indexB);
  }

  private StringB convertString(StringS stringS) {
    return bytecodeF.string(stringS.string());
  }

  private ObjB convertVal(ValS valS, ImmutableMap<VarS, TypeB> varMap) {
    var key = new CacheKey(valS.name(), varMap);
    return computeIfAbsent(cache, key, name -> convertValImpl(valS, varMap));
  }

  private ObjB convertValImpl(ValS valS, ImmutableMap<VarS, TypeB> varMap) {
    return switch (valS) {
      case AnnValS annValS -> convertAnnVal(annValS, varMap);
      case DefValS defValS -> convertExpr(defValS.body());
    };
  }

  private ObjB convertAnnVal(AnnValS annValS, ImmutableMap<VarS, TypeB> varMap) {
    var annName = annValS.ann().name();
    return switch (annName) {
      case BYTECODE ->  fetchValBytecode(annValS, varMap);
      default -> throw new ConvertSbExc("Illegal value annotation: " + q("@" + annName) + ".");
    };
  }

  // helpers

  private ObjB fetchValBytecode(AnnValS annValS, ImmutableMap<VarS, TypeB> varMap) {
    var typeB = convertT(annValS.type());
    return fetchBytecode(annValS.ann(), typeB, annValS.name(), varMap);
  }

  private ObjB fetchFuncBytecode(AnnFuncS annFuncS, ImmutableMap<VarS, TypeB> varMap) {
    var typeB = convertT(annFuncS.type());
    return fetchBytecode(annFuncS.ann(), typeB, annFuncS.name(), varMap);
  }

  private ObjB fetchBytecode(AnnS ann, TypeB typeB, String name, Map<VarS, TypeB> varMap) {
    var varNameToTypeMap = mapKeys(varMap, VarS::name);
    var jar = loadNativeJar(ann.loc());
    var bytecodeTry = bytecodeLoader.load(name, jar, ann.path().string(), varNameToTypeMap);
    if (!bytecodeTry.isPresent()) {
      throw new ConvertSbExc(ann.loc() + ": " + bytecodeTry.error());
    }
    var bytecodeB = bytecodeTry.result();
    if (!bytecodeB.type().equals(typeB)) {
      throw new ConvertSbExc(ann.loc() + ": Bytecode provider returned object of wrong type "
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
      throw new ConvertSbExc(message);
    }
  }

  private TypeB convertT(TypeS typeS) {
    return typeSbConverter.convert(typeS);
  }

  private ArrayTB convertArrayT(ArrayTS typeS) {
    return typeSbConverter.convert(typeS);
  }

  private FuncTB convertFuncT(FuncTS funcTS) {
    return typeSbConverter.convert(funcTS);
  }

  private static record CacheKey(String name, ImmutableMap<VarS, TypeB> varMap) {
  }
}
