package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.base.Throwables.unexpectedCaseException;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.lang.name.Bindings.bindings;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.compile.ast.define.PAnnotation;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PConstructor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.POrder;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolyEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.base.MonoReferenceable;
import org.smoothbuild.compilerfrontend.lang.base.PolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SAnnotatedFunc;
import org.smoothbuild.compilerfrontend.lang.define.SAnnotatedValue;
import org.smoothbuild.compilerfrontend.lang.define.SAnnotation;
import org.smoothbuild.compilerfrontend.lang.define.SBlob;
import org.smoothbuild.compilerfrontend.lang.define.SCall;
import org.smoothbuild.compilerfrontend.lang.define.SCombine;
import org.smoothbuild.compilerfrontend.lang.define.SConstructor;
import org.smoothbuild.compilerfrontend.lang.define.SDefaultValue;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SInstantiate;
import org.smoothbuild.compilerfrontend.lang.define.SInt;
import org.smoothbuild.compilerfrontend.lang.define.SItem;
import org.smoothbuild.compilerfrontend.lang.define.SLambda;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SMonoReference;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprFunc;
import org.smoothbuild.compilerfrontend.lang.define.SNamedExprValue;
import org.smoothbuild.compilerfrontend.lang.define.SOrder;
import org.smoothbuild.compilerfrontend.lang.define.SPolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SPolyReference;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.define.SSelect;
import org.smoothbuild.compilerfrontend.lang.define.SString;
import org.smoothbuild.compilerfrontend.lang.define.STypeDefinition;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.STypes;

public class TranslatePs implements Task2<PModule, SScope, SModule> {
  @Override
  public Output<SModule> execute(PModule pModule, SScope imported) {
    var label = COMPILER_FRONT_LABEL.append(":buildIr");
    var sModule = new Worker(imported).convertModule(pModule);
    return output(sModule, label, list());
  }

  public static class Worker {
    private final SScope imported;

    private Worker(SScope imported) {
      this.imported = imported;
    }

    private SModule convertModule(PModule pModule) {
      var structs =
          pModule.structs().map(this::convertStruct).toMap(s -> s.fqn().last(), v -> v);
      var evaluables =
          pModule.evaluables().map(this::convertPolyEvaluable).toMap(Identifiable::name, v -> v);
      var sScope = new SScope(
          bindings(imported.types(), structs), bindings(imported.evaluables(), evaluables));
      return new SModule(structs, evaluables, sScope);
    }

    private SPolyEvaluable convertPolyEvaluable(PPolyEvaluable pPolyEvaluable) {
      var typeParams = pPolyEvaluable.typeParams();
      var evaluable = convertNamedEvaluable(pPolyEvaluable.evaluable());
      return new SPolyEvaluable(typeParams, evaluable);
    }

    private STypeDefinition convertStruct(PStruct pStruct) {
      return new STypeDefinition(pStruct.type(), pStruct.type().fqn(), pStruct.location());
    }

    private SNamedEvaluable convertNamedEvaluable(PNamedEvaluable pNamedEvaluable) {
      return switch (pNamedEvaluable) {
        case PConstructor pConstructor -> convertConstructor(pConstructor);
        case PNamedFunc pNamedFunc -> convertNamedFunc(pNamedFunc);
        case PNamedValue pNamedValue -> convertNamedValue(pNamedValue);
      };
    }

    private SNamedEvaluable convertConstructor(PConstructor pConstructor) {
      var type = pConstructor.type();
      var params = pConstructor
          .params()
          .map(f -> new SItem(f.pType().sType(), f.fqn(), none(), f.location()));
      return new SConstructor(type.result(), pConstructor.fqn(), params, pConstructor.location());
    }

    public SNamedEvaluable convertNamedValue(PNamedValue pNamedValue) {
      var fqn = pNamedValue.fqn();
      var location = pNamedValue.location();
      if (pNamedValue.annotation().isSome()) {
        var ann = convertAnnotation(pNamedValue.annotation().get());
        return new SAnnotatedValue(ann, pNamedValue.type(), fqn, location);
      } else if (pNamedValue.body().isSome()) {
        var body = convertExpr(pNamedValue.body().get());
        return new SNamedExprValue(pNamedValue.type(), fqn, body, location);
      } else {
        throw new RuntimeException("Internal error: PNamedValue without annotation and body.");
      }
    }

