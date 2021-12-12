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
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MethodH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.FuncTH;
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
  private final Map<String, FuncH> funcCache;
  private final Map<String, ObjH> valCache;
  private final Map<ObjH, Nal> nals;

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

  public ImmutableMap<ObjH, Nal> nals() {
    return ImmutableMap.copyOf(nals);
  }

  private FuncH convertFunc(FuncS funcS) {
    return computeIfAbsent(funcCache, funcS.name(), name -> convertFuncImpl(funcS));
  }

  private FuncH convertFuncImpl(FuncS funcS) {
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

  private FuncH convertDefFunc(DefFuncS defFuncS) {
    var funcTH = convertFuncT(defFuncS.type());
    var body = convertExpr(defFuncS.body());
    return objFactory.func(funcTH, body);
  }

  private FuncH convertIfFunc(IfFuncS ifFuncS) {
    var funcTH = convertFuncT(ifFuncS.type());
    var conditionH = objFactory.paramRef(ZERO, objFactory.boolT());
    var resTH = funcTH.res();
    var thenH = objFactory.paramRef(ONE, resTH);
    var elseH = objFactory.paramRef(TWO, resTH);
    var bodyH = objFactory.if_(conditionH, thenH, elseH);
    nals.put(bodyH, ifFuncS);
    return objFactory.func(funcTH, bodyH);
  }

  private FuncH convertMapFunc(MapFuncS mapFuncS) {
    var funcTH = convertFuncT(mapFuncS.type());
    var inputArrayT = (ArrayTH) funcTH.params().get(0);
    var mappingFuncT = (FuncTH) funcTH.params().get(1);
    var arrayParam = objFactory.paramRef(ZERO, inputArrayT);
    var mappingFuncParam = objFactory.paramRef(ONE, mappingFuncT);
    var bodyH = objFactory.map(arrayParam, mappingFuncParam);
    nals.put(bodyH, mapFuncS);
    return objFactory.func(funcTH, bodyH);
  }

  private FuncH convertNatFunc(NatFuncS natFuncS) {
    var funcTH = convertFuncT(natFuncS.type());
    var methodH = createMethodH(natFuncS.ann(), funcTH);
    var args = objFactory.combine(createParamRefsH(funcTH.params()));
    var bodyH = objFactory.invoke(methodH, args);
    nals.put(bodyH, natFuncS);
    return objFactory.func(funcTH, bodyH);
  }

  private MethodH createMethodH(AnnS annS, FuncTH funcTH) {
    var methodTH = objFactory.methodT(funcTH.res(), funcTH.params());
    var jarH = loadNativeJar(annS);
    var classBinaryNameH = objFactory.string(annS.path().string());
    var isPureH = objFactory.bool(annS.isPure());
    return objFactory.method(methodTH, jarH, classBinaryNameH, isPureH);
  }

  private ImmutableList<ObjH> createParamRefsH(ImmutableList<TypeH> paramTs) {
    Builder<ObjH> builder = ImmutableList.builder();
    for (int i = 0; i < paramTs.size(); i++) {
      builder.add(objFactory.paramRef(BigInteger.valueOf(i), paramTs.get(i)));
    }
    return builder.build();
  }

  // handling value

  private ObjH convertVal(DefValS defValS) {
    return computeIfAbsent(valCache, defValS.name(), name -> convertExpr(defValS.body()));
  }

  // handling expressions

  private ImmutableList<ObjH> convertExprs(ImmutableList<ExprS> exprs) {
    return map(exprs, this::convertExpr);
  }

  public ObjH convertExpr(ExprS exprS) {
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

  private <T extends ExprS> ObjH convertAndCacheNal(T exprS, Function<T, ObjH> mapping) {
    var objH = mapping.apply(exprS);
    nals.put(objH, exprS);
    return objH;
  }

  private BlobH convertBlob(BlobS blobS) {
    return objFactory.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallH convertCall(CallS callS) {
    var callableH = convertExpr(callS.callable());
    var argsH = convertExprs(callS.args());
    var combineH = objFactory.combine(argsH);
    nals.put(combineH, new NalImpl("{}", callS.loc()));
    return objFactory.call(callableH, combineH);
  }

  private CombineH convertCombine(CombineS combineS) {
    return objFactory.combine(convertExprs(combineS.elems()));
  }

  private IntH convertInt(IntS intS) {
    return objFactory.int_(intS.bigInteger());
  }

  private OrderH convertOrder(OrderS orderS) {
    return objFactory.order(convertExprs(orderS.elems()));
  }

  private ParamRefH convertParamRef(ParamRefS paramRefS) {
    var index = callStack.peek().indexMap().get(paramRefS.paramName());
    return objFactory.paramRef(BigInteger.valueOf(index), convertT(paramRefS.type()));
  }

  private ObjH convertTopRef(TopRefS topRefS) {
    return switch (defs.topEvals().get(topRefS.name())) {
      case FuncS f -> convertFunc(f);
      case DefValS v -> convertVal(v);
    };
  }

  private SelectH convertSelect(SelectS selectS) {
    var selectableH = convertExpr(selectS.selectable());
    var structTS = (StructTS) selectS.selectable().type();
    var indexJ = structTS.fields().indexMap().get(selectS.field());
    var indexH = objFactory.int_(BigInteger.valueOf(indexJ));
    nals.put(indexH, selectS);
    return objFactory.select(selectableH, indexH);
  }

  private StringH convertString(StringS stringS) {
    return objFactory.string(stringS.string());
  }

  // helpers

  private BlobH loadNativeJar(AnnS ann) {
    var filePath = ann.loc().file().withExtension("jar");
    try {
      return fileLoader.load(filePath);
    } catch (FileNotFoundException e) {
      String message = ann.loc() + ": Error loading native jar: File %s doesn't exist."
          .formatted(filePath.q());
      throw new QuitExc(message);
    }
  }

  private TypeH convertT(TypeS typeS) {
    return typeShConv.convert(typeS);
  }

  private FuncTH convertFuncT(FuncTS funcTS) {
    return typeShConv.convert(funcTS);
  }
}
