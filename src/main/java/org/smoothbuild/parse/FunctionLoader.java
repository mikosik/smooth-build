package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.lang.type.TypeChooser.arrayOfFirstChildType;
import static org.smoothbuild.lang.type.TypeChooser.fixedTypeChooser;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.Native;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.ParameterInfo;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.BoundValueExpression;
import org.smoothbuild.lang.expr.ConvertExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.LiteralExpression;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeChooser;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.parse.ast.AccessorNode;
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
  public static Function loadFunction(SRuntime runtime, ValuesDb valuesDb, FuncNode func) {
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

      private Parameter createParameter(ParamNode param) {
        Type type = param.type().get(Type.class);
        String name = param.name();
        Dag<Expression> defaultValue = param.hasDefaultValue()
            ? createExpression(param.defaultValue())
            : null;
        return new Parameter(type, name, defaultValue);
      }

      private Dag<Expression> createExpression(ExprNode expr) {
        if (expr instanceof AccessorNode) {
          return createAccessor((AccessorNode) expr);
        }
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
        throw new RuntimeException("Unknown AST node: " + expr.getClass().getSimpleName() + ".");
      }

      private Dag<Expression> createAccessor(AccessorNode accessor) {
        StructType type = (StructType) accessor.expr().get(Type.class);
        Accessor accessorFunction = type.accessor(accessor.fieldName());
        return new Dag<>(accessorFunction.createCallExpression(accessor.location()),
            list(createExpression(accessor.expr())));
      }

      private Dag<Expression> createReference(RefNode ref) {
        return new Dag<>(new BoundValueExpression(ref.get(Type.class), ref.name(), ref
            .location()));
      }

      private Dag<Expression> createCall(CallNode call) {
        Function function = runtime.functions().get(call.name());
        List<Dag<Expression>> argExpressions = createSortedArgumentExpressions(call, function);
        Type expressionType = call.get(Type.class);
        Expression callExpression = function.createCallExpression(expressionType,
            fixedTypeChooser((ConcreteType) expressionType), call.location());
        return new Dag<>(callExpression, argExpressions);
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
        List<Dag<Expression>> converted = map(
            elements, e -> implicitConversion(type.elemType(), e));
        TypeChooser<ConcreteType> typeChooser = converted.isEmpty()
            ? fixedTypeChooser((ConcreteArrayType) type)
            : arrayOfFirstChildType();
        return new Dag<>(new ArrayExpression(type, typeChooser, array.location()),
            converted);
      }

      public <T extends Value> Dag<Expression> implicitConversion(Type destinationType,
          Dag<Expression> source) {
        Expression elem = source.elem();
        Type sourceType = elem.type();
        if (sourceType.equals(destinationType) || destinationType.isGeneric()) {
          return source;
        }
        return new Dag<>(new ConvertExpression((ConcreteType) destinationType, elem.location()),
            list(source));
      }
    }.get();
  }
}
