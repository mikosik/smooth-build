package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.POrder;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolymorphic;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;

public class TypeInferrerResolve {
  private final Unifier unifier;
  private final Logger logger;

  public TypeInferrerResolve(Unifier unifier, Logger logger) {
    this.unifier = unifier;
    this.logger = logger;
  }

  public boolean resolveNamedValue(PNamedValue pNamedValue) {
    return resolveBody(pNamedValue.body()) && resolveSchema(pNamedValue);
  }

  private boolean resolveSchema(PNamedValue pEvaluable) {
    pEvaluable.setSSchema(resolveSchema(pEvaluable.sSchema()));
    return true;
  }

  public boolean resolveFunc(PFunc pNamedFunc) {
    return resolveBody(pNamedFunc.body()) && resolveSchema(pNamedFunc);
  }

  private boolean resolveSchema(PFunc pEvaluable) {
    pEvaluable.setSSchema(resolveSchema(pEvaluable.sSchema()));
    return true;
  }

  private SFuncSchema resolveSchema(SFuncSchema sFuncSchema) {
    return new SFuncSchema(
        resolveQuantifiedVars(sFuncSchema), (SFuncType) resolveType(sFuncSchema));
  }

  private SSchema resolveSchema(SSchema sSchema) {
    return new SSchema(resolveQuantifiedVars(sSchema), resolveType(sSchema));
  }

  private SVarSet resolveQuantifiedVars(SSchema sSchema) {
    return varSetS(
        sSchema.quantifiedVars().stream().map(v -> (SVar) unifier.resolve(v)).toList());
  }

  private SType resolveType(SSchema sSchema) {
    return unifier.resolve(sSchema.type());
  }

  private boolean resolveBody(Maybe<PExpr> body) {
    return body.map(this::resolveBody).getOr(true);
  }

  private boolean resolveBody(PExpr body) {
    inferUnitTypes(body);
    return resolveExpr(body);
  }

  private void inferUnitTypes(PExpr expr) {
    new UnitTypeInferrer(unifier).infer(expr);
  }

  private boolean resolveExpr(PExpr expr) {
    return switch (expr) {
      case PCall pCall -> resolveCall(pCall);
      case PInstantiate pInstantiate -> resolveInstantiate(pInstantiate);
      case PNamedArg pNamedArg -> resolveNamedArg(pNamedArg);
      case POrder pOrder -> resolveOrder(pOrder);
      case PSelect pSelect -> resolveSelect(pSelect);
      case PString pString -> resolveExprType(pString);
      case PInt pInt -> resolveExprType(pInt);
      case PBlob pBlob -> resolveExprType(pBlob);
    };
  }

  private boolean resolveCall(PCall pCall) {
    return resolveExpr(pCall.callee())
        && pCall.positionedArgs().stream().allMatch(this::resolveExpr)
        && resolveExprType(pCall);
  }

  private boolean resolveInstantiate(PInstantiate pInstantiate) {
    return resolvePolymorphic(pInstantiate.polymorphic())
        && resolveInstantiateTypeArgs(pInstantiate);
  }

  private boolean resolveInstantiateTypeArgs(PInstantiate pInstantiate) {
    var resolvedTypeArgs = pInstantiate.typeArgs().map(unifier::resolve);
    if (resolvedTypeArgs.stream().anyMatch(this::hasTempVar)) {
      logger.log(compileError(pInstantiate.location(), "Cannot infer actual type parameters."));
      return false;
    }
    pInstantiate.setTypeArgs(resolvedTypeArgs);
    return true;
  }

  private boolean resolvePolymorphic(PPolymorphic pPolymorphic) {
    return switch (pPolymorphic) {
      case PLambda pLambda -> resolveFunc(pLambda);
      case PReference pReference -> true;
    };
  }

  private boolean resolveNamedArg(PNamedArg pNamedArg) {
    return resolveExpr(pNamedArg.expr()) && resolveExprType(pNamedArg);
  }

  private boolean resolveOrder(POrder pOrder) {
    return pOrder.elements().stream().allMatch(this::resolveExpr) && resolveExprType(pOrder);
  }

  private boolean resolveSelect(PSelect pSelect) {
    return resolveExpr(pSelect.selectable()) && resolveExprType(pSelect);
  }

  private boolean resolveExprType(PExpr pExpr) {
    pExpr.setSType(unifier.resolve(pExpr.sType()));
    return true;
  }

  private boolean hasTempVar(SType t) {
    return t.vars().stream().anyMatch(SVar::isTemporary);
  }
}
