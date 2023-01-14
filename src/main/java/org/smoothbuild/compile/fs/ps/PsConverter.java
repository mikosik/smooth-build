package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.compile.fs.lang.define.ScopeS.scopeS;
import static org.smoothbuild.compile.fs.lang.type.TypeFS.BLOB;
import static org.smoothbuild.compile.fs.lang.type.TypeFS.INT;
import static org.smoothbuild.compile.fs.lang.type.TypeFS.STRING;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.mapValues;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.fs.lang.define.AnnotatedFuncS;
import org.smoothbuild.compile.fs.lang.define.AnnotatedValueS;
import org.smoothbuild.compile.fs.lang.define.AnnotationS;
import org.smoothbuild.compile.fs.lang.define.AnonymousFuncS;
import org.smoothbuild.compile.fs.lang.define.BlobS;
import org.smoothbuild.compile.fs.lang.define.CallS;
import org.smoothbuild.compile.fs.lang.define.CombineS;
import org.smoothbuild.compile.fs.lang.define.ConstructorS;
import org.smoothbuild.compile.fs.lang.define.ExprS;
import org.smoothbuild.compile.fs.lang.define.IntS;
import org.smoothbuild.compile.fs.lang.define.ItemS;
import org.smoothbuild.compile.fs.lang.define.ModuleS;
import org.smoothbuild.compile.fs.lang.define.MonoizableS;
import org.smoothbuild.compile.fs.lang.define.MonoizeS;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.fs.lang.define.NamedExprFuncS;
import org.smoothbuild.compile.fs.lang.define.NamedExprValueS;
import org.smoothbuild.compile.fs.lang.define.NamedFuncS;
import org.smoothbuild.compile.fs.lang.define.NamedValueS;
import org.smoothbuild.compile.fs.lang.define.OrderS;
import org.smoothbuild.compile.fs.lang.define.ReferenceS;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.lang.define.SelectS;
import org.smoothbuild.compile.fs.lang.define.StringS;
import org.smoothbuild.compile.fs.lang.define.TypeDefinitionS;
import org.smoothbuild.compile.fs.lang.type.ArrayTS;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.TupleTS;
import org.smoothbuild.compile.fs.ps.ast.define.AnnotationP;
import org.smoothbuild.compile.fs.ps.ast.define.AnonymousFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.BlobP;
import org.smoothbuild.compile.fs.ps.ast.define.CallP;
import org.smoothbuild.compile.fs.ps.ast.define.ConstructorP;
import org.smoothbuild.compile.fs.ps.ast.define.ExprP;
import org.smoothbuild.compile.fs.ps.ast.define.FuncP;
import org.smoothbuild.compile.fs.ps.ast.define.IntP;
import org.smoothbuild.compile.fs.ps.ast.define.ItemP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.MonoizableP;
import org.smoothbuild.compile.fs.ps.ast.define.MonoizeP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.define.OrderP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceableP;
import org.smoothbuild.compile.fs.ps.ast.define.SelectP;
import org.smoothbuild.compile.fs.ps.ast.define.StringP;
import org.smoothbuild.compile.fs.ps.ast.define.StructP;
import org.smoothbuild.compile.fs.ps.infer.TypeTeller;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class PsConverter {
  private final TypeTeller typeTeller;
  private final ScopeS imported;

  public static ModuleS convertPs(ModuleP moduleP, ScopeS imported) {
    var typeTeller = new TypeTeller(imported, moduleP.scope());
    return new PsConverter(typeTeller, imported)
        .convertModule(moduleP);
  }

  private PsConverter(TypeTeller typeTeller, ScopeS imported) {
    this.typeTeller = typeTeller;
    this.imported = imported;
  }

  private ModuleS convertModule(ModuleP moduleP) {
    var scopeP = moduleP.scope();
    var structs = mapValues(scopeP.types().toMap(), this::convertStruct);
    var evaluables = mapValues(scopeP.referencables().toMap(), this::convertReferenceableP);
    var members = new ScopeS(immutableBindings(structs), immutableBindings(evaluables));
    var scopeS = scopeS(imported, members);
    return new ModuleS(members, scopeS);
  }

  private TypeDefinitionS convertStruct(StructP structP) {
    return new TypeDefinitionS(structP.typeS(), structP.location());
  }

  private ConstructorS convertConstructor(ConstructorP constructorP) {
    var fields = constructorP.params();
    var params = fields.map(
        f -> new ItemS(fields.get(f.name()).typeS(), f.name(), Optional.empty(), f.location()));
    return new ConstructorS(
        constructorP.schemaS(), constructorP.name(), params, constructorP.location());
  }

  private NamedEvaluableS convertReferenceableP(ReferenceableP referenceableP) {
    return switch (referenceableP) {
      case ConstructorP constructorP -> convertConstructor(constructorP);
      case NamedFuncP namedFuncP -> convertNamedFunc(namedFuncP);
      case NamedValueP namedValueP -> convertNamedValue(namedValueP);
      case ItemP itemP -> throw new RuntimeException("Internal error: unexpected ItemP.");
    };
  }

  public NamedValueS convertNamedValue(NamedValueP namedValueP) {
    var schema = namedValueP.schemaS();
    var name = namedValueP.name();
    var location = namedValueP.location();
    if (namedValueP.annotation().isPresent()) {
      var ann = convertAnnotation(namedValueP.annotation().get());
      return new AnnotatedValueS(ann, schema, name, location);
    } else if (namedValueP.body().isPresent()) {
      var body = convertExpr(namedValueP.body().get());
      return new NamedExprValueS(schema, name, body, location);
    } else {
      throw new RuntimeException("Internal error: NamedValueP without annotation and body.");
    }
  }

  public NamedFuncS convertNamedFunc(NamedFuncP namedFuncP) {
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
    var body = paramP.defaultValue().map(this::convertNamedValue);
    return new ItemS(type, name, body, paramP.location());
  }

  private NamedFuncS convertNamedFunc(NamedFuncP namedFuncP, NList<ItemS> params) {
    var schema = namedFuncP.schemaS();
    var name = namedFuncP.name();
    var loc = namedFuncP.location();
    if (namedFuncP.annotation().isPresent()) {
      var annotationS = convertAnnotation(namedFuncP.annotation().get());
      return new AnnotatedFuncS(annotationS, schema, name, params, loc);
    } else if (namedFuncP.body().isPresent()){
      var body = convertFuncBody(namedFuncP, namedFuncP.body().get());
      return new NamedExprFuncS(schema, name, params, body, loc);
    } else {
      throw new RuntimeException("Internal error: NamedFuncP without annotation and body.");
    }
  }

  private AnnotationS convertAnnotation(AnnotationP annotationP) {
    var path = convertString(annotationP.value());
    return new AnnotationS(annotationP.name(), path, annotationP.location());
  }

  private ImmutableList<ExprS> convertExprs(List<ExprP> positionedArgs) {
    return map(positionedArgs, this::convertExpr);
  }

  private ExprS convertExpr(ExprP expr) {
    // @formatter:off
    return switch (expr) {
      case BlobP          blobP          -> convertBlob(blobP);
      case CallP          callP          -> convertCall(callP);
      case IntP           intP           -> convertInt(intP);
      case MonoizeP       monoizeP       -> convertMonoize(monoizeP);
      case NamedArgP      namedArgP      -> convertExpr(namedArgP.expr());
      case OrderP         orderP         -> convertOrder(orderP);
      case SelectP        selectP        -> convertSelect(selectP);
      case StringP        stringP        -> convertString(stringP);
    };
    // @formatter:on
  }

  private AnonymousFuncS convertAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    var params = convertParams(anonymousFuncP.params());
    var body = convertFuncBody(anonymousFuncP, anonymousFuncP.bodyGet());
    return new AnonymousFuncS(anonymousFuncP.schemaS(), params, body, anonymousFuncP.location());
  }

  private BlobS convertBlob(BlobP blob) {
    return new BlobS(BLOB, blob.byteString(), blob.location());
  }

  private ExprS convertCall(CallP call) {
    var callee = convertExpr(call.callee());
    var args = convertArgs(call);
    return new CallS(callee, args, call.location());
  }

  private CombineS convertArgs(CallP call) {
    var args = convertExprs(call.positionedArgs());
    var evaluationT = new TupleTS(map(args, ExprS::evaluationT));
    return new CombineS(evaluationT, args, call.location());
  }

  private ExprS convertFuncBody(FuncP funcP, ExprP body) {
    var typeTellerForBody = typeTeller.withScope(funcP.scope());
    return new PsConverter(typeTellerForBody, imported).convertExpr(body);
  }

  private IntS convertInt(IntP int_) {
    return new IntS(INT, int_.bigInteger(), int_.location());
  }

  private MonoizableS convertMonoizable(MonoizableP monoizable) {
    return switch (monoizable) {
      case AnonymousFuncP anonymousFuncP -> convertAnonymousFunc(anonymousFuncP);
      case ReferenceP referenceP -> convertReference(referenceP);
    };
  }

  private ExprS convertMonoize(MonoizeP monoizeP) {
    var monoizableS = convertMonoizable(monoizeP.monoizable());
    return new MonoizeS(monoizeP.typeArgs(), monoizableS, monoizeP.location());
  }

  private ExprS convertOrder(OrderP order) {
    var elems = convertExprs(order.elems());
    return new OrderS((ArrayTS) order.typeS(), elems, order.location());
  }

  private ReferenceS convertReference(ReferenceP referenceP) {
    return convertReference(referenceP, typeTeller.schemaFor(referenceP.name()).get());
  }

  private ReferenceS convertReference(ReferenceP referenceP, SchemaS schemaS) {
    return new ReferenceS(schemaS, referenceP.name(), referenceP.location());
  }

  private ExprS convertSelect(SelectP selectP) {
    var selectable = convertExpr(selectP.selectable());
    return new SelectS(selectable, selectP.field(), selectP.location());
  }

  private StringS convertString(StringP string) {
    return new StringS(STRING, string.unescapedValue(), string.location());
  }
}
