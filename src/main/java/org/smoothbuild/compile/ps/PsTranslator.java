package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.lang.define.PolyFuncS.polyFuncS;
import static org.smoothbuild.compile.lang.define.PolyValS.polyValS;
import static org.smoothbuild.compile.lang.type.TypeFS.BLOB;
import static org.smoothbuild.compile.lang.type.TypeFS.INT;
import static org.smoothbuild.compile.lang.type.TypeFS.STRING;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.mapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.lang.define.AnnFuncS;
import org.smoothbuild.compile.lang.define.AnnS;
import org.smoothbuild.compile.lang.define.AnnValS;
import org.smoothbuild.compile.lang.define.BlobS;
import org.smoothbuild.compile.lang.define.CallS;
import org.smoothbuild.compile.lang.define.DefFuncS;
import org.smoothbuild.compile.lang.define.DefValS;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.compile.lang.define.IntS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.ModPath;
import org.smoothbuild.compile.lang.define.MonoizeS;
import org.smoothbuild.compile.lang.define.OrderS;
import org.smoothbuild.compile.lang.define.PolyEvaluableS;
import org.smoothbuild.compile.lang.define.RefS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.define.SelectS;
import org.smoothbuild.compile.lang.define.StringS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.compile.ps.ast.AnnP;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.DefaultArgP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedValP;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ScopedBindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class PsTranslator {
  private final Bindings<? extends Optional<? extends RefableS>> bindings;

  public PsTranslator(Bindings<? extends Optional<? extends RefableS>> bindings) {
    this.bindings = bindings;
  }

  public Optional<PolyEvaluableS> translateVal(ModPath path, NamedValP namedValP, TypeS t) {
    var schema = new SchemaS(t.vars(), t);
    var name = namedValP.name();
    var loc = namedValP.loc();
    if (namedValP.ann().isPresent()) {
      var ann = translateAnn(namedValP.ann().get());
      return Optional.of(polyValS(schema, new AnnValS(ann, schema.type(), path, name, loc)));
    } else {
      var body = translateExpr(namedValP.body().get());
      return body.map(b -> polyValS(schema, new DefValS(schema.type(), path, name, b, loc)));
    }
  }

  public Optional<PolyEvaluableS> translateFunc(ModPath modPath, FuncP funcP, FuncTS funcT) {
    return translateFunc(modPath, funcP, translateParams(funcP), funcT);
  }

  private NList<ItemS> translateParams(FuncP funcP) {
    return NList.nlist(map(funcP.params().list(), this::translateParam));
  }

  public ItemS translateParam(ItemP param) {
    var type = param.typeS();
    var name = param.name();
    var body = param.body().flatMap(this::translateExpr);
    return new ItemS(type, name, body, param.loc());
  }

  private Optional<PolyEvaluableS> translateFunc(ModPath modPath, FuncP funcP, NList<ItemS> params,
      FuncTS funcT) {
    var schema = new FuncSchemaS(funcT.vars(), funcT);
    var name = funcP.name();
    var loc = funcP.loc();
    if (funcP.ann().isPresent()) {
      var ann = translateAnn(funcP.ann().get());
      return Optional.of(polyFuncS(schema, new AnnFuncS(ann, funcT, modPath, name, params, loc)));
    } else {
      var bindingsInBody = new ScopedBindings<Optional<? extends RefableS>>(bindings);
      params.forEach(p -> bindingsInBody.add(p.name(), Optional.of(p)));
      var body = new PsTranslator(bindingsInBody).translateExpr(funcP.body().get());
      return body.map(
          b -> polyFuncS(schema, new DefFuncS(funcT, modPath, name, params, b, loc)));
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
    return switch (expr) {
      case BlobP blobP -> Optional.of(translateBlob(blobP));
      case CallP callP -> translateCall(callP);
      case DefaultArgP defaultArgP -> translateDefaultArg(defaultArgP);
      case IntP intP -> Optional.of(translateInt(intP));
      case NamedArgP namedArgP -> translateExpr(namedArgP.expr());
      case OrderP orderP -> translateOrder(orderP);
      case RefP refP -> translateRef(refP);
      case SelectP selectP -> translateSelect(selectP);
      case StringP stringP -> Optional.of(translateString(stringP));
    };
  }

  private static Optional<ExprS> translateDefaultArg(DefaultArgP defaultArg) {
    return Optional.of(defaultArg.exprS().mapVars(defaultArg.refP().monoizationMapper()));
  }

  private Optional<ExprS> translateOrder(OrderP order) {
    var elems = translateExprs(order.elems());
    return elems.map(es -> new OrderS((ArrayTS) order.typeS(), es, order.loc()));
  }

  private Optional<ExprS> translateCall(CallP call) {
    var callee = translateExpr(call.callee());
    var argExprs = call.positionedArgs().flatMap(this::translateExprs);
    return mapPair(callee, argExprs, (c, as) -> new CallS(call.typeS(), c, as, call.loc()));
  }

  private Optional<ExprS> translateSelect(SelectP selectP) {
    var selectable = translateExpr(selectP.selectable());
    return selectable.map(s -> {
      var fieldName = selectP.field();
      var fieldT = ((StructTS) s.type()).fields().get(fieldName).type();
      return new SelectS(fieldT, s, fieldName, selectP.loc());
    });
  }

  private Optional<ExprS> translateRef(RefP ref) {
    return bindings.get(ref.name())
        .map(r -> translateRef(ref, r));
  }

  private ExprS translateRef(RefP ref, RefableS refable) {
    return switch (refable) {
      case ItemS itemS -> new RefS(itemS.type(), ref.name(), ref.loc());
      case PolyEvaluableS polyEvaluableS -> translateRefToPolyEvaluable(ref, polyEvaluableS);
    };
  }

  private static ExprS translateRefToPolyEvaluable(RefP ref, PolyEvaluableS polyEvaluableS) {
    if (polyEvaluableS.schema().quantifiedVars().isEmpty()) {
      return polyEvaluableS.mono();
    } else {
      // cast is safe because varMap is immutable
      @SuppressWarnings("unchecked")
      var varMap = (ImmutableMap<VarS, TypeS>) ref.monoizationMapping();
      var type = polyEvaluableS.schema().monoize(ref.monoizationMapper());
      return new MonoizeS(type, varMap, polyEvaluableS, ref.loc());
    }
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
