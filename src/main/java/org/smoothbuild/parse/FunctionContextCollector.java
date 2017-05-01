package org.smoothbuild.parse;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.parse.Maybe.errors;
import static org.smoothbuild.parse.Maybe.invoke;
import static org.smoothbuild.parse.Maybe.invokeWrap;
import static org.smoothbuild.parse.Maybe.maybe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;

public class FunctionContextCollector {
  public static Maybe<List<FunctionContext>> collectFunctionContexts(ModuleContext module,
      Functions functions) {
    Maybe<Map<Name, FunctionNode>> functionNodes = collectNodes(module, functions);
    if (!functionNodes.hasValue()) {
      return errors(functionNodes.errors());
    }
    Maybe<Map<Name, FunctionNode>> dependencies = invoke(functionNodes,
        fns -> collectDependencies(fns, functions));
    Maybe<List<Name>> sorted = invoke(dependencies, ds -> sortDependencies(functions, ds));
    return invokeWrap(functionNodes, sorted, (fns, s) -> sortFunctions(fns, s));
  }

  private static List<FunctionContext> sortFunctions(Map<Name, FunctionNode> functionNodes,
      List<Name> names) {
    return names.stream()
        .map(n -> functionNodes.get(n).context())
        .collect(toList());
  }

  private static Maybe<Map<Name, FunctionNode>> collectNodes(ModuleContext module,
      Functions functions) {
    Map<Name, FunctionNode> nodes = new HashMap<>();
    List<ParseError> errors = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      Set<Dependency> currentDependencies = new HashSet<>();

      public Void visitFunction(FunctionContext context) {
        FunctionNameContext nameContext = context.functionName();
        Name name = name(nameContext.getText());
        if (nodes.keySet().contains(name)) {
          errors.add(new ParseError(
              locationOf(nameContext), "Function " + name + " is already defined."));
          return null;
        }
        if (functions.contains(name)) {
          errors.add(new ParseError(locationOf(nameContext), "Function " + name
              + " cannot override builtin function with the same name."));
          return null;
        }
        visitChildren(context);
        nodes.put(name,
            new FunctionNode(name, context, currentDependencies, locationOf(nameContext)));
        return null;
      }

      public Void visitCall(CallContext call) {
        FunctionNameContext functionName = call.functionName();
        Name name = name(functionName.getText());
        CodeLocation location = locationOf(functionName);
        currentDependencies.add(new Dependency(location, name));
        return visitChildren(call);
      }

    }.visit(module);
    return maybe(nodes, errors);
  }
}
