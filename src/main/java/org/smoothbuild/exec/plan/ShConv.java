package org.smoothbuild.exec.plan;

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

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.expr.CallB;
import org.smoothbuild.db.object.obj.expr.CombineB;
import org.smoothbuild.db.object.obj.expr.OrderB;
import org.smoothbuild.db.object.obj.expr.ParamRefB;
import org.smoothbuild.db.object.obj.expr.SelectB;
import org.smoothbuild.db.object.obj.val.BlobB;
import org.smoothbuild.db.object.obj.val.FuncB;
import org.smoothbuild.db.object.obj.val.IntB;
import org.smoothbuild.db.object.obj.val.MethodB;
import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.db.object.type.val.ArrayTB;
import org.smoothbuild.db.object.type.val.FuncTB;
import org.smoothbuild.db.object.type.val.TupleTB;
import org.smoothbuild.exec.java.FileLoader;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class ShConv {
  private final ObjFactory objFactory;
  private final DefsS defs;
  private final TypeShConv typeShConv;
  private final FileLoader fileLoader;
  private final Deque<NList<ItemS>> callStack;
  private final Map<String, FuncB> funcCache;
  private final Map<String, ObjB> valCache;
  private final Map<ObjB, Nal> nals;

  @Inject
  public ShConv(ObjFactory objFactory, DefsS defs, TypeShConv typeShConv, FileLoader fileLoader) {
    this.objFactory = objFactory;
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

  private FuncB convertFunc(FuncS funcS) {
    return computeIfAbsent(funcCache, funcS.name(), name -> convertFuncImpl(funcS));
  }

  private FuncB convertFuncImpl(FuncS funcS) {
    try {
      callStack.push(funcS.params());
      var funcH = switch (funcS) {
        case DefFuncS d -> convertDefFunc(d);
        case IfFuncS i -> convertIfFunc(i);
        case MapFuncS m -> convertMapFunc(m);
        case NatFuncS n -> convertNatFunc(n);
      };
      nals.put(funcH, funcS);
      return funcH;
    } finally {
      callStack.pop();
    }
  }

  private FuncB convertDefFunc(DefFuncS defFuncS) {
    var funcTB = convertFuncT(defFuncS.type());
    var body = convertExpr(defFuncS.body());
    return objFactory.func(funcTB, body);
  }

  private FuncB convertIfFunc(IfFuncS ifFuncS) {
    var funcTB = convertFuncT(ifFuncS.type());
    var conditionH = objFactory.paramRef(objFactory.boolT(), ZERO);
    var resTB = funcTB.res();
    var thenB = objFactory.paramRef(resTB, ONE);
    var elseB = objFactory.paramRef(resTB, TWO);
    var bodyB = objFactory.if_(conditionH, thenB, elseB);
    nals.put(bodyB, ifFuncS);
    return objFactory.func(funcTB, bodyB);
  }

  private FuncB convertMapFunc(MapFuncS mapFuncS) {
    var funcTB = convertFuncT(mapFuncS.type());
    var inputArrayT = (ArrayTB) funcTB.params().get(0);
    var mappingFuncT = (FuncTB) funcTB.params().get(1);
    var arrayParam = objFactory.paramRef(inputArrayT, ZERO);
    var mappingFuncParam = objFactory.paramRef(mappingFuncT, ONE);
    var bodyB = objFactory.map(arrayParam, mappingFuncParam);
    nals.put(bodyB, mapFuncS);
    return objFactory.func(funcTB, bodyB);
  }

  private FuncB convertNatFunc(NatFuncS natFuncS) {
    var funcTB = convertFuncT(natFuncS.type());
    var methodB = createMethodH(natFuncS.ann(), funcTB);
    var args = objFactory.combine(funcTB.paramsTuple(), createParamRefsH(funcTB.params()));
    var bodyB = objFactory.invoke(methodB, args);
    nals.put(bodyB, natFuncS);
    return objFactory.func(funcTB, bodyB);
  }

  private MethodB createMethodH(AnnS annS, FuncTB funcTB) {
    var methodTB = objFactory.methodT(funcTB.res(), funcTB.params());
    var jarB = loadNativeJar(annS);
    var classBinaryNameB = objFactory.string(annS.path().string());
    var isPureB = objFactory.bool(annS.isPure());
    return objFactory.method(methodTB, jarB, classBinaryNameB, isPureB);
  }

  private ImmutableList<ObjB> createParamRefsH(ImmutableList<TypeB> paramTs) {
    Builder<ObjB> builder = ImmutableList.builder();
    for (int i = 0; i < paramTs.size(); i++) {
      builder.add(objFactory.paramRef(paramTs.get(i), BigInteger.valueOf(i)));
    }
    return builder.build();
  }

  // handling value

  private ObjB convertVal(DefValS defValS) {
    return computeIfAbsent(valCache, defValS.name(), name -> convertExpr(defValS.body()));
  }

  // handling expressions

  private ImmutableList<ObjB> convertExprs(ImmutableList<ExprS> exprs) {
    return map(exprs, this::convertExpr);
  }

  public ObjB convertExpr(ExprS exprS) {
    return switch (exprS) {
      case BlobS blobS -> convertAndCacheNal(blobS, this::convertBlob);
      case CallS callS -> convertAndCacheNal(callS, this::convertCall);
      case CombineS combineS -> convertAndCacheNal(combineS, this::convertCombine);
      case IntS intS -> convertAndCacheNal(intS, this::convertInt);
      case OrderS orderS -> convertAndCacheNal(orderS, this::convertOrder);
      case ParamRefS paramRefS -> convertAndCacheNal(paramRefS, this::convertParamRef);
      case TopRefS topRefS -> convertTopRef(topRefS);
      case SelectS selectS -> convertAndCacheNal(selectS, this::convertSelect);
      case StringS stringS -> convertAndCacheNal(stringS, this::convertString);
    };
  }

  private <T extends ExprS> ObjB convertAndCacheNal(T exprS, Function<T, ObjB> mapping) {
    var objB = mapping.apply(exprS);
    nals.put(objB, exprS);
    return objB;
  }

  private BlobB convertBlob(BlobS blobS) {
    return objFactory.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallB convertCall(CallS callS) {
    var callableB = convertExpr(callS.callable());
    var argsB = convertExprs(callS.args());

    var argTupleT = objFactory.tupleT(map(argsB, ObjB::type));
    var paramTupleT = ((FuncTB) callableB.type()).paramsTuple();
    var typing = objFactory.typing();
    var vars = typing.inferVarBounds(paramTupleT, argTupleT, typing.factory().lower());
    var actualParamTupleT = (TupleTB) typing.mapVarsLower(paramTupleT, vars);
    var combineB = objFactory.combine(actualParamTupleT, argsB);

    nals.put(combineB, new NalImpl("{}", callS.loc()));
    return objFactory.call(callableB, combineB);
  }

  private CombineB convertCombine(CombineS combineS) {
    var evalT = convertStructT(combineS.type());
    var items = convertExprs(combineS.elems());
    return objFactory.combine(evalT, items);
  }

  private IntB convertInt(IntS intS) {
    return objFactory.int_(intS.bigInteger());
  }

  private OrderB convertOrder(OrderS orderS) {
    var arrayTB = convertArrayT(orderS.type());
    var elemsB = convertExprs(orderS.elems());
    return objFactory.order(arrayTB, elemsB);
  }

  private ParamRefB convertParamRef(ParamRefS paramRefS) {
    var index = callStack.peek().indexMap().get(paramRefS.paramName());
    return objFactory.paramRef(convertT(paramRefS.type()), BigInteger.valueOf(index));
  }

  private ObjB convertTopRef(TopRefS topRefS) {
    return switch (defs.topEvals().get(topRefS.name())) {
      case FuncS f -> convertFunc(f);
      case DefValS v -> convertVal(v);
    };
  }

  private SelectB convertSelect(SelectS selectS) {
    var selectableB = convertExpr(selectS.selectable());
    var structTS = (StructTS) selectS.selectable().type();
    var indexJ = structTS.fields().indexMap().get(selectS.field());
    var indexB = objFactory.int_(BigInteger.valueOf(indexJ));
    nals.put(indexB, selectS);
    return objFactory.select(selectableB, indexB);
  }

  private StringB convertString(StringS stringS) {
    return objFactory.string(stringS.string());
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
