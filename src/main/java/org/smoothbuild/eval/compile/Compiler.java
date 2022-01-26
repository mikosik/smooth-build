package org.smoothbuild.eval.compile;

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

import org.smoothbuild.bytecode.ByteCodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.expr.CallB;
import org.smoothbuild.bytecode.obj.expr.CombineB;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.BoolB;
import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.lang.base.define.BoolValS;
import org.smoothbuild.lang.base.define.DefFuncS;
import org.smoothbuild.lang.base.define.DefValS;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.FuncS;
import org.smoothbuild.lang.base.define.IfFuncS;
import org.smoothbuild.lang.base.define.ItemS;
import org.smoothbuild.lang.base.define.MapFuncS;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.lang.base.define.NatFuncS;
import org.smoothbuild.lang.base.define.ValS;
import org.smoothbuild.lang.base.type.impl.ArrayTS;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.StructTS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.expr.AnnS;
import org.smoothbuild.lang.expr.BlobS;
import org.smoothbuild.lang.expr.CallS;
import org.smoothbuild.lang.expr.CombineS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.lang.expr.OrderS;
import org.smoothbuild.lang.expr.ParamRefS;
import org.smoothbuild.lang.expr.SelectS;
import org.smoothbuild.lang.expr.StringS;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.vm.java.FileLoader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class Compiler {
  private final ByteCodeF byteCodeF;
  private final TypingB typing;
  private final DefsS defs;
  private final TypeSbConv typeSbConv;
  private final FileLoader fileLoader;
  private final Deque<NList<ItemS>> callStack;
  private final Map<String, FuncB> funcCache;
  private final Map<String, ObjB> valCache;
  private final Map<ObjB, Nal> nals;

  @Inject
  public Compiler(ByteCodeF byteCodeF, TypingB typing, DefsS defs,
      TypeSbConv typeSbConv, FileLoader fileLoader) {
    this.byteCodeF = byteCodeF;
    this.typing = typing;
    this.defs = defs;
    this.typeSbConv = typeSbConv;
    this.fileLoader = fileLoader;
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
        case DefFuncS d -> compileDefFunc(d);
        case IfFuncS i -> compileIfFunc(i);
        case MapFuncS m -> compileMapFunc(m);
        case NatFuncS n -> compileNatFunc(n);
      };
      nals.put(funcB, funcS);
      return funcB;
    } finally {
      callStack.pop();
    }
  }

  private FuncB compileDefFunc(DefFuncS defFuncS) {
    var funcTB = convertFuncT(defFuncS.type());
    var body = compileExpr(defFuncS.body());
    return byteCodeF.func(funcTB, body);
  }

  private FuncB compileIfFunc(IfFuncS ifFuncS) {
    var funcTB = convertFuncT(ifFuncS.type());
    var paramRefsB = createParamRefsB(funcTB.params());
    var bodyB = byteCodeF.if_(paramRefsB.get(0), paramRefsB.get(1), paramRefsB.get(2));
    nals.put(bodyB, ifFuncS);
    return byteCodeF.func(funcTB, bodyB);
  }

  private FuncB compileMapFunc(MapFuncS mapFuncS) {
    var funcTB = convertFuncT(mapFuncS.type());
    var paramRefsB = createParamRefsB(funcTB.params());
    var bodyB = byteCodeF.map(paramRefsB.get(0), paramRefsB.get(1));
    nals.put(bodyB, mapFuncS);
    return byteCodeF.func(funcTB, bodyB);
  }

  private FuncB compileNatFunc(NatFuncS natFuncS) {
    var funcTB = convertFuncT(natFuncS.type());
    var methodB = createMethodB(natFuncS.ann(), funcTB);
    var paramRefsB = createParamRefsB(funcTB.params());
    var paramsTuple = byteCodeF.tupleT(map(paramRefsB, ObjB::type));
    var args = byteCodeF.combine(paramsTuple, paramRefsB);
    var bodyB = byteCodeF.invoke(typing.closeVars(funcTB.res()), methodB, args);
    nals.put(bodyB, natFuncS);
    return byteCodeF.func(funcTB, bodyB);
  }

  private MethodB createMethodB(AnnS annS, FuncTB funcTB) {
    var methodTB = byteCodeF.methodT(funcTB.res(), funcTB.params());
    var jarB = loadNativeJar(annS);
    var classBinaryNameB = byteCodeF.string(annS.path().string());
    var isPureB = byteCodeF.bool(annS.isPure());
    return byteCodeF.method(methodTB, jarB, classBinaryNameB, isPureB);
  }

  private ImmutableList<ObjB> createParamRefsB(ImmutableList<TypeB> paramTs) {
    Builder<ObjB> builder = ImmutableList.builder();
    for (int i = 0; i < paramTs.size(); i++) {
      var closedT = typing.closeVars(paramTs.get(i));
      builder.add(byteCodeF.paramRef(closedT, BigInteger.valueOf(i)));
    }
    return builder.build();
  }

  // handling value

  private ObjB compileVal(ValS valS) {
    return computeIfAbsent(valCache, valS.name(), name -> compileValImpl(valS));
  }

  private ObjB compileValImpl(ValS valS) {
    return switch (valS) {
      case DefValS defValS -> compileExpr(defValS.body());
      case BoolValS boolValS -> compileBoolVal(boolValS);
    };
  }

  private BoolB compileBoolVal(BoolValS boolValS) {
    var boolB = byteCodeF.bool(boolValS.valJ());
    nals.put(boolB, boolValS);
    return boolB;
  }

  // handling expressions

  private ImmutableList<ObjB> compileExprs(ImmutableList<ExprS> exprs) {
    return map(exprs, this::compileExpr);
  }

  public ObjB compileExpr(ExprS exprS) {
    return switch (exprS) {
      case BlobS blobS -> compileAndCacheNal(blobS, this::compileBlob);
      case CallS callS -> compileAndCacheNal(callS, this::compileCall);
      case CombineS combineS -> compileAndCacheNal(combineS, this::compileCombine);
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
    return byteCodeF.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallB compileCall(CallS callS) {
    var callableB = compileExpr(callS.callable());
    var argsB = compileExprs(callS.args());

    var argTupleT = byteCodeF.tupleT(map(argsB, ObjB::type));
    var paramTupleT = ((FuncTB) callableB.type()).paramsTuple();
    var vars = typing.inferVarBoundsLower(paramTupleT, argTupleT);
    var actualParamTupleT = (TupleTB) typing.mapVarsLower(paramTupleT, vars);
    var combineB = byteCodeF.combine(actualParamTupleT, argsB);

    nals.put(combineB, new NalImpl("{}", callS.loc()));
    return byteCodeF.call(convertT(callS.type()), callableB, combineB);
  }

  private CombineB compileCombine(CombineS combineS) {
    var evalT = convertStructT(combineS.type());
    var items = compileExprs(combineS.elems());
    return byteCodeF.combine(evalT, items);
  }

  private IntB compileInt(IntS intS) {
    return byteCodeF.int_(intS.bigInteger());
  }

  private OrderB compileOrder(OrderS orderS) {
    var arrayTB = convertArrayT(orderS.type());
    var elemsB = compileExprs(orderS.elems());
    return byteCodeF.order(arrayTB, elemsB);
  }

  private ParamRefB compileParamRef(ParamRefS paramRefS) {
    var index = callStack.peek().indexMap().get(paramRefS.paramName());
    return byteCodeF.paramRef(convertT(paramRefS.type()), BigInteger.valueOf(index));
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
    var indexB = byteCodeF.int_(BigInteger.valueOf(indexJ));
    nals.put(indexB, selectS);
    return byteCodeF.select(convertT(selectS.type()), selectableB, indexB);
  }

  private StringB compileString(StringS stringS) {
    return byteCodeF.string(stringS.string());
  }

  // helpers

  private BlobB loadNativeJar(AnnS ann) {
    var filePath = ann.loc().file().withExtension("jar");
    try {
      return fileLoader.load(filePath);
    } catch (FileNotFoundException e) {
      String message = ann.loc() + ": Error loading native jar: File %s doesn't exist."
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
