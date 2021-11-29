package org.smoothbuild.exec.plan;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.IntStream.range;
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
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
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
import org.smoothbuild.lang.base.define.BoolValueS;
import org.smoothbuild.lang.base.define.CtorS;
import org.smoothbuild.lang.base.define.DefFuncS;
import org.smoothbuild.lang.base.define.DefinedValueS;
import org.smoothbuild.lang.base.define.DefinitionsS;
import org.smoothbuild.lang.base.define.FuncS;
import org.smoothbuild.lang.base.define.IfFuncS;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.MapFuncS;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.lang.base.define.NatFuncS;
import org.smoothbuild.lang.base.define.ValueS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.expr.BlobS;
import org.smoothbuild.lang.expr.CallS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.lang.expr.OrderS;
import org.smoothbuild.lang.expr.ParamRefS;
import org.smoothbuild.lang.expr.RefS;
import org.smoothbuild.lang.expr.SelectS;
import org.smoothbuild.lang.expr.StringS;
import org.smoothbuild.run.QuitException;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ShConverter {
  private final ObjFactory objFactory;
  private final DefinitionsS definitions;
  private final TypeShConverter typeShConverter;
  private final FileLoader fileLoader;
  private final Deque<NList<Item>> callStack;
  private final Map<String, FuncH> funcCache;
  private final Map<String, ObjectH> valueCache;
  private final Map<ObjectH, Nal> nals;

  @Inject
  public ShConverter(ObjFactory objFactory, DefinitionsS definitions,
      TypeShConverter typeShConverter, FileLoader fileLoader) {
    this.objFactory = objFactory;
    this.definitions = definitions;
    this.typeShConverter = typeShConverter;
    this.fileLoader = fileLoader;
    this.callStack = new LinkedList<>();
    this.funcCache = new HashMap<>();
    this.valueCache = new HashMap<>();
    this.nals = new HashMap<>();
  }

  public ImmutableMap<ObjectH, Nal> nals() {
    return ImmutableMap.copyOf(nals);
  }

  public FuncH convertFunc(FuncS funcS) {
    return computeIfAbsent(funcCache, funcS.name(), name -> convertFuncImpl(funcS));
  }

  private FuncH convertFuncImpl(FuncS funcS) {
    try {
      callStack.push(funcS.params());
      var funcH = switch (funcS) {
        case CtorS c -> convertCtor(c);
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

  private DefFuncH convertCtor(CtorS ctorS) {
    var type = objFactory.defFuncT(
        convertType(ctorS.resultType()),
        convertParams(ctorS.params()));
    var paramRefs = ctorParamRefs(ctorS);
    var body = objFactory.construct(paramRefs);
    nals.put(body, ctorS);
    return objFactory.defFunc(type, body);
  }

  private ImmutableList<ObjectH> ctorParamRefs(CtorS ctorS) {
    NList<Item> params = ctorS.params();
    ImmutableList<ObjectH> paramRefsH =
        range(0, params.size())
            .mapToObj(i -> newParamRef(params, i))
            .collect(toImmutableList());
    paramRefsH.forEach(p -> nals.put(p, ctorS));
    return paramRefsH;
  }

  private RefH newParamRef(NList<Item> items, int i) {
    var index = BigInteger.valueOf(i);
    var item = items.get(i);
    var typeS = item.type();
    var typeH = convertType(typeS);
    return objFactory.ref(index, typeH);
  }

  private NatFuncH convertNatFunc(NatFuncS natFuncS) {
    var resType = convertType(natFuncS.resultType());
    var paramTypes = convertParams(natFuncS.params());
    var jar = loadNatJar(natFuncS);
    var type = objFactory.natFuncT(resType, paramTypes);
    var ann = natFuncS.annotation();
    var classBinaryName = objFactory.string(ann.path().string());
    var isPure = objFactory.bool(ann.isPure());
    return objFactory.natFunc(type, jar, classBinaryName, isPure);
  }

  private ImmutableList<TypeH> convertParams(NList<Item> items) {
    return map(items, item -> convertType(item.type()));
  }

  private DefFuncH convertDefFunc(DefFuncS defFuncS) {
    var body = convertExpr(defFuncS.body());
    var resTypeH = convertType(defFuncS.resultType());
    var paramTypesH = convertParams(defFuncS.params());
    var type = objFactory.defFuncT(resTypeH, paramTypesH);
    return objFactory.defFunc(type, body);
  }

  // handling value

  public ObjectH convertVal(ValueS valueS) {
    return computeIfAbsent(valueCache, valueS.name(), name -> convertValImpl(valueS));
  }

  private ObjectH convertValImpl(ValueS valueS) {
    return switch (valueS) {
      case DefinedValueS defValS -> convertExpr(defValS.body());
      case BoolValueS boolValS -> convertBoolVal(boolValS);
    };
  }

  private BoolH convertBoolVal(BoolValueS boolValS) {
    var boolH = objFactory.bool(boolValS.valJ());
    nals.put(boolH, boolValS);
    return boolH;
  }

  // handling expressions

  private ObjectH convertExpr(ExprS exprS) {
    return switch (exprS) {
      case BlobS blobS -> convertAndStoreMapping(blobS, this::convertBlob);
      case CallS callS -> convertAndStoreMapping(callS, this::convertCall);
      case IntS intS -> convertAndStoreMapping(intS, this::convertInt);
      case OrderS orderS -> convertAndStoreMapping(orderS, this::convertOrd);
      case ParamRefS paramRefS -> convertAndStoreMapping(paramRefS, this::convertParamRef);
      case RefS refS -> convertRef(refS);
      case SelectS selectS -> convertAndStoreMapping(selectS, this::convertSel);
      case StringS stringS -> convertAndStoreMapping(stringS, this::convertStr);
    };
  }

  private <T extends ExprS> ObjectH convertAndStoreMapping(T exprS, Function<T, ObjectH> mapping) {
    var objH = mapping.apply(exprS);
    nals.put(objH, exprS);
    return objH;
  }

  private BlobH convertBlob(BlobS blobS) {
    return objFactory.blob(sink -> sink.write(blobS.byteString()));
  }

  private CallH convertCall(CallS callS) {
    var funcExprH = convertExpr(callS.funcExpr());
    var argsH = map(callS.arguments(), this::convertExpr);
    var construct = objFactory.construct(argsH);
    nals.put(construct, new NalImpl("{}", callS.location()));
    return objFactory.call(funcExprH, construct);
  }

  private IntH convertInt(IntS intS) {
    return objFactory.int_(intS.bigInteger());
  }

  private OrderH convertOrd(OrderS orderS) {
    var elemsH = map(orderS.elems(), this::convertExpr);
    return objFactory.order(elemsH);
  }

  private RefH convertParamRef(ParamRefS paramRefS) {
    var index = callStack.peek().indexMap().get(paramRefS.paramName());
    return objFactory.ref(BigInteger.valueOf(index), convertType(paramRefS.type()));
  }

  public ObjectH convertRef(RefS refS) {
    return switch (definitions.referencables().get(refS.name())) {
      case FuncS f -> convertFunc(f);
      case ValueS v -> convertVal(v);
    };
  }

  private SelectH convertSel(SelectS selectS) {
    var tupleH = convertExpr(selectS.structExpr());
    var indexH = objFactory.int_(BigInteger.valueOf(selectS.index()));
    return objFactory.select(tupleH, indexH);
  }

  private StringH convertStr(StringS stringS) {
    return objFactory.string(stringS.string());
  }

  // helpers


  private BlobH loadNatJar(NatFuncS natFuncS) {
    var filePath = natFuncS.annotation().location().file().withExtension("jar");
    try {
      return fileLoader.load(filePath);
    } catch (FileNotFoundException e) {
      String message = "Error loading native jar for `%s`: File %s doesn't exist."
          .formatted(natFuncS.name(), filePath.q());
      throw new QuitException(message);
    }
  }

  private TypeH convertType(TypeS typeS) {
    return typeShConverter.visit(typeS);
  }
}
