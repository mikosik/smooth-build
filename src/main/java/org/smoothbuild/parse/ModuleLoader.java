package org.smoothbuild.parse;

import static org.smoothbuild.parse.AssignTypes.assignTypes;
import static org.smoothbuild.parse.DefinedFunctionLoader.loadDefinedFunction;
import static org.smoothbuild.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.invoke;
import static org.smoothbuild.util.Maybe.invokeWrap;
import static org.smoothbuild.util.Maybe.value;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstCreator;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.util.Lists;
import org.smoothbuild.util.Maybe;

public class ModuleLoader {
  private final FileSystem fileSystem;

  @Inject
  public ModuleLoader(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public Maybe<Functions> loadFunctions(Functions functions, Path smoothFile) {
    Maybe<InputStream> inputStream = scriptInputStream(smoothFile);
    return invoke(inputStream, is -> loadFunctions(functions, is, smoothFile));
  }

  private Maybe<InputStream> scriptInputStream(Path scriptFile) {
    try {
      return value(fileSystem.openInputStream(scriptFile));
    } catch (FileSystemException e) {
      return error("error: Cannot read build script file " + scriptFile + ". " + e.getMessage());
    }
  }

  private Maybe<Functions> loadFunctions(Functions functions, InputStream inputStream,
      Path scriptFile) {
    Maybe<ModuleContext> module = parseScript(inputStream, scriptFile);
    Maybe<Ast> ast = invokeWrap(module, m -> AstCreator.fromParseTree(m));
    ast = ast.addErrors(a -> findSemanticErrors(functions, a));
    ast = ast.addErrors(a -> assignTypes(a));
    ast = invoke(ast, a -> sortedByDependencies(functions, a));
    return invoke(ast, a -> loadDefinedFunctions(functions, a));
  }

  private static Maybe<Ast> sortedByDependencies(Functions functions, Ast ast) {
    Map<Name, FuncNode> nodeMap = ast.nameToFunctionMap();
    Map<Name, FuncNode> notSorted = new HashMap<>(nodeMap);
    Set<Name> availableFunctions = new HashSet<>(functions.names());
    List<Name> sorted = new ArrayList<>(nodeMap.size());
    DependencyStack stack = new DependencyStack();

    while (!notSorted.isEmpty() || !stack.isEmpty()) {
      if (stack.isEmpty()) {
        stack.push(removeNext(notSorted));
      }
      DependencyStackElem stackTop = stack.peek();
      Dependency missing = findUnreachableDependency(
          availableFunctions, sorted, stackTop.dependencies());
      if (missing == null) {
        sorted.add(stack.pop().name());
      } else {
        stackTop.setMissing(missing);
        FuncNode next = notSorted.remove(missing.functionName());
        if (next == null) {
          return error(stack.createCycleError());
        } else {
          stack.push(new DependencyStackElem(next));
        }
      }
    }
    return value(new Ast(Lists.map(sorted, n -> nodeMap.get(n))));
  }

  private static Dependency findUnreachableDependency(Set<Name> availableFunctions,
      List<Name> sorted, Set<Dependency> dependencies) {
    for (Dependency dependency : dependencies) {
      Name name = dependency.functionName();
      if (!(sorted.contains(name) || availableFunctions.contains(name))) {
        return dependency;
      }
    }
    return null;
  }

  private static DependencyStackElem removeNext(Map<Name, FuncNode> dependencies) {
    Iterator<Entry<Name, FuncNode>> it = dependencies.entrySet().iterator();
    Entry<Name, FuncNode> element = it.next();
    it.remove();
    return new DependencyStackElem(element.getValue());
  }

  private Maybe<Functions> loadDefinedFunctions(Functions functions, Ast ast) {
    Maybe<Functions> justLoaded = value(new Functions());
    for (FuncNode node : ast.functions()) {
      Maybe<Functions> all = invokeWrap(justLoaded, (j) -> j.addAll(functions));
      Maybe<DefinedFunction> function = invoke(all,
          a -> loadDefinedFunction(a, node));
      justLoaded = invokeWrap(justLoaded, function, Functions::add);
    }
    return justLoaded;
  }
}
