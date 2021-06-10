package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Body;
import org.smoothbuild.lang.base.define.Callable;
import org.smoothbuild.lang.base.define.DefinedBody;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class LoadReferencable {
  public static Referencable loadReferencable(
      ReferencableNode referencableNode, Referencables referencables) {
    if (referencableNode instanceof FuncNode funcNode) {
      return loadFunction(funcNode, referencables);
    } else {
      return loadValue(referencableNode, referencables);
    }
  }

  private static Value loadValue(ReferencableNode referencableNode, Referencables referencables) {
    ExpressionLoader loader = new ExpressionLoader(referencables, ImmutableMap.of());
    return new Value(
        referencableNode.type().get(),
        referencableNode.name(),
        loader.bodyExpression(referencableNode),
        referencableNode.location());
  }

  private static Function loadFunction(FuncNode funcNode, Referencables referencables) {
    ImmutableList<Item> parameters = loadParameters(funcNode, referencables);
    ExpressionLoader loader = new ExpressionLoader(referencables,
        parameters.stream().collect(toImmutableMap(Item::name, Item::type)));
    return new Function(
        funcNode.resultType().get(),
        funcNode.name(),
        parameters,
        loader.bodyExpression(funcNode),
        funcNode.location());
  }

  private static ImmutableList<Item> loadParameters(
      FuncNode funcNode, Referencables referencables) {
    ExpressionLoader parameterLoader = new ExpressionLoader(referencables, ImmutableMap.of());
    return map(funcNode.params(), parameterLoader::createParameter);
  }

  private static class ExpressionLoader {
    private final Referencables referencables;
    private final ImmutableMap<String, Type> functionParameters;

    public ExpressionLoader(
        Referencables referencables, ImmutableMap<String, Type> functionParameters) {
      this.referencables = referencables;
      this.functionParameters = functionParameters;
    }

    private Body bodyExpression(ReferencableNode referencable) {
      if (referencable.nativ().isPresent()) {
        return referencable.nativ().get();
      } else {
        return new DefinedBody(createExpression(referencable.expr().get()));
      }
    }

    public Item createParameter(ItemNode param) {
      Type type = param.typeNode().get().type().get();
      String name = param.name();
      Optional<Expression> defaultValue = param.expr()
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
      ReferencableLike referenced = ref.referenced();
      if (referenced instanceof ItemNode) {
        String name = ref.name();
        return new ParameterReferenceExpression(functionParameters.get(name), name, ref.location());
      }
      return new ReferenceExpression(ref.name(), referenced.inferredType().get(), ref.location());
    }

    private Expression createCall(CallNode call) {
      Callable callable = find(call.calledName());
      ImmutableList<Expression> argExpressions = createArgumentExpressions(call, callable);
      return callable.createCallExpression(argExpressions, call.location());
    }

    private Callable find(String name) {
      return (Callable) referencables.findReferencableLike(name);
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
