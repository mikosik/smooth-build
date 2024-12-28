package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.bindings.Bindings.mutableBindings;

import com.google.common.annotations.VisibleForTesting;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PConstructor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReferenceable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScoped;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.bindings.MutableBindings;
import org.smoothbuild.compilerfrontend.lang.define.SScope;

/**
 * For each syntactic construct that implements WithScope
 * ScopeInitializer calculates its Scope and sets via WithScopeP.setScope()
 */
public class GenerateScopes extends PModuleVisitor<RuntimeException>
    implements Task2<SScope, PModule, PModule> {
  @Override
  public Output<PModule> execute(SScope importedScope, PModule pModule) {
    var logger = new Logger();
    initializeScopes(importedScope, pModule, logger);
    var label = COMPILER_FRONT_LABEL.append(":initializeScopes");
    return output(pModule, label, logger.toList());
  }

  @VisibleForTesting
  static void initializeScopes(SScope importedScope, PModule pModule, Logger logger) {
    var pScope = new PScope(importedScope.evaluables(), importedScope.types());
    new Initializer(pScope, logger).visitModule(pModule);
  }

  private static class Initializer extends PScopingModuleVisitor<RuntimeException> {
    private final PScope scope;
    private final Logger logger;

    private Initializer(PScope scope, Logger logger) {
      this.scope = scope;
      this.logger = logger;
    }

    @Override
    protected PModuleVisitor<RuntimeException> createVisitorForScopeOf(PScoped pScoped) {
      var scopeFiller = new ScopeCreator(scope, logger);
      var newScope = scopeFiller.createScopeFor(pScoped);
      pScoped.setScope(newScope);
      return new Initializer(newScope, logger);
    }
  }

  private static class ScopeCreator {
    private final PScope scope;
    private final Logger log;
    private final MutableBindings<PReferenceable> referenceables = mutableBindings();
    private final MutableBindings<PStruct> types = mutableBindings();

    public ScopeCreator(PScope scope, Logger log) {
      this.scope = scope;
      this.log = log;
    }

    private PScope createScopeFor(PScoped pScoped) {
      switch (pScoped) {
        case PModule pModule -> initializeScopeFor(pModule);
        case PStruct pStruct -> initializeScopeFor(pStruct);
        case PNamedValue pNamedValue -> initializeScopeFor(pNamedValue);
        case PConstructor pConstructor -> initializeScopeFor(pConstructor);
        case PFunc pFunc -> initializeScopeFor(pFunc);
      }
      return scope.newInnerScope(referenceables.toFlatImmutable(), types.toFlatImmutable());
    }

    private void initializeScopeFor(PModule pModule) {
      pModule.structs().forEach(this::addType);
      pModule.evaluables().forEach(this::addNamedEvaluable);
    }

    private void addNamedEvaluable(PNamedEvaluable pNamedEvaluable) {
      // Do not report duplicate constructor names. As constructor names starts uppercase then the
      // only way duplicate constructor name can occur is when struct with duplicate names exists
      // which is reported as struct error so we don't want to duplicate that error here.
      var reportErrors = !(pNamedEvaluable instanceof PConstructor);
      addReferenceable(pNamedEvaluable, reportErrors);
    }

    private void initializeScopeFor(PStruct pStruct) {
      pStruct.fields().forEach(this::addReferenceable);
    }

    private void initializeScopeFor(PNamedValue pNamedValue) {}

    private void initializeScopeFor(PConstructor pConstructor) {
      // Do not report errors as duplicate parameters in constructor means there are duplicate
      // fields in its struct which is reported as Struct error.
      pConstructor.params().forEach(pReferenceable -> addReferenceable(pReferenceable, false));
    }

    private void initializeScopeFor(PFunc pFunc) {
      pFunc.params().forEach(this::addReferenceable);
    }

    private void addType(PStruct type) {
      addBinding(types, type, true);
    }

    private void addReferenceable(PReferenceable pReferenceable) {
      addReferenceable(pReferenceable, true);
    }

    private void addReferenceable(PReferenceable pReferenceable, boolean reportErrors) {
      addBinding(referenceables, pReferenceable, reportErrors);
    }

    private <T extends HasIdAndLocation> void addBinding(
        MutableBindings<T> bindings, T binding, boolean reportErrors) {
      // For now, we don't have anything (function or value) that can be enclosed inside other
      // function or value and have fully qualified name that contains enclosing name.
      // Everything is flat in the global scope. Parameter default values have workaround of
      // gluing function name and parameter name using '~' into a name.
      var last = binding.id().parts().getLast().toString();
      addBinding(bindings, binding, last, reportErrors);
    }

    // No need to report error when other constructor with same name is already defined.
    // Constructor name starts with capital letter, so it can collide only
    // with other constructor name. This can only happen when other structure
    // with same name is declared which will be reported when detecting duplicate struct name.

    private <T extends HasIdAndLocation> void addBinding(
        MutableBindings<T> bindings, T binding, String name, boolean reportErrors) {
      var previousBinding = bindings.add(name, binding);
      if (previousBinding != null && reportErrors) {
        log.log(alreadyDefinedError(
            binding.location(), previousBinding.location().description(), name));
      }
    }

    private static Log alreadyDefinedError(
        Location location, String previousLocation, String name) {
      return compileError(
          location, "`" + name + "` is already defined at " + previousLocation + ".");
    }
  }
}
