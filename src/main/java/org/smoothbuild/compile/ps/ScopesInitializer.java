package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.ps.CompileError.compileError;

import java.util.function.Consumer;

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
      handle(
          moduleP,
          i -> i.visitModule(moduleP),
          () -> {
            new BindingsAdder(scope, log)
                .visitModule(moduleP);
            super.visitModule(moduleP);
            addFunctionParameterDefaultValuesToModuleScope(scope);
          });
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
      handle(
          structP,
          i -> i.visitStruct(structP),
          () -> {
            visitFields(structP.fields());
            new BindingsAdder(scope, log)
                .visitFields(structP.fields());
          });
    }

    @Override
    public void visitNamedValue(NamedValueP namedValueP) {
      handle(
          namedValueP,
          i -> i.visitNamedValue(namedValueP),
          () -> handleBody(namedValueP));
    }

    @Override
    public void visitNamedFunc(NamedFuncP namedFuncP) {
      handle(
          namedFuncP,
          i -> i.visitNamedFunc(namedFuncP),
          () -> {
            var params = (namedFuncP).params();
            visitParams(params);
            handleFunc(namedFuncP, params);
          });

    }

    @Override
    public void visitAnonymousFunc(AnonymousFuncP anonymousFuncP) {
      handle(
          anonymousFuncP,
          i -> i.visitAnonymousFunc(anonymousFuncP),
          () -> handleFunc(anonymousFuncP, anonymousFuncP.params()));
    }

    private void handleFunc(FuncP funcP, NList<ItemP> params) {
      new BindingsAdder(scope, log)
          .visitParams(params);
      handleBody(funcP);
    }

    private <T extends WithScopeP> void handle(
        T withScope, Consumer<Initializer> consumer, Runnable runnable) {
      var ourScope = withScope.scope();
      if (ourScope == null) {
        withScope.setScope(scope.newInnerScope());
        var initializer = new Initializer(withScope.scope(), log);
        consumer.accept(initializer);
      } else {
        runnable.run();
      }
    }

    private void handleBody(EvaluableP evaluableP) {
      evaluableP.body()
          .ifPresent(b -> new Initializer(scope, log).visitExpr(b));
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
