package org.smoothbuild.parse;

import static org.smoothbuild.parse.AssignArgsToParams.assignArgsToParams;
import static org.smoothbuild.parse.AssignNatives.assignNatives;
import static org.smoothbuild.parse.AssignTypes.assignTypes;
import static org.smoothbuild.parse.FunctionLoader.loadFunction;
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
  public static Maybe<Functions> loadModule(Functions functions, Path script) {
    Maybe<ModuleContext> module = parseScript(script);
    Maybe<Ast> ast = invokeWrap(module, m -> AstCreator.fromParseTree(m));
    ast = ast.addErrors(a -> findSemanticErrors(functions, a));
    ast = invoke(ast, a -> sortedByDependencies(functions, a));
    ast = ast.addErrors(a -> assignTypes(functions, a));
    ast = ast.addErrors(a -> assignArgsToParams(functions, a));
    Maybe<Map<Name, Native>> natives = findNatives(changeExtension(script, "jar"));
    ast = invoke(ast, natives, (a, n) -> assignNatives(a, n));
    return invoke(ast, a -> loadFunctions(functions, a));
  }

  private static Maybe<Functions> loadFunctions(Functions functions, Ast ast) {
    Maybe<Functions> justLoaded = value(new Functions());
    for (FuncNode node : ast.functions()) {
      Maybe<Functions> all = invokeWrap(justLoaded, (j) -> j.addAll(functions));
      Maybe<Function> function = invokeWrap(all, a -> loadFunction(a, node));
      justLoaded = invokeWrap(justLoaded, function, Functions::add);
    }
    return justLoaded;
  }
}
