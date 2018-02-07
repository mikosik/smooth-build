package org.smoothbuild.parse;

import static org.smoothbuild.parse.AssignArgsToParams.assignArgsToParams;
import static org.smoothbuild.parse.AssignNatives.assignNatives;
import static org.smoothbuild.parse.FindNatives.findNatives;
import static org.smoothbuild.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.util.Maybe.errors;
import static org.smoothbuild.util.Maybe.invokeWrap;
import static org.smoothbuild.util.Maybe.value;
import static org.smoothbuild.util.Paths.changeExtension;

import java.nio.file.Path;
import java.util.List;
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
    Maybe<Ast> maybeAst = invokeWrap(module, m -> AstCreator.fromParseTree(script, m));
    if (!maybeAst.hasValue()) {
      return errors(maybeAst.errors());
    }
    Ast ast = maybeAst.value();
    List<? extends Object> errors = findSemanticErrors(functions, ast);
    if (!errors.isEmpty()) {
      return errors(errors);
    }
    errors = ast.sortFunctionsByDependencies(functions);
    if (!errors.isEmpty()) {
      return errors(errors);
    }
    errors = assignTypes.assignTypes(functions, ast);
    if (!errors.isEmpty()) {
      return errors(errors);
    }
    errors = assignArgsToParams(functions, ast);
    if (!errors.isEmpty()) {
      return errors(errors);
    }
    Maybe<Map<Name, Native>> maybeNatives = findNatives(changeExtension(script, "jar"));
    if (!maybeNatives.hasValue()) {
      return errors(maybeNatives.errors());
    }
    Map<Name, Native> natives = maybeNatives.value();
    errors = assignNatives(ast, natives);
    if (!errors.isEmpty()) {
      return errors(errors);
    }
    return loadFunctions(functions, ast);
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
