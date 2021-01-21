package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Callable;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.FieldReadExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayNode;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.ExprNode;
import org.smoothbuild.lang.parse.ast.FieldReadNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.lang.parse.ast.ValueNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class LoadReferencable {
  public static Value loadValue(
      ValueNode value,
      Map<String, Referencable> importedValues,
      Map<String, Referencable> localValues) {
    return new ReferencableSupplier(value, importedValues, localValues).loadValue();
  }

  public static Function loadFunction(
      FuncNode func,
      Map<String, Referencable> importedValues,
      Map<String, Referencable> localValues) {
    return new ReferencableSupplier(func, importedValues, localValues).loadFunction();
  }

  private static class ReferencableSupplier {
    private final ReferencableNode referencable;
    private final Map<String, Referencable> imported;
    private final Map<String, Referencable> local;
    private ImmutableMap<String, Type> functionParameters;

    public ReferencableSupplier(ReferencableNode referencable, Map<String, Referencable> imported,
        Map<String, Referencable> local) {
      this.referencable = referencable;
      this.imported = imported;
      this.local = local;
    }

    public Value loadValue() {
      return new Value(referencable.type().get(), referencable.name(), bodyExpression(),
          referencable.location());
    }

    public Function loadFunction() {
      String name = referencable.name();
      FuncNode funcNode = (FuncNode) referencable;
      ImmutableList<Item> parameters = map(funcNode.params(), this::createParameter);
      functionParameters = parameters.stream().collect(toImmutableMap(Item::name, Item::type));
      return new Function(
          funcNode.resultType().get(), name, parameters, bodyExpression(), referencable.location());
    }

    private Optional<Expression> bodyExpression() {
      return referencable.expr().map(this::createExpression);
    }

    private Item createParameter(ItemNode param) {
      Type type = param.typeNode().type().get();
      String name = param.name();
      Optional<Expression> defaultValue = param.defaultValue()
          .map(this::createExpression);
      return new Item(type, name, defaultValue);
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
      ItemSignature field = type.fieldWithName(fieldReadNode.fieldName());
      Expression expression = createExpression(fieldReadNode.expr());
      return new FieldReadExpression(field, expression, fieldReadNode.location());
    }

    private Expression createReference(RefNode ref) {
      ReferencableLike target = ref.target();
      if (target instanceof ItemNode) {
        String name = ref.name();
        return new ParameterReferenceExpression(functionParameters.get(name), name, ref.location());
      }
      return new ReferenceExpression(ref.name(), target.inferredType().get(), ref.location());
    }

    private Expression createCall(CallNode call) {
      Callable callable = (Callable) find(call.calledName());
      ImmutableList<Expression> argExpressions = createArgumentExpressions(call, callable);
      return callable.createCallExpression(argExpressions, call.location());
    }

    private Defined find(String name) {
      return requireNonNullElseGet(local.get(name), () -> imported.get(name));
    }

    private ImmutableList<Expression> createArgumentExpressions(CallNode call, Callable callable) {
      ImmutableList<Item> parameters = callable.parameters();
      Builder<Expression> resultBuilder = ImmutableList.builder();
      List<ArgNode> assignedArgs = call.assignedArgs();
      for (int i = 0; i < parameters.size(); i++) {
        if (assignedArgs.get(i) == null) {
          resultBuilder.add(parameters.get(i).defaultValue().get());
        } else {
          resultBuilder.add(createExpression(assignedArgs.get(i).expr()));
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
