package org.smoothbuild.compilerfrontend;

import static org.smoothbuild.common.dag.Dag.apply0;
import static org.smoothbuild.common.dag.Dag.apply1;
import static org.smoothbuild.common.dag.Dag.apply2;
import static org.smoothbuild.common.dag.Dag.evaluate;
import static org.smoothbuild.common.dag.Dag.prefix;
import static org.smoothbuild.common.dag.Dag.value;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerfrontend.lang.define.ScopeS.scopeS;

import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.dag.Dag;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.compile.ConvertPs;
import org.smoothbuild.compilerfrontend.compile.DecodeLiterals;
import org.smoothbuild.compilerfrontend.compile.DetectUndefined;
import org.smoothbuild.compilerfrontend.compile.InitializeScopes;
import org.smoothbuild.compilerfrontend.compile.InjectDefaultArguments;
import org.smoothbuild.compilerfrontend.compile.LoadInternalModuleMembers;
import org.smoothbuild.compilerfrontend.compile.ast.SortModuleMembersByDependency;
import org.smoothbuild.compilerfrontend.compile.infer.InferTypes;
import org.smoothbuild.compilerfrontend.lang.define.ModuleS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.parse.FindSyntaxErrors;
import org.smoothbuild.compilerfrontend.parse.Parse;
import org.smoothbuild.compilerfrontend.parse.TranslateAp;

public class ModuleFrontendCompilationDag {
  public static Dag<ScopeS> frontendCompilationDag(List<FullPath> modules) {
    var graph = apply0(LoadInternalModuleMembers.class);
    for (var fullPath : modules) {
      graph = evaluate(apply2(ModuleFrontendCompilationDag::create, graph, value(fullPath)));
    }
    return prefix(label("parse"), graph);
  }

  public static Try<Dag<ScopeS>> create(ScopeS scopeS, FullPath fullPath) {
    var environment = value(scopeS);
    var path = value(fullPath);
    var fileContent = apply1(ReadFileContent.class, path);
    var moduleContext = apply2(Parse.class, fileContent, path);
    var moduleP = apply2(TranslateAp.class, moduleContext, path);
    var withSyntaxCheck = apply1(FindSyntaxErrors.class, moduleP);
    var withDecodedLiterals = apply1(DecodeLiterals.class, withSyntaxCheck);
    var withInitializedScopes = apply1(InitializeScopes.class, withDecodedLiterals);
    var withUndefinedDetected = apply2(DetectUndefined.class, withInitializedScopes, environment);
    var withInjected = apply2(InjectDefaultArguments.class, withUndefinedDetected, environment);
    var sorted = apply1(SortModuleMembersByDependency.class, withInjected);
    var typesInferred = apply2(InferTypes.class, sorted, environment);
    var moduleS = apply2(ConvertPs.class, typesInferred, environment);
    var newScopeS = apply1((ModuleS m) -> success(scopeS(scopeS, m.members())), moduleS);
    return success(newScopeS);
  }
}
