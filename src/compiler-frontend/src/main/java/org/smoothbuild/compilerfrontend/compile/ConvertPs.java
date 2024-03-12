package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.log.base.Try.success;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerfrontend.compile.ast.define.AnnotationP;
import org.smoothbuild.compilerfrontend.compile.ast.define.BlobP;
import org.smoothbuild.compilerfrontend.compile.ast.define.CallP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ConstructorP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ExprP;
import org.smoothbuild.compilerfrontend.compile.ast.define.FuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compilerfrontend.compile.ast.define.IntP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ItemP;
import org.smoothbuild.compilerfrontend.compile.ast.define.LambdaP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ModuleP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedArgP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compilerfrontend.compile.ast.define.OrderP;
import org.smoothbuild.compilerfrontend.compile.ast.define.PolymorphicP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.SelectP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StringP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StructP;
import org.smoothbuild.compilerfrontend.compile.infer.TypeTeller;
import org.smoothbuild.compilerfrontend.lang.define.AnnotatedFuncS;
import org.smoothbuild.compilerfrontend.lang.define.AnnotatedValueS;
import org.smoothbuild.compilerfrontend.lang.define.AnnotationS;
import org.smoothbuild.compilerfrontend.lang.define.BlobS;
import org.smoothbuild.compilerfrontend.lang.define.CallS;
import org.smoothbuild.compilerfrontend.lang.define.CombineS;
import org.smoothbuild.compilerfrontend.lang.define.ConstructorS;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.compilerfrontend.lang.define.InstantiateS;
import org.smoothbuild.compilerfrontend.lang.define.IntS;
import org.smoothbuild.compilerfrontend.lang.define.ItemS;
import org.smoothbuild.compilerfrontend.lang.define.LambdaS;
import org.smoothbuild.compilerfrontend.lang.define.ModuleS;
import org.smoothbuild.compilerfrontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compilerfrontend.lang.define.NamedExprFuncS;
import org.smoothbuild.compilerfrontend.lang.define.NamedExprValueS;
import org.smoothbuild.compilerfrontend.lang.define.NamedFuncS;
import org.smoothbuild.compilerfrontend.lang.define.NamedValueS;
import org.smoothbuild.compilerfrontend.lang.define.OrderS;
import org.smoothbuild.compilerfrontend.lang.define.PolymorphicS;
import org.smoothbuild.compilerfrontend.lang.define.ReferenceS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.lang.define.SelectS;
import org.smoothbuild.compilerfrontend.lang.define.StringS;
import org.smoothbuild.compilerfrontend.lang.define.TypeDefinitionS;
import org.smoothbuild.compilerfrontend.lang.type.ArrayTS;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;
import org.smoothbuild.compilerfrontend.lang.type.TupleTS;
import org.smoothbuild.compilerfrontend.lang.type.TypeFS;

public class ConvertPs implements TryFunction<Tuple2<ModuleP, ScopeS>, ModuleS> {
  @Override
  public Try<ModuleS> apply(Tuple2<ModuleP, ScopeS> context) {
    var moduleP = context.element1();
    var environment = context.element2();
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
      var structs = scopeP.types().toMap().mapValues(this::convertStruct);
      var evaluables = scopeP.referencables().toMap().mapValues(this::convertReferenceableP);
      var members = new ScopeS(immutableBindings(structs), immutableBindings(evaluables));
      var scopeS = ScopeS.scopeS(imported, members);
      return new ModuleS(members, scopeS);
    }

    private TypeDefinitionS convertStruct(StructP structP) {
      return new TypeDefinitionS(structP.typeS(), structP.location());
    }

    private ConstructorS convertConstructor(ConstructorP constructorP) {
      var fields = constructorP.params();
      var params =
          fields.map(f -> new ItemS(fields.get(f.name()).typeS(), f.name(), none(), f.location()));
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
      if (namedValueP.annotation().isSome()) {
        var ann = convertAnnotation(namedValueP.annotation().get());
        return new AnnotatedValueS(ann, schema, name, location);
      } else if (namedValueP.body().isSome()) {
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
      return namedFuncP.params().map(this::convertParam);
    }

    private NList<ItemS> convertParams(NList<ItemP> params) {
      return params.map(this::convertParam);
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
      if (namedFuncP.annotation().isSome()) {
        var annotationS = convertAnnotation(namedFuncP.annotation().get());
        return new AnnotatedFuncS(annotationS, schema, name, params, loc);
      } else if (namedFuncP.body().isSome()) {
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

    private List<ExprS> convertExprs(List<ExprP> positionedArgs) {
      return positionedArgs.map(this::convertExpr);
    }

    private ExprS convertExpr(ExprP expr) {
      return switch (expr) {
        case BlobP blobP -> convertBlob(blobP);
        case CallP callP -> convertCall(callP);
        case IntP intP -> convertInt(intP);
        case InstantiateP instantiateP -> convertInstantiate(instantiateP);
        case NamedArgP namedArgP -> convertExpr(namedArgP.expr());
        case OrderP orderP -> convertOrder(orderP);
        case SelectP selectP -> convertSelect(selectP);
        case StringP stringP -> convertString(stringP);
      };
    }

    private LambdaS convertLambda(LambdaP lambdaP) {
      var params = convertParams(lambdaP.params());
      var body = convertFuncBody(lambdaP, lambdaP.bodyGet());
      return new LambdaS(lambdaP.schemaS(), params, body, lambdaP.location());
    }

    private BlobS convertBlob(BlobP blob) {
      return new BlobS(TypeFS.BLOB, blob.byteString(), blob.location());
    }

    private ExprS convertCall(CallP call) {
      var callee = convertExpr(call.callee());
      var args = convertArgs(call);
      return new CallS(callee, args, call.location());
    }

    private CombineS convertArgs(CallP call) {
      var args = convertExprs(call.positionedArgs());
      var evaluationType = new TupleTS(args.map(ExprS::evaluationType));
      return new CombineS(evaluationType, args, call.location());
    }

    private ExprS convertFuncBody(FuncP funcP, ExprP body) {
      var typeTellerForBody = typeTeller.withScope(funcP.scope());
      return new Worker(typeTellerForBody, imported).convertExpr(body);
    }

    private IntS convertInt(IntP int_) {
      return new IntS(TypeFS.INT, int_.bigInteger(), int_.location());
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
      var elems = convertExprs(order.elements());
      return new OrderS((ArrayTS) order.typeS(), elems, order.location());
    }

    private ReferenceS convertReference(ReferenceP referenceP) {
      return convertReference(
          referenceP, typeTeller.schemaFor(referenceP.name()).get());
    }

    private ReferenceS convertReference(ReferenceP referenceP, SchemaS schemaS) {
      return new ReferenceS(schemaS, referenceP.name(), referenceP.location());
    }

    private ExprS convertSelect(SelectP selectP) {
      var selectable = convertExpr(selectP.selectable());
      return new SelectS(selectable, selectP.field(), selectP.location());
    }

    private StringS convertString(StringP string) {
      return new StringS(TypeFS.STRING, string.unescapedValue(), string.location());
    }
  }
}
