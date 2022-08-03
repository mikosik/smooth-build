package org.smoothbuild.parse;

import static org.smoothbuild.lang.define.PolyFuncS.polyFuncS;
import static org.smoothbuild.lang.define.PolyValS.polyValS;
import static org.smoothbuild.lang.type.TypeFS.BLOB;
import static org.smoothbuild.lang.type.TypeFS.INT;
import static org.smoothbuild.lang.type.TypeFS.STRING;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.mapPair;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.define.AnnFuncS;
import org.smoothbuild.lang.define.AnnS;
import org.smoothbuild.lang.define.AnnValS;
import org.smoothbuild.lang.define.BlobS;
import org.smoothbuild.lang.define.CallS;
import org.smoothbuild.lang.define.DefFuncS;
import org.smoothbuild.lang.define.DefValS;
import org.smoothbuild.lang.define.ExprS;
import org.smoothbuild.lang.define.IntS;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.MonoRefableS;
import org.smoothbuild.lang.define.MonoizeS;
import org.smoothbuild.lang.define.OrderS;
import org.smoothbuild.lang.define.ParamRefS;
import org.smoothbuild.lang.define.PolyRefableS;
import org.smoothbuild.lang.define.RefableS;
import org.smoothbuild.lang.define.SelectS;
import org.smoothbuild.lang.define.StringS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.FuncSchemaS;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.SchemaS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.parse.ast.AnnP;
import org.smoothbuild.parse.ast.expr.BlobP;
import org.smoothbuild.parse.ast.expr.CallP;
import org.smoothbuild.parse.ast.expr.DefaultArgP;
import org.smoothbuild.parse.ast.expr.ExprP;
import org.smoothbuild.parse.ast.expr.IntP;
import org.smoothbuild.parse.ast.expr.NamedArgP;
import org.smoothbuild.parse.ast.expr.OrderP;
import org.smoothbuild.parse.ast.expr.RefP;
import org.smoothbuild.parse.ast.expr.SelectP;
import org.smoothbuild.parse.ast.expr.StringP;
import org.smoothbuild.parse.ast.refable.FuncP;
import org.smoothbuild.parse.ast.refable.ItemP;
import org.smoothbuild.parse.ast.refable.ValP;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ScopedBindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class PsConverter {
  private final Bindings<? extends Optional<? extends RefableS>> bindings;

  public PsConverter(Bindings<? extends Optional<? extends RefableS>> bindings) {
    this.bindings = bindings;
  }

  public Optional<PolyRefableS> convertVal(ModPath path, ValP valP, TypeS t) {
    var schema = new SchemaS(t.vars(), t);
    var name = valP.name();
    var loc = valP.loc();
    if (valP.ann().isPresent()) {
      var ann = convertAnn(valP.ann().get());
      return Optional.of(polyValS(schema, new AnnValS(ann, schema.type(), path, name, loc)));
    } else {
      var body = convertExpr(valP.body().get());
      return body.map(b -> polyValS(schema, new DefValS(schema.type(), path, name, b, loc)));
    }
  }

  public Optional<PolyRefableS> convertFunc(ModPath modPath, FuncP funcP, FuncTS funcT) {
    return convertFunc(modPath, funcP, convertParams(funcP), funcT);
  }

  private NList<ItemS> convertParams(FuncP funcP) {
    return NList.nlist(map(funcP.params().list(), this::convertParam));
  }

  public ItemS convertParam(ItemP param) {
    var type = param.typeS();
    var name = param.name();
    var body = param.body().flatMap(this::convertExpr);
    return new ItemS(type, name, body, param.loc());
  }

  private Optional<PolyRefableS> convertFunc(ModPath modPath, FuncP funcP, NList<ItemS> params,
      FuncTS funcT) {
    var schema = new FuncSchemaS(funcT.vars(), funcT);
    var name = funcP.name();
    var loc = funcP.loc();
    if (funcP.ann().isPresent()) {
      var ann = convertAnn(funcP.ann().get());
      return Optional.of(polyFuncS(schema, new AnnFuncS(ann, funcT, modPath, name, params, loc)));
    } else {
      var bindingsInBody = new ScopedBindings<Optional<? extends RefableS>>(bindings);
      params.forEach(p -> bindingsInBody.add(p.name(), Optional.of(p)));
      var body = new PsConverter(bindingsInBody).convertExpr(funcP.body().get());
      return body.map(
          b -> polyFuncS(schema, new DefFuncS(funcT, modPath, name, params, b, loc)));
    }
  }

  private AnnS convertAnn(AnnP annP) {
    var path = convertString(annP.path());
    return new AnnS(annP.name(), path, annP.loc());
  }

  private Optional<ImmutableList<ExprS>> convertExprs(List<ExprP> positionedArgs) {
    return pullUp(map(positionedArgs, this::convertExpr));
  }

  private Optional<ExprS> convertExpr(ExprP expr) {
    return switch (expr) {
      case BlobP blobP -> Optional.of(convertBlob(blobP));
      case CallP callP -> convertCall(callP);
      case DefaultArgP defaultArgP -> convertDefaultArg(defaultArgP);
      case IntP intP -> Optional.of(convertInt(intP));
      case NamedArgP namedArgP -> convertExpr(namedArgP.expr());
      case OrderP orderP -> convertOrder(orderP);
      case RefP refP -> convertRef(refP);
      case SelectP selectP -> convertSelect(selectP);
      case StringP stringP -> Optional.of(convertString(stringP));
    };
  }

  private static Optional<ExprS> convertDefaultArg(DefaultArgP defaultArg) {
    return Optional.of(defaultArg.exprS().mapVars(defaultArg.refP().monoizationMapper()));
  }

  private Optional<ExprS> convertOrder(OrderP order) {
    var elems = convertExprs(order.elems());
    return elems.map(es -> new OrderS((ArrayTS) order.typeS(), es, order.loc()));
  }

  private Optional<ExprS> convertCall(CallP call) {
    var callee = convertExpr(call.callee());
    var argExprs = call.positionedArgs().flatMap(this::convertExprs);
    return mapPair(callee, argExprs, (c, as) -> new CallS(call.typeS(), c, as, call.loc()));
  }

  private Optional<ExprS> convertSelect(SelectP selectP) {
    var selectable = convertExpr(selectP.selectable());
    return selectable.map(s -> {
      var fieldName = selectP.field();
      var fieldT = ((StructTS) s.type()).fields().get(fieldName).type();
      return new SelectS(fieldT, s, fieldName, selectP.loc());
    });
  }

  private Optional<ExprS> convertRef(RefP ref) {
    return bindings.get(ref.name())
        .map(r -> convertRef(ref, r));
  }

  private ExprS convertRef(RefP ref, RefableS refable) {
    return switch (refable) {
      case ItemS itemP -> new ParamRefS(itemP.type(), ref.name(), ref.loc());
      case MonoRefableS monoRefableS -> monoRefableS;
      case PolyRefableS polyRefableS -> convertRefToPolyRefable(ref, polyRefableS);
    };
  }

  private static ExprS convertRefToPolyRefable(RefP ref, PolyRefableS polyRefableS) {
    if (polyRefableS.schema().quantifiedVars().isEmpty()) {
      return polyRefableS.mono();
    } else {
      // cast is safe because varMap is immutable
      @SuppressWarnings("unchecked")
      var varMap = (ImmutableMap<VarS, TypeS>) ref.monoizationMapping();
      var type = polyRefableS.schema().mapQuantifiedVars(ref.monoizationMapper());
      return new MonoizeS(type, varMap, polyRefableS, ref.loc());
    }
  }

  private BlobS convertBlob(BlobP blob) {
    return new BlobS(BLOB, blob.byteString(), blob.loc());
  }

  private IntS convertInt(IntP intP) {
    return new IntS(INT, intP.bigInteger(), intP.loc());
  }

  private StringS convertString(StringP string) {
    return new StringS(STRING, string.unescapedValue(), string.loc());
  }
}
