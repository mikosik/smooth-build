package org.smoothbuild.parse;

import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.lang.define.AnnFuncS;
import org.smoothbuild.lang.define.AnnS;
import org.smoothbuild.lang.define.AnnValS;
import org.smoothbuild.lang.define.DefFuncS;
import org.smoothbuild.lang.define.DefValS;
import org.smoothbuild.lang.define.DefinedS;
import org.smoothbuild.lang.define.FuncS;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.TopEvalS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.expr.BlobS;
import org.smoothbuild.lang.expr.CallS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.lang.expr.OrderS;
import org.smoothbuild.lang.expr.ParamRefS;
import org.smoothbuild.lang.expr.SelectS;
import org.smoothbuild.lang.expr.StringS;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.lang.like.Eval;
import org.smoothbuild.lang.like.Expr;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeSF;
import org.smoothbuild.parse.ast.AnnN;
import org.smoothbuild.parse.ast.BlobN;
import org.smoothbuild.parse.ast.CallN;
import org.smoothbuild.parse.ast.EvalN;
import org.smoothbuild.parse.ast.ExprN;
import org.smoothbuild.parse.ast.FuncN;
import org.smoothbuild.parse.ast.IntN;
import org.smoothbuild.parse.ast.ItemN;
import org.smoothbuild.parse.ast.OrderN;
import org.smoothbuild.parse.ast.RefN;
import org.smoothbuild.parse.ast.SelectN;
import org.smoothbuild.parse.ast.StringN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TopEvalLoader {
  private final TypeSF typeSF;

  @Inject
  public TopEvalLoader(TypeSF typeSF) {
    this.typeSF = typeSF;
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
    if (valN.ann().isPresent()) {
      var ann = loadAnn(valN.ann().get());
      return new AnnValS(ann, type, path, name, loc);
    } else {
      var body = createExpr(valN.body().get());
      return new DefValS(type, path, name, body, loc);
    }
  }

  private FuncS loadFunc(ModPath path, FuncN funcN) {
    var params = loadParams(path, funcN);
    var resT = funcN.resT().get();
    var name = funcN.name();
    var loc = funcN.loc();
    var paramTs = map(params, DefinedS::type);
    var funcT = typeSF.func(resT, paramTs);
    if (funcN.ann().isPresent()) {
      var ann = loadAnn(funcN.ann().get());
      return new AnnFuncS(ann, funcT, path, name, params, loc);
    } else {
      var body = createExpr(funcN.body().get());
      return new DefFuncS(funcT, path, name, params, body, loc);
    }
  }

  private AnnS loadAnn(AnnN annN) {
    var path = createString(annN.path());
    return new AnnS(annN.name(), path, annN.loc());
  }

  private NList<ItemS> loadParams(ModPath path, FuncN funcN) {
    return funcN.params().map(param -> createParam(param, path));
  }

  private ItemS createParam(ItemN param, ModPath path) {
    var type = param.evalT().get().type().get();
    var name = param.name();
    var body = param.body().map(this::createExpr);
    return new ItemS(type, path, name, body, param.loc());
  }

  private ExprS createExpr(ExprN expr) {
    return switch (expr) {
      case OrderN orderN -> createArray(orderN);
      case BlobN blobN -> createBlob(blobN);
      case CallN callN -> createCall(callN);
      case IntN intN -> createInt(intN);
      case RefN refN -> createRef(refN);
      case SelectN selectN -> createSelect(selectN);
      case StringN stringN -> createString(stringN);
    };
  }

  private ExprS createArray(OrderN order) {
    var type = (ArrayTS) order.type().get();
    ImmutableList<ExprS> elems = map(order.elems(), this::createExpr);
    return new OrderS(type, elems, order.loc());
  }

  private ExprS createCall(CallN call) {
    var callable = createExpr(call.callable());
    var argExpressions = map(call.assignedArgs(), a -> createArgExpr(a.expr()));
    var resT = call.type().get();
    return new CallS(resT, callable, argExpressions, call.loc());
  }

  private ExprS createArgExpr(Expr expr) {
    return switch (expr) {
      case ExprN exprN -> createExpr(exprN);
      case ExprS exprS -> exprS;
      default -> throw unexpectedCaseExc(expr);
    };
  }

  private ExprS createSelect(SelectN selectN) {
    var structT = (StructTS) selectN.selectable().type().get();
    var index = structT.fields().indexMap().get(selectN.field());
    var fieldT = structT.fields().get(index).type();
    var selectable = createExpr(selectN.selectable());
    return new SelectS(fieldT, selectable, selectN.field(), selectN.loc());
  }

  private ExprS createRef(RefN ref) {
    Eval referenced = ref.referenced();
    if (referenced instanceof ItemN) {
      return new ParamRefS(ref.type().get(), ref.name(), ref.loc());
    } else {
      return new TopRefS(ref.type().get(), ref.name(), ref.loc());
    }
  }

  public BlobS createBlob(BlobN blob) {
    return new BlobS(
        typeSF.blob(),
        blob.byteString(),
        blob.loc());
  }

  public IntS createInt(IntN intN) {
    return new IntS(
        typeSF.int_(),
        intN.bigInteger(),
        intN.loc());
  }

  public StringS createString(StringN string) {
    return new StringS(
        typeSF.string(),
        string.unescapedValue(),
        string.loc());
  }
}
