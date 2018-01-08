package org.smoothbuild.parse;

import static org.smoothbuild.parse.AssignArgsToParams.assignArgsToParams;
import static org.smoothbuild.parse.AssignNatives.assignNatives;
import static org.smoothbuild.parse.FindNatives.findNatives;
import static org.smoothbuild.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.parse.deps.SortByDependencies.sortedByDependencies;
import static org.smoothbuild.util.Maybe.invoke;
import static org.smoothbuild.util.Maybe.invokeWrap;
import static org.smoothbuild.util.Maybe.value;
import static org.smoothbuild.util.Paths.changeExtension;

import java.nio.file.Path;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.nativ.Native;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstCreator;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.util.Maybe;

public class ModuleLoader {
  private final AssignTypes assignTypes;
  private final FunctionLoader functionLoader;

  @Inject
  public ModuleLoader(
      AssignTypes assignTypes,
      FunctionLoader functionLoader) {
    this.assignTypes = assignTypes;
    this.functionLoader = functionLoader;
  }

  public Maybe<Functions> loadModule(Functions functions, Path script) {
    Maybe<ModuleContext> module = parseScript(script);
    Maybe<Ast> ast = invokeWrap(module, m -> AstCreator.fromParseTree(script, m));
    ast = invoke(ast, a -> findSemanticErrors(functions, a));
    ast = invoke(ast, a -> sortedByDependencies(functions, a));
    ast = invoke(ast, a -> assignTypes.assignTypes(functions, a));
    ast = invoke(ast, a -> assignArgsToParams(functions, a));
    Maybe<Map<Name, Native>> natives = findNatives(changeExtension(script, "jar"));
    ast = invoke(ast, natives, (a, n) -> assignNatives(a, n));
    return invoke(ast, a -> loadFunctions(functions, a));
  }

  private Maybe<Functions> loadFunctions(Functions functions, Ast ast) {
    Maybe<Functions> justLoaded = value(new Functions());
    for (FuncNode node : ast.functions()) {
      Maybe<Functions> all = invokeWrap(justLoaded, (j) -> j.addAll(functions));
      Maybe<Function> function = invokeWrap(all, a -> functionLoader.loadFunction(a, node));
      justLoaded = invokeWrap(justLoaded, function, Functions::add);
    }
    return justLoaded;
  }
}
