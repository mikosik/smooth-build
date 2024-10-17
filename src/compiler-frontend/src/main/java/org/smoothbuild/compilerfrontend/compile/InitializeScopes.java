package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.bindings.Bindings.mutableBindings;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.compile.ast.define.PScope.emptyScope;

import com.google.common.annotations.VisibleForTesting;
import org.smoothbuild.common.bindings.MutableBindings;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReferenceable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScoped;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

/**
 * For each syntactic construct that implements WithScope
 * ScopeInitializer calculates its Scope and sets via WithScopeP.setScope()
 */
public class InitializeScopes extends PModuleVisitor implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var logger = new Logger();
    initializeScopes(pModule, logger);
    var label = COMPILE_FRONT_LABEL.append("initializeScopes");
    return output(pModule, label, logger.toList());
  }

  @VisibleForTesting
  static void initializeScopes(PModule pModule, Logger logger) {
    new Initializer(emptyScope(), logger).visitModule(pModule);
  }

  private static class Initializer extends PScopingModuleVisitor {
    private final PScope scope;
    private final Logger logger;

    private Initializer(PScope scope, Logger logger) {
      this.scope = scope;
      this.logger = logger;
    }

    @Override
    protected PModuleVisitor createVisitorForScopeOf(PScoped pScoped) {
      var scopeFiller = new ScopeCreator(scope, logger);
      var newScope = scopeFiller.createScopeFor(pScoped);
      pScoped.setScope(newScope);
      return new Initializer(newScope, logger);
    }
  }

  private static class ScopeCreator extends PModuleVisitor {
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
        case PFunc pFunc -> initializeScopeFor(pFunc);
      }
      return scope.newInnerScope(
          pScoped.name(), referenceables.toFlatImmutable(), types.toFlatImmutable());
    }

    private void initializeScopeFor(PModule pModule) {
      pModule.structs().forEach(this::addType);
      pModule.structs().forEach(this::addConstructor);
      pModule.evaluables().forEach(this::addNamedEvaluable);
    }

    private void addNamedEvaluable(PNamedEvaluable pNamedEvaluable) {
      addReferenceable(pNamedEvaluable);
      if (pNamedEvaluable instanceof PNamedFunc pNamedFunc) {
        for (var param : pNamedFunc.params()) {
          if (param.defaultValue().isSome()) {
            var defaultValue = param.defaultValue().get();
            addReferenceable(defaultValue);
          }
        }
      }
    }

    private void addConstructor(PStruct pStruct) {
      var constructor = pStruct.constructor();
      // No need to report error when other referenceable with same name is already defined.
      // Constructor name starts with capital letter, so it can collide only
      // with other constructor name. This can only happen when other structure
      // with same name is declared which will be reported when adding struct type.
      referenceables.add(constructor.name(), constructor);
    }

    private void initializeScopeFor(PStruct pStruct) {
      pStruct.fields().forEach(this::addReferenceable);
    }

    private void initializeScopeFor(PNamedValue pNamedValue) {}

    private void initializeScopeFor(PFunc pFunc) {
      pFunc.params().forEach(this::addReferenceable);
    }

    private void addType(PStruct type) {
      addBinding(types, type);
    }

    private void addReferenceable(PReferenceable pReferenceable) {
      addBinding(referenceables, pReferenceable);
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
