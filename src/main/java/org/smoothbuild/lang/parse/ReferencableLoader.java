package org.smoothbuild.lang.parse;

import static org.smoothbuild.lang.base.define.Item.toTypes;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.lang.base.define.DefinedFunction;
import org.smoothbuild.lang.base.define.DefinedValue;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.GlobalReferencable;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.NativeFunction;
import org.smoothbuild.lang.base.define.NativeValue;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.expr.AnnotationExpression;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.IntLiteralExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.expr.SelectExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.parse.ast.AnnotationNode;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayNode;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.ExprNode;
import org.smoothbuild.lang.parse.ast.FunctionNode;
import org.smoothbuild.lang.parse.ast.IntNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.RealFuncNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.SelectNode;
import org.smoothbuild.lang.parse.ast.StringNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ReferencableLoader {
  private final Typing typing;

  @Inject
  public ReferencableLoader(Typing typing) {
    this.typing = typing;
  }

  public GlobalReferencable loadReferencable(ModulePath path,
      ReferencableNode referencableNode) {
    if (referencableNode instanceof RealFuncNode realFuncNode) {
      return loadFunction(path, realFuncNode);
    } else {
      return loadValue(path, referencableNode);
    }
  }

  private Value loadValue(ModulePath path, ReferencableNode valueNode) {
    Type type = valueNode.type().get();
    String name = valueNode.name();
    Location location = valueNode.location();
    if (valueNode.annotation().isPresent()) {
      return new NativeValue(type, path, name, loadAnnotation(valueNode.annotation().get()), location);
    } else {
      ExpressionLoader loader = new ExpressionLoader(path, ImmutableMap.of());
      return new DefinedValue(
          type, path, name, loader.createExpression(valueNode.body().get()), location);
    }
  }

  private Function loadFunction(ModulePath path, RealFuncNode realFuncNode) {
    ImmutableList<Item> parameters = loadParameters(path, realFuncNode);
    Type resultType = realFuncNode.resultType().get();
    String name = realFuncNode.name();
    Location location = realFuncNode.location();
    FunctionType type = typing.function(resultType, toTypes(parameters));
    if (realFuncNode.annotation().isPresent()) {
      return new NativeFunction(type,
          path, name, parameters, loadAnnotation(realFuncNode.annotation().get()), location
      );
    } else {
      var expressionLoader = new ExpressionLoader(path, toMap(parameters, Item::name, Item::type));
      return new DefinedFunction(type, path,
          name, parameters, expressionLoader.createExpression(realFuncNode.body().get()), location);
    }
  }

  private AnnotationExpression loadAnnotation(AnnotationNode annotationNode) {
    StructType type = typing.struct(
        "Native", list(typing.string(), typing.blob()), list("path", "content"));
    var path = createStringLiteral(annotationNode.path());
    return new AnnotationExpression(type, path, annotationNode.isPure(), annotationNode.location());
  }

  private ImmutableList<Item> loadParameters(ModulePath path, RealFuncNode realFuncNode) {
    ExpressionLoader parameterLoader = new ExpressionLoader(path, ImmutableMap.of());
    return map(realFuncNode.params(), parameterLoader::createParameter);
  }

  private class ExpressionLoader {
    private final ModulePath modulePath;
    private final ImmutableMap<String, Type> functionParameters;

    public ExpressionLoader(ModulePath modulePath,
        ImmutableMap<String, Type> functionParameters) {
      this.modulePath = modulePath;
      this.functionParameters = functionParameters;
    }

    public Item createParameter(ItemNode param) {
      Type type = param.typeNode().get().type().get();
      String name = param.name();
      Optional<Expression> defaultArgument = param.body()
          .map(this::createExpression);
      return new Item(type, modulePath, name, defaultArgument, param.location());
    }

    private Expression createExpression(ExprNode expr) {
      if (expr instanceof ArrayNode arrayNode) {
        return createArrayLiteral(arrayNode);
      }
      if (expr instanceof BlobNode blobNode) {
        return createBlobLiteral(blobNode);
      }
      if (expr instanceof CallNode callNode) {
        return createCall(callNode);
      }
      if (expr instanceof SelectNode selectNode) {
        return createSelect(selectNode);
      }
      if (expr instanceof IntNode intNode) {
        return createIntLiteral(intNode);
      }
      if (expr instanceof RefNode refNode) {
        return createReference(refNode);
      }
      if (expr instanceof StringNode stringNode) {
        return createStringLiteral(stringNode);
      }
      throw new RuntimeException("Unknown AST node: " + expr.getClass().getSimpleName() + ".");
    }

    private Expression createArrayLiteral(ArrayNode array) {
      ArrayType type = (ArrayType) array.type().get();
      ImmutableList<Expression> elements = map(array.elements(), this::createExpression);
      return new ArrayLiteralExpression(type, elements, array.location());
    }

    private Expression createCall(CallNode call) {
      Expression called = createExpression(call.function());
      var argumentExpressions = createArgumentExpressions(call);
      var resultType = call.type().get();
      return new CallExpression(resultType, called, argumentExpressions, call.location());
    }

    private ImmutableList<Expression> createArgumentExpressions(CallNode call) {
      var builder = ImmutableList.<Expression>builder();
      List<Optional<ArgNode>> args = call.assignedArgs();
      for (int i = 0; i < args.size(); i++) {
        builder.add(createArgumentExpression(call, args, i));
      }
      return builder.build();
    }

    private Expression createArgumentExpression(
        CallNode call, List<Optional<ArgNode>> args, int i) {
      Optional<ArgNode> arg = args.get(i);
      if (arg.isPresent()) {
        return createExpression(arg.get().expr());
      } else {
        return createDefaultArgumentExpression(call, i);
      }
    }

    private Expression createDefaultArgumentExpression(CallNode call, int i) {
      // Argument is not present so we have to use function default argument.
      // This means that this call is made on reference to actual function and that function
      // has default argument for given parameter, otherwise checkers that ran so far would
      // report an error.
      ReferencableLike referenced = ((RefNode) call.function()).referenced();
      if (referenced instanceof Function function) {
        return function.parameters().get(i).defaultValue().get();
      } else if (referenced instanceof FunctionNode functionNode) {
        return createExpression(functionNode.params().get(i).body().get());
      } else {
        throw new RuntimeException("Unexpected case");
      }
    }

    private Expression createSelect(SelectNode selectNode) {
      StructType structType = (StructType) selectNode.expr().type().get();
      int index = structType.nameToIndex().get(selectNode.fieldName());
      Type itemType = structType.fields().get(index);
      Expression expression = createExpression(selectNode.expr());
      return new SelectExpression(itemType, index, expression, selectNode.location());
    }

    private Expression createReference(RefNode ref) {
      ReferencableLike referenced = ref.referenced();
      if (referenced instanceof ItemNode) {
        String name = ref.name();
        return new ParameterReferenceExpression(functionParameters.get(name), name, ref.location());
      }
      return new ReferenceExpression(referenced.inferredType().get(), ref.name(), ref.location());
    }
  }

  public BlobLiteralExpression createBlobLiteral(BlobNode blob) {
    return new BlobLiteralExpression(
        typing.blob(),
        blob.byteString(),
        blob.location());
  }

  public IntLiteralExpression createIntLiteral(IntNode intNode) {
    return new IntLiteralExpression(
        typing.int_(),
        intNode.bigInteger(),
        intNode.location());
  }

  public StringLiteralExpression createStringLiteral(StringNode string) {
    return new StringLiteralExpression(
        typing.string(),
        string.unescapedValue(),
        string.location());
  }
}
