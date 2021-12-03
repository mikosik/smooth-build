package org.smoothbuild.lang.parse;

import static org.smoothbuild.slib.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.lang.base.define.DefFuncS;
import org.smoothbuild.lang.base.define.DefValS;
import org.smoothbuild.lang.base.define.DefinedS;
import org.smoothbuild.lang.base.define.FuncS;
import org.smoothbuild.lang.base.define.ItemS;
import org.smoothbuild.lang.base.define.ModPath;
import org.smoothbuild.lang.base.define.NatFuncS;
import org.smoothbuild.lang.base.define.TopEvalS;
import org.smoothbuild.lang.base.define.ValS;
import org.smoothbuild.lang.base.like.EvalLike;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.expr.AnnS;
import org.smoothbuild.lang.expr.BlobS;
import org.smoothbuild.lang.expr.CallS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.lang.expr.OrderS;
import org.smoothbuild.lang.expr.ParamRefS;
import org.smoothbuild.lang.expr.RefS;
import org.smoothbuild.lang.expr.SelectS;
import org.smoothbuild.lang.expr.StringS;
import org.smoothbuild.lang.parse.ast.AnnN;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayN;
import org.smoothbuild.lang.parse.ast.BlobN;
import org.smoothbuild.lang.parse.ast.CallN;
import org.smoothbuild.lang.parse.ast.EvalN;
import org.smoothbuild.lang.parse.ast.ExprN;
import org.smoothbuild.lang.parse.ast.FuncN;
import org.smoothbuild.lang.parse.ast.IntN;
import org.smoothbuild.lang.parse.ast.ItemN;
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

  public TopEvalS loadEval(ModPath path, EvalN evalN) {
    if (evalN instanceof FuncN funcN) {
      return loadFunc(path, funcN);
    } else {
      return loadVal(path, evalN);
    }
  }

  private ValS loadVal(ModPath path, EvalN valN) {
    var type = valN.type().get();
    var name = valN.name();
    var loc = valN.loc();
    var loader = new ExpressionLoader(path, nList());
    return new DefValS(
        type, path, name, loader.createExpression(valN.body().get()), loc);
  }

  private FuncS loadFunc(ModPath path, FuncN funcN) {
    var params = loadParams(path, funcN);
    var resultType = funcN.resType().get();
    var name = funcN.name();
    var loc = funcN.loc();
    var type = factory.func(resultType, map(params, DefinedS::type));
    if (funcN.ann().isPresent()) {
      return new NatFuncS(type,
          path, name, params, loadAnn(funcN.ann().get()), loc
      );
    } else {
      var expressionLoader = new ExpressionLoader(path, params);
      return new DefFuncS(type, path,
          name, params, expressionLoader.createExpression(funcN.body().get()), loc);
    }
  }

  private AnnS loadAnn(AnnN annN) {
    var path = createStringLiteral(annN.path());
    return new AnnS(path, annN.isPure(), annN.loc());
  }

  private NList<ItemS> loadParams(ModPath path, FuncN funcN) {
    ExpressionLoader paramLoader = new ExpressionLoader(path, nList());
    return funcN.params().map(paramLoader::createParam);
  }

  private class ExpressionLoader {
    private final ModPath modPath;
    private final NList<ItemS> funcParams;

    public ExpressionLoader(ModPath modPath, NList<ItemS> funcParams) {
      this.modPath = modPath;
      this.funcParams = funcParams;
    }

    public ItemS createParam(ItemN param) {
      var type = param.typeNode().get().type().get();
      var name = param.name();
      var defaultArg = param.body().map(this::createExpression);
      return new ItemS(type, modPath, name, defaultArg, param.loc());
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
      };
    }

    private ExprS createArrayLiteral(ArrayN array) {
      var type = (ArrayTypeS) array.type().get();
      ImmutableList<ExprS> elems = map(array.elems(), this::createExpression);
      return new OrderS(type, elems, array.loc());
    }

    private ExprS createCall(CallN call) {
      var called = createExpression(call.func());
      var argExpressions = createArgExpressions(call);
      var resultType = call.type().get();
      return new CallS(resultType, called, argExpressions, call.loc());
    }

    private ImmutableList<ExprS> createArgExpressions(CallN call) {
      var builder = ImmutableList.<ExprS>builder();
      List<Optional<ArgNode>> args = call.assignedArgs();
      for (int i = 0; i < args.size(); i++) {
        builder.add(createArgExpression(call, args, i));
      }
      return builder.build();
    }

    private ExprS createArgExpression(CallN call, List<Optional<ArgNode>> args, int i) {
      Optional<ArgNode> arg = args.get(i);
      if (arg.isPresent()) {
        return createExpression(arg.get().expr());
      } else {
        return createDefaultArgExpression(call, i);
      }
    }

    private ExprS createDefaultArgExpression(CallN call, int i) {
      // Arg is not present so we have to use func default arg.
      // This means that this call is made on reference to actual func and that func
      // has default arg for given param, otherwise checkers that ran so far would
      // report an error.
      EvalLike referenced = ((RefN) call.func()).referenced();
      return switch (referenced) {
        case FuncS func -> func.params().get(i).defaultVal().get();
        case FuncN node -> createExpression(node.params().get(i).body().get());
        default -> throw unexpectedCaseExc(referenced);
      };
    }

    private ExprS createSelect(SelectN selectN) {
      var structType = (StructTypeS) selectN.expr().type().get();
      var index = structType.fields().indexMap().get(selectN.fieldName());
      var fieldType = structType.fields().get(index).type();
      var expr = createExpression(selectN.expr());
      return new SelectS(fieldType, expr, selectN.fieldName(), selectN.loc());
    }

    private ExprS createReference(RefN ref) {
      EvalLike referenced = ref.referenced();
      return switch (referenced) {
        case ItemN n ->  new ParamRefS(
            funcParams.get(ref.name()).type(), ref.name(), ref.loc());
        default -> new RefS(referenced.inferredType().get(), ref.name(), ref.loc());
      };
    }
  }

  public BlobS createBlobLiteral(BlobN blob) {
    return new BlobS(
        factory.blob(),
        blob.byteString(),
        blob.loc());
  }

  public IntS createIntLiteral(IntN intN) {
    return new IntS(
        factory.int_(),
        intN.bigInteger(),
        intN.loc());
  }

  public StringS createStringLiteral(StringN string) {
    return new StringS(
        factory.string(),
        string.unescapedValue(),
        string.loc());
  }
}
