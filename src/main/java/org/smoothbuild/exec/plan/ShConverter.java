package org.smoothbuild.exec.plan;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.IntStream.range;
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

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.DefinedFunctionH;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NativeFunctionH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.exec.java.FileLoader;
import org.smoothbuild.lang.base.define.BoolValueS;
import org.smoothbuild.lang.base.define.ConstructorS;
import org.smoothbuild.lang.base.define.DefinedEvaluableS;
import org.smoothbuild.lang.base.define.DefinedFunctionS;
import org.smoothbuild.lang.base.define.DefinedValueS;
import org.smoothbuild.lang.base.define.DefinitionsS;
import org.smoothbuild.lang.base.define.FunctionS;
import org.smoothbuild.lang.base.define.IfFunctionS;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.MapFunctionS;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.lang.base.define.NativeEvaluableS;
import org.smoothbuild.lang.base.define.NativeFunctionS;
import org.smoothbuild.lang.base.define.TopEvaluableS;
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
  private final Map<String, FunctionH> cache;
  private final Map<ObjectH, Nal> nals;

  @Inject
  public ShConverter(ObjFactory objFactory, DefinitionsS definitions,
      TypeShConverter typeShConverter, FileLoader fileLoader) {
    this.objFactory = objFactory;
    this.definitions = definitions;
    this.typeShConverter = typeShConverter;
    this.fileLoader = fileLoader;
    this.callStack = new LinkedList<>();
    this.cache = new HashMap<>();
    this.nals = new HashMap<>();
  }

  public ImmutableMap<ObjectH, Nal> nals() {
    return ImmutableMap.copyOf(nals);
  }

  public FunctionH convertFunc(FunctionS functionS) {
    return computeIfAbsent(cache, functionS.name(), name -> convertFuncImpl(functionS));
  }

  private FunctionH convertFuncImpl(FunctionS functionS) {
    try {
      callStack.push(functionS.evaluationParameters());
      var functionH = switch (functionS) {
        case ConstructorS c -> convertCtor(c);
        case IfFunctionS i -> objFactory.ifFunction();
        case MapFunctionS m -> objFactory.mapFunction();
        case DefinedFunctionS d -> convertDefEval(d);
        case NativeFunctionS n -> convertNatFunc(n);
      };
      nals.put(functionH, functionS);
      return functionH;
    } finally {
      callStack.pop();
    }
  }

  private DefinedFunctionH convertCtor(ConstructorS constructorS) {
    var type = objFactory.definedFunctionType(
        convertType(constructorS.evaluationType()),
        convertParams(constructorS.evaluationParameters()));
    var paramRefs = ctorParamRefs(constructorS);
    var body = objFactory.construct(paramRefs);
    nals.put(body, constructorS);
    return objFactory.definedFunction(type, body);
  }

  private ImmutableList<ObjectH> ctorParamRefs(ConstructorS constructorS) {
    NList<Item> parameters = constructorS.parameters();
    ImmutableList<ObjectH> paramRefsH =
        range(0, parameters.size())
            .mapToObj(i -> newParamRef(parameters, i))
            .collect(toImmutableList());
    paramRefsH.forEach(p -> nals.put(p, constructorS));
    return paramRefsH;
  }

  private RefH newParamRef(NList<Item> items, int i) {
    var index = BigInteger.valueOf(i);
    var item = items.get(i);
    var typeS = item.type();
    var typeH = convertType(typeS);
    return objFactory.ref(index, typeH);
  }

  private NativeFunctionH convertNatFunc(NativeFunctionS nativeFunctionS) {
    var resType = convertType(nativeFunctionS.evaluationType());
    var paramTypes = convertParams(nativeFunctionS.evaluationParameters());
    var jar = loadNatJar(nativeFunctionS);
    var type = objFactory.nativeFunctionType(resType, paramTypes);
    var ann = nativeFunctionS.annotation();
    var classBinaryName = objFactory.string(ann.path().string());
    var isPure = objFactory.bool(ann.isPure());
    return objFactory.nativeFunction(type, jar, classBinaryName, isPure);
  }

  private ImmutableList<TypeHV> convertParams(NList<Item> items) {
    return map(items, item -> convertType(item.type()));
  }

  private DefinedFunctionH convertDefEval(DefinedEvaluableS definedEvaluableS) {
    var body = convertExpr(definedEvaluableS.body());
    var resTypeH = convertType(definedEvaluableS.evaluationType());
    var paramTypesH = convertParams(definedEvaluableS.evaluationParameters());
    var type = objFactory.definedFunctionType(resTypeH, paramTypesH);
    return objFactory.definedFunction(type, body);
  }

  // handling value

  public FunctionH convertVal(ValueS valueS) {
    return computeIfAbsent(cache, valueS.name(), name -> convertValImpl(valueS));
  }

  private FunctionH convertValImpl(ValueS valueS) {
    FunctionH exprH = switch (valueS) {
      case DefinedValueS defValS -> convertDefEval(defValS);
      case BoolValueS boolValS -> convertBoolVal(boolValS);
    };
    nals.put(exprH, valueS);
    return exprH;
  }

  private FunctionH convertBoolVal(BoolValueS boolValS) {
    var boolH = objFactory.bool(boolValS.valJ());
    nals.put(boolH, boolValS);
    var resTypeH = boolH.type();
    var type = objFactory.definedFunctionType(resTypeH, list());
    return objFactory.definedFunction(type, boolH);
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
      case default -> throw new RuntimeException(
          "Unknown expression class " + exprS.getClass().getCanonicalName());
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
    var funcExprH = convertExpr(callS.functionExpr());
    var argsH = map(callS.arguments(), this::convertExpr);
    var construct = objFactory.construct(argsH);
    nals.put(construct, new NalImpl("{}", callS.location()));
    return objFactory.call(funcExprH, construct);
  }

  private IntH convertInt(IntS intS) {
    return objFactory.int_(intS.bigInteger());
  }

  private OrderH convertOrd(OrderS orderS) {
    var elemsH = map(orderS.elements(), this::convertExpr);
    return objFactory.order(elemsH);
  }

  private RefH convertParamRef(ParamRefS paramRefS) {
    var index = callStack.peek().indexMap().get(paramRefS.paramName());
    return objFactory.ref(BigInteger.valueOf(index), convertType(paramRefS.type()));
  }

  public ObjectH convertRef(RefS refS) {
    TopEvaluableS evalS = definitions.referencables().get(refS.name());
    return switch (evalS) {
      case FunctionS functionS -> convertFunc(functionS);
      case ValueS valueS -> {
        var args = objFactory.construct(list());
        var func = convertVal(valueS);
        var callH = objFactory.call(func, args);
        nals.put(callH, refS);
        yield callH;
      }
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


  private BlobH loadNatJar(NativeEvaluableS nativeEvaluableS) {
    var filePath = nativeEvaluableS.annotation().location().file().withExtension("jar");
    try {
      return fileLoader.load(filePath);
    } catch (FileNotFoundException e) {
      String message = "Error loading native jar for `%s`: File %s doesn't exist."
          .formatted(nativeEvaluableS.name(), filePath.q());
      throw new QuitException(message);
    }
  }

  private TypeHV convertType(TypeS typeS) {
    return typeShConverter.visit(typeS);
  }
}
