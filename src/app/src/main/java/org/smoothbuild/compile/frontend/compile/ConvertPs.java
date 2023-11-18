package org.smoothbuild.compile.frontend.compile;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.Lists.map;
import static org.smoothbuild.common.collect.Maps.mapValues;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compile.frontend.lang.define.ScopeS.scopeS;
import static org.smoothbuild.compile.frontend.lang.type.TypeFS.BLOB;
import static org.smoothbuild.compile.frontend.lang.type.TypeFS.INT;
import static org.smoothbuild.compile.frontend.lang.type.TypeFS.STRING;
import static org.smoothbuild.out.log.Maybe.success;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compile.frontend.compile.ast.define.AnnotationP;
import org.smoothbuild.compile.frontend.compile.ast.define.BlobP;
import org.smoothbuild.compile.frontend.compile.ast.define.CallP;
import org.smoothbuild.compile.frontend.compile.ast.define.ConstructorP;
import org.smoothbuild.compile.frontend.compile.ast.define.ExprP;
import org.smoothbuild.compile.frontend.compile.ast.define.FuncP;
import org.smoothbuild.compile.frontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compile.frontend.compile.ast.define.IntP;
import org.smoothbuild.compile.frontend.compile.ast.define.ItemP;
import org.smoothbuild.compile.frontend.compile.ast.define.LambdaP;
import org.smoothbuild.compile.frontend.compile.ast.define.ModuleP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedArgP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compile.frontend.compile.ast.define.OrderP;
import org.smoothbuild.compile.frontend.compile.ast.define.PolymorphicP;
import org.smoothbuild.compile.frontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compile.frontend.compile.ast.define.ReferenceableP;
import org.smoothbuild.compile.frontend.compile.ast.define.SelectP;
import org.smoothbuild.compile.frontend.compile.ast.define.StringP;
import org.smoothbuild.compile.frontend.compile.ast.define.StructP;
import org.smoothbuild.compile.frontend.compile.infer.TypeTeller;
import org.smoothbuild.compile.frontend.lang.define.AnnotatedFuncS;
import org.smoothbuild.compile.frontend.lang.define.AnnotatedValueS;
import org.smoothbuild.compile.frontend.lang.define.AnnotationS;
import org.smoothbuild.compile.frontend.lang.define.BlobS;
import org.smoothbuild.compile.frontend.lang.define.CallS;
import org.smoothbuild.compile.frontend.lang.define.CombineS;
import org.smoothbuild.compile.frontend.lang.define.ConstructorS;
import org.smoothbuild.compile.frontend.lang.define.ExprS;
import org.smoothbuild.compile.frontend.lang.define.InstantiateS;
import org.smoothbuild.compile.frontend.lang.define.IntS;
import org.smoothbuild.compile.frontend.lang.define.ItemS;
import org.smoothbuild.compile.frontend.lang.define.LambdaS;
import org.smoothbuild.compile.frontend.lang.define.ModuleS;
import org.smoothbuild.compile.frontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.frontend.lang.define.NamedExprFuncS;
import org.smoothbuild.compile.frontend.lang.define.NamedExprValueS;
import org.smoothbuild.compile.frontend.lang.define.NamedFuncS;
import org.smoothbuild.compile.frontend.lang.define.NamedValueS;
import org.smoothbuild.compile.frontend.lang.define.OrderS;
import org.smoothbuild.compile.frontend.lang.define.PolymorphicS;
import org.smoothbuild.compile.frontend.lang.define.ReferenceS;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;
import org.smoothbuild.compile.frontend.lang.define.SelectS;
import org.smoothbuild.compile.frontend.lang.define.StringS;
import org.smoothbuild.compile.frontend.lang.define.TypeDefinitionS;
import org.smoothbuild.compile.frontend.lang.type.ArrayTS;
import org.smoothbuild.compile.frontend.lang.type.SchemaS;
import org.smoothbuild.compile.frontend.lang.type.TupleTS;
import org.smoothbuild.out.log.Maybe;

import com.google.common.collect.ImmutableList;

import io.vavr.Tuple2;

public class ConvertPs implements Function<Tuple2<ModuleP, ScopeS>, Maybe<ModuleS>> {
  @Override
  public Maybe<ModuleS> apply(Tuple2<ModuleP, ScopeS> context) {
    var moduleP = context._1();
    var environment = context._2();
    var typeTeller = new TypeTeller(environment, moduleP.scope());
    return success(new Worker(typeTeller, environment).convertModule(moduleP));
  }

  public static class Worker {
    private final TypeTeller typeTeller;
    private final ScopeS imported;

    private Worker(TypeTeller typeTeller, ScopeS imported) {
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
        case InstantiateP   instantiateP   -> convertInstantiate(instantiateP);
        case NamedArgP      namedArgP      -> convertExpr(namedArgP.expr());
        case OrderP         orderP         -> convertOrder(orderP);
        case SelectP        selectP        -> convertSelect(selectP);
        case StringP        stringP        -> convertString(stringP);
      };
      // @formatter:on
    }

    private LambdaS convertLambda(LambdaP lambdaP) {
      var params = convertParams(lambdaP.params());
      var body = convertFuncBody(lambdaP, lambdaP.bodyGet());
      return new LambdaS(lambdaP.schemaS(), params, body, lambdaP.location());
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
      return new Worker(typeTellerForBody, imported).convertExpr(body);
    }

    private IntS convertInt(IntP int_) {
      return new IntS(INT, int_.bigInteger(), int_.location());
    }

    private PolymorphicS convertPolymorphic(PolymorphicP polymorphicP) {
      return switch (polymorphicP) {
        case LambdaP lambdaP -> convertLambda(lambdaP);
        case ReferenceP referenceP -> convertReference(referenceP);
      };
    }

    private ExprS convertInstantiate(InstantiateP instantiateP) {
      var polymorphicS = convertPolymorphic(instantiateP.polymorphic());
      return new InstantiateS(instantiateP.typeArgs(), polymorphicS, instantiateP.location());
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
}
