package org.smoothbuild.parse.ast;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.sane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.lang.SmoothBaseVisitor;
import org.smoothbuild.antlr.lang.SmoothParser.AnnContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArrayTContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainCallContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainPartContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprHeadContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldListContext;
import org.smoothbuild.antlr.lang.SmoothParser.FuncTContext;
import org.smoothbuild.antlr.lang.SmoothParser.LiteralContext;
import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamListContext;
import org.smoothbuild.antlr.lang.SmoothParser.SelectContext;
import org.smoothbuild.antlr.lang.SmoothParser.StructContext;
import org.smoothbuild.antlr.lang.SmoothParser.TopContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeListContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeNameContext;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.parse.LocHelpers;

import com.google.common.collect.ImmutableList;

public class AstCreator {
  public static Ast fromParseTree(FilePath filePath, ModContext module) {
    List<StructN> structs = new ArrayList<>();
    List<RefableObjN> refables = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      @Override
      public Void visitStruct(StructContext struct) {
        String name = struct.TNAME().getText();
        Loc loc = LocHelpers.locOf(filePath, struct.TNAME().getSymbol());
        List<ItemN> fields = createFields(struct.fieldList());
        structs.add(new StructN(name, fields, loc));
        return null;
      }

      private List<ItemN> createFields(FieldListContext fieldList) {
        if (fieldList != null) {
          return sane(fieldList.field())
              .stream()
              .map(this::createField)
              .collect(toImmutableList());
        }
        return new ArrayList<>();
      }

      private ItemN createField(FieldContext field) {
        TypeN type = createT(field.type());
        TerminalNode nameNode = field.NAME();
        String name = nameNode.getText();
        Loc loc = LocHelpers.locOf(filePath, nameNode);
        return new ItemN(type, name, Optional.empty(), loc);
      }

      @Override
      public Void visitTop(TopContext top) {
        TerminalNode nameNode = top.NAME();
        visitChildren(top);
        Optional<TypeN> type = createTypeSane(top.type());
        String name = nameNode.getText();
        Optional<ObjN> obj = createObjSane(top.expr());
        Optional<AnnN> annotation = createNativeSane(top.ann());
        Loc loc = LocHelpers.locOf(filePath, nameNode);
        if (top.paramList() == null) {
          refables.add(new ValN(type, name, obj, annotation, loc));
        } else {
          List<ItemN> params = createParams(top.paramList());
          refables.add(new FuncN(type, name, params, obj, annotation, loc));
        }
        return null;
      }

      private Optional<AnnN> createNativeSane(AnnContext annotation) {
        if (annotation == null) {
          return Optional.empty();
        } else {
          String name = annotation.TNAME().getText();
          return Optional.of(new AnnN(
              name,
              createStringNode(annotation, annotation.STRING()),
              LocHelpers.locOf(filePath, annotation)));
        }
      }

      private List<ItemN> createParams(ParamListContext paramList) {
        ArrayList<ItemN> result = new ArrayList<>();
        if (paramList != null) {
          return sane(paramList.param())
              .stream().map(this::createParam)
              .collect(toImmutableList());
        }
        return result;
      }

      private ItemN createParam(ParamContext param) {
        var type = createT(param.type());
        var name = param.NAME().getText();
        var defaultArg = Optional.ofNullable(param.expr()).map(this::createObj);
        var loc = LocHelpers.locOf(filePath, param);
        return new ItemN(type, name, defaultArg, loc);
      }

      private Optional<ObjN> createObjSane(ExprContext expr) {
        return expr == null ? Optional.empty() : Optional.of(createObj(expr));
      }

      private ObjN createObj(ExprContext expr) {
        ObjN result = createChainHead(expr.exprHead());
        List<ChainCallContext> chainCallsInPipe = expr.chainCall();
        for (int i = 0; i < chainCallsInPipe.size(); i++) {
          var pipedArg = pipedArg(result, expr.p.get(i));
          ChainCallContext chain = chainCallsInPipe.get(i);
          result = createChainCallObj(pipedArg, chain);
        }
        return result;
      }

      private ObjN createChainHead(ExprHeadContext expr) {
        if (expr.chain() != null) {
          return createChainObj(expr.chain());
        }
        if (expr.literal() != null) {
          return createLiteral(expr.literal());
        }
        throw newRuntimeException(ExprHeadContext.class);
      }

      private ObjN createChainObj(ChainContext chain) {
        ObjN result = newRefNode(chain.NAME());
        return createChainParts(result, chain.chainPart());
      }

      private ArgN pipedArg(ObjN result, Token pipeCharacter) {
        // Loc of nameless piped arg is set to the loc of pipe character '|'.
        Loc loc = LocHelpers.locOf(filePath, pipeCharacter);
        return new ExplicitArgN(null, result, loc);
      }

      private ObjN createLiteral(LiteralContext expr) {
        if (expr.array() != null) {
          List<ObjN> elems = map(expr.array().expr(), this::createObj);
          return new OrderN(elems, LocHelpers.locOf(filePath, expr));
        }
        if (expr.BLOB() != null) {
          return new BlobN(expr.BLOB().getText().substring(2), LocHelpers.locOf(filePath, expr));
        }
        if (expr.INT() != null) {
          return new IntN(expr.INT().getText(), LocHelpers.locOf(filePath, expr));
        }
        if (expr.STRING() != null) {
          return createStringNode(expr, expr.STRING());
        }
        throw newRuntimeException(LiteralContext.class);
      }

      private StringN createStringNode(ParserRuleContext expr, TerminalNode quotedString) {
        String unquoted = unquote(quotedString.getText());
        Loc loc = LocHelpers.locOf(filePath, expr);
        return new StringN(unquoted, loc);
      }

      private ObjN createChainCallObj(ArgN pipedArg, ChainCallContext chainCall) {
        ObjN result = newRefNode(chainCall.NAME());
        for (SelectContext fieldRead : chainCall.select()) {
          result = createSelect(result, fieldRead);
        }

        var args = createArgList(chainCall.argList());
        result = createCall(result, concat(pipedArg, args), chainCall.argList());

        return createChainParts(result, chainCall.chainPart());
      }

      private RefN newRefNode(TerminalNode name) {
        return new RefN(name.getText(), LocHelpers.locOf(filePath, name));
      }

      private SelectN createSelect(ObjN selectable, SelectContext fieldRead) {
        String name = fieldRead.NAME().getText();
        Loc loc = LocHelpers.locOf(filePath, fieldRead);
        return new SelectN(selectable, name, loc);
      }

      private ObjN createChainParts(ObjN obj, List<ChainPartContext> chainParts) {
        ObjN result = obj;
        for (ChainPartContext chainPart : chainParts) {
          if (chainPart.argList() != null) {
            var args = createArgList(chainPart.argList());
            result = createCall(result, args, chainPart.argList());
          } else if (chainPart.select() != null) {
            result = createSelect(result, chainPart.select());
          } else {
            throw newRuntimeException(ChainContext.class);
          }
        }
        return result;
      }

      private List<ArgN> createArgList(ArgListContext argList) {
        List<ArgN> result = new ArrayList<>();
        for (ArgContext arg : argList.arg()) {
          ExprContext expr = arg.expr();
          TerminalNode nameNode = arg.NAME();
          String name = nameNode == null ? null : nameNode.getText();
          ObjN objN = createObj(expr);
          result.add(new ExplicitArgN(name, objN, LocHelpers.locOf(filePath, arg)));
        }
        return result;
      }

      private ObjN createCall(
          ObjN callable, List<ArgN> args, ArgListContext argListContext) {
        Loc loc = LocHelpers.locOf(filePath, argListContext);
        return new CallN(callable, args, loc);
      }

      private Optional<TypeN> createTypeSane(TypeContext type) {
        return type == null ? Optional.empty() : Optional.of(createT(type));
      }

      private TypeN createT(TypeContext type) {
        return switch (type) {
          case TypeNameContext name -> createT(name);
          case ArrayTContext arrayT -> createArrayT(arrayT);
          case FuncTContext funcT -> createFuncT(funcT);
          default -> throw unexpectedCaseExc(type);
        };
      }

      private TypeN createT(TypeNameContext type) {
        return new TypeN(type.getText(), LocHelpers.locOf(filePath, type.TNAME()));
      }

      private TypeN createArrayT(ArrayTContext arrayT) {
        TypeN elemType = createT(arrayT.type());
        return new ArrayTN(elemType, LocHelpers.locOf(filePath, arrayT));
      }

      private TypeN createFuncT(FuncTContext funcT) {
        TypeN resultType = createT(funcT.type());
        return new FuncTN(resultType, createTs(funcT.typeList()),
            LocHelpers.locOf(filePath, funcT));
      }

      private ImmutableList<TypeN> createTs(TypeListContext typeList) {
        if (typeList != null) {
          return map(typeList.type(), this::createT);
        } else {
          return list();
        }
      }

      private RuntimeException newRuntimeException(Class<?> clazz) {
        return new RuntimeException("Illegal parse tree: " + clazz.getSimpleName()
            + " without children.");
      }
    }.visit(module);
    return new Ast(structs, refables);
  }

  private static String unquote(String quotedString) {
    return quotedString.substring(1, quotedString.length() - 1);
  }
}
