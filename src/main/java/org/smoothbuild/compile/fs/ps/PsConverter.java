package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.compile.fs.lang.type.TypeFS.BLOB;
import static org.smoothbuild.compile.fs.lang.type.TypeFS.INT;
import static org.smoothbuild.compile.fs.lang.type.TypeFS.STRING;
import static org.smoothbuild.compile.fs.ps.infer.BindingsHelper.funcBodyScopeBindings;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nlist;
import static org.smoothbuild.util.collect.Optionals.mapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.fs.lang.define.AnnotatedFuncS;
import org.smoothbuild.compile.fs.lang.define.AnnotatedValueS;
import org.smoothbuild.compile.fs.lang.define.AnnotationS;
import org.smoothbuild.compile.fs.lang.define.AnonymousFuncS;
import org.smoothbuild.compile.fs.lang.define.BlobS;
import org.smoothbuild.compile.fs.lang.define.CallS;
import org.smoothbuild.compile.fs.lang.define.ExprS;
import org.smoothbuild.compile.fs.lang.define.IntS;
import org.smoothbuild.compile.fs.lang.define.ItemS;
import org.smoothbuild.compile.fs.lang.define.MonoizableS;
import org.smoothbuild.compile.fs.lang.define.MonoizeS;
import org.smoothbuild.compile.fs.lang.define.NamedExprFuncS;
import org.smoothbuild.compile.fs.lang.define.NamedExprValueS;
import org.smoothbuild.compile.fs.lang.define.NamedFuncS;
import org.smoothbuild.compile.fs.lang.define.NamedValueS;
import org.smoothbuild.compile.fs.lang.define.OrderS;
import org.smoothbuild.compile.fs.lang.define.RefS;
import org.smoothbuild.compile.fs.lang.define.RefableS;
import org.smoothbuild.compile.fs.lang.define.SelectS;
import org.smoothbuild.compile.fs.lang.define.StringS;
import org.smoothbuild.compile.fs.lang.type.ArrayTS;
import org.smoothbuild.compile.fs.ps.ast.expr.AnnotationP;
import org.smoothbuild.compile.fs.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.fs.ps.ast.expr.BlobP;
import org.smoothbuild.compile.fs.ps.ast.expr.CallP;
import org.smoothbuild.compile.fs.ps.ast.expr.ExprP;
import org.smoothbuild.compile.fs.ps.ast.expr.IntP;
import org.smoothbuild.compile.fs.ps.ast.expr.ItemP;
import org.smoothbuild.compile.fs.ps.ast.expr.MonoizableP;
import org.smoothbuild.compile.fs.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.expr.OrderP;
import org.smoothbuild.compile.fs.ps.ast.expr.RefP;
import org.smoothbuild.compile.fs.ps.ast.expr.SelectP;
import org.smoothbuild.compile.fs.ps.ast.expr.StringP;
import org.smoothbuild.util.bindings.OptionalBindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class PsConverter {
  private final OptionalBindings<? extends RefableS> bindings;

  public PsConverter(OptionalBindings<? extends RefableS> bindings) {
    this.bindings = bindings;
  }

  public Optional<NamedValueS> convertNamedValue(NamedValueP namedValueP) {
    var schema = namedValueP.schemaS();
    var name = namedValueP.name();
    var location = namedValueP.location();
    if (namedValueP.annotation().isPresent()) {
      var ann = convertAnnotation(namedValueP.annotation().get());
      return Optional.of(new AnnotatedValueS(ann, schema, name, location));
    } else {
      var body = convertExpr(namedValueP.body().get());
      return body.map(b -> new NamedExprValueS(schema, name, b, location));
    }
  }

  public Optional<NamedFuncS> convertNamedFunc(NamedFuncP namedFuncP) {
    return convertNamedFunc(namedFuncP, convertParams(namedFuncP));
  }

  private NList<ItemS> convertParams(NamedFuncP namedFuncP) {
    return nlist(map(namedFuncP.params().list(), this::convertParam));
  }

  private NList<ItemS> convertParams(NList<ItemP> params) {
    return nlist(map(params.list(), this::convertParam));
  }

  public ItemS convertParam(ItemP paramP) {
    var type = paramP.typeS();
    var name = paramP.name();
    var body = paramP.defaultValue().flatMap(this::convertNamedValue);
    return new ItemS(type, name, body, paramP.location());
  }

  private Optional<NamedFuncS> convertNamedFunc(NamedFuncP namedFuncP, NList<ItemS> params) {
    var schema = namedFuncP.schemaS();
    var name = namedFuncP.name();
    var loc = namedFuncP.location();
    if (namedFuncP.annotation().isPresent()) {
      var annotationS = convertAnnotation(namedFuncP.annotation().get());
      var annotatedFuncS = new AnnotatedFuncS(annotationS, schema, name, params, loc);
      return Optional.of(annotatedFuncS);
    } else {
      return convertFuncBody(params, namedFuncP.body().get())
          .map(b -> new NamedExprFuncS(schema, name, params, b, loc));
    }
  }

  private AnnotationS convertAnnotation(AnnotationP annotationP) {
    var path = convertString(annotationP.value());
    return new AnnotationS(annotationP.name(), path, annotationP.location());
  }

  private Optional<ImmutableList<ExprS>> convertExprs(List<ExprP> positionedArgs) {
    return pullUp(map(positionedArgs, this::convertExpr));
  }

  private Optional<ExprS> convertExpr(ExprP expr) {
    // @formatter:off
    return switch (expr) {
      case BlobP          blobP          -> Optional.of(convertBlob(blobP));
      case CallP          callP          -> convertCall(callP);
      case IntP           intP           -> Optional.of(convertInt(intP));
      case AnonymousFuncP anonymousFuncP -> convertAnonymousFunc(anonymousFuncP);
      case NamedArgP      namedArgP      -> convertExpr(namedArgP.expr());
      case OrderP         orderP         -> convertOrder(orderP);
      case RefP           refP           -> convertRef(refP);
      case SelectP        selectP        -> convertSelect(selectP);
      case StringP        stringP        -> Optional.of(convertString(stringP));
    };
    // @formatter:on
  }

  private Optional<ExprS> convertOrder(OrderP order) {
    var elems = convertExprs(order.elems());
    return elems.map(es -> new OrderS((ArrayTS) order.typeS(), es, order.location()));
  }

  private Optional<ExprS> convertCall(CallP call) {
    var callee = convertExpr(call.callee());
    var args = convertExprs(call.positionedArgs());
    return mapPair(callee, args, (c, as) -> new CallS(c, as, call.location()));
  }

  private Optional<ExprS> convertAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    var params = convertParams(anonymousFuncP.params());
    return convertFuncBody(params, anonymousFuncP.bodyGet())
        .map(b -> monoizeAnonymousFunc(anonymousFuncP, params, b));
  }

  private Optional<ExprS> convertFuncBody(NList<ItemS> params, ExprP expr) {
    var bindingsInBody = funcBodyScopeBindings(bindings, params);
    return new PsConverter(bindingsInBody)
        .convertExpr(expr);
  }

  private static MonoizeS monoizeAnonymousFunc(
      AnonymousFuncP anonymousFuncP, NList<ItemS> params, ExprS body) {
    var anonymousFuncS =
        new AnonymousFuncS(anonymousFuncP.schemaS(), params, body, anonymousFuncP.location());
    return newMonoize(anonymousFuncP, anonymousFuncS);
  }

  private Optional<ExprS> convertSelect(SelectP selectP) {
    var selectable = convertExpr(selectP.selectable());
    return selectable.map(s -> new SelectS(s, selectP.field(), selectP.location()));
  }

  private Optional<ExprS> convertRef(RefP ref) {
    return bindings.get(ref.name())
        .map(r -> convertRef(ref, r));
  }

  private ExprS convertRef(RefP ref, RefableS refable) {
    return monoizeRefable(ref, refable);
  }

  private static ExprS monoizeRefable(MonoizableP monoizableP, RefableS refableS) {
    var refS = new RefS(refableS.schema(), refableS.name(), monoizableP.location());
    return newMonoize(monoizableP, refS);
  }

  private static MonoizeS newMonoize(MonoizableP monoizableP, MonoizableS monoizableS) {
    return new MonoizeS(monoizableP.monoizeVarMap(), monoizableS, monoizableP.location());
  }

  private BlobS convertBlob(BlobP blob) {
    return new BlobS(BLOB, blob.byteString(), blob.location());
  }

  private IntS convertInt(IntP int_) {
    return new IntS(INT, int_.bigInteger(), int_.location());
  }

  private StringS convertString(StringP string) {
    return new StringS(STRING, string.unescapedValue(), string.location());
  }
}
