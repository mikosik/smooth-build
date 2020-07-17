package org.smoothbuild.parse;

import static org.smoothbuild.lang.base.Signature.signature;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.Native;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BoundValueExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.parse.ast.AccessorNode;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ItemNode;
import org.smoothbuild.parse.ast.RefNode;
import org.smoothbuild.parse.ast.StringNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class FunctionLoader {
  public static Callable loadFunction(
      FuncNode func,
      ImmutableMap<String, Callable> importedCallables,
      HashMap<String, Callable> localCallables) {
    return new Supplier<Callable>() {
      @Override
      public Callable get() {
        if (func.isNative()) {
          return nativeFunction();
        } else {
          return definedFunction();
        }
      }

      private DefinedFunction definedFunction() {
        return new DefinedFunction(
            createSignature(),
            func.location(),
            createExpression(func.expr()));
      }

      private Callable nativeFunction() {
        Native nativ = func.nativ();
        Signature signature = createSignature();
        Hash hash = createNativeFunctionHash(nativ.jarFile().hash(), signature);
        boolean isCacheable = nativ.cacheable();
        return new NativeFunction(nativ, signature, func.location(), isCacheable, hash);
      }

      private Signature createSignature() {
        List<Parameter> parameters = map(func.params(), this::createParameter);
        return signature(func.type().get(), func.name(), parameters);
      }

      private Hash createNativeFunctionHash(Hash jarHash, Signature signature) {
        return Hash.of(jarHash, Hash.of(signature.name()));
      }

      private Parameter createParameter(ItemNode param) {
        Type type = param.typeNode().type().get();
        String name = param.name();
        Expression defaultValue = param.declaresDefaultValue()
            ? createExpression(param.defaultValue())
            : null;
        return new Parameter(param.index(), type, name, defaultValue, param.location());
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
        StructType type = (StructType) accessor.expr().type().get();
        Accessor accessorFunction = type.accessor(accessor.fieldName());
        return accessorFunction.createCallExpression(
            list(createExpression(accessor.expr())), accessor.location());
      }

      private Expression createReference(RefNode ref) {
        return new BoundValueExpression(ref.name(), ref.location());
      }

      private Expression createCall(CallNode call) {
        Callable callable = localCallables.get(call.calledName());
        if (callable == null) {
          callable = importedCallables.get(call.calledName());
        }
        List<Expression> argExpressions = createArgumentExpressions(call, callable);
        return callable.createCallExpression(argExpressions, call.location());
      }

      private List<Expression> createArgumentExpressions(CallNode call,
          Callable callable) {
        ImmutableList<Parameter> parameters = callable.parameters();
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
        return new StringLiteralExpression(
            string.unescapedValue(),
            string.location());
      }

      private Expression createArray(ArrayNode array) {
        ArrayType type = (ArrayType) array.type().get();
        List<Expression> elements = map(array.elements(), this::createExpression);
        return new ArrayLiteralExpression(type, elements, array.location());
      }
    }.get();
  }
}
