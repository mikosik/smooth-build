package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.BoundValueExpression;
import org.smoothbuild.lang.expr.ConvertExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.LiteralExpression;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.ParameterInfo;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.Native;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.RefNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.util.Dag;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class FunctionLoader {
  private final ValuesDb valuesDb;

  @Inject
  public FunctionLoader(ValuesDb valuesDb) {
    this.valuesDb = valuesDb;
  }

  public Function loadFunction(Functions loadedFunctions, FuncNode func) {
    return new Supplier<Function>() {
      @Override
      public Function get() {
        List<Parameter> parameters = map(func.params(), this::createParameter);
        Signature signature = new Signature(func.get(Type.class), func.name(), parameters);
        if (func.isNative()) {
          Native nativ = func.get(Native.class);
          HashCode hash = createNativeFunctionHash(nativ.jarFile().hash(), signature);
          boolean isCacheable = !nativ.method().isAnnotationPresent(NotCacheable.class);
          return new NativeFunction(nativ, signature, func.location(), isCacheable, hash);
        } else {
          return new DefinedFunction(signature, func.location(),
              implicitConversion(signature.type(), createExpression(func.expr())));
        }
      }

      private HashCode createNativeFunctionHash(HashCode jarHash, Signature signature) {
        Hasher hasher = Hash.newHasher();
        hasher.putBytes(jarHash.asBytes());
        hasher.putString(signature.name().toString(), SmoothConstants.CHARSET);
        return hasher.hash();
      }

      private Parameter createParameter(ParamNode p) {
        Type type = p.type().get(Type.class);
        Name name = p.name();
        Dag<Expression> defaultValue = p.hasDefaultValue()
            ? createExpression(p.defaultValue())
            : null;
        return new Parameter(type, name, defaultValue);
      }

      private Dag<Expression> createExpression(ExprNode expr) {
        if (expr instanceof CallNode) {
          return createCall((CallNode) expr);
        }
        if (expr instanceof RefNode) {
          return createReference((RefNode) expr);
        }
        if (expr instanceof StringNode) {
          return createStringLiteral((StringNode) expr);
        }
        if (expr instanceof ArrayNode) {
          return createArray((ArrayNode) expr);
        }
        throw new RuntimeException("Illegal parse tree: " + expr.getClass().getSimpleName()
            + " without children.");
      }

      private Dag<Expression> createReference(RefNode ref) {
        return new Dag<>(new BoundValueExpression(ref.get(Type.class), ref.name(), ref.location()));
      }

      private Dag<Expression> createCall(CallNode call) {
        Function function = loadedFunctions.get(call.name());
        List<Dag<Expression>> expressions = createSortedArgumentExpressions(call, function);
        return new Dag<>(function.createCallExpression(false, call.location()), expressions);
      }

      private List<Dag<Expression>> createSortedArgumentExpressions(CallNode call,
          Function function) {
        Map<ParameterInfo, Dag<Expression>> assignedExpressions = call
            .args()
            .stream()
            .collect(toMap(a -> a.get(ParameterInfo.class), a -> createExpression(a.expr())));
        return function
            .parameters()
            .stream()
            .map(p -> implicitConversion(p.type(), assignedExpressions.containsKey(p)
                ? assignedExpressions.get(p)
                : p.defaultValueExpression()))
            .collect(toImmutableList());
      }

      private Dag<Expression> createStringLiteral(StringNode string) {
        Value literal = valuesDb.string(string.get(String.class));
        return new Dag<>(new LiteralExpression(literal, string.location()));
      }

      private Dag<Expression> createArray(ArrayNode array) {
        List<Dag<Expression>> exprList = map(array.elements(), this::createExpression);
        return createArray(array, exprList);
      }

      private Dag<Expression> createArray(ArrayNode array, List<Dag<Expression>> elements) {
        ArrayType type = (ArrayType) array.get(Type.class);
        List<Dag<Expression>> converted = map(elements, e -> implicitConversion(type.elemType(),
            e));
        return new Dag<>(new ArrayExpression(type, array.location()), converted);
      }

      public <T extends Value> Dag<Expression> implicitConversion(Type destinationType,
          Dag<Expression> source) {
        Expression elem = source.elem();
        Type sourceType = elem.type();
        if (sourceType.equals(destinationType)) {
          return source;
        }
        return new Dag<>(new ConvertExpression(destinationType, elem.location()), asList(source));
      }
    }.get();
  }
}
