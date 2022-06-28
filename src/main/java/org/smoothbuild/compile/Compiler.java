package org.smoothbuild.compile;

import static org.smoothbuild.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.lang.type.solver.DeduceVarMap.deduceVarMap;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.computeIfAbsent;
import static org.smoothbuild.util.collect.Maps.map;

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
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.IntS;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.MonoFuncS;
import org.smoothbuild.lang.define.MonoObjS;
import org.smoothbuild.lang.define.MonoRefS;
import org.smoothbuild.lang.define.MonoizeS;
import org.smoothbuild.lang.define.OrderS;
import org.smoothbuild.lang.define.ParamRefS;
import org.smoothbuild.lang.define.PolyFuncS;
import org.smoothbuild.lang.define.SelectS;
import org.smoothbuild.lang.define.StringS;
import org.smoothbuild.lang.define.SyntCtorS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class Compiler {
  private final BytecodeF bytecodeF;
  private final DefsS defs;
  private final TypeSbConv typeSbConv;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;
  private final Deque<NList<ItemS>> callStack;
  private final Map<CacheKey, FuncB> funcCache;
  private final Map<String, ObjB> valCache;
  private final Map<ObjB, Nal> nals;

  @Inject
  public Compiler(BytecodeF bytecodeF, DefsS defs, TypeSbConv typeSbConv, FileLoader fileLoader,
      BytecodeLoader bytecodeLoader) {
    this.bytecodeF = bytecodeF;
    this.defs = defs;
    this.typeSbConv = typeSbConv;
    this.fileLoader = fileLoader;
    this.bytecodeLoader = bytecodeLoader;
    this.callStack = new LinkedList<>();
    this.funcCache = new HashMap<>();
    this.valCache = new HashMap<>();
    this.nals = new HashMap<>();
  }

  public ImmutableMap<ObjB, Nal> nals() {
    return ImmutableMap.copyOf(nals);
  }


  private ImmutableList<ObjB> compileObjs(ImmutableList<MonoObjS> objs) {
    return map(objs, this::compileObj);
  }

  public ObjB compileObj(MonoObjS objS) {
    return switch (objS) {
      case BlobS blobS -> compileAndCacheNal(blobS, this::compileBlob);
      case CallS callS -> compileAndCacheNal(callS, this::compileCall);
      case IntS intS -> compileAndCacheNal(intS, this::compileInt);
      case MonoizeS monoizeS -> compileMonoize(monoizeS);
      case MonoFuncS monoFuncS -> compileMonoFunc(monoFuncS, ImmutableMap.of());
      case MonoRefS monoRefS -> compileMonoRef(monoRefS);
      case OrderS orderS -> compileAndCacheNal(orderS, this::compileOrder);
      case ParamRefS paramRefS -> compileAndCacheNal(paramRefS, this::compileParamRef);
      case SelectS selectS -> compileAndCacheNal(selectS, this::compileSelect);
      case StringS stringS -> compileAndCacheNal(stringS, this::compileString);
      case ValS v -> compileVal(v);
    };
  }

  private <T extends MonoObjS> ObjB compileAndCacheNal(T objS, Function<T, ObjB> mapping) {
    var objB = mapping.apply(objS);
    nals.put(objB, objS);
    return objB;
  }

  private BlobB compileBlob(BlobS blobS) {
    return bytecodeF.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallB compileCall(CallS callS) {
    var callableB = compileObj(callS.callee());
    var argsB = compileObjs(callS.args());
    var paramTupleT = ((FuncTB) callableB.type()).paramsTuple();
    var combineB = bytecodeF.combine(paramTupleT, argsB);

    nals.put(combineB, new NalImpl("{}", callS.loc()));
    return bytecodeF.call(convertT(callS.type()), callableB, combineB);
  }

  private IntB compileInt(IntS intS) {
    return bytecodeF.int_(intS.bigInteger());
  }

  private FuncB compileMonoize(MonoizeS monoizeS) {
    var varMap = deduceVarMap(monoizeS.funcRef().type().mono(), monoizeS.type());
    var varMapB = map(varMap, MonoTS::name, this::convertT);
    typeSbConv.addLastVarMap(varMapB);
    try {
      var topRefableS = defs.topRefables().get(monoizeS.funcRef().name());
      return switch (topRefableS) {
        case PolyFuncS polyFuncS -> compileMonoFunc(polyFuncS.func(), varMapB);
        default -> throw unexpectedCaseExc(topRefableS);
      };
    } finally {
      typeSbConv.removeLastVarMap();
    }
  }

  private FuncB compileMonoFunc(MonoFuncS monoFuncS, Map<String, TypeB> varMap) {
    var key = new CacheKey(monoFuncS.name(), varMap);
    return computeIfAbsent(funcCache, key, name -> compileMonoFuncImpl(monoFuncS, varMap));
  }

  private FuncB compileMonoFuncImpl(MonoFuncS funcS, Map<String, TypeB> varMap) {
    try {
      callStack.push(funcS.params());
      var funcB = switch (funcS) {
        case AnnFuncS n -> compileAnnFunc(n, varMap);
        case DefFuncS d -> compileDefFunc(d);
        case SyntCtorS c -> compileSyntCtor(c);
      };
      nals.put(funcB, funcS);
      return funcB;
    } finally {
      callStack.pop();
    }
  }

  private FuncB compileAnnFunc(AnnFuncS annFuncS, Map<String, TypeB> varMap) {
    var annName = annFuncS.ann().name();
    return switch (annName) {
      case BYTECODE -> fetchFuncBytecode(annFuncS, varMap);
      case NATIVE_PURE, NATIVE_IMPURE -> compileNatFunc(annFuncS);
      default -> throw new CompilerExc("Illegal function annotation: " + annName + ".");
    };
  }

  private FuncB fetchFuncBytecode(AnnFuncS annFuncS, Map<String, TypeB> varMap) {
    var ann = annFuncS.ann();
    var funcTB = convertFuncT(annFuncS.type());
    var name = annFuncS.name();
    return (FuncB) fetchBytecode(ann, funcTB, name, varMap);
  }

  private FuncB compileDefFunc(DefFuncS defFuncS) {
    var funcTB = convertFuncT(defFuncS.type());
    var body = compileObj(defFuncS.body());
    return bytecodeF.func(funcTB, body);
  }

  private FuncB compileNatFunc(AnnFuncS natFuncS) {
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

  private FuncB compileSyntCtor(SyntCtorS syntCtorS) {
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

  private ObjB compileMonoRef(MonoRefS monoRefS) {
    return switch (defs.topRefables().get(monoRefS.name())) {
      case MonoFuncS monoFuncS -> compileObj(monoFuncS);
      case ValS valS -> compileObj(valS);
      case PolyFuncS f -> throw unexpectedCaseExc(f);
    };
  }

  private OrderB compileOrder(OrderS orderS) {
    var arrayTB = convertArrayT(orderS.type());
    var elemsB = compileObjs(orderS.elems());
    return bytecodeF.order(arrayTB, elemsB);
  }

  private ParamRefB compileParamRef(ParamRefS paramRefS) {
    var index = callStack.peek().indexMap().get(paramRefS.paramName());
    return bytecodeF.paramRef(convertT(paramRefS.type()), BigInteger.valueOf(index));
  }

  private SelectB compileSelect(SelectS selectS) {
    var selectableB = compileObj(selectS.selectable());
    var structTS = (StructTS) selectS.selectable().type();
    var indexJ = structTS.fields().indexMap().get(selectS.field());
    var indexB = bytecodeF.int_(BigInteger.valueOf(indexJ));
    nals.put(indexB, selectS);
    return bytecodeF.select(convertT(selectS.type()), selectableB, indexB);
  }

  private StringB compileString(StringS stringS) {
    return bytecodeF.string(stringS.string());
  }

  private ObjB compileVal(ValS valS) {
    return computeIfAbsent(valCache, valS.name(), name -> compileValImpl(valS));
  }

  private ObjB compileValImpl(ValS valS) {
    var objB = switch (valS) {
      case AnnValS annValS -> compileAnnVal(annValS);
      case DefValS defValS -> compileObj(defValS.body());
    };
    var typeB = typeSbConv.convert(valS.type());
    if (!typeB.equals(objB.type())) {
      var funcB = bytecodeF.func(bytecodeF.funcT(typeB, list()), objB);
      var callB = bytecodeF.call(typeB, funcB, bytecodeF.combine(bytecodeF.tupleT(list()), list()));
      nals.put(funcB, valS);
      nals.put(callB, valS);
      return callB;
    }
    return objB;
  }

  private ObjB compileAnnVal(AnnValS annValS) {
    var annName = annValS.ann().name();
    return switch (annName) {
      case BYTECODE ->  fetchValBytecode(annValS);
      default -> throw new CompilerExc("Illegal value annotation: " + annName + ".");
    };
  }

  private ObjB fetchValBytecode(AnnValS annValS) {
    var ann = annValS.ann();
    var typeB = convertT(annValS.type());
    var name = annValS.name();
    var varMap = ImmutableMap.<String, TypeB>of();
    return fetchBytecode(ann, typeB, name, varMap);
  }

  // helpers

  private ObjB fetchBytecode(AnnS ann, TypeB typeB, String name, Map<String, TypeB> varMap) {
    var jar = loadNativeJar(ann.loc());
    var bytecodeTry = bytecodeLoader.load(name, jar, ann.path().string(), varMap);
    if (!bytecodeTry.isPresent()) {
      throw new CompilerExc(ann.loc() + ": " + bytecodeTry.error());
    }
    var bytecodeB = bytecodeTry.result();
    if (!bytecodeB.type().equals(typeB)) {
      throw new CompilerExc(ann.loc() + ": Bytecode provider returned object of wrong type "
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
      throw new CompilerExc(message);
    }
  }

  private TypeB convertT(MonoTS monoTS) {
    return typeSbConv.convert(monoTS);
  }

  private ArrayTB convertArrayT(ArrayTS typeS) {
    return typeSbConv.convert(typeS);
  }

  private FuncTB convertFuncT(MonoFuncTS monoFuncTS) {
    return typeSbConv.convert(monoFuncTS);
  }

  private static record CacheKey(String funcName, Map<String, TypeB> varMap) {
  }
}
