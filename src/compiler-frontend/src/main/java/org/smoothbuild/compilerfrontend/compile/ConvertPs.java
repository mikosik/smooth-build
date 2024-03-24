package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.dag.TryFunction2;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;
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
import org.smoothbuild.compilerfrontend.lang.define.SAnnotatedFunc;
import org.smoothbuild.compilerfrontend.lang.define.SAnnotatedValue;
import org.smoothbuild.compilerfrontend.lang.define.SAnnotation;
import org.smoothbuild.compilerfrontend.lang.define.SBlob;
import org.smoothbuild.compilerfrontend.lang.define.SCall;
import org.smoothbuild.compilerfrontend.lang.define.SCombine;
import org.smoothbuild.compilerfrontend.lang.define.SConstructor;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SInstantiate;
import org.smoothbuild.compilerfrontend.lang.define.SInt;
import org.smoothbuild.compilerfrontend.lang.define.SItem;
import org.smoothbuild.compilerfrontend.lang.define.SLambda;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprFunc;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprValue;
import org.smoothbuild.compilerfrontend.lang.define.SNamedFunc;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;
import org.smoothbuild.compilerfrontend.lang.define.SOrder;
import org.smoothbuild.compilerfrontend.lang.define.SPolymorphic;
import org.smoothbuild.compilerfrontend.lang.define.SReference;
import org.smoothbuild.compilerfrontend.lang.define.SString;
import org.smoothbuild.compilerfrontend.lang.define.STypeDefinition;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.lang.define.SelectS;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

