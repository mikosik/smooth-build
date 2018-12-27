package org.smoothbuild.parse;

import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.List;
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
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.BoundValueExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.LiteralExpression;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.parse.ast.AccessorNode;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.RefNode;
import org.smoothbuild.parse.ast.StringNode;

import com.google.common.collect.ImmutableList;
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
          return new DefinedFunction(signature, func.location(), createExpression(func.expr()));
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
        Expression defaultValue = param.hasDefaultValue()
            ? createExpression(param.defaultValue())
            : null;
        return new Parameter(param.index(), type, name, defaultValue);
      }

      private Expression createExpression(ExprNode expr) {
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

      private Expression createAccessor(AccessorNode accessor) {
        StructType type = (StructType) accessor.expr().get(Type.class);
        Accessor accessorFunction = type.accessor(accessor.fieldName());
        return accessorFunction.createCallExpression(
            list(createExpression(accessor.expr())), accessor.location());
      }

      private Expression createReference(RefNode ref) {
        return new BoundValueExpression(ref.name(), ref.location());
      }

      private Expression createCall(CallNode call) {
        Function function = runtime.functions().get(call.name());
        List<Expression> argExpressions = createArgumentExpressions(call, function);
        return function.createCallExpression(argExpressions, call.location());
      }

      private List<Expression> createArgumentExpressions(CallNode call,
          Function function) {
        ImmutableList<Parameter> parameters = function.parameters();
        ArrayList<Expression> result = new ArrayList<>(parameters.size());
        List<ArgNode> args = call.assignedArgs();
        for (int i = 0; i < parameters.size(); i++) {
          if (args.get(i) == null) {
            result.add(parameters.get(i).defaultValueExpression());
          } else {
            result.add(createExpression(args.get(i).expr()));
          }
        }
        return result;
      }

      private Expression createStringLiteral(StringNode string) {
        Value literal = valuesDb.string(string.get(String.class));
        return new LiteralExpression(literal, string.location());
      }

      private Expression createArray(ArrayNode array) {
        ArrayType type = (ArrayType) array.get(Type.class);
        List<Expression> elements = map(array.elements(), this::createExpression);
        return new ArrayExpression(type, elements, array.location());
      }
    }.get();
  }
}
