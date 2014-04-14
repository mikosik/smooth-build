package org.smoothbuild.parse;

import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.basicTypes;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.def.args.Arg.namedArg;
import static org.smoothbuild.lang.function.def.args.Arg.namelessArg;
import static org.smoothbuild.lang.function.def.args.Arg.pipedArg;
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
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.STypes;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.ArrayNode;
import org.smoothbuild.lang.function.def.CachingNode;
import org.smoothbuild.lang.function.def.CallNode;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.def.InvalidNode;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.function.def.StringNode;
import org.smoothbuild.lang.function.def.args.Arg;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.parse.err.ForbiddenArrayElemError;
import org.smoothbuild.parse.err.IncompatibleArrayElemsError;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DefinedFunctionsCreator {
  private final ObjectsDb objectsDb;
  private final ArgNodesCreator argNodesCreator;

  @Inject
  public DefinedFunctionsCreator(ObjectsDb objectsDb, ArgNodesCreator argNodesCreator) {
    this.objectsDb = objectsDb;
    this.argNodesCreator = argNodesCreator;
  }

  public Map<Name, Function<?>> createDefinedFunctions(LoggedMessages messages,
      Module builtinModule, Map<Name, FunctionContext> functionContexts, List<Name> sorted) {
    Worker worker =
        new Worker(messages, builtinModule, functionContexts, sorted, objectsDb, argNodesCreator);
    Map<Name, Function<?>> result = worker.run();
    messages.failIfContainsProblems();
    return result;
  }

  private static class Worker {
    private final LoggedMessages messages;
    private final Module builtinModule;
    private final Map<Name, FunctionContext> functionContexts;
    private final List<Name> sorted;
    private final ObjectsDb objectsDb;
    private final ArgNodesCreator argNodesCreator;

    private final Map<Name, Function<?>> functions = Maps.newHashMap();

    public Worker(LoggedMessages messages, Module builtinModule,
        Map<Name, FunctionContext> functionContexts, List<Name> sorted, ObjectsDb objectsDb,
        ArgNodesCreator argNodesCreator) {
      this.messages = messages;
      this.builtinModule = builtinModule;
      this.functionContexts = functionContexts;
      this.sorted = sorted;
      this.objectsDb = objectsDb;
      this.argNodesCreator = argNodesCreator;
    }

    public Map<Name, Function<?>> run() {
      for (Name name : sorted) {
        DefinedFunction<?> definedFunction = build(functionContexts.get(name));
        functions.put(name, definedFunction);
      }
      return functions;
    }

    public DefinedFunction<?> build(FunctionContext function) {
      Node<?> node = build(function.pipe());
      return buildDefinedFunction(function, node);
    }

    private <T extends SValue> DefinedFunction<T> buildDefinedFunction(FunctionContext function,
        Node<T> node) {
      Name name = name(function.functionName().getText());
      Signature<T> signature = new Signature<>(node.type(), name, ImmutableList.<Param> of());
      return new DefinedFunction<>(signature, node);
    }

    private Node<?> build(PipeContext pipe) {
      Node<?> result = build(pipe.expression());
      List<CallContext> elements = pipe.call();
      for (int i = 0; i < elements.size(); i++) {
        CallContext call = elements.get(i);
        List<Arg> args = build(call.argList());
        // nameless piped argument's location is set to the pipe character '|'
        CodeLocation codeLocation = locationOf(pipe.p.get(i));
        args.add(pipedArg(result, codeLocation));
        result = build(call, args);
      }
      return result;
    }

    private Node<?> build(ExpressionContext expression) {
      if (expression.array() != null) {
        return build(expression.array());
      }
      if (expression.call() != null) {
        return build(expression.call());
      }
      if (expression.STRING() != null) {
        return buildStringNode(expression.STRING());
      }
      throw new Message(FATAL, "Bug in smooth binary: Illegal parse tree: "
          + ExpressionContext.class.getSimpleName() + " without children.");
    }

    private Node<?> build(ArrayContext list) {
      List<ArrayElemContext> elems = list.arrayElem();
      ImmutableList<Node<?>> elemNodes = build(elems);

      CodeLocation location = locationOf(list);
      SType<?> elemType = commonSuperType(elems, elemNodes, location);

      if (elemType != null) {
        return buildArray(elemType, elemNodes, location);
      } else {
        return new InvalidNode<>(EMPTY_ARRAY, location);
      }
    }

    private <T extends SValue> Node<SArray<T>> buildArray(SType<T> elemType,
        ImmutableList<Node<?>> elemNodes, CodeLocation location) {
      SArrayType<T> arrayType = STypes.arrayTypeContaining(elemType);
      ImmutableList<Node<T>> convertedNodes = Convert.ifNeeded(elemType, elemNodes);
      return new CachingNode<>(new ArrayNode<>(arrayType, convertedNodes, location));
    }

    private ImmutableList<Node<?>> build(List<ArrayElemContext> elems) {
      Builder<Node<?>> builder = ImmutableList.builder();
      for (ArrayElemContext elem : elems) {
        Node<?> node = build(elem);
        if (!basicTypes().contains(node.type())) {
          CodeLocation location = locationOf(elem);
          messages.log(new ForbiddenArrayElemError(location, node.type()));
          builder.add(new InvalidNode<>(NOTHING, location));
        } else {
          builder.add(node);
        }
      }
      return builder.build();
    }

    private Node<?> build(ArrayElemContext elem) {
      if (elem.STRING() != null) {
        return buildStringNode(elem.STRING());
      }
      if (elem.call() != null) {
        return build(elem.call());
      }

      throw new Message(FATAL, "Bug in smooth binary: Illegal parse tree: "
          + ArrayElemContext.class.getSimpleName() + " without children.");
    }

    private SType<?> commonSuperType(List<ArrayElemContext> elems,
        ImmutableList<Node<?>> elemNodes, CodeLocation location) {
      if (elems.size() == 0) {
        return NOTHING;
      }
      SType<?> firstType = elemNodes.get(0).type();
      SType<?> commonSuperType = firstType;

      for (int i = 1; i < elemNodes.size(); i++) {
        SType<?> currentType = elemNodes.get(i).type();
        commonSuperType = commonSuperType(commonSuperType, currentType);

        if (commonSuperType == null) {
          messages.log(new IncompatibleArrayElemsError(location, firstType, i, currentType));
          return null;
        }
      }
      return commonSuperType;
    }

    private static SType<?> commonSuperType(SType<?> type1, SType<?> type2) {
      if (type1 == STRING) {
        if (type2 == STRING) {
          return STRING;
        } else {
          return null;
        }
      } else if (type1 == BLOB) {
        if (type2 == BLOB || type2 == FILE) {
          return BLOB;
        } else {
          return null;
        }
      } else if (type1 == FILE) {
        if (type2 == FILE) {
          return FILE;
        } else if (type2 == BLOB) {
          return BLOB;
        } else {
          return null;
        }
      }
      return null;
    }

    private Node<?> build(CallContext call) {
      List<Arg> args = build(call.argList());
      return build(call, args);
    }

    private Node<?> build(CallContext call, List<Arg> args) {
      String functionName = call.functionName().getText();

      Function<?> function = getFunction(functionName);

      CodeLocation codeLocation = locationOf(call.functionName());
      Map<String, Node<?>> namedArgs =
          argNodesCreator.createArgumentNodes(codeLocation, messages, function, args);

      if (namedArgs == null) {
        InvalidNode<?> node = new InvalidNode<>(function.type(), locationOf(call.functionName()));
        return new CachingNode<>(node);
      } else {
        return new CachingNode<>(new CallNode<>(function, codeLocation, namedArgs));
      }
    }

    private Function<?> getFunction(String functionName) {
      // UndefinedFunctionDetector has been run already so we can be sure at
      // this point that function with given name exists either among imported
      // functions or among already handled defined functions.

      Name name = Name.name(functionName);
      Function<?> function = builtinModule.getFunction(name);
      if (function == null) {
        return functions.get(name);
      } else {
        return function;
      }
    }

    private List<Arg> build(ArgListContext argList) {
      List<Arg> result = Lists.newArrayList();
      if (argList != null) {
        List<ArgContext> argContextList = argList.arg();
        for (int i = 0; i < argContextList.size(); i++) {
          result.add(build(i, argContextList.get(i)));
        }
      }
      return result;
    }

    private Arg build(int index, ArgContext arg) {
      Node<?> node = build(arg.expression());

      CodeLocation location = locationOf(arg);
      ParamNameContext paramName = arg.paramName();
      if (paramName == null) {
        return namelessArg(index + 1, node, location);
      } else {
        return namedArg(index + 1, paramName.getText(), node, location);
      }
    }

    private Node<?> buildStringNode(TerminalNode stringToken) {
      String quotedString = stringToken.getText();
      String string = quotedString.substring(1, quotedString.length() - 1);
      try {
        SString stringValue = objectsDb.string(unescaped(string));
        return new CachingNode<>(new StringNode(stringValue, locationOf(stringToken.getSymbol())));
      } catch (UnescapingFailedException e) {
        CodeLocation location = locationOf(stringToken.getSymbol());
        messages.log(new CodeMessage(ERROR, location, e.getMessage()));
        return new CachingNode<>(new InvalidNode<>(STRING, locationOf(stringToken.getSymbol())));
      }
    }
  }
}
