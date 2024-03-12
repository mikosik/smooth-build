package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.bindings.Bindings.mutableBindings;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.compile.ast.define.ScopeP.emptyScope;

import com.google.common.annotations.VisibleForTesting;
import org.smoothbuild.common.bindings.MutableBindings;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.compilerfrontend.compile.ast.ModuleVisitorP;
import org.smoothbuild.compilerfrontend.compile.ast.ScopingModuleVisitorP;
import org.smoothbuild.compilerfrontend.compile.ast.define.FuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ModuleP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedEvaluableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ScopeP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ScopedP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StructP;
import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

/**
 * For each syntactic construct that implements WithScope
 * ScopeInitializer calculates its Scope and sets via WithScopeP.setScope()
 */
public class InitializeScopes extends ModuleVisitorP implements TryFunction<ModuleP, ModuleP> {
  @Override
  public Try<ModuleP> apply(ModuleP moduleP) {
    var logger = new Logger();
    initializeScopes(moduleP, logger);
    return Try.of(moduleP, logger);
  }

  @VisibleForTesting
  static void initializeScopes(ModuleP moduleP, Logger logger) {
    new Initializer(emptyScope(), logger).visitModule(moduleP);
  }

  private static class Initializer extends ScopingModuleVisitorP {
    private final ScopeP scope;
    private final Logger logger;

    private Initializer(ScopeP scope, Logger logger) {
      this.scope = scope;
      this.logger = logger;
    }

    @Override
    protected ModuleVisitorP createVisitorForScopeOf(ScopedP scopedP) {
      var scopeFiller = new ScopeCreator(scope, logger);
      var newScope = scopeFiller.createScopeFor(scopedP);
      scopedP.setScope(newScope);
      return new Initializer(newScope, logger);
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
      switch (scopedP) {
        case ModuleP moduleP -> initializeScopeFor(moduleP);
        case StructP structP -> initializeScopeFor(structP);
        case NamedValueP namedValueP -> initializeScopeFor(namedValueP);
        case FuncP funcP -> initializeScopeFor(funcP);
      }
      return scope.newInnerScope(
          scopedP.name(), referenceables.toFlatImmutable(), types.toFlatImmutable());
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
          if (param.defaultValue().isSome()) {
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

    private void initializeScopeFor(NamedValueP namedValueP) {}

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
      return compileError(
          nal.location(),
          "`" + nal.name() + "` is already defined at " + location.description() + ".");
    }
  }
}
