package org.smoothbuild.lang.parse;

import static org.smoothbuild.lang.base.define.Item.toTypes;
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
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.expr.Annotation;
import org.smoothbuild.lang.expr.BlobS;
import org.smoothbuild.lang.expr.CallS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.lang.expr.OrderS;
import org.smoothbuild.lang.expr.ParamRefS;
import org.smoothbuild.lang.expr.RefS;
import org.smoothbuild.lang.expr.SelectS;
import org.smoothbuild.lang.expr.StringS;
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
  private final TypeFactoryS factory;

  @Inject
  public ReferencableLoader(TypeFactoryS factory) {
    this.factory = factory;
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
    var type = valueNode.type().get();
    var name = valueNode.name();
    var location = valueNode.location();
    if (valueNode.annotation().isPresent()) {
      return new NativeValue(type, path, name, loadAnnotation(valueNode.annotation().get()), location);
    } else {
      ExpressionLoader loader = new ExpressionLoader(path, ImmutableMap.of());
      return new DefinedValue(
          type, path, name, loader.createExpression(valueNode.body().get()), location);
    }
  }

  private Function loadFunction(ModulePath path, RealFuncNode realFuncNode) {
    var parameters = loadParameters(path, realFuncNode);
    var resultType = realFuncNode.resultType().get();
    String name = realFuncNode.name();
    Location location = realFuncNode.location();
    FunctionTypeS type = factory.function(resultType, toTypes(parameters));
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

  private Annotation loadAnnotation(AnnotationNode annotationNode) {
    var path = createStringLiteral(annotationNode.path());
    return new Annotation(path, annotationNode.isPure(), annotationNode.location());
  }

  private ImmutableList<Item> loadParameters(ModulePath path, RealFuncNode realFuncNode) {
    ExpressionLoader parameterLoader = new ExpressionLoader(path, ImmutableMap.of());
    return map(realFuncNode.params(), parameterLoader::createParameter);
  }

  private class ExpressionLoader {
    private final ModulePath modulePath;
    private final ImmutableMap<String, TypeS> functionParameters;

    public ExpressionLoader(ModulePath modulePath, ImmutableMap<String, TypeS> functionParameters) {
      this.modulePath = modulePath;
      this.functionParameters = functionParameters;
    }

    public Item createParameter(ItemNode param) {
      var type = param.typeNode().get().type().get();
      var name = param.name();
      var defaultArgument = param.body().map(this::createExpression);
      return new Item(type, modulePath, name, defaultArgument, param.location());
    }

    private ExprS createExpression(ExprNode expr) {
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

    private ExprS createArrayLiteral(ArrayNode array) {
      var type = (ArrayTypeS) array.type().get();
      ImmutableList<ExprS> elements = map(array.elements(), this::createExpression);
      return new OrderS(type, elements, array.location());
    }

    private ExprS createCall(CallNode call) {
      ExprS called = createExpression(call.function());
      var argumentExpressions = createArgumentExpressions(call);
      var resultType = call.type().get();
      return new CallS(resultType, called, argumentExpressions, call.location());
    }

    private ImmutableList<ExprS> createArgumentExpressions(CallNode call) {
      var builder = ImmutableList.<ExprS>builder();
      List<Optional<ArgNode>> args = call.assignedArgs();
      for (int i = 0; i < args.size(); i++) {
        builder.add(createArgumentExpression(call, args, i));
      }
      return builder.build();
    }

    private ExprS createArgumentExpression(
        CallNode call, List<Optional<ArgNode>> args, int i) {
      Optional<ArgNode> arg = args.get(i);
      if (arg.isPresent()) {
        return createExpression(arg.get().expr());
      } else {
        return createDefaultArgumentExpression(call, i);
      }
    }

    private ExprS createDefaultArgumentExpression(CallNode call, int i) {
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

    private ExprS createSelect(SelectNode selectNode) {
      var structType = (StructTypeS) selectNode.expr().type().get();
      var index = structType.fields().indexMap().get(selectNode.fieldName());
      var fieldType = structType.fields().getObject(index);
      ExprS expr = createExpression(selectNode.expr());
      return new SelectS(fieldType, index, expr, selectNode.location());
    }

    private ExprS createReference(RefNode ref) {
      ReferencableLike referenced = ref.referenced();
      if (referenced instanceof ItemNode) {
        String name = ref.name();
        return new ParamRefS(functionParameters.get(name), name, ref.location());
      }
      return new RefS(referenced.inferredType().get(), ref.name(), ref.location());
    }
  }

  public BlobS createBlobLiteral(BlobNode blob) {
    return new BlobS(
        factory.blob(),
        blob.byteString(),
        blob.location());
  }

  public IntS createIntLiteral(IntNode intNode) {
    return new IntS(
        factory.int_(),
        intNode.bigInteger(),
        intNode.location());
  }

  public StringS createStringLiteral(StringNode string) {
    return new StringS(
        factory.string(),
        string.unescapedValue(),
        string.location());
  }
}
