package org.smoothbuild.lang.parse;

import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.lang.base.Signature.signature;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.DefinedValue;
import org.smoothbuild.lang.base.Evaluable;
import org.smoothbuild.lang.base.Native;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.NativeValue;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.AccessorCallExpression;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.ConvertExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.parse.ast.AccessorNode;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayNode;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.EvaluableNode;
import org.smoothbuild.lang.parse.ast.ExprNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.lang.parse.ast.ValueNode;
import org.smoothbuild.lang.parse.ast.ValueTarget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class FunctionLoader {
  public static Callable loadFunction(
      FuncNode func,
      ImmutableMap<String, Evaluable> importedEvaluables,
      HashMap<String, Evaluable> localEvaluables) {
    return new CallableSupplier(func, localEvaluables, importedEvaluables).getFunction();
  }

  public static Value loadValue(
      ValueNode value,
      ImmutableMap<String, Evaluable> importedEvaluables,
      HashMap<String, Evaluable> localEvaluables) {
    return new CallableSupplier(value, localEvaluables, importedEvaluables).getValue();
  }

  private static class CallableSupplier {
    private final EvaluableNode evaluable;
    private final HashMap<String, Evaluable> localEvaluables;
    private final ImmutableMap<String, Evaluable> importedEvaluables;

    public CallableSupplier(EvaluableNode evaluable, HashMap<String, Evaluable> localEvaluables,
        ImmutableMap<String, Evaluable> importedEvaluables) {
      this.evaluable = evaluable;
      this.localEvaluables = localEvaluables;
      this.importedEvaluables = importedEvaluables;
    }

    public Callable getFunction() {
      if (evaluable.isNative()) {
        return nativeFunction();
      } else {
        return definedFunction();
      }
    }

    public Value getValue() {
      if (evaluable.isNative()) {
        return nativeValue();
      } else {
        return definedValue();
      }
    }

    private DefinedFunction definedFunction() {
      return new DefinedFunction(
          createSignature(),
          evaluable.location(),
          createExpression(evaluable.expr()));
    }

    private Value definedValue() {
      ConcreteType type = (ConcreteType) evaluable.type().get();
      Type exprType = evaluable.expr().type().get();
      Expression expression = createExpression(evaluable.expr());
      if (!type.equals(exprType)) {
        expression = new ConvertExpression(type, expression, evaluable.location());
      }
      return new DefinedValue(type, evaluable.name(), expression, evaluable.location());
    }

    private Callable nativeFunction() {
      Native nativ = evaluable.nativ();
      Signature signature = createSignature();
      boolean isCacheable = nativ.cacheable();
      return new NativeFunction(nativ, signature, evaluable.location(), isCacheable);
    }

    private Signature createSignature() {
      List<Parameter> parameters = map(((FuncNode) evaluable).params(), this::createParameter);
      return signature(evaluable.type().get(), evaluable.name(), parameters);
    }

    private Parameter createParameter(ItemNode param) {
      Type type = param.typeNode().type().get();
      String name = param.name();
      Expression defaultValue = param.declaresDefaultValue()
          ? createExpression(param.defaultValue())
          : null;
      return new Parameter(param.index(), type, name, defaultValue, param.location());
    }

    private Value nativeValue() {
      Native nativ = evaluable.nativ();
      return new NativeValue((ConcreteType) evaluable.type().get(), evaluable.name(), nativ,
          evaluable.location(), nativ.cacheable());
    }

    private Expression createExpression(ExprNode expr) {
      if (expr instanceof AccessorNode accessorNode) {
        return createAccessor(accessorNode);
      }
      if (expr instanceof CallNode callNode) {
        return createCall(callNode);
      }
      if (expr instanceof RefNode refNode) {
        return createReference(refNode);
      }
      if (expr instanceof StringNode stringNode) {
        return createStringLiteral(stringNode);
      }
      if (expr instanceof BlobNode blobNode) {
        return createBlobLiteral(blobNode);
      }
      if (expr instanceof ArrayNode arrayNode) {
        return createArray(arrayNode);
      }
      throw new RuntimeException("Unknown AST node: " + expr.getClass().getSimpleName() + ".");
    }

    private Expression createAccessor(AccessorNode accessorNode) {
      StructType type = (StructType) accessorNode.expr().type().get();
      Accessor accessor = type.accessor(accessorNode.fieldName());
      Expression expression = createExpression(accessorNode.expr());
      return new AccessorCallExpression(accessor, list(expression), accessorNode.location());
    }

    private Expression createReference(RefNode ref) {
      if (ref.target() instanceof ItemNode) {
        return new ParameterReferenceExpression(ref.name(), ref.location());
      } else if (ref.target() instanceof ValueNode) {
        Value value = (Value) find(ref.name());
        return value.createReferenceExpression(ref.location());
      } else if (ref.target() instanceof ValueTarget valueTarget) {
        return  valueTarget.value().createReferenceExpression(ref.location());
      } else {
        throw new RuntimeException("Unexpected case: " + ref.getClass().getCanonicalName());
      }
    }

    private Expression createCall(CallNode call) {
      Callable callable = (Callable) find(call.calledName());
      List<Expression> argExpressions = createArgumentExpressions(call, callable);
      return callable.createCallExpression(argExpressions, call.location());
    }

    private Evaluable find(String name) {
      return requireNonNullElseGet(localEvaluables.get(name), () -> importedEvaluables.get(name));
    }

    private List<Expression> createArgumentExpressions(CallNode call, Callable callable) {
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

    private Expression createBlobLiteral(BlobNode blob) {
      return new BlobLiteralExpression(
          blob.byteString(),
          blob.location());
    }

    private Expression createArray(ArrayNode array) {
      ArrayType type = (ArrayType) array.type().get();
      List<Expression> elements = map(array.elements(), this::createExpression);
      return new ArrayLiteralExpression(type, elements, array.location());
    }
  }
}
