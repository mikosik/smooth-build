package org.smoothbuild.compile;

import static org.smoothbuild.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.computeIfAbsent;

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
import org.smoothbuild.bytecode.obj.expr.CallB;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.MethodTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.lang.define.AnnFuncS;
import org.smoothbuild.lang.define.AnnS;
import org.smoothbuild.lang.define.AnnValS;
import org.smoothbuild.lang.define.DefFuncS;
import org.smoothbuild.lang.define.DefValS;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.FuncS;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.define.Nal;
import org.smoothbuild.lang.define.NalImpl;
import org.smoothbuild.lang.define.SyntCtorS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.expr.BlobS;
import org.smoothbuild.lang.expr.CallS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.lang.expr.OrderS;
import org.smoothbuild.lang.expr.ParamRefS;
import org.smoothbuild.lang.expr.SelectS;
import org.smoothbuild.lang.expr.StringS;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class Compiler {
  private final BytecodeF bytecodeF;
  private final TypingB typing;
  private final DefsS defs;
  private final TypeSbConv typeSbConv;
  private final FileLoader fileLoader;
  private final BytecodeLoader bytecodeLoader;
  private final Deque<NList<ItemS>> callStack;
  private final Map<String, FuncB> funcCache;
  private final Map<String, ObjB> valCache;
  private final Map<ObjB, Nal> nals;

  @Inject
  public Compiler(BytecodeF bytecodeF, TypingB typing, DefsS defs,
      TypeSbConv typeSbConv, FileLoader fileLoader, BytecodeLoader bytecodeLoader) {
    this.bytecodeF = bytecodeF;
    this.typing = typing;
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

  private FuncB compileFunc(FuncS funcS) {
    return computeIfAbsent(funcCache, funcS.name(), name -> compileFuncImpl(funcS));
  }

  private FuncB compileFuncImpl(FuncS funcS) {
    try {
      callStack.push(funcS.params());
      var funcB = switch (funcS) {
        case AnnFuncS n -> compileAnnFunc(n);
        case DefFuncS d -> compileDefFunc(d);
        case SyntCtorS c -> compileSyntCtor(c);
      };
      nals.put(funcB, funcS);
      return funcB;
    } finally {
      callStack.pop();
    }
  }

  private FuncB compileAnnFunc(AnnFuncS annFuncS) {
    var annName = annFuncS.ann().name();
    return switch (annName) {
      case BYTECODE -> fetchFuncBytecode(annFuncS);
      case NATIVE_PURE, NATIVE_IMPURE -> compileNatFunc(annFuncS);
      default -> throw new CompilerExc("Illegal function annotation: " + annName + ".");
    };
  }

  private FuncB fetchFuncBytecode(AnnFuncS annFuncS) {
    return (FuncB) fetchBytecode(annFuncS.ann(), convertFuncT(annFuncS.type()), annFuncS.name());
  }

  private FuncB compileDefFunc(DefFuncS defFuncS) {
    var funcTB = convertFuncT(defFuncS.type());
    var body = compileExpr(defFuncS.body());
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

  // handling value

  private ObjB compileVal(ValS valS) {
    return computeIfAbsent(valCache, valS.name(), name -> compileValImpl(valS));
  }

  private ObjB compileValImpl(ValS valS) {
    var exprB = switch (valS) {
      case AnnValS annValS -> compileAnnVal(annValS);
      case DefValS defValS -> compileExpr(defValS.body());
    };
    var typeB = typeSbConv.convert(valS.type());
    if (!typeB.equals(exprB.type())) {
      var funcB = bytecodeF.func(bytecodeF.funcT(typeB, list()), exprB);
      var callB = bytecodeF.call(typeB, funcB, bytecodeF.combine(bytecodeF.tupleT(list()), list()));
      nals.put(funcB, valS);
      nals.put(callB, valS);
      return callB;
    }
    return exprB;
  }

  private ObjB compileAnnVal(AnnValS annValS) {
    var annName = annValS.ann().name();
    return switch (annName) {
      case BYTECODE ->  fetchBytecode(annValS.ann(), convertT(annValS.type()), annValS.name());
      default -> throw new CompilerExc("Illegal value annotation: " + annName + ".");
    };
  }

  private ObjB fetchBytecode(AnnS ann, TypeB typeB, String name) {
    var jar = loadNativeJar(ann.loc());
    var bytecodeTry = bytecodeLoader.load(name, jar, ann.path().string());
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

  // handling expressions

  private ImmutableList<ObjB> compileExprs(ImmutableList<ExprS> exprs) {
    return map(exprs, this::compileExpr);
  }

  public ObjB compileExpr(ExprS exprS) {
    return switch (exprS) {
      case BlobS blobS -> compileAndCacheNal(blobS, this::compileBlob);
      case CallS callS -> compileAndCacheNal(callS, this::compileCall);
      case IntS intS -> compileAndCacheNal(intS, this::compileInt);
      case OrderS orderS -> compileAndCacheNal(orderS, this::compileOrder);
      case ParamRefS paramRefS -> compileAndCacheNal(paramRefS, this::compileParamRef);
      case TopRefS topRefS -> compileTopRef(topRefS);
      case SelectS selectS -> compileAndCacheNal(selectS, this::compileSelect);
      case StringS stringS -> compileAndCacheNal(stringS, this::compileString);
    };
  }

  private <T extends ExprS> ObjB compileAndCacheNal(T exprS, Function<T, ObjB> mapping) {
    var objB = mapping.apply(exprS);
    nals.put(objB, exprS);
    return objB;
  }

  private BlobB compileBlob(BlobS blobS) {
    return bytecodeF.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallB compileCall(CallS callS) {
    var callableB = compileExpr(callS.callable());
    var argsB = compileExprs(callS.args());

    var argTupleT = bytecodeF.tupleT(map(argsB, ObjB::type));
    var paramTupleT = ((FuncTB) callableB.type()).paramsTuple();
    var vars = typing.inferVarBoundsLower(paramTupleT, argTupleT);
    var actualParamTupleT = (TupleTB) typing.mapVarsLower(paramTupleT, vars);
    var combineB = bytecodeF.combine(actualParamTupleT, argsB);

    nals.put(combineB, new NalImpl("{}", callS.loc()));
    return bytecodeF.call(convertT(callS.type()), callableB, combineB);
  }

  private IntB compileInt(IntS intS) {
    return bytecodeF.int_(intS.bigInteger());
  }

  private OrderB compileOrder(OrderS orderS) {
    var arrayTB = convertArrayT(orderS.type());
    var elemsB = compileExprs(orderS.elems());
    return bytecodeF.order(arrayTB, elemsB);
  }

  private ParamRefB compileParamRef(ParamRefS paramRefS) {
    var index = callStack.peek().indexMap().get(paramRefS.paramName());
    return bytecodeF.paramRef(convertT(paramRefS.type()), BigInteger.valueOf(index));
  }

  private ObjB compileTopRef(TopRefS topRefS) {
    return switch (defs.topEvals().get(topRefS.name())) {
      case FuncS f -> compileFunc(f);
      case ValS v -> compileVal(v);
    };
  }

  private SelectB compileSelect(SelectS selectS) {
    var selectableB = compileExpr(selectS.selectable());
    var structTS = (StructTS) selectS.selectable().type();
    var indexJ = structTS.fields().indexMap().get(selectS.field());
    var indexB = bytecodeF.int_(BigInteger.valueOf(indexJ));
    nals.put(indexB, selectS);
    return bytecodeF.select(convertT(selectS.type()), selectableB, indexB);
  }

  private StringB compileString(StringS stringS) {
    return bytecodeF.string(stringS.string());
  }

  // helpers

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

  private TypeB convertT(TypeS typeS) {
    return typeSbConv.convert(typeS);
  }

  private ArrayTB convertArrayT(ArrayTS typeS) {
    return typeSbConv.convert(typeS);
  }

  private TupleTB convertStructT(StructTS typeS) {
    return typeSbConv.convert(typeS);
  }

  private FuncTB convertFuncT(FuncTS funcTS) {
    return typeSbConv.convert(funcTS);
  }
}
