package org.smoothbuild.parse;

import static org.smoothbuild.lang.expr.Expressions.callExpression;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.def.Argument.namedArgument;
import static org.smoothbuild.lang.function.def.Argument.namelessArgument;
import static org.smoothbuild.lang.function.def.Argument.pipedArgument;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.SmoothParser.ArrayContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ImplicitConverter;
import org.smoothbuild.lang.expr.InvalidExpression;
import org.smoothbuild.lang.expr.ValueExpression;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.Argument;
import org.smoothbuild.lang.function.def.ArgumentExpressionCreator;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.parse.err.ForbiddenArrayElemError;
import org.smoothbuild.parse.err.IncompatibleArrayElemsError;
import org.smoothbuild.util.Empty;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.Maps;

public class DefinedFunctionsCreator {
  private final ObjectsDb objectsDb;
  private final ArgumentExpressionCreator argumentExpressionCreator;
  private final ImplicitConverter implicitConverter;

  @Inject
  public DefinedFunctionsCreator(ObjectsDb objectsDb,
      ArgumentExpressionCreator argumentExpressionCreator, ImplicitConverter implicitConverter) {
    this.objectsDb = objectsDb;
    this.argumentExpressionCreator = argumentExpressionCreator;
    this.implicitConverter = implicitConverter;
  }

  public Map<Name, Function> createDefinedFunctions(LoggedMessages messages, Module builtinModule,
      Map<Name, FunctionContext> functionContexts, List<Name> sorted) {
    Worker worker =
        new Worker(messages, builtinModule, functionContexts, sorted, objectsDb,
            argumentExpressionCreator, implicitConverter);
    Map<Name, Function> result = worker.run();
    messages.failIfContainsProblems();
    return result;
  }

  private static class Worker {
    private final LoggedMessages messages;
    private final Module builtinModule;
    private final Map<Name, FunctionContext> functionContexts;
    private final List<Name> sorted;
    private final ObjectsDb objectsDb;
    private final ArgumentExpressionCreator argumentExpressionCreator;
    private final ImplicitConverter implicitConverter;

    private final Map<Name, Function> functions = Maps.newHashMap();

    public Worker(LoggedMessages messages, Module builtinModule,
        Map<Name, FunctionContext> functionContexts, List<Name> sorted, ObjectsDb objectsDb,
        ArgumentExpressionCreator argumentExpressionCreator, ImplicitConverter implicitConverter) {
      this.messages = messages;
      this.builtinModule = builtinModule;
      this.functionContexts = functionContexts;
      this.sorted = sorted;
      this.objectsDb = objectsDb;
      this.argumentExpressionCreator = argumentExpressionCreator;
      this.implicitConverter = implicitConverter;
    }

    public Map<Name, Function> run() {
      for (Name name : sorted) {
        DefinedFunction definedFunction = build(functionContexts.get(name));
        functions.put(name, definedFunction);
      }
      return functions;
    }

    public DefinedFunction build(FunctionContext functionContext) {
      return toFunction(functionContext);
    }

    private DefinedFunction toFunction(FunctionContext functionContext) {
      Expression expression = toExpression(functionContext.pipe());
      Name name = name(functionContext.functionName().getText());
      Signature signature = new Signature(expression.type(), name, Empty.paramList());
      return new DefinedFunction(signature, expression);
    }

    private Expression toExpression(PipeContext pipeContext) {
      Expression result = toExpression(pipeContext.expression());
      List<CallContext> calls = pipeContext.call();
      for (int i = 0; i < calls.size(); i++) {
        CallContext call = calls.get(i);
        List<Argument> arguments = toArguments(call.argList());
        // nameless piped argument's location is set to the pipe character '|'
        CodeLocation codeLocation = locationOf(pipeContext.p.get(i));
        arguments.add(pipedArgument(result, codeLocation));
        result = toExpression(call, arguments);
      }
      return result;
    }

    private List<Expression> toExpression(List<ExpressionContext> expressionContexts) {
      List<Expression> result = new ArrayList<>();
      for (ExpressionContext expressionContext : expressionContexts) {
        result.add(toExpression(expressionContext));
      }
      return result;
    }

