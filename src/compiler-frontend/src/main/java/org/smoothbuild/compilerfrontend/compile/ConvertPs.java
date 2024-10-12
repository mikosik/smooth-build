package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task2;
import org.smoothbuild.compilerfrontend.compile.ast.define.PAnnotation;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PConstructor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.POrder;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolymorphic;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReferenceable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
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
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.define.SSelect;
import org.smoothbuild.compilerfrontend.lang.define.SString;
import org.smoothbuild.compilerfrontend.lang.define.STypeDefinition;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

public class ConvertPs implements Task2<SModule, PModule, SScope> {
  @Override
  public Output<SModule> execute(PModule pModule, SScope environment) {
    var typeTeller = new TypeTeller(environment, pModule.scope());
    var label = label(COMPILE_PREFIX, "buildIr");
    var sModule = new Worker(typeTeller, environment).convertModule(pModule);
    return output(sModule, label, list());
  }

  public static class Worker {
    private final TypeTeller typeTeller;
    private final SScope imported;

    private Worker(TypeTeller typeTeller, SScope imported) {
      this.typeTeller = typeTeller;
      this.imported = imported;
    }

    private SModule convertModule(PModule pModule) {
      var scopeP = pModule.scope();
      var structs = scopeP.types().toMap().mapValues(this::convertStruct);
      var evaluables = scopeP.referencables().toMap().mapValues(this::convertReferenceableP);
      var members = new SScope(immutableBindings(structs), immutableBindings(evaluables));
      var scopeS = SScope.scopeS(imported, members);
      return new SModule(members, scopeS);
    }

    private STypeDefinition convertStruct(PStruct pStruct) {
      return new STypeDefinition(pStruct.typeS(), pStruct.location());
    }

    private SConstructor convertConstructor(PConstructor pConstructor) {
      var fields = pConstructor.params();
      var params =
          fields.map(f -> new SItem(fields.get(f.name()).typeS(), f.name(), none(), f.location()));
      return new SConstructor(
          pConstructor.schemaS(), pConstructor.name(), params, pConstructor.location());
    }

    private SNamedEvaluable convertReferenceableP(PReferenceable pReferenceable) {
      return switch (pReferenceable) {
        case PConstructor pConstructor -> convertConstructor(pConstructor);
        case PNamedFunc pNamedFunc -> convertNamedFunc(pNamedFunc);
        case PNamedValue pNamedValue -> convertNamedValue(pNamedValue);
        case PItem pItem -> throw new RuntimeException("Internal error: unexpected ItemP.");
      };
    }

    public SNamedValue convertNamedValue(PNamedValue pNamedValue) {
      var schema = pNamedValue.schemaS();
      var name = pNamedValue.name();
      var location = pNamedValue.location();
      if (pNamedValue.annotation().isSome()) {
        var ann = convertAnnotation(pNamedValue.annotation().get());
        return new SAnnotatedValue(ann, schema, name, location);
      } else if (pNamedValue.body().isSome()) {
        var body = convertExpr(pNamedValue.body().get());
        return new SNamedExprValue(schema, name, body, location);
      } else {
        throw new RuntimeException("Internal error: NamedValueP without annotation and body.");
      }
    }

    public SNamedFunc convertNamedFunc(PNamedFunc pNamedFunc) {
      return convertNamedFunc(pNamedFunc, convertParams(pNamedFunc));
    }

    private NList<SItem> convertParams(PNamedFunc pNamedFunc) {
      return pNamedFunc.params().map(this::convertParam);
    }

    private NList<SItem> convertParams(NList<PItem> params) {
      return params.map(this::convertParam);
    }

    public SItem convertParam(PItem paramP) {
      var type = paramP.typeS();
      var name = paramP.name();
      var body = paramP.defaultValue().map(this::convertNamedValue);
      return new SItem(type, name, body, paramP.location());
    }

