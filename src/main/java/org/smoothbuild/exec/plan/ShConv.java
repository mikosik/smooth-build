package org.smoothbuild.exec.plan;

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
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.DefFuncH;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.java.FileLoader;
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
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.expr.BlobS;
import org.smoothbuild.lang.expr.CallS;
import org.smoothbuild.lang.expr.CombineS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.lang.expr.OrderS;
import org.smoothbuild.lang.expr.ParamRefS;
import org.smoothbuild.lang.expr.RefS;
import org.smoothbuild.lang.expr.SelectS;
import org.smoothbuild.lang.expr.StringS;
import org.smoothbuild.run.QuitExc;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
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

  public FuncH convertFunc(FuncS funcS) {
    return computeIfAbsent(funcCache, funcS.name(), name -> convertFuncImpl(funcS));
  }

  private FuncH convertFuncImpl(FuncS funcS) {
    try {
      callStack.push(funcS.params());
      var funcH = switch (funcS) {
        case IfFuncS i -> objFactory.ifFunc();
        case MapFuncS m -> objFactory.mapFunc();
        case DefFuncS d -> convertDefFunc(d);
        case NatFuncS n -> convertNatFunc(n);
      };
      nals.put(funcH, funcS);
      return funcH;
    } finally {
      callStack.pop();
    }
  }

  private NatFuncH convertNatFunc(NatFuncS natFuncS) {
    var resType = convertType(natFuncS.resType());
    var paramTypes = convertParams(natFuncS.params());
    var jar = loadNatJar(natFuncS);
    var type = objFactory.natFuncT(resType, paramTypes);
    var ann = natFuncS.ann();
    var classBinaryName = objFactory.string(ann.path().string());
    var isPure = objFactory.bool(ann.isPure());
    return objFactory.natFunc(type, jar, classBinaryName, isPure);
  }

  private ImmutableList<TypeH> convertParams(NList<ItemS> items) {
    return map(items, item -> convertType(item.type()));
  }

  private DefFuncH convertDefFunc(DefFuncS defFuncS) {
    var body = convertExpr(defFuncS.body());
    var resTypeH = convertType(defFuncS.resType());
    var paramTypesH = convertParams(defFuncS.params());
    var type = objFactory.defFuncT(resTypeH, paramTypesH);
    return objFactory.defFunc(type, body);
  }

  // handling value

  public ObjH convertVal(ValS valS) {
    return computeIfAbsent(valCache, valS.name(), name -> convertValImpl(valS));
  }

  private ObjH convertValImpl(ValS valS) {
    return switch (valS) {
      case DefValS defValS -> convertExpr(defValS.body());
      case BoolValS boolValS -> convertBoolVal(boolValS);
    };
  }

  private BoolH convertBoolVal(BoolValS boolValS) {
    var boolH = objFactory.bool(boolValS.valJ());
    nals.put(boolH, boolValS);
    return boolH;
  }

  // handling expressions

  private ImmutableList<ObjH> convertExprs(ImmutableList<ExprS> exprs) {
    return map(exprs, this::convertExpr);
  }

  private ObjH convertExpr(ExprS exprS) {
    return switch (exprS) {
      case BlobS blobS -> convertAndStoreMapping(blobS, this::convertBlob);
      case CallS callS -> convertAndStoreMapping(callS, this::convertCall);
      case CombineS combineS -> convertAndStoreMapping(combineS, this::convertCombine);
      case IntS intS -> convertAndStoreMapping(intS, this::convertInt);
      case OrderS orderS -> convertAndStoreMapping(orderS, this::convertOrd);
      case ParamRefS paramRefS -> convertAndStoreMapping(paramRefS, this::convertParamRef);
      case RefS refS -> convertRef(refS);
      case SelectS selectS -> convertAndStoreMapping(selectS, this::convertSel);
      case StringS stringS -> convertAndStoreMapping(stringS, this::convertStr);
    };
  }

  private <T extends ExprS> ObjH convertAndStoreMapping(T exprS, Function<T, ObjH> mapping) {
    var objH = mapping.apply(exprS);
    nals.put(objH, exprS);
    return objH;
  }

  private BlobH convertBlob(BlobS blobS) {
    return objFactory.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallH convertCall(CallS callS) {
    var funcExprH = convertExpr(callS.funcExpr());
    var argsH = convertExprs(callS.args());
    var combine = objFactory.combine(argsH);
    nals.put(combine, new NalImpl("{}", callS.loc()));
    return objFactory.call(funcExprH, combine);
  }

  private CombineH convertCombine(CombineS combineS) {
    return objFactory.combine(convertExprs(combineS.elems()));
  }

  private IntH convertInt(IntS intS) {
    return objFactory.int_(intS.bigInteger());
  }

  private OrderH convertOrd(OrderS orderS) {
    return objFactory.order(convertExprs(orderS.elems()));
  }

  private ParamRefH convertParamRef(ParamRefS paramRefS) {
    var index = callStack.peek().indexMap().get(paramRefS.paramName());
    return objFactory.paramRef(BigInteger.valueOf(index), convertType(paramRefS.type()));
  }

  public ObjH convertRef(RefS refS) {
    return switch (defs.topEvals().get(refS.name())) {
      case FuncS f -> convertFunc(f);
      case ValS v -> convertVal(v);
    };
  }

  private SelectH convertSel(SelectS selectS) {
    var tupleH = convertExpr(selectS.structExpr());
    var structTypeS = (StructTypeS) selectS.structExpr().type();
    var indexJ = structTypeS.fields().indexMap().get(selectS.field());
    var indexH = objFactory.int_(BigInteger.valueOf(indexJ));
    return objFactory.select(tupleH, indexH);
  }

  private StringH convertStr(StringS stringS) {
    return objFactory.string(stringS.string());
  }

  // helpers

  private BlobH loadNatJar(NatFuncS natFuncS) {
    var filePath = natFuncS.ann().loc().file().withExtension("jar");
    try {
      return fileLoader.load(filePath);
    } catch (FileNotFoundException e) {
      String message = "Error loading native jar for `%s`: File %s doesn't exist."
          .formatted(natFuncS.name(), filePath.q());
      throw new QuitExc(message);
    }
  }

  private TypeH convertType(TypeS typeS) {
    return typeShConv.visit(typeS);
  }
}
