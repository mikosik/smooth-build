package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.compile.fs.ps.CompileError.compileError;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.bindings.Bindings.mutableBindings;

import org.smoothbuild.compile.fs.lang.base.Nal;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.ScopingModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.define.FuncP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedEvaluableP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceableP;
import org.smoothbuild.compile.fs.ps.ast.define.ScopeP;
import org.smoothbuild.compile.fs.ps.ast.define.ScopedP;
import org.smoothbuild.compile.fs.ps.ast.define.StructP;
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
    new Initializer(new ScopeP(immutableBindings(), immutableBindings()), log)
        .visitModule(moduleP);
    return log;
  }

  private static class Initializer extends ScopingModuleVisitorP {
    private final ScopeP scope;
    private final Logger log;

    private Initializer(ScopeP scope, Logger log) {
      this.scope = scope;
      this.log = log;
    }

    @Override
    protected ModuleVisitorP createVisitorForScopeOf(ScopedP scopedP) {
      var scopeFiller = new ScopeCreator(scope, log);
      var newScope = scopeFiller.createScopeFor(scopedP);
      scopedP.setScope(newScope);
      return new Initializer(newScope, log);
    }
  }

  private static class ScopeCreator extends ModuleVisitorP {
    private final ScopeP scope;
    private final Logger log;
    private final MutableBindings<ReferenceableP> referenceables = mutableBindings();
    private final MutableBindings<StructP> types = mutableBindings();

    public ScopeCreator(ScopeP scope, Logger log) {
      this.scope = scope;
      this.log = log;
    }

    private ScopeP createScopeFor(ScopedP scopedP) {
      // @formatter:off
      switch (scopedP) {
        case ModuleP     moduleP     -> initializeScopeFor(moduleP);
        case StructP     structP     -> initializeScopeFor(structP);
        case NamedValueP namedValueP -> initializeScopeFor(namedValueP);
        case FuncP       funcP       -> initializeScopeFor(funcP);
      }
      // @formatter:on
      return scope.newInnerScope(referenceables.toFlatImmutable(), types.toFlatImmutable());
    }

    private void initializeScopeFor(ModuleP moduleP) {
      moduleP.structs().forEach(this::addType);
      moduleP.structs().forEach(this::addConstructor);
      moduleP.evaluables().forEach(this::addNamedEvaluable);
    }

    private void addNamedEvaluable(NamedEvaluableP namedEvaluableP) {
      addReferenceable(namedEvaluableP);
      if (namedEvaluableP instanceof NamedFuncP namedFuncP) {
        for (var param : namedFuncP.params()) {
          if (param.defaultValue().isPresent()) {
            var defaultValue = param.defaultValue().get();
            addReferenceable(defaultValue);
          }
        }
      }
    }

    private void addConstructor(StructP structP) {
      var constructor = structP.constructor();
      // No need to report error when other referenceable with same name is already defined.
      // Constructor name starts with capital letter, so it can collide only
      // with other constructor name. This can only happen when other structure
      // with same name is declared which will be reported when adding struct type.
      referenceables.add(constructor.name(), constructor);
    }

    private void initializeScopeFor(StructP structP) {
      structP.fields().forEach(this::addReferenceable);
    }

    private void initializeScopeFor(NamedValueP namedValueP) {
    }

    private void initializeScopeFor(FuncP funcP) {
      funcP.params().forEach(this::addReferenceable);
    }

    private void addType(StructP type) {
      addBinding(types, type);
    }

    private void addReferenceable(ReferenceableP referenceableP) {
      addBinding(referenceables, referenceableP);
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
