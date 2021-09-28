package org.smoothbuild.lang.parse;

import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Maps.toMap;

import java.util.List;
import java.util.Optional;

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
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.IntLiteralExpression;
import org.smoothbuild.lang.expr.NativeExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.expr.SelectExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayNode;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.ExprNode;
import org.smoothbuild.lang.parse.ast.IntNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.NativeNode;
import org.smoothbuild.lang.parse.ast.RealFuncNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.SelectNode;
import org.smoothbuild.lang.parse.ast.StringNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class LoadReferencable {
  public static GlobalReferencable loadReferencable(ModulePath path,
      ReferencableNode referencableNode) {
    if (referencableNode instanceof RealFuncNode realFuncNode) {
      return loadFunction(path, realFuncNode);
    } else {
      return loadValue(path, referencableNode);
    }
  }

  private static Value loadValue(ModulePath path, ReferencableNode valueNode) {
    Type type = valueNode.type().get();
    String name = valueNode.name();
    Location location = valueNode.location();
    if (valueNode.nativ().isPresent()) {
      return new NativeValue(type, path, name, loadNative(valueNode.nativ().get()), location);
    } else {
      ExpressionLoader loader = new ExpressionLoader(path, ImmutableMap.of());
      return new DefinedValue(
          type, path, name, loader.createExpression(valueNode.body().get()), location);
    }
  }

  private static Function loadFunction(ModulePath path, RealFuncNode realFuncNode) {
    ImmutableList<Item> parameters = loadParameters(path, realFuncNode);
    Type resultType = realFuncNode.resultType().get();
    String name = realFuncNode.name();
    Location location = realFuncNode.location();
    if (realFuncNode.nativ().isPresent()) {
      return new NativeFunction(
          resultType, path, name, parameters, loadNative(realFuncNode.nativ().get()), location);
    } else {
      var expressionLoader = new ExpressionLoader(path, toMap(parameters, Item::name, Item::type));
      return new DefinedFunction(resultType, path, name, parameters,
          expressionLoader.createExpression(realFuncNode.body().get()), location);
    }
  }

  private static NativeExpression loadNative(NativeNode nativeNode) {
    var path = createStringLiteral(nativeNode.path());
    return new NativeExpression(path, nativeNode.isPure(), nativeNode.location());
  }

  private static ImmutableList<Item> loadParameters(ModulePath path, RealFuncNode realFuncNode) {
    ExpressionLoader parameterLoader = new ExpressionLoader(path, ImmutableMap.of());
    return map(realFuncNode.params(), parameterLoader::createParameter);
  }

  private static class ExpressionLoader {
    private final ModulePath modulePath;
    private final ImmutableMap<String, Type> functionParameters;

    public ExpressionLoader(ModulePath modulePath, ImmutableMap<String, Type> functionParameters) {
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
      var functionType = ((FunctionType) called.type());
      var resultType = functionType.inferResultType(createArgumentTypes(call));
      return new CallExpression(resultType, called, argumentExpressions, call.location());
    }

    private ImmutableList<Optional<Expression>> createArgumentExpressions(CallNode call) {
      return map(call.assignedArgs(),
          optionalArg -> optionalArg.map(a -> createExpression(a.expr())));
    }

    private ImmutableList<Type> createArgumentTypes(CallNode call) {
      FunctionType functionType = ((FunctionType) call.function().type().get());
      List<Optional<ArgNode>> assignedArgs = call.assignedArgs();
      ImmutableList<ItemSignature> parameters = functionType.parameters();
      Builder<Type> resultBuilder = ImmutableList.builder();
      for (int i = 0; i < parameters.size(); i++) {
        if (assignedArgs.get(i).isPresent()) {
          resultBuilder.add(assignedArgs.get(i).get().type().get());
        } else {
          resultBuilder.add(parameters.get(i).defaultValueType().get());
        }
      }
      return resultBuilder.build();
    }

    private Expression createSelect(SelectNode selectNode) {
      StructType type = (StructType) selectNode.expr().type().get();
      ItemSignature field = type.fieldWithName(selectNode.fieldName());
      Expression expression = createExpression(selectNode.expr());
      return new SelectExpression(field, expression, selectNode.location());
    }

    private Expression createReference(RefNode ref) {
      ReferencableLike referenced = ref.referenced();
      if (referenced instanceof ItemNode) {
        String name = ref.name();
        return new ParameterReferenceExpression(functionParameters.get(name), name, ref.location());
      }
      return new ReferenceExpression(ref.name(), referenced.inferredType().get(), ref.location());
    }
  }

  public static BlobLiteralExpression createBlobLiteral(BlobNode blob) {
    return new BlobLiteralExpression(
        blob.byteString(),
        blob.location());
  }

  public static IntLiteralExpression createIntLiteral(IntNode intNode) {
    return new IntLiteralExpression(
        intNode.bigInteger(),
        intNode.location());
  }

  public static StringLiteralExpression createStringLiteral(StringNode string) {
    return new StringLiteralExpression(
        string.unescapedValue(),
        string.location());
  }
}