    private SNamedFunc convertNamedFunc(PNamedFunc pNamedFunc, NList<SItem> params) {
      var schema = pNamedFunc.schemaS();
      var name = pNamedFunc.name();
      var loc = pNamedFunc.location();
      if (pNamedFunc.annotation().isSome()) {
        var annotationS = convertAnnotation(pNamedFunc.annotation().get());
        return new SAnnotatedFunc(annotationS, schema, name, params, loc);
      } else if (pNamedFunc.body().isSome()) {
        var body = convertFuncBody(pNamedFunc, pNamedFunc.body().get());
        return new SNamedExprFunc(schema, name, params, body, loc);
      } else {
        throw new RuntimeException("Internal error: NamedFuncP without annotation and body.");
      }
    }

    private SAnnotation convertAnnotation(PAnnotation pAnnotation) {
      var path = convertString(pAnnotation.value());
      return new SAnnotation(pAnnotation.name(), path, pAnnotation.location());
    }

    private List<SExpr> convertExprs(List<PExpr> positionedArgs) {
      return positionedArgs.map(this::convertExpr);
    }

    private SExpr convertExpr(PExpr expr) {
      return switch (expr) {
        case PBlob pBlob -> convertBlob(pBlob);
        case PCall pCall -> convertCall(pCall);
        case PInt pInt -> convertInt(pInt);
        case PInstantiate pInstantiate -> convertInstantiate(pInstantiate);
        case PNamedArg pNamedArg -> convertExpr(pNamedArg.expr());
        case POrder pOrder -> convertOrder(pOrder);
        case PSelect pSelect -> convertSelect(pSelect);
        case PString pString -> convertString(pString);
      };
    }

    private SLambda convertLambda(PLambda pLambda) {
      var params = convertParams(pLambda.params());
      var body = convertFuncBody(pLambda, pLambda.bodyGet());
      return new SLambda(pLambda.schemaS(), params, body, pLambda.location());
    }

    private SBlob convertBlob(PBlob blob) {
      return new SBlob(STypes.BLOB, blob.byteString(), blob.location());
    }

    private SExpr convertCall(PCall call) {
      var callee = convertExpr(call.callee());
      var args = convertArgs(call);
      return new SCall(callee, args, call.location());
    }

    private SCombine convertArgs(PCall call) {
      var args = convertExprs(call.positionedArgs());
      var evaluationType = new STupleType(args.map(SExpr::evaluationType));
      return new SCombine(evaluationType, args, call.location());
    }

    private SExpr convertFuncBody(PFunc pFunc, PExpr body) {
      var typeTellerForBody = typeTeller.withScope(pFunc.scope());
      return new Worker(typeTellerForBody, imported).convertExpr(body);
    }

    private SInt convertInt(PInt int_) {
      return new SInt(STypes.INT, int_.bigInteger(), int_.location());
    }

    private SPolymorphic convertPolymorphic(PPolymorphic pPolymorphic) {
      return switch (pPolymorphic) {
        case PLambda pLambda -> convertLambda(pLambda);
        case PReference pReference -> convertReference(pReference);
      };
    }

    private SExpr convertInstantiate(PInstantiate pInstantiate) {
      var polymorphicS = convertPolymorphic(pInstantiate.polymorphic());
      return new SInstantiate(pInstantiate.typeArgs(), polymorphicS, pInstantiate.location());
    }

    private SExpr convertOrder(POrder order) {
      var elems = convertExprs(order.elements());
      return new SOrder((SArrayType) order.typeS(), elems, order.location());
    }

    private SReference convertReference(PReference pReference) {
      return convertReference(
          pReference, typeTeller.schemaFor(pReference.referencedName()).get());
    }

    private SReference convertReference(PReference pReference, SchemaS schemaS) {
      return new SReference(schemaS, pReference.referencedName(), pReference.location());
    }

    private SExpr convertSelect(PSelect pSelect) {
      var selectable = convertExpr(pSelect.selectable());
      return new SSelect(selectable, pSelect.field(), pSelect.location());
    }

    private SString convertString(PString string) {
      return new SString(STypes.STRING, string.unescapedValue(), string.location());
    }
  }
}
