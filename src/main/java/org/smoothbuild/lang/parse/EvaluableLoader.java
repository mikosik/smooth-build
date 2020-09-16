package org.smoothbuild.lang.parse;

import static java.util.Objects.requireNonNullElseGet;
import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.Signature.signature;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Evaluable;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.FieldReadExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayNode;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.EvaluableNode;
import org.smoothbuild.lang.parse.ast.ExprNode;
import org.smoothbuild.lang.parse.ast.FieldReadNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.lang.parse.ast.ValueNode;
import org.smoothbuild.lang.parse.ast.ValueTarget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class EvaluableLoader {
  public static Value loadValue(
      ValueNode value,
      Map<String, Evaluable> importedEvaluables,
      Map<String, Evaluable> localEvaluables) {
    return new EvaluableSupplier(value, localEvaluables, importedEvaluables).loadValue();
  }

  public static Callable loadFunction(
      FuncNode func,
      Map<String, Evaluable> importedEvaluables,
      Map<String, Evaluable> localEvaluables) {
    return new EvaluableSupplier(func, localEvaluables, importedEvaluables).loadFunction();
  }

  private static class EvaluableSupplier {
    private final EvaluableNode evaluable;
    private final Map<String, Evaluable> localEvaluables;
    private final Map<String, Evaluable> importedEvaluables;

    public EvaluableSupplier(EvaluableNode evaluable, Map<String, Evaluable> localEvaluables,
        Map<String, Evaluable> importedEvaluables) {
      this.evaluable = evaluable;
      this.localEvaluables = localEvaluables;
      this.importedEvaluables = importedEvaluables;
    }

    public Value loadValue() {
      return new Value((ConcreteType) evaluable.type().get(), evaluable.name(),
          bodyExpression(), evaluable.location());
    }

    public Callable loadFunction() {
      return new Function(createSignature(), evaluable.location(), bodyExpression());
    }

    private Optional<Expression> bodyExpression() {
      return evaluable.isNative() ? empty() : Optional.of(createExpression(evaluable.expr()));
    }

    private Signature createSignature() {
      List<Parameter> parameters = map(((FuncNode) evaluable).params(), this::createParameter);
      return signature(evaluable.type().get(), evaluable.name(), parameters);
    }

    private Parameter createParameter(ItemNode param) {
      Type type = param.typeNode().type().get();
      String name = param.name();
      Optional<Expression> defaultValue = param.defaultValue()
          .map(this::createExpression);
      return new Parameter(param.index(), type, name, defaultValue, param.location());
    }

    private Expression createExpression(ExprNode expr) {
      if (expr instanceof FieldReadNode fieldReadNode) {
        return createFieldRead(fieldReadNode);
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

    private Expression createFieldRead(FieldReadNode fieldReadNode) {
      StructType type = (StructType) fieldReadNode.expr().type().get();
      Field field = type.fields().get(fieldReadNode.fieldName());
      Expression expression = createExpression(fieldReadNode.expr());
      return new FieldReadExpression(field, expression, fieldReadNode.location());
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
      ImmutableList<Expression> argExpressions = createArgumentExpressions(call, callable);
      return callable.createCallExpression(argExpressions, call.location());
    }

    private Evaluable find(String name) {
      return requireNonNullElseGet(localEvaluables.get(name), () -> importedEvaluables.get(name));
    }

    private ImmutableList<Expression> createArgumentExpressions(CallNode call, Callable callable) {
      ImmutableList<Parameter> parameters = callable.parameters();
      Builder<Expression> resultBuilder = ImmutableList.builder();
      List<ArgNode> args = call.assignedArgs();
      for (int i = 0; i < parameters.size(); i++) {
        if (args.get(i) == null) {
          resultBuilder.add(parameters.get(i).defaultValueExpression().get());
        } else {
          resultBuilder.add(createExpression(args.get(i).expr()));
        }
      }
      return resultBuilder.build();
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
      ImmutableList<Expression> elements = map(array.elements(), this::createExpression);
      return new ArrayLiteralExpression(type, elements, array.location());
    }
  }
}
