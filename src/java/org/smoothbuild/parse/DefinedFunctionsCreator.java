package org.smoothbuild.parse;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.def.args.Argument.namedArg;
import static org.smoothbuild.lang.function.def.args.Argument.namelessArg;
import static org.smoothbuild.lang.function.def.args.Argument.pipedArg;
import static org.smoothbuild.lang.type.Type.FILE_ARRAY;
import static org.smoothbuild.lang.type.Type.STRING;
import static org.smoothbuild.lang.type.Type.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.SmoothParser.ArrayContext;
import org.smoothbuild.antlr.SmoothParser.ArrayElemContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.ArrayNode;
import org.smoothbuild.lang.function.def.CachingNode;
import org.smoothbuild.lang.function.def.CallNode;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.def.EmptyArrayNode;
import org.smoothbuild.lang.function.def.InvalidNode;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.function.def.StringNode;
import org.smoothbuild.lang.function.def.args.Argument;
import org.smoothbuild.lang.function.def.args.ArgumentNodesCreator;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.parse.err.ForbiddenArrayElemTypeError;
import org.smoothbuild.parse.err.IncompatibleArrayElemsError;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DefinedFunctionsCreator {
  private final ValueDb valueDb;
  private final ArgumentNodesCreator argumentNodesCreator;

  @Inject
  public DefinedFunctionsCreator(ValueDb valueDb, ArgumentNodesCreator argumentNodesCreator) {
    this.valueDb = valueDb;
    this.argumentNodesCreator = argumentNodesCreator;
  }

  public Map<Name, Function> createDefinedFunctions(MessageGroup messages, Module builtinModule,
      Map<Name, FunctionContext> functionContexts, List<Name> sorted) {
    Worker worker = new Worker(messages, builtinModule, functionContexts, sorted, valueDb,
        argumentNodesCreator);
    Map<Name, Function> result = worker.run();
    messages.failIfContainsProblems();
    return result;
  }

  private static class Worker {
    private final MessageGroup messages;
    private final Module builtinModule;
    private final Map<Name, FunctionContext> functionContexts;
    private final List<Name> sorted;
    private final ValueDb valueDb;
    private final ArgumentNodesCreator argumentNodesCreator;

    private final Map<Name, Function> functions = Maps.newHashMap();

    public Worker(MessageGroup messages, Module builtinModule,
        Map<Name, FunctionContext> functionContexts, List<Name> sorted, ValueDb valueDb,
        ArgumentNodesCreator argumentNodesCreator) {
      this.messages = messages;
      this.builtinModule = builtinModule;
      this.functionContexts = functionContexts;
      this.sorted = sorted;
      this.valueDb = valueDb;
      this.argumentNodesCreator = argumentNodesCreator;
    }

    public Map<Name, Function> run() {
      for (Name name : sorted) {
        DefinedFunction definedFunction = build(functionContexts.get(name));
        functions.put(name, definedFunction);
      }
      return functions;
    }

    public DefinedFunction build(FunctionContext function) {
      Node node = build(function.pipe());

      Type<?> type = node.type();
      String name = function.functionName().getText();
      ImmutableList<Param> params = ImmutableList.of();
      Signature signature = new Signature(type, name(name), params);

      return new DefinedFunction(signature, node);
    }

    private Node build(PipeContext pipe) {
      Node result = build(pipe.expression());
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

    private Node build(ExpressionContext expression) {
      if (expression.array() != null) {
        return build(expression.array());
      }
      if (expression.call() != null) {
        return build(expression.call());
      }
      if (expression.STRING() != null) {
        return buildStringNode(expression.STRING());
      }
      throw new ErrorMessageException(new Message(FATAL,
          "Bug in smooth binary: Illegal parse tree: " + ExpressionContext.class.getSimpleName()
              + " without children."));
    }

    private Node build(ArrayContext list) {
      List<ArrayElemContext> elems = list.arrayElem();
      ImmutableList<Node> elemNodes = build(elems);

      if (elemNodes.isEmpty()) {
        return new CachingNode(new EmptyArrayNode(locationOf(list)));
      }

      if (!areAllElemTypesEqual(elems, elemNodes)) {
        return new CachingNode(new EmptyArrayNode(locationOf(list)));
      }

      Type<?> elemsType = elemNodes.get(0).type();
      if (elemsType == Type.STRING) {
        return new CachingNode(new ArrayNode(STRING_ARRAY, elemNodes, locationOf(list)));
      }
      if (elemsType == Type.FILE) {
        return new CachingNode(new ArrayNode(FILE_ARRAY, elemNodes, locationOf(list)));
      }

      throw new ErrorMessageException(new Message(FATAL,
          "Bug in smooth binary: Unexpected list element type = " + elemsType));
    }

    private ImmutableList<Node> build(List<ArrayElemContext> elems) {
      Builder<Node> builder = ImmutableList.builder();
      for (ArrayElemContext elem : elems) {
        Node node = build(elem);
        if (!Type.allowedForArrayElem().contains(node.type())) {
          messages.report(new ForbiddenArrayElemTypeError(locationOf(elem), node.type()));
        } else {
          builder.add(node);
        }
      }
      return builder.build();
    }

    private Node build(ArrayElemContext elem) {
      if (elem.STRING() != null) {
        return buildStringNode(elem.STRING());
      }
      if (elem.call() != null) {
        return build(elem.call());
      }

      throw new ErrorMessageException(new Message(FATAL,
          "Bug in smooth binary: Illegal parse tree: " + ArrayElemContext.class.getSimpleName()
              + " without children."));
    }

    private boolean areAllElemTypesEqual(List<ArrayElemContext> elems, List<Node> elemNodes) {
      boolean success = true;
      Type<?> firstType = elemNodes.get(0).type();
      for (int i = 0; i < elemNodes.size(); i++) {
        Node elemNode = elemNodes.get(i);
        if (elemNode.type() != firstType) {
          CodeLocation location = locationOf(elems.get(i));
          messages.report(new IncompatibleArrayElemsError(location, firstType, i, elemNode.type()));
          success = false;
        }
      }
      return success;
    }

    private Node build(CallContext call) {
      List<Argument> arguments = build(call.argList());
      return build(call, arguments);
    }

    private Node build(CallContext call, List<Argument> args) {
      String functionName = call.functionName().getText();

      Function function = getFunction(functionName);

      CodeLocation codeLocation = locationOf(call.functionName());
      Map<String, Node> namedArgs = argumentNodesCreator.createArgumentNodes(codeLocation,
          messages, function, args);

      if (namedArgs == null) {
        InvalidNode node = new InvalidNode(function.type(), locationOf(call.functionName()));
        return new CachingNode(node);
      } else {
        return new CachingNode(new CallNode(function, codeLocation, namedArgs));
      }
    }

    private Function getFunction(String functionName) {
      // UndefinedFunctionDetector has been run already so we can be sure at
      // this point that function with given name exists either among imported
      // functions or among already handled defined functions.

      Name name = Name.name(functionName);
      Function function = builtinModule.getFunction(name);
      if (function == null) {
        return functions.get(name);
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
      Node node = build(arg.expression());

      CodeLocation location = locationOf(arg);
      ParamNameContext paramName = arg.paramName();
      if (paramName == null) {
        return namelessArg(index + 1, node, location);
      } else {
        return namedArg(index + 1, paramName.getText(), node, location);
      }
    }

    private Node buildStringNode(TerminalNode stringToken) {
      String quotedString = stringToken.getText();
      String string = quotedString.substring(1, quotedString.length() - 1);
      try {
        SString stringValue = valueDb.writeString(unescaped(string));
        return new CachingNode(new StringNode(stringValue, locationOf(stringToken.getSymbol())));
      } catch (UnescapingFailedException e) {
        CodeLocation location = locationOf(stringToken.getSymbol());
        messages.report(new CodeMessage(ERROR, location, e.getMessage()));
        return new CachingNode(new InvalidNode(STRING, locationOf(stringToken.getSymbol())));
      }
    }
  }
}
