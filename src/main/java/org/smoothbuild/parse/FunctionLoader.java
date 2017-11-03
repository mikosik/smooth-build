package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.BoundValueExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.base.TypedName;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.Native;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Conversions;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.RefNode;
import org.smoothbuild.parse.ast.StringNode;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class FunctionLoader {
  public static Function loadFunction(Functions loadedFunctions, FuncNode func) {
    return new Supplier<Function>() {
      public Function get() {
        List<Parameter> parameters = map(func.params(), this::createParameter);
        Signature signature = new Signature(func.get(Type.class), func.name(), parameters);
        if (func.isNative()) {
          Native nativ = func.get(Native.class);
          HashCode hash = createNativeFunctionHash(nativ.jarFile().hash(), signature);
          boolean isCacheable = !nativ.method().isAnnotationPresent(NotCacheable.class);
          return new NativeFunction(nativ, signature, isCacheable, hash);
        } else {
          Expression expression = createExpression(func.expr());
          return new DefinedFunction(signature, expression);
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
        Expression defaultValue = p.hasDefaultValue()
            ? createExpression(p.defaultValue())
            : null;
        return new Parameter(type, name, defaultValue);
      }

      private Expression createExpression(ExprNode expr) {
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

      private Expression createReference(RefNode ref) {
        return new BoundValueExpression(ref.get(Type.class), ref.name(), ref.location());
      }

      private Expression createCall(CallNode call) {
        Function function = loadedFunctions.get(call.name());
        List<Expression> expressions = createSortedArgumentExpressions(call, function);
        return function.createCallExpression(expressions, false, call.location());
      }

      private List<Expression> createSortedArgumentExpressions(CallNode call, Function function) {
        Map<TypedName, Expression> assignedExpressions = call
            .args()
            .stream()
            .collect(toMap(a -> a.get(TypedName.class), a -> createExpression(a.expr())));
        return function
            .parameters()
            .stream()
            .map(p -> implicitConversion(p.type(), assignedExpressions.containsKey(p)
                ? assignedExpressions.get(p)
                : p.defaultValueExpression()))
            .collect(toImmutableList());
      }

      private Expression createStringLiteral(StringNode string) {
        return new StringLiteralExpression(string.get(String.class), string.location());
      }

      private Expression createArray(ArrayNode array) {
        List<Expression> exprList = map(array.elements(), this::createExpression);
        return createArray(array, exprList);
      }

      private Expression createArray(ArrayNode array, List<Expression> elements) {
        ArrayType type = (ArrayType) array.get(Type.class);
        List<Expression> converted = map(elements, e -> implicitConversion(type.elemType(), e));
        return new ArrayExpression(type, converted, array.location());
      }

      public <T extends Value> Expression implicitConversion(Type destinationType,
          Expression source) {
        Type sourceType = source.type();
        if (sourceType == destinationType) {
          return source;
        }

        Name functionName = Conversions.convertFunctionName(sourceType, destinationType);
        Function function = loadedFunctions.get(functionName);
        return function.createCallExpression(asList(source), true, source.location());
      }
    }.get();
  }
}
