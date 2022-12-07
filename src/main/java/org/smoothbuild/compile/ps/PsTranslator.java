package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.lang.type.TypeFS.BLOB;
import static org.smoothbuild.compile.lang.type.TypeFS.INT;
import static org.smoothbuild.compile.lang.type.TypeFS.STRING;
import static org.smoothbuild.compile.ps.infer.BindingsHelper.funcBodyScopeBindings2;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nlist;
import static org.smoothbuild.util.collect.Optionals.mapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.lang.define.AnnFuncS;
import org.smoothbuild.compile.lang.define.AnnS;
import org.smoothbuild.compile.lang.define.AnnValueS;
import org.smoothbuild.compile.lang.define.AnonFuncS;
import org.smoothbuild.compile.lang.define.BlobS;
import org.smoothbuild.compile.lang.define.CallS;
import org.smoothbuild.compile.lang.define.DefFuncS;
import org.smoothbuild.compile.lang.define.DefValueS;
import org.smoothbuild.compile.lang.define.EvaluableRefS;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.lang.define.IntS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.MonoizableS;
import org.smoothbuild.compile.lang.define.MonoizeS;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.lang.define.NamedFuncS;
import org.smoothbuild.compile.lang.define.OrderS;
import org.smoothbuild.compile.lang.define.ParamRefS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.define.SelectS;
import org.smoothbuild.compile.lang.define.StringS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.ps.ast.AnnP;
import org.smoothbuild.compile.ps.ast.expr.AnonFuncP;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.MonoizableP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedFuncP;
import org.smoothbuild.compile.ps.ast.refable.NamedValueP;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class PsTranslator {
  private final Bindings<? extends Optional<? extends RefableS>> bindings;

  public PsTranslator(Bindings<? extends Optional<? extends RefableS>> bindings) {
    this.bindings = bindings;
  }

  public Optional<NamedEvaluableS> translateNamedValue(NamedValueP namedValueP) {
    var schema = namedValueP.schemaS();
    var name = namedValueP.name();
    var loc = namedValueP.loc();
    if (namedValueP.ann().isPresent()) {
      var ann = translateAnn(namedValueP.ann().get());
      return Optional.of(new AnnValueS(ann, schema, name, loc));
    } else {
      var body = translateExpr(namedValueP.body().get());
      return body.map(b -> new DefValueS(schema, name, b, loc));
    }
  }

  public Optional<NamedFuncS> translateNamedFunc(NamedFuncP namedFuncP) {
    return translateNamedFunc(namedFuncP, translateParams(namedFuncP));
  }

  private NList<ItemS> translateParams(NamedFuncP namedFuncP) {
    return NList.nlist(map(namedFuncP.params().list(), param -> translateParam(namedFuncP, param)));
  }

  private NList<ItemS> translateParams(NamedFuncP namedFuncP, NList<ItemP> params) {
    return nlist(map(params.list(), param -> translateParam(namedFuncP, param)));
  }

  public ItemS translateParam(NamedFuncP namedFuncP, ItemP paramP) {
    var type = paramP.typeS();
    var name = paramP.name();
    var body = paramP.defaultValue().flatMap(expr -> translateParamBody(namedFuncP, paramP, expr));
    return new ItemS(type, name, body, paramP.loc());
  }

  private Optional<NamedEvaluableS> translateParamBody(
      NamedFuncP namedFuncP, ItemP paramP, ExprP exprP) {
    return translateExpr(exprP).map(exprS -> {
      var name = namedFuncP.name() + ":" + paramP.name();
      var type = exprS.evalT();
      return new DefValueS(new SchemaS(type.vars(), type), name, exprS, paramP.loc());
    });
  }

  private Optional<NamedFuncS> translateNamedFunc(NamedFuncP namedFuncP, NList<ItemS> params) {
    var schema = namedFuncP.schemaS();
    var name = namedFuncP.name();
    var loc = namedFuncP.loc();
    if (namedFuncP.ann().isPresent()) {
      var ann = translateAnn(namedFuncP.ann().get());
      var annFuncS = new AnnFuncS(ann, schema, name, params, loc);
      return Optional.of(annFuncS);
    } else {
      return translateFuncBody(params, namedFuncP.body().get())
          .map(b -> new DefFuncS(schema, name, params, b, loc));
    }
  }

  private AnnS translateAnn(AnnP annP) {
    var path = translateString(annP.path());
    return new AnnS(annP.name(), path, annP.loc());
  }

  private Optional<ImmutableList<ExprS>> translateExprs(List<ExprP> positionedArgs) {
    return pullUp(map(positionedArgs, this::translateExpr));
  }

  private Optional<ExprS> translateExpr(ExprP expr) {
    // @formatter:off
    return switch (expr) {
      case BlobP       blobP       -> Optional.of(translateBlob(blobP));
      case CallP       callP       -> translateCall(callP);
      case IntP        intP        -> Optional.of(translateInt(intP));
      case AnonFuncP   anonFuncP   -> translateAnonFunc(anonFuncP);
      case NamedArgP   namedArgP   -> translateExpr(namedArgP.expr());
      case OrderP      orderP      -> translateOrder(orderP);
      case RefP        refP        -> translateRef(refP);
      case SelectP     selectP     -> translateSelect(selectP);
      case StringP     stringP     -> Optional.of(translateString(stringP));
    };
    // @formatter:on
  }

  private Optional<ExprS> translateOrder(OrderP order) {
    var elems = translateExprs(order.elems());
    return elems.map(es -> new OrderS((ArrayTS) order.typeS(), es, order.loc()));
  }

  private Optional<ExprS> translateCall(CallP call) {
    var callee = translateExpr(call.callee());
    var argExprs = call.positionedArgs().flatMap(this::translateExprs);
    return mapPair(callee, argExprs, (c, as) -> new CallS(c, as, call.loc()));
  }

  private Optional<ExprS> translateAnonFunc(AnonFuncP anonFuncP) {
    var params = translateParams(null, anonFuncP.params());
    return translateFuncBody(params, anonFuncP.bodyGet())
        .map(b -> monoizeAnonFunc(anonFuncP, params, b));
  }

  private Optional<ExprS> translateFuncBody(NList<ItemS> params, ExprP expr) {
    var bindingsInBody = funcBodyScopeBindings2(bindings, params);
    return new PsTranslator(bindingsInBody)
        .translateExpr(expr);
  }

  private static MonoizeS monoizeAnonFunc(AnonFuncP anonFuncP, NList<ItemS> params, ExprS body) {
    var anonFuncS = new AnonFuncS(anonFuncP.schemaS(), params, body, anonFuncP.loc());
    return newMonoize(anonFuncP, anonFuncS);
  }

  private Optional<ExprS> translateSelect(SelectP selectP) {
    var selectable = translateExpr(selectP.selectable());
    return selectable.map(s -> new SelectS(s, selectP.field(), selectP.loc()));
  }

  private Optional<ExprS> translateRef(RefP ref) {
    return bindings.get(ref.name())
        .map(r -> translateRef(ref, r));
  }

  private ExprS translateRef(RefP ref, RefableS refable) {
    return switch (refable) {
      case ItemS itemS -> new ParamRefS(itemS.type(), ref.name(), ref.loc());
      case NamedEvaluableS evaluableS -> monoizeNamedEvaluable(ref, evaluableS);
    };
  }

  private static ExprS monoizeNamedEvaluable(
      MonoizableP monoizableP, NamedEvaluableS namedEvaluableS) {
    var evaluableRefS = new EvaluableRefS(namedEvaluableS, monoizableP.loc());
    return newMonoize(monoizableP, evaluableRefS);
  }

  private static MonoizeS newMonoize(MonoizableP monoizableP, MonoizableS monoizableS) {
    return new MonoizeS(monoizableP.monoizeVarMap(), monoizableS, monoizableP.loc());
  }

  private BlobS translateBlob(BlobP blob) {
    return new BlobS(BLOB, blob.byteString(), blob.loc());
  }

  private IntS translateInt(IntP int_) {
    return new IntS(INT, int_.bigInteger(), int_.loc());
  }

  private StringS translateString(StringP string) {
    return new StringS(STRING, string.unescapedValue(), string.loc());
  }
}
