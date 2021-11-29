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
import org.smoothbuild.lang.base.define.TopEvalS;
import org.smoothbuild.lang.base.define.ValueS;
import org.smoothbuild.lang.base.like.EvalLike;
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
import org.smoothbuild.lang.parse.ast.AnnotationN;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayN;
import org.smoothbuild.lang.parse.ast.BlobN;
import org.smoothbuild.lang.parse.ast.CallN;
import org.smoothbuild.lang.parse.ast.EvalN;
import org.smoothbuild.lang.parse.ast.ExprN;
import org.smoothbuild.lang.parse.ast.FunctionN;
import org.smoothbuild.lang.parse.ast.IntN;
import org.smoothbuild.lang.parse.ast.ItemN;
import org.smoothbuild.lang.parse.ast.RealFuncN;
import org.smoothbuild.lang.parse.ast.RefN;
import org.smoothbuild.lang.parse.ast.SelectN;
import org.smoothbuild.lang.parse.ast.StringN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TopEvalLoader {
  private final TypeFactoryS factory;

  @Inject
  public TopEvalLoader(TypeFactoryS factory) {
    this.factory = factory;
  }

  public TopEvalS loadEvaluables(ModulePath path, EvalN evalN) {
    if (evalN instanceof RealFuncN realFuncN) {
      return loadFunction(path, realFuncN);
    } else {
      return loadValue(path, evalN);
    }
  }

  private ValueS loadValue(ModulePath path, EvalN valueNode) {
    var type = valueNode.type().get();
    var name = valueNode.name();
    var location = valueNode.location();
    var loader = new ExpressionLoader(path, nList());
    return new DefinedValueS(
        type, path, name, loader.createExpression(valueNode.body().get()), location);
  }

  private FunctionS loadFunction(ModulePath path, RealFuncN realFuncN) {
    var params = loadParams(path, realFuncN);
    var resultType = realFuncN.resultType().get();
    var name = realFuncN.name();
    var location = realFuncN.location();
    var type = factory.function(resultType, map(params, Defined::type));
    if (realFuncN.annotation().isPresent()) {
      return new NativeFunctionS(type,
          path, name, params, loadAnnotation(realFuncN.annotation().get()), location
      );
    } else {
      var expressionLoader = new ExpressionLoader(path, params);
      return new DefinedFunctionS(type, path,
          name, params, expressionLoader.createExpression(realFuncN.body().get()), location);
    }
  }

  private Annotation loadAnnotation(AnnotationN annotationN) {
    var path = createStringLiteral(annotationN.path());
    return new Annotation(path, annotationN.isPure(), annotationN.location());
  }

  private NList<Item> loadParams(ModulePath path, RealFuncN realFuncN) {
    ExpressionLoader paramLoader = new ExpressionLoader(path, nList());
    return realFuncN.params().map(paramLoader::createParam);
  }

  private class ExpressionLoader {
    private final ModulePath modulePath;
    private final NList<Item> funcParams;

    public ExpressionLoader(ModulePath modulePath, NList<Item> funcParams) {
      this.modulePath = modulePath;
      this.funcParams = funcParams;
    }

    public Item createParam(ItemN param) {
      var type = param.typeNode().get().type().get();
      var name = param.name();
      var defaultArgument = param.body().map(this::createExpression);
      return new Item(type, modulePath, name, defaultArgument, param.location());
    }

    private ExprS createExpression(ExprN expr) {
      return switch (expr) {
        case ArrayN arrayN -> createArrayLiteral(arrayN);
        case BlobN blobN -> createBlobLiteral(blobN);
        case CallN callN -> createCall(callN);
        case IntN intN -> createIntLiteral(intN);
        case RefN refN -> createReference(refN);
        case SelectN selectN -> createSelect(selectN);
        case StringN stringN -> createStringLiteral(stringN);
        case AnnotationN node -> null;
        default -> throw new RuntimeException(
            "Unknown AST node: " + expr.getClass().getSimpleName() + ".");
      };
    }

    private ExprS createArrayLiteral(ArrayN array) {
      var type = (ArrayTypeS) array.type().get();
      ImmutableList<ExprS> elems = map(array.elems(), this::createExpression);
      return new OrderS(type, elems, array.location());
    }

    private ExprS createCall(CallN call) {
      var called = createExpression(call.function());
      var argumentExpressions = createArgumentExpressions(call);
      var resultType = call.type().get();
      return new CallS(resultType, called, argumentExpressions, call.location());
    }

    private ImmutableList<ExprS> createArgumentExpressions(CallN call) {
      var builder = ImmutableList.<ExprS>builder();
      List<Optional<ArgNode>> args = call.assignedArgs();
      for (int i = 0; i < args.size(); i++) {
        builder.add(createArgumentExpression(call, args, i));
      }
      return builder.build();
    }

    private ExprS createArgumentExpression(CallN call, List<Optional<ArgNode>> args, int i) {
      Optional<ArgNode> arg = args.get(i);
      if (arg.isPresent()) {
        return createExpression(arg.get().expr());
      } else {
        return createDefaultArgumentExpression(call, i);
      }
    }

    private ExprS createDefaultArgumentExpression(CallN call, int i) {
      // Argument is not present so we have to use function default argument.
      // This means that this call is made on reference to actual function and that function
      // has default argument for given param, otherwise checkers that ran so far would
      // report an error.
      EvalLike referenced = ((RefN) call.function()).referenced();
      return switch (referenced) {
        case FunctionS function -> function.params().get(i).defaultValue().get();
        case FunctionN node -> createExpression(node.params().get(i).body().get());
        default -> throw new RuntimeException("Unexpected case");
      };
    }

    private ExprS createSelect(SelectN selectN) {
      var structType = (StructTypeS) selectN.expr().type().get();
      var index = structType.fields().indexMap().get(selectN.fieldName());
      var fieldType = structType.fields().get(index).type();
      var expr = createExpression(selectN.expr());
      return new SelectS(fieldType, expr, index, selectN.location());
    }

    private ExprS createReference(RefN ref) {
      EvalLike referenced = ref.referenced();
      return switch (referenced) {
        case ItemN n ->  new ParamRefS(
            funcParams.get(ref.name()).type(), ref.name(), ref.location());
        default -> new RefS(referenced.inferredType().get(), ref.name(), ref.location());
      };
    }
  }

  public BlobS createBlobLiteral(BlobN blob) {
    return new BlobS(
        factory.blob(),
        blob.byteString(),
        blob.location());
  }

  public IntS createIntLiteral(IntN intN) {
    return new IntS(
        factory.int_(),
        intN.bigInteger(),
        intN.location());
  }

  public StringS createStringLiteral(StringN string) {
    return new StringS(
        factory.string(),
        string.unescapedValue(),
        string.location());
  }
}
