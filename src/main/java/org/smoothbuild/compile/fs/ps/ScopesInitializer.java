package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.compile.fs.ps.CompileError.compileError;

import org.smoothbuild.compile.fs.lang.base.Nal;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.fs.ps.ast.expr.FuncP;
import org.smoothbuild.compile.fs.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.expr.RefableP;
import org.smoothbuild.compile.fs.ps.ast.expr.ScopeP;
import org.smoothbuild.compile.fs.ps.ast.expr.StructP;
import org.smoothbuild.compile.fs.ps.ast.expr.WithScopeP;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.util.bindings.MutableBindings;

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
      createScopeWithBindingsAndWrapInsideInitializer(moduleP)
          .visitModuleChildren(moduleP);
    }

    @Override
    public void visitModuleChildren(ModuleP moduleP) {
      super.visitModuleChildren(moduleP);
      addFunctionParameterDefaultValuesToModuleScope(scope);
    }

    private void addFunctionParameterDefaultValuesToModuleScope(ScopeP moduleScope) {
      for (var refableP : moduleScope.refables().innermostScopeMap().values()) {
        if (refableP instanceof NamedFuncP namedFuncP) {
          for (var param : namedFuncP.params()) {
            if (param.defaultValue().isPresent()) {
              var defaultValue = param.defaultValue().get();
              moduleScope.refables().add(defaultValue.name(), defaultValue);
            }
          }
        }
      }
    }

    @Override
    public void visitStruct(StructP structP) {
      createScopeWithBindingsAndWrapInsideInitializer(structP)
          .visitStructSignature(structP);
    }

    @Override
    public void visitNamedValue(NamedValueP namedValueP) {
      visitNamedValueSignature(namedValueP);
      createScopeWithBindingsAndWrapInsideInitializer(namedValueP)
          .visitNamedValueBody(namedValueP);
    }

    @Override
    public void visitNamedFunc(NamedFuncP namedFuncP) {
      visitNamedFuncSignature(namedFuncP);
      createScopeWithBindingsAndWrapInsideInitializer(namedFuncP)
          .visitFuncBody(namedFuncP);
    }

    @Override
    public void visitAnonymousFunc(AnonymousFuncP anonymousFuncP) {
      visitAnonymousFuncSignature(anonymousFuncP);
      createScopeWithBindingsAndWrapInsideInitializer(anonymousFuncP)
          .visitFuncBody(anonymousFuncP);
    }

    private Initializer createScopeWithBindingsAndWrapInsideInitializer(WithScopeP withScopeP) {
      var newScope = scope.newInnerScope();
      withScopeP.setScope(newScope);
      var initializer = new Initializer(newScope, log);
      initializer.addBindingsFromScopeOf(withScopeP);
      return initializer;
    }

    private void addBindingsFromScopeOf(WithScopeP withScopeP) {
      // @formatter:off
      switch (withScopeP) {
        case ModuleP     moduleP     -> addBindingsFromScopeOf(moduleP);
        case StructP     structP     -> addBindingsFromScopeOf(structP);
        case NamedValueP namedValueP -> addBindingsFromScopeOf(namedValueP);
        case FuncP       funcP       -> addBindingsFromScopeOf(funcP);
      }
      // @formatter:on
    }

    private void addBindingsFromScopeOf(ModuleP moduleP) {
      moduleP.structs().forEach(this::addType);
      moduleP.structs().forEach(this::addConstructor);
      moduleP.evaluables().forEach(this::addRefable);
    }

    private void addConstructor(StructP structP) {
      var constructor = structP.constructor();
      // No need to report error when other refable with same name is already defined.
      // Constructor name starts with capital letter, so it can collide only
      // with other constructor name. This can only happen when other structure
      // with same name is declared which will be reported when adding struct type.
      scope.refables().add(constructor.name(), constructor);
    }

    private void addBindingsFromScopeOf(StructP structP) {
      structP.fields().forEach(this::addRefable);
    }

    private void addBindingsFromScopeOf(NamedValueP namedValueP) {
    }

    private void addBindingsFromScopeOf(FuncP funcP) {
      funcP.params().forEach(this::addRefable);
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