public class ConvertPs implements TryFunction2<ModuleP, ScopeS, SModule> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "buildIr");
  }

  @Override
  public Try<SModule> apply(ModuleP moduleP, ScopeS environment) {
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

    private SModule convertModule(ModuleP moduleP) {
      var scopeP = moduleP.scope();
      var structs = scopeP.types().toMap().mapValues(this::convertStruct);
      var evaluables = scopeP.referencables().toMap().mapValues(this::convertReferenceableP);
      var members = new ScopeS(immutableBindings(structs), immutableBindings(evaluables));
      var scopeS = ScopeS.scopeS(imported, members);
      return new SModule(members, scopeS);
    }

    private STypeDefinition convertStruct(StructP structP) {
      return new STypeDefinition(structP.typeS(), structP.location());
    }

    private SConstructor convertConstructor(ConstructorP constructorP) {
      var fields = constructorP.params();
      var params =
          fields.map(f -> new SItem(fields.get(f.name()).typeS(), f.name(), none(), f.location()));
      return new SConstructor(
          constructorP.schemaS(), constructorP.name(), params, constructorP.location());
    }

    private SNamedEvaluable convertReferenceableP(ReferenceableP referenceableP) {
      return switch (referenceableP) {
        case ConstructorP constructorP -> convertConstructor(constructorP);
        case NamedFuncP namedFuncP -> convertNamedFunc(namedFuncP);
        case NamedValueP namedValueP -> convertNamedValue(namedValueP);
        case ItemP itemP -> throw new RuntimeException("Internal error: unexpected ItemP.");
      };
    }

    public SNamedValue convertNamedValue(NamedValueP namedValueP) {
      var schema = namedValueP.schemaS();
      var name = namedValueP.name();
      var location = namedValueP.location();
      if (namedValueP.annotation().isSome()) {
        var ann = convertAnnotation(namedValueP.annotation().get());
        return new SAnnotatedValue(ann, schema, name, location);
      } else if (namedValueP.body().isSome()) {
        var body = convertExpr(namedValueP.body().get());
        return new SNamedExprValue(schema, name, body, location);
      } else {
        throw new RuntimeException("Internal error: NamedValueP without annotation and body.");
      }
    }

    public SNamedFunc convertNamedFunc(NamedFuncP namedFuncP) {
      return convertNamedFunc(namedFuncP, convertParams(namedFuncP));
    }

    private NList<SItem> convertParams(NamedFuncP namedFuncP) {
      return namedFuncP.params().map(this::convertParam);
    }

    private NList<SItem> convertParams(NList<ItemP> params) {
      return params.map(this::convertParam);
    }

    public SItem convertParam(ItemP paramP) {
      var type = paramP.typeS();
      var name = paramP.name();
      var body = paramP.defaultValue().map(this::convertNamedValue);
      return new SItem(type, name, body, paramP.location());
    }

    private SNamedFunc convertNamedFunc(NamedFuncP namedFuncP, NList<SItem> params) {
      var schema = namedFuncP.schemaS();
      var name = namedFuncP.name();
      var loc = namedFuncP.location();
      if (namedFuncP.annotation().isSome()) {
        var annotationS = convertAnnotation(namedFuncP.annotation().get());
        return new SAnnotatedFunc(annotationS, schema, name, params, loc);
      } else if (namedFuncP.body().isSome()) {
        var body = convertFuncBody(namedFuncP, namedFuncP.body().get());
        return new SNamedExprFunc(schema, name, params, body, loc);
      } else {
        throw new RuntimeException("Internal error: NamedFuncP without annotation and body.");
      }
    }

    private SAnnotation convertAnnotation(AnnotationP annotationP) {
      var path = convertString(annotationP.value());
      return new SAnnotation(annotationP.name(), path, annotationP.location());
    }

    private List<SExpr> convertExprs(List<ExprP> positionedArgs) {
      return positionedArgs.map(this::convertExpr);
    }

    private SExpr convertExpr(ExprP expr) {
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

    private SLambda convertLambda(LambdaP lambdaP) {
      var params = convertParams(lambdaP.params());
      var body = convertFuncBody(lambdaP, lambdaP.bodyGet());
      return new SLambda(lambdaP.schemaS(), params, body, lambdaP.location());
    }

    private SBlob convertBlob(BlobP blob) {
      return new SBlob(STypes.BLOB, blob.byteString(), blob.location());
    }

    private SExpr convertCall(CallP call) {
      var callee = convertExpr(call.callee());
      var args = convertArgs(call);
      return new SCall(callee, args, call.location());
    }

    private SCombine convertArgs(CallP call) {
      var args = convertExprs(call.positionedArgs());
      var evaluationType = new STupleType(args.map(SExpr::evaluationType));
      return new SCombine(evaluationType, args, call.location());
    }

    private SExpr convertFuncBody(FuncP funcP, ExprP body) {
      var typeTellerForBody = typeTeller.withScope(funcP.scope());
      return new Worker(typeTellerForBody, imported).convertExpr(body);
    }

    private SInt convertInt(IntP int_) {
      return new SInt(STypes.INT, int_.bigInteger(), int_.location());
    }

    private SPolymorphic convertPolymorphic(PolymorphicP polymorphicP) {
      return switch (polymorphicP) {
        case LambdaP lambdaP -> convertLambda(lambdaP);
        case ReferenceP referenceP -> convertReference(referenceP);
      };
    }

    private SExpr convertInstantiate(InstantiateP instantiateP) {
      var polymorphicS = convertPolymorphic(instantiateP.polymorphic());
      return new SInstantiate(instantiateP.typeArgs(), polymorphicS, instantiateP.location());
    }

    private SExpr convertOrder(OrderP order) {
      var elems = convertExprs(order.elements());
      return new SOrder((SArrayType) order.typeS(), elems, order.location());
    }

    private SReference convertReference(ReferenceP referenceP) {
      return convertReference(
          referenceP, typeTeller.schemaFor(referenceP.referencedName()).get());
    }

    private SReference convertReference(ReferenceP referenceP, SchemaS schemaS) {
      return new SReference(schemaS, referenceP.referencedName(), referenceP.location());
    }

    private SExpr convertSelect(SelectP selectP) {
      var selectable = convertExpr(selectP.selectable());
      return new SelectS(selectable, selectP.field(), selectP.location());
    }

    private SString convertString(StringP string) {
      return new SString(STypes.STRING, string.unescapedValue(), string.location());
    }
  }
}
