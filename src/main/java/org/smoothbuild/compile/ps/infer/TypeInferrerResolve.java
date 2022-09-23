package org.smoothbuild.compile.ps.infer;

import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.filter;
import static org.smoothbuild.util.collect.Maps.mapValues;
import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.Optional;

import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.lang.type.tool.UnusedVarsGenerator;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.DefaultArgP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OperP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.ValP;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedValP;
import org.smoothbuild.out.log.Logger;

public class TypeInferrerResolve {
  private final Unifier unifier;
  private final Logger logger;

  public TypeInferrerResolve(Unifier unifier, Logger logger) {
    this.unifier = unifier;
    this.logger = logger;
  }

  public Optional<SchemaS> resolve(NamedValP val, TypeS evalT) {
    TypeS resolvedEvalT = unifier.resolve(evalT);
    if (val.evalT().isPresent()) {
      if (!evalT.equals(resolvedEvalT)) {
        logger.log(compileError(val.loc(), val.q() + " body type is not equal to declared type."));
        return Optional.empty();
      }
    } else {
      resolvedEvalT = fixPrefixedVars(resolvedEvalT);
    }
    if (!resolveBody(val.body())) {
      return Optional.empty();
    }
    // This only works because val so far cannot be enclosed within other val.
    var quantifiedVars = resolvedEvalT.vars();
    return Optional.of(new SchemaS(quantifiedVars, resolvedEvalT));
  }

  public void resolve(ItemP param) {
    resolveBody(param.body());
  }

  public Optional<FuncSchemaS> resolve(FuncP func, FuncTS unresolvedFuncT) {
    var resolvedFuncT = (FuncTS) unifier.resolve(unresolvedFuncT);
    var unresolvedParamTs = unresolvedFuncT.params();
    var resolvedParamTs = resolvedFuncT.params();
    if (!allMatch(unresolvedParamTs.items(), resolvedParamTs.items(), TypeS::equals)) {
      logger.log(compileError(func.loc(), "<Add error message here> 4"));
      return Optional.empty();
    }

    if (func.resT().isPresent()) {
      if (!unresolvedFuncT.res().equals(resolvedFuncT.res())) {
        logger.log(compileError(func.resT().get().loc(),
            func.q() + " body type is not equal to declared type."));
        return Optional.empty();
      }
    } else {
      resolvedFuncT = (FuncTS) fixPrefixedVars(resolvedFuncT);
    }
    if (!resolveBody(func.body())) {
      return Optional.empty();
    }
    // This only works because function so far cannot be enclosed within other function.
    var quantifiedVars = resolvedFuncT.vars();
    return Optional.of(new FuncSchemaS(quantifiedVars, resolvedFuncT));
  }

  private boolean resolveBody(Optional<ExprP> body) {
    if (body.isPresent()) {
      setDefaultTypes(body.get());
      return resolve(body.get());
    }
    return true;
  }

  private void setDefaultTypes(ExprP expr) {
    new DefaultTypeInferrer(unifier).infer(expr);
  }

  private TypeS fixPrefixedVars(TypeS resolvedT) {
    var vars = resolvedT.vars().asList();
    var prefixedVars = filter(vars, VarS::hasPrefix);
    if (prefixedVars.isEmpty()) {
      return resolvedT;
    }
    var unprefixedVars = resolvedT.vars().filter(v -> !v.hasPrefix());
    var varGenerator = new UnusedVarsGenerator(unprefixedVars);
    var mapping = toMap(prefixedVars, pv -> varGenerator.next());
    var resolvedAndFixedEvalT = resolvedT.mapVars(key -> {
      if (key.hasPrefix()) {
        return mapping.get(key);
      } else {
        return key;
      }});
    unifier.unifySafe(resolvedAndFixedEvalT, resolvedT);
    return resolvedAndFixedEvalT;
  }

  public boolean resolve(ExprP expr) {
    return switch (expr) {
      case CallP callP -> resolve(callP);
      case ValP valP -> true;
      case NamedArgP namedArgP -> resolve(namedArgP);
      case OrderP orderP -> resolve(orderP);
      case SelectP selectP -> resolve(selectP);
      case RefP refP -> resolve(refP);
      // DefaultArgP are only present in callP.positionedArgs which is never visited during resolve.
      case DefaultArgP defaultArgP -> throw new RuntimeException("shouldn't happen");
    };
  }

  private boolean resolve(CallP callP) {
    return resolve(callP.callee())
        && callP.args().stream().allMatch(this::resolve)
        && resolveOperator(callP);
  }

  private boolean resolve(NamedArgP namedArgP) {
    return resolve(namedArgP.expr())
        && resolveOperator(namedArgP);
  }

  private boolean resolve(OrderP orderP) {
    return orderP.elems().stream().allMatch(this::resolve)
        && resolveOperator(orderP);
  }

  private boolean resolve(SelectP selectP) {
    return resolve(selectP.selectable())
        && resolveOperator(selectP);
  }

  private boolean resolveOperator(OperP operP) {
    operP.setTypeS(unifier.resolve(operP.typeS()));
    return true;
  }

  private boolean resolve(RefP refP) {
    var resolvedMonoizationMapping = mapValues(refP.monoizationMapping(), unifier::resolve);
    refP.setMonoizationMapping(resolvedMonoizationMapping);
    if (resolvedMonoizationMapping.values().stream().anyMatch(this::hasPrefixedVar)) {
      logger.log(compileError(refP.loc(), "Cannot infer actual type parameters."));
      return false;
    }
    return true;
  }

  private boolean hasPrefixedVar(TypeS t) {
    return t.vars().stream().anyMatch(VarS::hasPrefix);
  }
}
