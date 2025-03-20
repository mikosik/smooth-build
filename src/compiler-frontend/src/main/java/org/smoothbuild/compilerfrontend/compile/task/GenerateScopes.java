package org.smoothbuild.compilerfrontend.compile.task;

import static org.smoothbuild.common.collect.Map.mapOfAll;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;

import com.google.common.annotations.VisibleForTesting;
import java.util.HashMap;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PConstructor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PContainer;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExplicitTypeParams;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitTypeParams;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolyEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReferenceable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.compile.ast.define.PTypeDefinition;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.name.Name;

/**
 * For each syntactic construct that implements WithScope
 * ScopeInitializer calculates its Scope and sets via WithScopeP.setScope()
 */
public class GenerateScopes implements Task2<SScope, PModule, PModule> {
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
    new Initializer(pScope, logger).visit(pModule);
  }

  private static class Initializer extends PModuleVisitor<RuntimeException> {
    private PScope scope;
    private final Logger logger;

    private Initializer(PScope scope, Logger logger) {
      this.scope = scope;
      this.logger = logger;
    }

    @Override
    public void visit(PContainer pContainer) {
      var oldScope = scope;
      scope = new ScopeCreator(oldScope, logger).createScopeFor(pContainer);
      pContainer.setScope(scope);
      try {
        super.visit(pContainer);
      } finally {
        scope = oldScope;
      }
    }
  }

  private static class ScopeCreator {
    private final PScope scope;
    private final Logger log;
    private final java.util.Map<Name, PReferenceable> referenceables = new HashMap<>();
    private final java.util.Map<Name, PTypeDefinition> types = new HashMap<>();

    public ScopeCreator(PScope scope, Logger log) {
      this.scope = scope;
      this.log = log;
    }

    private PScope createScopeFor(PContainer pContainer) {
      switch (pContainer) {
        case PModule pModule -> initializeScopeFor(pModule);
        case PStruct pStruct -> initializeScopeFor(pStruct);
        case PPolyEvaluable pPolyEvaluable -> initializeScopeFor(pPolyEvaluable);
        case PNamedValue pNamedValue -> {}
        case PConstructor pConstructor -> initializeScopeFor(pConstructor);
        case PFunc pFunc -> initializeScopeFor(pFunc);
      }
      return scope.newInnerScope(mapOfAll(referenceables), mapOfAll(types));
    }

    private void initializeScopeFor(PModule pModule) {
      // For now, we don't have anything (function or value) that can be enclosed inside other
      // function or value and have fully qualified name that contains enclosing name.
      // Everything is flat in the global scope. Parameter default values have workaround of
      // gluing function name and parameter name using '~' into a name.
      pModule.structs().forEach(type -> addType(type, type.name()));
      pModule.evaluables().forEach(this::addNamedEvaluable);
    }

    private void addNamedEvaluable(PPolyEvaluable pNamedEvaluable) {
      // Do not report duplicate constructor names. As constructor names starts uppercase then the
      // only way duplicate constructor name can occur is when struct with duplicate names exists
      // which is reported as struct error so we don't want to duplicate that error here.
      var reportErrors = !(pNamedEvaluable.evaluable() instanceof PConstructor);
      addReferenceable(pNamedEvaluable, reportErrors);
    }

    private void initializeScopeFor(PStruct pStruct) {
      pStruct.fields().forEach(this::addReferenceable);
    }

    private void initializeScopeFor(PPolyEvaluable pPolyEvaluable) {
      switch (pPolyEvaluable.pTypeParams()) {
        // For now, we don't have anything (function or value) that can be enclosed inside other
        // function or value and have fully qualified name that contains enclosing name.
        // Everything is flat in the global scope. Parameter default values have workaround of
        // gluing function name and parameter name using '~' into a name.
        case PExplicitTypeParams explicit ->
          explicit.typeParams().foreach(type -> addType(type, type.name()));
        case PImplicitTypeParams implicit -> {}
      }
    }

    private void initializeScopeFor(PConstructor pConstructor) {
      // Do not report errors as duplicate parameters in constructor means there are duplicate
      // fields in its struct which is reported as Struct error.
      pConstructor.params().forEach(pReferenceable -> addReferenceable(pReferenceable, false));
    }

    private void initializeScopeFor(PFunc pFunc) {
      pFunc.params().forEach(this::addReferenceable);
    }

    private void addType(PTypeDefinition type, Name name) {
      PTypeDefinition previousBinding = types.put(name, type);
      if (previousBinding != null) {
        log.log(
            alreadyDefinedError(type.location(), previousBinding.location().description(), name));
      }
    }

    private void addReferenceable(PReferenceable pReferenceable) {
      addReferenceable(pReferenceable, true);
    }

    private void addReferenceable(PReferenceable pReferenceable, boolean reportErrors) {
      // For now, we don't have anything (function or value) that can be enclosed inside other
      // function or value and have fully qualified name that contains enclosing name.
      // Everything is flat in the global scope. Parameter default values have workaround of
      // gluing function name and parameter name using '~' into a name.
      var name = pReferenceable.name();
      PReferenceable previousBinding = referenceables.put(name, pReferenceable);
      if (previousBinding != null && reportErrors) {
        log.log(alreadyDefinedError(
            pReferenceable.location(), previousBinding.location().description(), name));
      }
    }

    private static Log alreadyDefinedError(Location location, String previousLocation, Name name) {
      return compileError(location, name.q() + " is already defined at " + previousLocation + ".");
    }
  }
}