    private Expression toExpression(ExpressionContext expressionContext) {
      if (expressionContext.array() != null) {
        return toExpression(expressionContext.array());
      }
      if (expressionContext.call() != null) {
        return toExpression(expressionContext.call());
      }
      if (expressionContext.STRING() != null) {
        return toStringExpression(expressionContext.STRING());
      }
      throw new Message(FATAL, "Illegal parse tree: " + ExpressionContext.class.getSimpleName()
          + " without children.");
    }

    private Expression toExpression(ArrayContext array) {
      List<ExpressionContext> elems = array.expression();
      List<Expression> expressions = toExpression(elems);

      CodeLocation location = locationOf(array);
      Type elemType = commonSuperType(expressions, location);

      if (elemType != null) {
        return toArrayExpression(elemType, expressions, location);
      } else {
        return new InvalidExpression(NIL, location);
      }
    }

    private <T extends Value> Expression toArrayExpression(Type elemType,
        List<Expression> expressions, CodeLocation location) {
      ArrayType arrayType = Types.arrayTypeContaining(elemType);
      if (arrayType == null) {
        messages.log(new ForbiddenArrayElemError(location, elemType));
        return new InvalidExpression(NIL, location);
      }
      return new ArrayExpression(arrayType, toConvertedExpressions(elemType, expressions), location);
    }

    public <T extends Value> List<Expression> toConvertedExpressions(Type type,
        Iterable<Expression> expressions) {
      List<Expression> result = new ArrayList<>();
      for (Expression expression : expressions) {
        result.add(implicitConverter.apply(type, expression));
      }
      return result;
    }

    private Type commonSuperType(List<Expression> expressions, CodeLocation location) {
      if (expressions.size() == 0) {
        return NOTHING;
      }
      Type firstType = expressions.get(0).type();
      Type superType = firstType;

      for (int i = 1; i < expressions.size(); i++) {
        Type type = expressions.get(i).type();
        superType = commonSuperType(superType, type);

        if (superType == null) {
          messages.log(new IncompatibleArrayElemsError(location, firstType, i, type));
          return null;
        }
      }
      return superType;
    }

    private static Type commonSuperType(Type type1, Type type2) {
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

    private Expression toExpression(CallContext callContext) {
      return toExpression(callContext, toArguments(callContext.argList()));
    }

    private Expression toExpression(CallContext callContext, List<Argument> arguments) {
      FunctionNameContext functionNameContext = callContext.functionName();
      Function function = getFunction(name(functionNameContext.getText()));
      CodeLocation codeLocation = locationOf(functionNameContext);
      List<Expression> argumentExpressions =
          argumentExpressionCreator.createArgExprs(codeLocation, messages, function, arguments);

      if (argumentExpressions == null) {
        return new InvalidExpression(function.type(), codeLocation);
      } else {
        return callExpression(function, false, codeLocation, argumentExpressions);
      }
    }

    private Function getFunction(Name name) {
      // UndefinedFunctionDetector has been run already so we can be sure at
      // this point that function with given name exists either among imported
      // functions or among already handled defined functions.
      Function function = builtinModule.getFunction(name);
      if (function == null) {
        return functions.get(name);
      } else {
        return function;
      }
    }

    private List<Argument> toArguments(ArgListContext argListContext) {
      List<Argument> result = new ArrayList<>();
      if (argListContext != null) {
        List<ArgContext> argContexts = argListContext.arg();
        for (int i = 0; i < argContexts.size(); i++) {
          result.add(toArgument(i, argContexts.get(i)));
        }
      }
      return result;
    }

    private Argument toArgument(int index, ArgContext arg) {
      Expression expression = toExpression(arg.expression());

      CodeLocation location = locationOf(arg);
      ParamNameContext paramName = arg.paramName();
      if (paramName == null) {
        return namelessArgument(index + 1, expression, location);
      } else {
        return namedArgument(index + 1, paramName.getText(), expression, location);
      }
    }

    private Expression toStringExpression(TerminalNode stringToken) {
      String quotedString = stringToken.getText();
      String string = quotedString.substring(1, quotedString.length() - 1);
      try {
        SString stringValue = objectsDb.string(unescaped(string));
        return new ValueExpression(stringValue, locationOf(stringToken.getSymbol()));
      } catch (UnescapingFailedException e) {
        CodeLocation location = locationOf(stringToken.getSymbol());
        messages.log(new CodeMessage(ERROR, location, e.getMessage()));
        return new InvalidExpression(STRING, locationOf(stringToken.getSymbol()));
      }
    }
  }
}