    public SNamedEvaluable convertNamedFunc(PNamedFunc pNamedFunc) {
      var params = convertParams(pNamedFunc);
      var fqn = pNamedFunc.fqn();
      var location = pNamedFunc.location();
      if (pNamedFunc.annotation().isSome()) {
        var annotationS = convertAnnotation(pNamedFunc.annotation().get());
        return new SAnnotatedFunc(annotationS, pNamedFunc.type().result(), fqn, params, location);
      } else if (pNamedFunc.body().isSome()) {
        var body = convertFuncBody(pNamedFunc.body().get());
        return new SNamedExprFunc(pNamedFunc.type().result(), fqn, params, body, location);
      } else {
        throw new RuntimeException("Internal error: NamedFuncP without annotation and body.");
      }
    }

    private NList<SItem> convertParams(PNamedFunc pNamedFunc) {
      return pNamedFunc.params().map(this::convertParam);
    }

    private NList<SItem> convertParams(NList<PItem> params) {
      return params.map(this::convertParam);
    }

    public SItem convertParam(PItem pParam) {
      return new SItem(
          pParam.pType().sType(),
          pParam.fqn(),
          pParam.defaultValue().map(dv -> new SDefaultValue(dv.fqn())),
          pParam.location());
    }

    private SAnnotation convertAnnotation(PAnnotation pAnnotation) {
      var path = convertString(pAnnotation.value());
      return new SAnnotation(pAnnotation.nameText(), path, pAnnotation.location());
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
        case PLambda pLambda -> convertLambda(pLambda);
        case PNamedArg pNamedArg -> convertExpr(pNamedArg.expr());
        case POrder pOrder -> convertOrder(pOrder);
        case PSelect pSelect -> convertSelect(pSelect);
        case PString pString -> convertString(pString);
      };
    }

    private SLambda convertLambda(PLambda pLambda) {
      var params = convertParams(pLambda.params());
      var body = convertFuncBody(pLambda.bodyGet());
      var resultType = pLambda.type().result();
      return new SLambda(resultType, pLambda.fqn(), params, body, pLambda.location());
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

    private SExpr convertFuncBody(PExpr body) {
      return new Worker(imported).convertExpr(body);
    }

    private SInt convertInt(PInt int_) {
      return new SInt(STypes.INT, int_.bigInteger(), int_.location());
    }

    private SExpr convertInstantiate(PInstantiate pInstantiate) {
      return switch (pInstantiate.reference()) {
        case PReference pReference -> convertReference(pInstantiate, pReference);
      };
    }

    private static SExpr convertReference(PInstantiate pInstantiate, PReference pReference) {
      final var fqn = pReference.fqn();
      final var location = pReference.location();
      return switch (pReference.referenced()) {
        case MonoReferenceable mono -> new SMonoReference(mono.type(), fqn, location);
        case PolyEvaluable poly -> {
          var sPolyReference = new SPolyReference(poly.typeScheme(), fqn, location);
          var type = poly.instantiatedType(pInstantiate.typeArgs());
          yield new SInstantiate(
              pInstantiate.typeArgs(), sPolyReference, type, pInstantiate.location());
        }
        default -> throw unexpectedCaseException(pReference.referenced());
      };
    }

    private SExpr convertOrder(POrder order) {
      var elems = convertExprs(order.elements());
      return new SOrder((SArrayType) order.sType(), elems, order.location());
    }

    private SExpr convertSelect(PSelect pSelect) {
      var selectable = convertExpr(pSelect.selectable());
      return new SSelect(selectable, pSelect.fieldName(), pSelect.location());
    }

    private SString convertString(PString string) {
      return new SString(STypes.STRING, string.unescapedValue(), string.location());
    }
  }
}
