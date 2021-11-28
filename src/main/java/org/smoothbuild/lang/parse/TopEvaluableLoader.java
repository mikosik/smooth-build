package org.smoothbuild.lang.parse;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.DefinedFunctionS;
import org.smoothbuild.lang.base.define.DefinedValueS;
import org.smoothbuild.lang.base.define.FunctionS;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.NativeFunctionS;
import org.smoothbuild.lang.base.define.TopEvaluableS;
import org.smoothbuild.lang.base.define.ValueS;
import org.smoothbuild.lang.base.like.EvaluableLike;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
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
import org.smoothbuild.lang.parse.ast.EvaluableNode;
import org.smoothbuild.lang.parse.ast.ExprNode;
import org.smoothbuild.lang.parse.ast.FunctionNode;
import org.smoothbuild.lang.parse.ast.IntNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.RealFuncNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.SelectNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TopEvaluableLoader {
  private final TypeFactoryS factory;

  @Inject
  public TopEvaluableLoader(TypeFactoryS factory) {
    this.factory = factory;
  }

  public TopEvaluableS loadEvaluables(ModulePath path, EvaluableNode evaluableNode) {
    if (evaluableNode instanceof RealFuncNode realFuncNode) {
      return loadFunction(path, realFuncNode);
    } else {
      return loadValue(path, evaluableNode);
    }
  }

  private ValueS loadValue(ModulePath path, EvaluableNode valueNode) {
    var type = valueNode.type().get();
    var name = valueNode.name();
    var location = valueNode.location();
    var loader = new ExpressionLoader(path, nList());
    return new DefinedValueS(
        type, path, name, loader.createExpression(valueNode.body().get()), location);
  }

  private FunctionS loadFunction(ModulePath path, RealFuncNode realFuncNode) {
    var parameters = loadParameters(path, realFuncNode);
    var resultType = realFuncNode.resultType().get();
    var name = realFuncNode.name();
    var location = realFuncNode.location();
    var type = factory.function(resultType, map(parameters, Defined::type));
    if (realFuncNode.annotation().isPresent()) {
      return new NativeFunctionS(type,
          path, name, parameters, loadAnnotation(realFuncNode.annotation().get()), location
      );
    } else {
      var expressionLoader = new ExpressionLoader(path, parameters);
      return new DefinedFunctionS(type, path,
          name, parameters, expressionLoader.createExpression(realFuncNode.body().get()), location);
    }
  }

  private Annotation loadAnnotation(AnnotationNode annotationNode) {
    var path = createStringLiteral(annotationNode.path());
    return new Annotation(path, annotationNode.isPure(), annotationNode.location());
  }

  private NList<Item> loadParameters(ModulePath path, RealFuncNode realFuncNode) {
    ExpressionLoader parameterLoader = new ExpressionLoader(path, nList());
    return realFuncNode.params().map(parameterLoader::createParameter);
  }

  private class ExpressionLoader {
    private final ModulePath modulePath;
    private final NList<Item> functionParameters;

    public ExpressionLoader(ModulePath modulePath, NList<Item> functionParameters) {
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
      return switch (expr) {
        case ArrayNode arrayNode -> createArrayLiteral(arrayNode);
        case BlobNode blobNode -> createBlobLiteral(blobNode);
        case CallNode callNode -> createCall(callNode);
        case IntNode intNode -> createIntLiteral(intNode);
        case RefNode refNode -> createReference(refNode);
        case SelectNode selectNode -> createSelect(selectNode);
        case StringNode stringNode -> createStringLiteral(stringNode);
        case AnnotationNode node -> null;
        default -> throw new RuntimeException(
            "Unknown AST node: " + expr.getClass().getSimpleName() + ".");
      };
    }

    private ExprS createArrayLiteral(ArrayNode array) {
      var type = (ArrayTypeS) array.type().get();
      ImmutableList<ExprS> elements = map(array.elements(), this::createExpression);
      return new OrderS(type, elements, array.location());
    }

    private ExprS createCall(CallNode call) {
      var called = createExpression(call.function());
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

    private ExprS createArgumentExpression(CallNode call, List<Optional<ArgNode>> args, int i) {
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
      EvaluableLike referenced = ((RefNode) call.function()).referenced();
      return switch (referenced) {
        case FunctionS function -> function.parameters().get(i).defaultValue().get();
        case FunctionNode node -> createExpression(node.params().get(i).body().get());
        default -> throw new RuntimeException("Unexpected case");
      };
    }

    private ExprS createSelect(SelectNode selectNode) {
      var structType = (StructTypeS) selectNode.expr().type().get();
      var index = structType.fields().indexMap().get(selectNode.fieldName());
      var fieldType = structType.fields().get(index).type();
      var expr = createExpression(selectNode.expr());
      return new SelectS(fieldType, expr, index, selectNode.location());
    }

    private ExprS createReference(RefNode ref) {
      EvaluableLike referenced = ref.referenced();
      return switch (referenced) {
        case ItemNode n ->  new ParamRefS(
            functionParameters.get(ref.name()).type(), ref.name(), ref.location());
        default -> new RefS(referenced.inferredType().get(), ref.name(), ref.location());
      };
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
