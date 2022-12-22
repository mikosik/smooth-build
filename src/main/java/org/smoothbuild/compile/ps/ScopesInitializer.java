package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.ps.CompileError.compileError;

import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.ps.ast.expr.EvaluableP;
import org.smoothbuild.compile.ps.ast.expr.FuncP;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.ps.ast.expr.RefableP;
import org.smoothbuild.compile.ps.ast.expr.ScopeP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.compile.ps.ast.expr.WithScopeP;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.util.bindings.MutableBindings;
import org.smoothbuild.util.collect.NList;

/**
 * For each syntactic construct that implements WithScope
 * ScopeInitializer calculates its Scope and sets via WithScopeP.setScope()
 */
public class ScopesInitializer extends ModuleVisitorP {
  public static Logs initializeScopes(ModuleP moduleP) {
    var log = new LogBuffer();
    new Initializer(new ScopeP(null, null), log)
        .visitModule(moduleP);
    return log;
  }

  private static class Initializer extends ModuleVisitorP {
    private final ScopeP scope;
    private final Logger log;

    private Initializer(ScopeP scope, Logger log) {
      this.scope = scope;
      this.log = log;
    }

    @Override
    public void visitModule(ModuleP moduleP) {
      newInitializerForScopeOf(moduleP)
          .visitModuleChildren(moduleP);
    }

    @Override
    public void visitModuleChildren(ModuleP moduleP) {
      new BindingsAdder(scope, log)
          .visitModule(moduleP);
      super.visitModuleChildren(moduleP);
      addFunctionParameterDefaultValuesToModuleScope(scope);
    }

    private void addFunctionParameterDefaultValuesToModuleScope(ScopeP moduleScope) {
      for (var refableP : moduleScope.refables().innermostScopeMap().values()) {
        if (refableP instanceof NamedFuncP namedFuncP) {
          for (var param : namedFuncP.params()) {
            if (param.defaultValue().isPresent()) {
              var defaultValue = param.defaultValue().get();
              var name = namedFuncP.name() + ":" + defaultValue.name();
              moduleScope.refables().add(name, defaultValue);
            }
          }
        }
      }
    }

    @Override
    public void visitStruct(StructP structP) {
      newInitializerForScopeOf(structP)
          .visitStructChildren(structP);
    }

    @Override
    public void visitStructChildren(StructP structP) {
      visitFields(structP.fields());
      new BindingsAdder(scope, log)
          .visitFields(structP.fields());
    }

    @Override
    public void visitNamedValue(NamedValueP namedValueP) {
      newInitializerForScopeOf(namedValueP)
          .visitNamedValueChildren(namedValueP);
    }

    @Override
    public void visitNamedValueChildren(NamedValueP namedValueP) {
      handleBody(namedValueP);
    }

    @Override
    public void visitNamedFunc(NamedFuncP namedFuncP) {
      newInitializerForScopeOf(namedFuncP)
          .visitNamedFuncChildren(namedFuncP);
    }

    @Override
    public void visitNamedFuncChildren(NamedFuncP namedFuncP) {
      var params = namedFuncP.params();
      visitParams(params);
      handleFunc(namedFuncP, params);
    }

    @Override
    public void visitAnonymousFunc(AnonymousFuncP anonymousFuncP) {
      newInitializerForScopeOf(anonymousFuncP)
          .visitAnonymousFuncChildren(anonymousFuncP);
    }

    @Override
    public void visitAnonymousFuncChildren(AnonymousFuncP anonymousFuncP) {
      handleFunc(anonymousFuncP, anonymousFuncP.params());
    }

    private void handleFunc(FuncP funcP, NList<ItemP> params) {
      new BindingsAdder(scope, log)
          .visitParams(params);
      handleBody(funcP);
    }

    private void handleBody(EvaluableP evaluableP) {
      evaluableP.body()
          .ifPresent(b -> new Initializer(scope, log).visitExpr(b));
    }

    private Initializer newInitializerForScopeOf(WithScopeP withScopeP) {
      var newScope = scope.newInnerScope();
      withScopeP.setScope(newScope);
      return new Initializer(withScopeP.scope(), log);
    }
  }

  private static class BindingsAdder extends ModuleVisitorP {
    private final ScopeP scope;
    private final Logger log;

    private BindingsAdder(ScopeP scope, Logger log) {
      this.scope = scope;
      this.log = log;
    }

    @Override
    public void visitNamedValue(NamedValueP namedValueP) {
      addRefable(namedValueP);
    }

    @Override
    public void visitNamedFunc(NamedFuncP namedFuncP) {
      addRefable(namedFuncP);
    }

    @Override
    public void visitParam(int index, ItemP param) {
      addRefable(param);
    }

    @Override
    public void visitField(ItemP field) {
      addRefable(field);
    }

    @Override
    public void visitStruct(StructP structP) {
      addRefable(structP.constructor());
      addType(structP);
    }

    private void addType(StructP type) {
      addBinding(scope.types(), type);
    }

    private void addRefable(RefableP refableP) {
      addBinding(scope.refables(), refableP);
    }

    private <T extends Nal> void addBinding(MutableBindings<T> bindings, T binding) {
      var previousBinding = bindings.add(binding.name(), binding);
      if (previousBinding != null) {
        log.log(alreadyDefinedError(binding, previousBinding.location()));
      }
    }

    private static Log alreadyDefinedError(Nal nal, Location location) {
      return compileError(nal.location(),
          "`" + nal.name() + "` is already defined at " + location.description() + ".");
    }
  }
}
