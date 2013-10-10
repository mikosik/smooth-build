package org.smoothbuild.parse;

import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.def.args.Argument.namedArg;
import static org.smoothbuild.function.def.args.Argument.namelessArg;
import static org.smoothbuild.function.def.args.Argument.pipedArg;
import static org.smoothbuild.function.def.args.ArgumentNodesCreator.createArgumentNodes;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.parse.LocationHelpers.locationIn;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.antlr.SmoothParser.SetContext;
import org.smoothbuild.antlr.SmoothParser.SetElemContext;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinedFunction;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.function.def.NodeCreator;
import org.smoothbuild.function.def.args.Argument;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.parse.err.ForbiddenSetElemTypeError;
import org.smoothbuild.parse.err.IncompatibleSetElemsError;
import org.smoothbuild.util.Empty;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DefinedFunctionsCreator {
  private final NodeCreator nodeCreator;

  @Inject
  public DefinedFunctionsCreator(NodeCreator nodeCreator) {
    this.nodeCreator = nodeCreator;
  }

  public Map<Name, DefinedFunction> createDefinedFunctions(MessageListener messages,
      SymbolTable symbolTable, Map<String, FunctionContext> functionContexts, List<String> sorted) {
    return new Worker(messages, symbolTable, functionContexts, sorted, nodeCreator).run();
  }

  private static class Worker {
    private final MessageListener messages;
    private final SymbolTable symbolTable;
    private final Map<String, FunctionContext> functionContexts;
    private final List<String> sorted;
    private final NodeCreator nodeCreator;

    private final Map<Name, DefinedFunction> functions = Maps.newHashMap();

    public Worker(MessageListener messages, SymbolTable symbolTable,
        Map<String, FunctionContext> functionContexts, List<String> sorted, NodeCreator nodeCreator) {
      this.messages = messages;
      this.symbolTable = symbolTable;
      this.functionContexts = functionContexts;
      this.sorted = sorted;
      this.nodeCreator = nodeCreator;
    }

    public Map<Name, DefinedFunction> run() {
      for (String name : sorted) {
        DefinedFunction definedFunction = build(functionContexts.get(name));
        functions.put(simpleName(name), definedFunction);
      }
      return functions;
    }

    public DefinedFunction build(FunctionContext function) {
      DefinitionNode node = build(function.pipe());

      Type type = node.type();
      String name = function.functionName().getText();
      ImmutableMap<String, Param> params = Empty.stringParamMap();
      Signature signature = new Signature(type, simpleName(name), params);

      return new DefinedFunction(signature, node);
    }

    private DefinitionNode build(PipeContext pipe) {
      DefinitionNode result = build(pipe.expression());
      List<CallContext> elements = pipe.call();
      for (int i = 0; i < elements.size(); i++) {
        CallContext call = elements.get(i);
        List<Argument> arguments = build(call.argList());
        // nameless piped argument's location is set to the pipe character '|'
        CodeLocation codeLocation = locationOf(pipe.p.get(i));
        arguments.add(pipedArg(result, codeLocation));
        result = build(call, arguments);
      }
      return result;
    }

    private DefinitionNode build(ExpressionContext expression) {
      if (expression.set() != null) {
        return build(expression.set());
      }
      if (expression.call() != null) {
        return build(expression.call());
      }
      if (expression.STRING() != null) {
        return buildStringNode(expression.STRING());
      }
      throw new RuntimeException("Illegal parse tree: " + ExpressionContext.class.getSimpleName()
          + " without children.");
    }

    private DefinitionNode build(SetContext list) {
      List<SetElemContext> elems = list.setElem();
      ImmutableList<DefinitionNode> elemNodes = build(elems);

      if (elemNodes.isEmpty()) {
        return nodeCreator.emptySet();
      }

      if (!areAllElemTypesEqual(elems, elemNodes)) {
        return nodeCreator.emptySet();
      }

      Type elemsType = elemNodes.get(0).type();
      if (elemsType == Type.STRING) {
        return nodeCreator.stringSet(elemNodes, locationOf(list));
      }
      if (elemsType == Type.FILE) {
        return nodeCreator.fileSet(elemNodes, locationOf(list));
      }
      throw new RuntimeException("Bug in Smooth implementation. No code to handle type = "
          + elemsType);
    }

    private ImmutableList<DefinitionNode> build(List<SetElemContext> elems) {
      Builder<DefinitionNode> builder = ImmutableList.builder();
      for (SetElemContext elem : elems) {
        DefinitionNode node = build(elem);
        if (!Type.allowedForSetElem().contains(node.type())) {
          messages.report(new ForbiddenSetElemTypeError(locationOf(elem), node.type()));
        } else {
          builder.add(node);
        }
      }
      return builder.build();
    }

    private DefinitionNode build(SetElemContext elem) {
      if (elem.STRING() != null) {
        return buildStringNode(elem.STRING());
      }
      if (elem.call() != null) {
        return build(elem.call());
      }
      throw new RuntimeException("Illegal parse tree: " + SetElemContext.class.getSimpleName()
          + " without children.");
    }

    private boolean areAllElemTypesEqual(List<SetElemContext> elems, List<DefinitionNode> elemNodes) {
      boolean success = true;
      Type firstType = elemNodes.get(0).type();
      for (int i = 0; i < elemNodes.size(); i++) {
        DefinitionNode elemNode = elemNodes.get(i);
        if (elemNode.type() != firstType) {
          CodeLocation location = locationOf(elems.get(i));
          messages.report(new IncompatibleSetElemsError(location, firstType, i, elemNode.type()));
          success = false;
        }
      }
      return success;
    }

    private DefinitionNode build(CallContext call) {
      List<Argument> arguments = build(call.argList());
      return build(call, arguments);
    }

    private DefinitionNode build(CallContext call, List<Argument> args) {
      String functionName = call.functionName().getText();

      Function function = getFunction(functionName);

      CodeLocation codeLocation = locationOf(call.functionName());
      Map<String, DefinitionNode> namedArgs = createArgumentNodes(codeLocation, messages, function,
          args);

      if (namedArgs == null) {
        return nodeCreator.invalid(function.type());
      } else {
        return nodeCreator.call(function, codeLocation, namedArgs);
      }
    }

    private Function getFunction(String functionName) {
      // UndefinedFunctionDetector has been run already so we can be sure at
      // this point that function with given name exists either among imported
      // functions or among already handled defined functions.

      Function function = symbolTable.getFunction(functionName);
      if (function == null) {
        return functions.get(simpleName(functionName));
      } else {
        return function;
      }
    }

    private List<Argument> build(ArgListContext argList) {
      List<Argument> result = Lists.newArrayList();
      if (argList != null) {
        List<ArgContext> argContextList = argList.arg();
        for (int i = 0; i < argContextList.size(); i++) {
          result.add(build(i, argContextList.get(i)));
        }
      }
      return result;
    }

    private Argument build(int index, ArgContext arg) {
      DefinitionNode node = build(arg.expression());

      CodeLocation location = locationOf(arg);
      ParamNameContext paramName = arg.paramName();
      if (paramName == null) {
        return namelessArg(index + 1, node, location);
      } else {
        return namedArg(index + 1, paramName.getText(), node, location);
      }
    }

    private DefinitionNode buildStringNode(TerminalNode stringToken) {
      String quotedString = stringToken.getText();
      String string = quotedString.substring(1, quotedString.length() - 1);
      try {
        return nodeCreator.string(unescaped(string));
      } catch (UnescapingFailedException e) {
        CodeLocation location = locationIn(stringToken.getSymbol(), 1 + e.charIndex());
        messages.report(new CodeMessage(ERROR, location, e.getMessage()));
        return nodeCreator.invalid(STRING);
      }
    }
  }
}
