package org.smoothbuild.eval.compile;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.math.BigInteger.ZERO;
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

import org.smoothbuild.bytecode.ByteCodeFactory;
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
import org.smoothbuild.run.QuitExc;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.vm.java.FileLoader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class Compiler {
  private final ByteCodeFactory byteCodeFactory;
  private final DefsS defs;
  private final TypeShConv typeShConv;
  private final FileLoader fileLoader;
  private final Deque<NList<ItemS>> callStack;
  private final Map<String, FuncB> funcCache;
  private final Map<String, ObjB> valCache;
  private final Map<ObjB, Nal> nals;

  @Inject
  public Compiler(ByteCodeFactory byteCodeFactory, DefsS defs, TypeShConv typeShConv, FileLoader fileLoader) {
    this.byteCodeFactory = byteCodeFactory;
    this.defs = defs;
    this.typeShConv = typeShConv;
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
      var funcH = switch (funcS) {
        case DefFuncS d -> compileDefFunc(d);
        case IfFuncS i -> compileIfFunc(i);
        case MapFuncS m -> compileMapFunc(m);
        case NatFuncS n -> compileNatFunc(n);
      };
      nals.put(funcH, funcS);
      return funcH;
    } finally {
      callStack.pop();
    }
  }

  private FuncB compileDefFunc(DefFuncS defFuncS) {
    var funcTB = convertFuncT(defFuncS.type());
    var body = compileExpr(defFuncS.body());
    return byteCodeFactory.func(funcTB, body);
  }

  private FuncB compileIfFunc(IfFuncS ifFuncS) {
    var funcTB = convertFuncT(ifFuncS.type());
    var conditionH = byteCodeFactory.paramRef(byteCodeFactory.boolT(), ZERO);
    var resTB = funcTB.res();
    var thenB = byteCodeFactory.paramRef(resTB, ONE);
    var elseB = byteCodeFactory.paramRef(resTB, TWO);
    var bodyB = byteCodeFactory.if_(conditionH, thenB, elseB);
    nals.put(bodyB, ifFuncS);
    return byteCodeFactory.func(funcTB, bodyB);
  }

  private FuncB compileMapFunc(MapFuncS mapFuncS) {
    var funcTB = convertFuncT(mapFuncS.type());
    var inputArrayT = (ArrayTB) funcTB.params().get(0);
    var mappingFuncT = (FuncTB) funcTB.params().get(1);
    var arrayParam = byteCodeFactory.paramRef(inputArrayT, ZERO);
    var mappingFuncParam = byteCodeFactory.paramRef(mappingFuncT, ONE);
    var bodyB = byteCodeFactory.map(arrayParam, mappingFuncParam);
    nals.put(bodyB, mapFuncS);
    return byteCodeFactory.func(funcTB, bodyB);
  }

  private FuncB compileNatFunc(NatFuncS natFuncS) {
    var funcTB = convertFuncT(natFuncS.type());
    var methodB = createMethodH(natFuncS.ann(), funcTB);
    var args = byteCodeFactory.combine(funcTB.paramsTuple(), createParamRefsH(funcTB.params()));
    var bodyB = byteCodeFactory.invoke(funcTB.res(), methodB, args);
    nals.put(bodyB, natFuncS);
    return byteCodeFactory.func(funcTB, bodyB);
  }

  private MethodB createMethodH(AnnS annS, FuncTB funcTB) {
    var methodTB = byteCodeFactory.methodT(funcTB.res(), funcTB.params());
    var jarB = loadNativeJar(annS);
    var classBinaryNameB = byteCodeFactory.string(annS.path().string());
    var isPureB = byteCodeFactory.bool(annS.isPure());
    return byteCodeFactory.method(methodTB, jarB, classBinaryNameB, isPureB);
  }

  private ImmutableList<ObjB> createParamRefsH(ImmutableList<TypeB> paramTs) {
    Builder<ObjB> builder = ImmutableList.builder();
    for (int i = 0; i < paramTs.size(); i++) {
      builder.add(byteCodeFactory.paramRef(paramTs.get(i), BigInteger.valueOf(i)));
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
    var boolB = byteCodeFactory.bool(boolValS.valJ());
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
    return byteCodeFactory.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallB compileCall(CallS callS) {
    var callableB = compileExpr(callS.callable());
    var argsB = compileExprs(callS.args());

    var argTupleT = byteCodeFactory.tupleT(map(argsB, ObjB::type));
    var paramTupleT = ((FuncTB) callableB.type()).paramsTuple();
    var typing = byteCodeFactory.typing();
    var vars = typing.inferVarBoundsLower(paramTupleT, argTupleT);
    var actualParamTupleT = (TupleTB) typing.mapVarsLower(paramTupleT, vars);
    var combineB = byteCodeFactory.combine(actualParamTupleT, argsB);

    nals.put(combineB, new NalImpl("{}", callS.loc()));
    return byteCodeFactory.call(convertT(callS.type()), callableB, combineB);
  }

  private CombineB compileCombine(CombineS combineS) {
    var evalT = convertStructT(combineS.type());
    var items = compileExprs(combineS.elems());
    return byteCodeFactory.combine(evalT, items);
  }

  private IntB compileInt(IntS intS) {
    return byteCodeFactory.int_(intS.bigInteger());
  }

  private OrderB compileOrder(OrderS orderS) {
    var arrayTB = convertArrayT(orderS.type());
    var elemsB = compileExprs(orderS.elems());
    return byteCodeFactory.order(arrayTB, elemsB);
  }

  private ParamRefB compileParamRef(ParamRefS paramRefS) {
    var index = callStack.peek().indexMap().get(paramRefS.paramName());
    return byteCodeFactory.paramRef(convertT(paramRefS.type()), BigInteger.valueOf(index));
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
    var indexB = byteCodeFactory.int_(BigInteger.valueOf(indexJ));
    nals.put(indexB, selectS);
    return byteCodeFactory.select(convertT(selectS.type()), selectableB, indexB);
  }

  private StringB compileString(StringS stringS) {
    return byteCodeFactory.string(stringS.string());
  }

  // helpers

  private BlobB loadNativeJar(AnnS ann) {
    var filePath = ann.loc().file().withExtension("jar");
    try {
      return fileLoader.load(filePath);
    } catch (FileNotFoundException e) {
      String message = ann.loc() + ": Error loading native jar: File %s doesn't exist."
          .formatted(filePath.q());
      throw new QuitExc(message);
    }
  }

  private TypeB convertT(TypeS typeS) {
    return typeShConv.convert(typeS);
  }

  private ArrayTB convertArrayT(ArrayTS typeS) {
    return typeShConv.convert(typeS);
  }

  private TupleTB convertStructT(StructTS typeS) {
    return typeShConv.convert(typeS);
  }

  private FuncTB convertFuncT(FuncTS funcTS) {
    return typeShConv.convert(funcTS);
  }
}