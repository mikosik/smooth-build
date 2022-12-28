package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.lang.type.TypeFS.BLOB;
import static org.smoothbuild.compile.lang.type.TypeFS.INT;
import static org.smoothbuild.compile.lang.type.TypeFS.STRING;
import static org.smoothbuild.compile.ps.infer.BindingsHelper.funcBodyScopeBindings;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nlist;
import static org.smoothbuild.util.collect.Optionals.mapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.lang.define.AnnotatedFuncS;
import org.smoothbuild.compile.lang.define.AnnotatedValueS;
import org.smoothbuild.compile.lang.define.AnnotationS;
import org.smoothbuild.compile.lang.define.AnonymousFuncS;
import org.smoothbuild.compile.lang.define.BlobS;
import org.smoothbuild.compile.lang.define.CallS;
import org.smoothbuild.compile.lang.define.EvaluableRefS;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.lang.define.IntS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.MonoizableS;
import org.smoothbuild.compile.lang.define.MonoizeS;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.lang.define.NamedExprFuncS;
import org.smoothbuild.compile.lang.define.NamedExprValueS;
import org.smoothbuild.compile.lang.define.NamedFuncS;
import org.smoothbuild.compile.lang.define.OrderS;
import org.smoothbuild.compile.lang.define.ParamRefS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.define.SelectS;
import org.smoothbuild.compile.lang.define.StringS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.ps.ast.expr.AnnotationP;
import org.smoothbuild.compile.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
import org.smoothbuild.compile.ps.ast.expr.MonoizableP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.util.bindings.OptionalBindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class PsTranslator {
  private final OptionalBindings<? extends RefableS> bindings;

  public PsTranslator(OptionalBindings<? extends RefableS> bindings) {
    this.bindings = bindings;
  }

  public Optional<NamedEvaluableS> translateNamedValue(NamedValueP namedValueP) {
    var schema = namedValueP.schemaS();
    var name = namedValueP.name();
    var location = namedValueP.location();
    if (namedValueP.annotation().isPresent()) {
      var ann = translateAnnotation(namedValueP.annotation().get());
      return Optional.of(new AnnotatedValueS(ann, schema, name, location));
    } else {
      var body = translateExpr(namedValueP.body().get());
      return body.map(b -> new NamedExprValueS(schema, name, b, location));
    }
  }

  public Optional<NamedFuncS> translateNamedFunc(NamedFuncP namedFuncP) {
    return translateNamedFunc(namedFuncP, translateParams(namedFuncP));
  }

  private NList<ItemS> translateParams(NamedFuncP namedFuncP) {
    return NList.nlist(map(namedFuncP.params().list(), this::translateParam));
  }

  private NList<ItemS> translateParams(NList<ItemP> params) {
    return nlist(map(params.list(), this::translateParam));
  }

  public ItemS translateParam(ItemP paramP) {
    var type = paramP.typeS();
    var name = paramP.name();
    var body = paramP.defaultValue().flatMap(this::translateNamedValue);
    return new ItemS(type, name, body, paramP.location());
  }

  private Optional<NamedFuncS> translateNamedFunc(NamedFuncP namedFuncP, NList<ItemS> params) {
    var schema = namedFuncP.schemaS();
    var name = namedFuncP.name();
    var loc = namedFuncP.location();
    if (namedFuncP.annotation().isPresent()) {
      var annotationS = translateAnnotation(namedFuncP.annotation().get());
      var annotatedFuncS = new AnnotatedFuncS(annotationS, schema, name, params, loc);
      return Optional.of(annotatedFuncS);
    } else {
      return translateFuncBody(params, namedFuncP.body().get())
          .map(b -> new NamedExprFuncS(schema, name, params, b, loc));
    }
  }

  private AnnotationS translateAnnotation(AnnotationP annotationP) {
    var path = translateString(annotationP.value());
    return new AnnotationS(annotationP.name(), path, annotationP.location());
  }

  private Optional<ImmutableList<ExprS>> translateExprs(List<ExprP> positionedArgs) {
    return pullUp(map(positionedArgs, this::translateExpr));
  }

  private Optional<ExprS> translateExpr(ExprP expr) {
    // @formatter:off
    return switch (expr) {
      case BlobP          blobP          -> Optional.of(translateBlob(blobP));
      case CallP          callP          -> translateCall(callP);
      case IntP           intP           -> Optional.of(translateInt(intP));
      case AnonymousFuncP anonymousFuncP -> translateAnonymousFunc(anonymousFuncP);
      case NamedArgP      namedArgP      -> translateExpr(namedArgP.expr());
      case OrderP         orderP         -> translateOrder(orderP);
      case RefP           refP           -> translateRef(refP);
      case SelectP        selectP        -> translateSelect(selectP);
      case StringP        stringP        -> Optional.of(translateString(stringP));
    };
    // @formatter:on
  }

  private Optional<ExprS> translateOrder(OrderP order) {
    var elems = translateExprs(order.elems());
    return elems.map(es -> new OrderS((ArrayTS) order.typeS(), es, order.location()));
  }

  private Optional<ExprS> translateCall(CallP call) {
    var callee = translateExpr(call.callee());
    var args = translateExprs(call.positionedArgs());
    return mapPair(callee, args, (c, as) -> new CallS(c, as, call.location()));
  }

  private Optional<ExprS> translateAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    var params = translateParams(anonymousFuncP.params());
    return translateFuncBody(params, anonymousFuncP.bodyGet())
        .map(b -> monoizeAnonymousFunc(anonymousFuncP, params, b));
  }

  private Optional<ExprS> translateFuncBody(NList<ItemS> params, ExprP expr) {
    var bindingsInBody = funcBodyScopeBindings(bindings, params);
    return new PsTranslator(bindingsInBody)
        .translateExpr(expr);
  }

  private static MonoizeS monoizeAnonymousFunc(
      AnonymousFuncP anonymousFuncP, NList<ItemS> params, ExprS body) {
    var anonymousFuncS =
        new AnonymousFuncS(anonymousFuncP.schemaS(), params, body, anonymousFuncP.location());
    return newMonoize(anonymousFuncP, anonymousFuncS);
  }

  private Optional<ExprS> translateSelect(SelectP selectP) {
    var selectable = translateExpr(selectP.selectable());
    return selectable.map(s -> new SelectS(s, selectP.field(), selectP.location()));
  }

  private Optional<ExprS> translateRef(RefP ref) {
    return bindings.get(ref.name())
        .map(r -> translateRef(ref, r));
  }

  private ExprS translateRef(RefP ref, RefableS refable) {
    return switch (refable) {
      case ItemS itemS -> new ParamRefS(itemS.type(), ref.name(), ref.location());
      case NamedEvaluableS evaluableS -> monoizeNamedEvaluable(ref, evaluableS);
    };
  }

  private static ExprS monoizeNamedEvaluable(
      MonoizableP monoizableP, NamedEvaluableS namedEvaluableS) {
    var evaluableRefS = new EvaluableRefS(namedEvaluableS, monoizableP.location());
    return newMonoize(monoizableP, evaluableRefS);
  }

  private static MonoizeS newMonoize(MonoizableP monoizableP, MonoizableS monoizableS) {
    return new MonoizeS(monoizableP.monoizeVarMap(), monoizableS, monoizableP.location());
  }

  private BlobS translateBlob(BlobP blob) {
    return new BlobS(BLOB, blob.byteString(), blob.location());
  }

  private IntS translateInt(IntP int_) {
    return new IntS(INT, int_.bigInteger(), int_.location());
  }

  private StringS translateString(StringP string) {
    return new StringS(STRING, string.unescapedValue(), string.location());
  }
}
