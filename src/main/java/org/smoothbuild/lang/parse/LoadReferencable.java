package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.lang.expr.Expression.toTypes;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.RealFunction;
import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.CallExpression;
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
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.RealFuncNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.StringNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class LoadReferencable {
  public static Referencable loadReferencable(ModulePath path, ReferencableNode referencableNode,
      Referencables referencables) {
    if (referencableNode instanceof RealFuncNode realFuncNode) {
      return loadFunction(path, realFuncNode, referencables);
    } else {
      return loadValue(path, referencableNode, referencables);
    }
  }

  private static Value loadValue(
      ModulePath path, ReferencableNode referencableNode, Referencables referencables) {
    ExpressionLoader loader = new ExpressionLoader(referencables, ImmutableMap.of());
    return new Value(
        referencableNode.type().get(),
        path,
        referencableNode.name(),
        loader.bodyExpression(referencableNode),
        referencableNode.location());
  }

  private static RealFunction loadFunction(ModulePath path, RealFuncNode realFuncNode,
      Referencables referencables) {
    ImmutableList<Item> parameters = loadParameters(realFuncNode, referencables);
    ExpressionLoader loader = new ExpressionLoader(referencables,
        parameters.stream().collect(toImmutableMap(Item::name, Item::type)));
    return new RealFunction(
        realFuncNode.resultType().get(),
        path,
        realFuncNode.name(),
        parameters,
        loader.bodyExpression(realFuncNode),
        realFuncNode.location());
  }

  private static ImmutableList<Item> loadParameters(
      RealFuncNode realFuncNode, Referencables referencables) {
    ExpressionLoader parameterLoader = new ExpressionLoader(referencables, ImmutableMap.of());
    return map(realFuncNode.params(), parameterLoader::createParameter);
  }

  private static class ExpressionLoader {
    private final Referencables referencables;
    private final ImmutableMap<String, Type> functionParameters;

    public ExpressionLoader(
        Referencables referencables, ImmutableMap<String, Type> functionParameters) {
      this.referencables = referencables;
      this.functionParameters = functionParameters;
    }

    private Expression bodyExpression(ReferencableNode referencable) {
      if (referencable.nativ().isPresent()) {
        return referencable.nativ().get();
      } else {
        return createExpression(referencable.expr().get());
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
      Function function = find(call.called().name());
      ImmutableList<Expression> arguments = createArgumentExpressions(call, function);
      Type resultType = function.type().inferResultType(toTypes(arguments));
      ReferenceExpression reference = new ReferenceExpression(
          call.called().name(), function.type(), call.location());
      return new CallExpression(resultType, reference, arguments, call.location());
    }

    private Function find(String name) {
      return (Function) referencables.findReferencableLike(name);
    }

    private ImmutableList<Expression> createArgumentExpressions(CallNode call, Function function) {
      ImmutableList<Item> parameters = function.parameters();
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
