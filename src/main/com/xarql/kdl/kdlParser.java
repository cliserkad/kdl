// Generated from kdl.g4 by ANTLR 4.7.1

package main.com.xarql.kdl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class kdlParser extends Parser {
	public static final int T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, T__8 = 9, T__9 = 10, T__10 = 11, WS = 12, CLASS = 13, CONST = 14, RUN = 15, METHOD = 16, FUNCTION = 17, TRUE = 18, FALSE = 19, RETURN = 20, INT = 21, BODY_OPEN = 22, BODY_CLOSE = 23, PARAM_OPEN = 24, PARAM_CLOSE = 25, DOT = 26, SEPARATOR = 27, STATEMENT_END = 28, ASSIGN = 29, COMPARE = 30, PLUS = 31, MINUS = 32, DIVIDE = 33, MULTIPLY = 34, MODULUS = 35, CONSTNAME = 36, CLASSNAME = 37, VARNAME = 38, ESCAPED_QUOTE = 39, STRING = 40, DIGIT = 41;
	public static final int RULE_bool = 0, RULE_literal = 1, RULE_number = 2, RULE_statement = 3, RULE_mathExpression = 4, RULE_valueExpression = 5, RULE_operator = 6, RULE_variableDeclaration = 7, RULE_variableAssignment = 8, RULE_typedVariable = 9, RULE_methodCallStatement = 10, RULE_methodCallChain = 11, RULE_methodCall = 12, RULE_regularMethodCall = 13, RULE_objectiveMethodCall = 14, RULE_staticMethodCall = 15, RULE_parameterSet = 16, RULE_parameter = 17, RULE_methodDefinition = 18, RULE_methodType = 19, RULE_parameterDefinition = 20, RULE_methodBody = 21, RULE_returnStatement = 22, RULE_type = 23, RULE_basetype = 24, RULE_source = 25, RULE_clazz = 26, RULE_constant = 27, RULE_run = 28;
	public static final    String[]               ruleNames           = {"bool", "literal", "number", "statement", "mathExpression", "valueExpression", "operator", "variableDeclaration", "variableAssignment", "typedVariable", "methodCallStatement", "methodCallChain", "methodCall", "regularMethodCall", "objectiveMethodCall", "staticMethodCall", "parameterSet", "parameter", "methodDefinition", "methodType", "parameterDefinition", "methodBody", "returnStatement", "type", "basetype", "source", "clazz", "constant", "run"};
	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final    String[]               tokenNames;
	public static final    String                 _serializedATN      = "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3+\u00fb\4\2\t\2\4" + "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t" + "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" + "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" + "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3\3\3\3" + "\5\3B\n\3\3\4\7\4E\n\4\f\4\16\4H\13\4\3\5\3\5\3\5\5\5M\n\5\3\6\3\6\3\6" + "\3\6\3\7\3\7\3\7\3\7\3\7\5\7X\n\7\3\b\3\b\3\t\3\t\3\t\7\t_\n\t\f\t\16" + "\tb\13\t\3\t\3\t\3\t\5\tg\n\t\5\ti\n\t\3\t\3\t\3\n\3\n\3\n\7\np\n\n\f" + "\n\16\ns\13\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\7" + "\r\u0082\n\r\f\r\16\r\u0085\13\r\3\16\3\16\3\16\5\16\u008a\n\16\3\17\3" + "\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3\22\5\22\u0099" + "\n\22\3\22\3\22\7\22\u009d\n\22\f\22\16\22\u00a0\13\22\3\22\3\22\3\23" + "\6\23\u00a5\n\23\r\23\16\23\u00a6\3\23\3\23\3\23\3\23\5\23\u00ad\n\23" + "\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26\5\26\u00bb" + "\n\26\3\26\3\26\7\26\u00bf\n\26\f\26\16\26\u00c2\13\26\3\26\3\26\3\27" + "\7\27\u00c7\n\27\f\27\16\27\u00ca\13\27\3\27\3\27\3\30\3\30\3\30\5\30" + "\u00d1\n\30\3\30\3\30\3\31\3\31\5\31\u00d7\n\31\3\32\3\32\3\33\3\33\3" + "\34\3\34\3\34\3\34\3\34\3\34\3\34\7\34\u00e4\n\34\f\34\16\34\u00e7\13" + "\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\7\36\u00f4" + "\n\36\f\36\16\36\u00f7\13\36\3\36\3\36\3\36\2\2\37\2\4\6\b\n\f\16\20\22" + "\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:\2\b\3\2\24\25\3\2\3\f\3\2!!" + "\3\2!%\3\2\21\23\4\2\r\r\27\27\2\u00fd\2<\3\2\2\2\4A\3\2\2\2\6F\3\2\2" + "\2\bL\3\2\2\2\nN\3\2\2\2\fW\3\2\2\2\16Y\3\2\2\2\20[\3\2\2\2\22l\3\2\2" + "\2\24x\3\2\2\2\26{\3\2\2\2\30~\3\2\2\2\32\u0089\3\2\2\2\34\u008b\3\2\2" + "\2\36\u008e\3\2\2\2 \u0092\3\2\2\2\"\u0096\3\2\2\2$\u00ac\3\2\2\2&\u00ae" + "\3\2\2\2(\u00b6\3\2\2\2*\u00b8\3\2\2\2,\u00c8\3\2\2\2.\u00cd\3\2\2\2\60" + "\u00d6\3\2\2\2\62\u00d8\3\2\2\2\64\u00da\3\2\2\2\66\u00dc\3\2\2\28\u00ea" + "\3\2\2\2:\u00f0\3\2\2\2<=\t\2\2\2=\3\3\2\2\2>B\5\2\2\2?B\7*\2\2@B\5\6" + "\4\2A>\3\2\2\2A?\3\2\2\2A@\3\2\2\2B\5\3\2\2\2CE\t\3\2\2DC\3\2\2\2EH\3" + "\2\2\2FD\3\2\2\2FG\3\2\2\2G\7\3\2\2\2HF\3\2\2\2IM\5\26\f\2JM\5\20\t\2" + "KM\5\22\n\2LI\3\2\2\2LJ\3\2\2\2LK\3\2\2\2M\t\3\2\2\2NO\5\f\7\2OP\5\16" + "\b\2PQ\5\f\7\2Q\13\3\2\2\2RS\n\4\2\2SX\5\30\r\2TX\5\4\3\2UX\7(\2\2VX\7" + "&\2\2WR\3\2\2\2WT\3\2\2\2WU\3\2\2\2WV\3\2\2\2X\r\3\2\2\2YZ\t\5\2\2Z\17" + "\3\2\2\2[`\5\24\13\2\\]\7\35\2\2]_\7(\2\2^\\\3\2\2\2_b\3\2\2\2`^\3\2\2" + "\2`a\3\2\2\2ah\3\2\2\2b`\3\2\2\2cf\7\37\2\2dg\5\f\7\2eg\5\n\6\2fd\3\2" + "\2\2fe\3\2\2\2gi\3\2\2\2hc\3\2\2\2hi\3\2\2\2ij\3\2\2\2jk\7\36\2\2k\21" + "\3\2\2\2lq\7(\2\2mn\7\35\2\2np\7(\2\2om\3\2\2\2ps\3\2\2\2qo\3\2\2\2qr" + "\3\2\2\2rt\3\2\2\2sq\3\2\2\2tu\7\37\2\2uv\5\f\7\2vw\7\36\2\2w\23\3\2\2" + "\2xy\5\60\31\2yz\7(\2\2z\25\3\2\2\2{|\5\30\r\2|}\7\36\2\2}\27\3\2\2\2" + "~\u0083\5\32\16\2\177\u0080\7\34\2\2\u0080\u0082\5\34\17\2\u0081\177\3" + "\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084" + "\31\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u008a\5\34\17\2\u0087\u008a\5\36" + "\20\2\u0088\u008a\5 \21\2\u0089\u0086\3\2\2\2\u0089\u0087\3\2\2\2\u0089" + "\u0088\3\2\2\2\u008a\33\3\2\2\2\u008b\u008c\7(\2\2\u008c\u008d\5\"\22" + "\2\u008d\35\3\2\2\2\u008e\u008f\7(\2\2\u008f\u0090\7\34\2\2\u0090\u0091" + "\5\34\17\2\u0091\37\3\2\2\2\u0092\u0093\7\'\2\2\u0093\u0094\7\34\2\2\u0094" + "\u0095\5\34\17\2\u0095!\3\2\2\2\u0096\u0098\7\32\2\2\u0097\u0099\5$\23" + "\2\u0098\u0097\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u009e\3\2\2\2\u009a\u009b" + "\7\35\2\2\u009b\u009d\5$\23\2\u009c\u009a\3\2\2\2\u009d\u00a0\3\2\2\2" + "\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a1\3\2\2\2\u00a0\u009e" + "\3\2\2\2\u00a1\u00a2\7\33\2\2\u00a2#\3\2\2\2\u00a3\u00a5\7+\2\2\u00a4" + "\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a4\3\2\2\2\u00a6\u00a7\3\2" + "\2\2\u00a7\u00ad\3\2\2\2\u00a8\u00ad\5\4\3\2\u00a9\u00ad\7&\2\2\u00aa" + "\u00ad\5\32\16\2\u00ab\u00ad\7(\2\2\u00ac\u00a4\3\2\2\2\u00ac\u00a8\3" + "\2\2\2\u00ac\u00a9\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ab\3\2\2\2\u00ad" + "%\3\2\2\2\u00ae\u00af\5(\25\2\u00af\u00b0\5\60\31\2\u00b0\u00b1\7(\2\2" + "\u00b1\u00b2\5*\26\2\u00b2\u00b3\7\30\2\2\u00b3\u00b4\5,\27\2\u00b4\u00b5" + "\7\31\2\2\u00b5\'\3\2\2\2\u00b6\u00b7\t\6\2\2\u00b7)\3\2\2\2\u00b8\u00ba" + "\7\32\2\2\u00b9\u00bb\5\24\13\2\u00ba\u00b9\3\2\2\2\u00ba\u00bb\3\2\2" + "\2\u00bb\u00c0\3\2\2\2\u00bc\u00bd\7\35\2\2\u00bd\u00bf\5\24\13\2\u00be" + "\u00bc\3\2\2\2\u00bf\u00c2\3\2\2\2\u00c0\u00be\3\2\2\2\u00c0\u00c1\3\2" + "\2\2\u00c1\u00c3\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c3\u00c4\7\33\2\2\u00c4" + "+\3\2\2\2\u00c5\u00c7\5\26\f\2\u00c6\u00c5\3\2\2\2\u00c7\u00ca\3\2\2\2" + "\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00cb\3\2\2\2\u00ca\u00c8" + "\3\2\2\2\u00cb\u00cc\5.\30\2\u00cc-\3\2\2\2\u00cd\u00d0\7\26\2\2\u00ce" + "\u00d1\7(\2\2\u00cf\u00d1\5\4\3\2\u00d0\u00ce\3\2\2\2\u00d0\u00cf\3\2" + "\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d3\7\36\2\2\u00d3/\3\2\2\2\u00d4\u00d7" + "\5\62\32\2\u00d5\u00d7\7\'\2\2\u00d6\u00d4\3\2\2\2\u00d6\u00d5\3\2\2\2" + "\u00d7\61\3\2\2\2\u00d8\u00d9\t\7\2\2\u00d9\63\3\2\2\2\u00da\u00db\5\66" + "\34\2\u00db\65\3\2\2\2\u00dc\u00dd\7\17\2\2\u00dd\u00de\7\'\2\2\u00de" + "\u00e5\7\30\2\2\u00df\u00e4\58\35\2\u00e0\u00e4\5:\36\2\u00e1\u00e4\5" + "\20\t\2\u00e2\u00e4\5&\24\2\u00e3\u00df\3\2\2\2\u00e3\u00e0\3\2\2\2\u00e3" + "\u00e1\3\2\2\2\u00e3\u00e2\3\2\2\2\u00e4\u00e7\3\2\2\2\u00e5\u00e3\3\2" + "\2\2\u00e5\u00e6\3\2\2\2\u00e6\u00e8\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e8" + "\u00e9\7\31\2\2\u00e9\67\3\2\2\2\u00ea\u00eb\7\20\2\2\u00eb\u00ec\7&\2" + "\2\u00ec\u00ed\7\37\2\2\u00ed\u00ee\5\4\3\2\u00ee\u00ef\7\36\2\2\u00ef" + "9\3\2\2\2\u00f0\u00f1\7\21\2\2\u00f1\u00f5\7\30\2\2\u00f2\u00f4\5\b\5" + "\2\u00f3\u00f2\3\2\2\2\u00f4\u00f7\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f5\u00f6" + "\3\2\2\2\u00f6\u00f8\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f8\u00f9\7\31\2\2" + "\u00f9;\3\2\2\2\30AFLW`fhq\u0083\u0089\u0098\u009e\u00a6\u00ac\u00ba\u00c0" + "\u00c8\u00d0\u00d6\u00e3\u00e5\u00f5";
	public static final    ATN                    _ATN                = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	protected static final DFA[]                  _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
	private static final   String[]               _LITERAL_NAMES      = {null, "'0'", "'1'", "'2'", "'3'", "'4'", "'5'", "'6'", "'7'", "'8'", "'9'", "'string'", null, "'class'", "'const'", "'run'", "'mtd'", "'fnc'", "'true'", "'false'", "'return'", "'int'", "'{'", "'}'", "'('", "')'", "'.'", "','", "';'", "'='", "'?'", "'+'", "'-'", "'/'", "'*'", "'%'", null, null, null, "'\\\"'"};
	private static final   String[]               _SYMBOLIC_NAMES     = {null, null, null, null, null, null, null, null, null, null, null, null, "WS", "CLASS", "CONST", "RUN", "METHOD", "FUNCTION", "TRUE", "FALSE", "RETURN", "INT", "BODY_OPEN", "BODY_CLOSE", "PARAM_OPEN", "PARAM_CLOSE", "DOT", "SEPARATOR", "STATEMENT_END", "ASSIGN", "COMPARE", "PLUS", "MINUS", "DIVIDE", "MULTIPLY", "MODULUS", "CONSTNAME", "CLASSNAME", "VARNAME", "ESCAPED_QUOTE", "STRING", "DIGIT"};
	public static final    Vocabulary             VOCABULARY          = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	static {
		RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION);
	}

	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for(int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if(tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if(tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for(int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}

	public kdlParser(final TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@Override
	@Deprecated
	public String[] getTokenNames( ) {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary( ) {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName( ) {
		return "kdl.g4";
	}

	@Override
	public String[] getRuleNames( ) {
		return ruleNames;
	}

	@Override
	public String getSerializedATN( ) {
		return _serializedATN;
	}

	@Override
	public ATN getATN( ) {
		return _ATN;
	}

	public final BoolContext bool( ) throws RecognitionException {
		final BoolContext _localctx = new BoolContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_bool);
		final int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(58);
				_la = _input.LA(1);
				if(!(_la == TRUE || _la == FALSE)) {
					_errHandler.recoverInline(this);
				}
				else {
					if(_input.LA(1) == Token.EOF)
						matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final LiteralContext literal( ) throws RecognitionException {
		final LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_literal);
		try {
			setState(63);
			_errHandler.sync(this);
			switch(_input.LA(1)) {
				case TRUE:
				case FALSE:
					enterOuterAlt(_localctx, 1);
				{
					setState(60);
					bool();
				}
				break;
				case STRING:
					enterOuterAlt(_localctx, 2);
				{
					setState(61);
					match(STRING);
				}
				break;
				case T__0:
				case T__1:
				case T__2:
				case T__3:
				case T__4:
				case T__5:
				case T__6:
				case T__7:
				case T__8:
				case T__9:
				case PARAM_CLOSE:
				case SEPARATOR:
				case STATEMENT_END:
				case PLUS:
				case MINUS:
				case DIVIDE:
				case MULTIPLY:
				case MODULUS:
					enterOuterAlt(_localctx, 3);
				{
					setState(62);
					number();
				}
				break;
				default:
					throw new NoViableAltException(this);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final NumberContext number( ) throws RecognitionException {
		final NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0)) {
					{
						{
							setState(65);
							_la = _input.LA(1);
							if(!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0))) {
								_errHandler.recoverInline(this);
							}
							else {
								if(_input.LA(1) == Token.EOF)
									matchedEOF = true;
								_errHandler.reportMatch(this);
								consume();
							}
						}
					}
					setState(70);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final StatementContext statement( ) throws RecognitionException {
		final StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_statement);
		try {
			setState(74);
			_errHandler.sync(this);
			switch(getInterpreter().adaptivePredict(_input, 2, _ctx)) {
				case 1:
					enterOuterAlt(_localctx, 1);
				{
					setState(71);
					methodCallStatement();
				}
				break;
				case 2:
					enterOuterAlt(_localctx, 2);
				{
					setState(72);
					variableDeclaration();
				}
				break;
				case 3:
					enterOuterAlt(_localctx, 3);
				{
					setState(73);
					variableAssignment();
				}
				break;
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final MathExpressionContext mathExpression( ) throws RecognitionException {
		final MathExpressionContext _localctx = new MathExpressionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_mathExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(76);
				valueExpression();
				setState(77);
				operator();
				setState(78);
				valueExpression();
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final ValueExpressionContext valueExpression( ) throws RecognitionException {
		final ValueExpressionContext _localctx = new ValueExpressionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_valueExpression);
		final int _la;
		try {
			setState(85);
			_errHandler.sync(this);
			switch(getInterpreter().adaptivePredict(_input, 3, _ctx)) {
				case 1:
					enterOuterAlt(_localctx, 1);
				{
					setState(80);
					_la = _input.LA(1);
					if(_la <= 0 || (_la == PLUS)) {
						_errHandler.recoverInline(this);
					}
					else {
						if(_input.LA(1) == Token.EOF)
							matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(81);
					methodCallChain();
				}
				break;
				case 2:
					enterOuterAlt(_localctx, 2);
				{
					setState(82);
					literal();
				}
				break;
				case 3:
					enterOuterAlt(_localctx, 3);
				{
					setState(83);
					match(VARNAME);
				}
				break;
				case 4:
					enterOuterAlt(_localctx, 4);
				{
					setState(84);
					match(CONSTNAME);
				}
				break;
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final OperatorContext operator( ) throws RecognitionException {
		final OperatorContext _localctx = new OperatorContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_operator);
		final int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(87);
				_la = _input.LA(1);
				if(!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << DIVIDE) | (1L << MULTIPLY) | (1L << MODULUS))) != 0))) {
					_errHandler.recoverInline(this);
				}
				else {
					if(_input.LA(1) == Token.EOF)
						matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final VariableDeclarationContext variableDeclaration( ) throws RecognitionException {
		final VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_variableDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(89);
				typedVariable();
				setState(94);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while(_la == SEPARATOR) {
					{
						{
							setState(90);
							match(SEPARATOR);
							setState(91);
							match(VARNAME);
						}
					}
					setState(96);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(102);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if(_la == ASSIGN) {
					{
						setState(97);
						match(ASSIGN);
						setState(100);
						_errHandler.sync(this);
						switch(getInterpreter().adaptivePredict(_input, 5, _ctx)) {
							case 1: {
								setState(98);
								valueExpression();
							}
							break;
							case 2: {
								setState(99);
								mathExpression();
							}
							break;
						}
					}
				}

				setState(104);
				match(STATEMENT_END);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final VariableAssignmentContext variableAssignment( ) throws RecognitionException {
		final VariableAssignmentContext _localctx = new VariableAssignmentContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_variableAssignment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(106);
				match(VARNAME);
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while(_la == SEPARATOR) {
					{
						{
							setState(107);
							match(SEPARATOR);
							setState(108);
							match(VARNAME);
						}
					}
					setState(113);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(114);
				match(ASSIGN);
				setState(115);
				valueExpression();
				setState(116);
				match(STATEMENT_END);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final TypedVariableContext typedVariable( ) throws RecognitionException {
		final TypedVariableContext _localctx = new TypedVariableContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_typedVariable);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(118);
				type();
				setState(119);
				match(VARNAME);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final MethodCallStatementContext methodCallStatement( ) throws RecognitionException {
		final MethodCallStatementContext _localctx = new MethodCallStatementContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_methodCallStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(121);
				methodCallChain();
				setState(122);
				match(STATEMENT_END);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final MethodCallChainContext methodCallChain( ) throws RecognitionException {
		final MethodCallChainContext _localctx = new MethodCallChainContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_methodCallChain);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(124);
				methodCall();
				setState(129);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while(_la == DOT) {
					{
						{
							setState(125);
							match(DOT);
							setState(126);
							regularMethodCall();
						}
					}
					setState(131);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final MethodCallContext methodCall( ) throws RecognitionException {
		final MethodCallContext _localctx = new MethodCallContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_methodCall);
		try {
			setState(135);
			_errHandler.sync(this);
			switch(getInterpreter().adaptivePredict(_input, 9, _ctx)) {
				case 1:
					enterOuterAlt(_localctx, 1);
				{
					setState(132);
					regularMethodCall();
				}
				break;
				case 2:
					enterOuterAlt(_localctx, 2);
				{
					setState(133);
					objectiveMethodCall();
				}
				break;
				case 3:
					enterOuterAlt(_localctx, 3);
				{
					setState(134);
					staticMethodCall();
				}
				break;
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final RegularMethodCallContext regularMethodCall( ) throws RecognitionException {
		final RegularMethodCallContext _localctx = new RegularMethodCallContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_regularMethodCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(137);
				match(VARNAME);
				setState(138);
				parameterSet();
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final ObjectiveMethodCallContext objectiveMethodCall( ) throws RecognitionException {
		final ObjectiveMethodCallContext _localctx = new ObjectiveMethodCallContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_objectiveMethodCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(140);
				match(VARNAME);
				setState(141);
				match(DOT);
				setState(142);
				regularMethodCall();
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final StaticMethodCallContext staticMethodCall( ) throws RecognitionException {
		final StaticMethodCallContext _localctx = new StaticMethodCallContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_staticMethodCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(144);
				match(CLASSNAME);
				setState(145);
				match(DOT);
				setState(146);
				regularMethodCall();
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final ParameterSetContext parameterSet( ) throws RecognitionException {
		final ParameterSetContext _localctx = new ParameterSetContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_parameterSet);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(148);
				match(PARAM_OPEN);
				setState(150);
				_errHandler.sync(this);
				switch(getInterpreter().adaptivePredict(_input, 10, _ctx)) {
					case 1: {
						setState(149);
						parameter();
					}
					break;
				}
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while(_la == SEPARATOR) {
					{
						{
							setState(152);
							match(SEPARATOR);
							setState(153);
							parameter();
						}
					}
					setState(158);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(159);
				match(PARAM_CLOSE);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final ParameterContext parameter( ) throws RecognitionException {
		final ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_parameter);
		int _la;
		try {
			setState(170);
			_errHandler.sync(this);
			switch(getInterpreter().adaptivePredict(_input, 13, _ctx)) {
				case 1:
					enterOuterAlt(_localctx, 1);
				{
					setState(162);
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
							{
								setState(161);
								match(DIGIT);
							}
						}
						setState(164);
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while(_la == DIGIT);
				}
				break;
				case 2:
					enterOuterAlt(_localctx, 2);
				{
					setState(166);
					literal();
				}
				break;
				case 3:
					enterOuterAlt(_localctx, 3);
				{
					setState(167);
					match(CONSTNAME);
				}
				break;
				case 4:
					enterOuterAlt(_localctx, 4);
				{
					setState(168);
					methodCall();
				}
				break;
				case 5:
					enterOuterAlt(_localctx, 5);
				{
					setState(169);
					match(VARNAME);
				}
				break;
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final MethodDefinitionContext methodDefinition( ) throws RecognitionException {
		final MethodDefinitionContext _localctx = new MethodDefinitionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_methodDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(172);
				methodType();
				setState(173);
				type();
				setState(174);
				match(VARNAME);
				setState(175);
				parameterDefinition();
				setState(176);
				match(BODY_OPEN);
				setState(177);
				methodBody();
				setState(178);
				match(BODY_CLOSE);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final MethodTypeContext methodType( ) throws RecognitionException {
		final MethodTypeContext _localctx = new MethodTypeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_methodType);
		final int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(180);
				_la = _input.LA(1);
				if(!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RUN) | (1L << METHOD) | (1L << FUNCTION))) != 0))) {
					_errHandler.recoverInline(this);
				}
				else {
					if(_input.LA(1) == Token.EOF)
						matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final ParameterDefinitionContext parameterDefinition( ) throws RecognitionException {
		final ParameterDefinitionContext _localctx = new ParameterDefinitionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_parameterDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(182);
				match(PARAM_OPEN);
				setState(184);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << INT) | (1L << CLASSNAME))) != 0)) {
					{
						setState(183);
						typedVariable();
					}
				}

				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while(_la == SEPARATOR) {
					{
						{
							setState(186);
							match(SEPARATOR);
							setState(187);
							typedVariable();
						}
					}
					setState(192);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(193);
				match(PARAM_CLOSE);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final MethodBodyContext methodBody( ) throws RecognitionException {
		final MethodBodyContext _localctx = new MethodBodyContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_methodBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(198);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while(_la == CLASSNAME || _la == VARNAME) {
					{
						{
							setState(195);
							methodCallStatement();
						}
					}
					setState(200);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(201);
				returnStatement();
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final ReturnStatementContext returnStatement( ) throws RecognitionException {
		final ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_returnStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(203);
				match(RETURN);
				setState(206);
				_errHandler.sync(this);
				switch(_input.LA(1)) {
					case VARNAME: {
						setState(204);
						match(VARNAME);
					}
					break;
					case T__0:
					case T__1:
					case T__2:
					case T__3:
					case T__4:
					case T__5:
					case T__6:
					case T__7:
					case T__8:
					case T__9:
					case TRUE:
					case FALSE:
					case STATEMENT_END:
					case STRING: {
						setState(205);
						literal();
					}
					break;
					default:
						throw new NoViableAltException(this);
				}
				setState(208);
				match(STATEMENT_END);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final TypeContext type( ) throws RecognitionException {
		final TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_type);
		try {
			setState(212);
			_errHandler.sync(this);
			switch(_input.LA(1)) {
				case T__10:
				case INT:
					enterOuterAlt(_localctx, 1);
				{
					setState(210);
					basetype();
				}
				break;
				case CLASSNAME:
					enterOuterAlt(_localctx, 2);
				{
					setState(211);
					match(CLASSNAME);
				}
				break;
				default:
					throw new NoViableAltException(this);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final BasetypeContext basetype( ) throws RecognitionException {
		final BasetypeContext _localctx = new BasetypeContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_basetype);
		final int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(214);
				_la = _input.LA(1);
				if(!(_la == T__10 || _la == INT)) {
					_errHandler.recoverInline(this);
				}
				else {
					if(_input.LA(1) == Token.EOF)
						matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final SourceContext source( ) throws RecognitionException {
		final SourceContext _localctx = new SourceContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_source);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(216);
				clazz();
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final ClazzContext clazz( ) throws RecognitionException {
		final ClazzContext _localctx = new ClazzContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_clazz);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(218);
				match(CLASS);
				setState(219);
				match(CLASSNAME);
				setState(220);
				match(BODY_OPEN);
				setState(227);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << CONST) | (1L << RUN) | (1L << METHOD) | (1L << FUNCTION) | (1L << INT) | (1L << CLASSNAME))) != 0)) {
					{
						setState(225);
						_errHandler.sync(this);
						switch(getInterpreter().adaptivePredict(_input, 19, _ctx)) {
							case 1: {
								setState(221);
								constant();
							}
							break;
							case 2: {
								setState(222);
								run();
							}
							break;
							case 3: {
								setState(223);
								variableDeclaration();
							}
							break;
							case 4: {
								setState(224);
								methodDefinition();
							}
							break;
						}
					}
					setState(229);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(230);
				match(BODY_CLOSE);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final ConstantContext constant( ) throws RecognitionException {
		final ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_constant);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(232);
				match(CONST);
				setState(233);
				match(CONSTNAME);
				setState(234);
				match(ASSIGN);
				setState(235);
				literal();
				setState(236);
				match(STATEMENT_END);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public final RunContext run( ) throws RecognitionException {
		final RunContext _localctx = new RunContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_run);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(238);
				match(RUN);
				setState(239);
				match(BODY_OPEN);
				setState(243);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << INT) | (1L << CLASSNAME) | (1L << VARNAME))) != 0)) {
					{
						{
							setState(240);
							statement();
						}
					}
					setState(245);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(246);
				match(BODY_CLOSE);
			}
		} catch(final RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BoolContext extends ParserRuleContext {
		public BoolContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode TRUE( ) {
			return getToken(kdlParser.TRUE, 0);
		}

		public TerminalNode FALSE( ) {
			return getToken(kdlParser.FALSE, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_bool;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterBool(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitBool(this);
		}
	}

	public static class LiteralContext extends ParserRuleContext {
		public LiteralContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public BoolContext bool( ) {
			return getRuleContext(BoolContext.class, 0);
		}

		public TerminalNode STRING( ) {
			return getToken(kdlParser.STRING, 0);
		}

		public NumberContext number( ) {
			return getRuleContext(NumberContext.class, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_literal;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterLiteral(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitLiteral(this);
		}
	}

	public static class NumberContext extends ParserRuleContext {
		public NumberContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_number;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterNumber(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitNumber(this);
		}
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public MethodCallStatementContext methodCallStatement( ) {
			return getRuleContext(MethodCallStatementContext.class, 0);
		}

		public VariableDeclarationContext variableDeclaration( ) {
			return getRuleContext(VariableDeclarationContext.class, 0);
		}

		public VariableAssignmentContext variableAssignment( ) {
			return getRuleContext(VariableAssignmentContext.class, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_statement;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterStatement(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitStatement(this);
		}
	}

	public static class MathExpressionContext extends ParserRuleContext {
		public MathExpressionContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public List<ValueExpressionContext> valueExpression( ) {
			return getRuleContexts(ValueExpressionContext.class);
		}

		public ValueExpressionContext valueExpression(final int i) {
			return getRuleContext(ValueExpressionContext.class, i);
		}

		public OperatorContext operator( ) {
			return getRuleContext(OperatorContext.class, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_mathExpression;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterMathExpression(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitMathExpression(this);
		}
	}

	public static class ValueExpressionContext extends ParserRuleContext {
		public ValueExpressionContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public MethodCallChainContext methodCallChain( ) {
			return getRuleContext(MethodCallChainContext.class, 0);
		}

		public LiteralContext literal( ) {
			return getRuleContext(LiteralContext.class, 0);
		}

		public TerminalNode VARNAME( ) {
			return getToken(kdlParser.VARNAME, 0);
		}

		public TerminalNode CONSTNAME( ) {
			return getToken(kdlParser.CONSTNAME, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_valueExpression;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterValueExpression(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitValueExpression(this);
		}
	}

	public static class OperatorContext extends ParserRuleContext {
		public OperatorContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode PLUS( ) {
			return getToken(kdlParser.PLUS, 0);
		}

		public TerminalNode MINUS( ) {
			return getToken(kdlParser.MINUS, 0);
		}

		public TerminalNode DIVIDE( ) {
			return getToken(kdlParser.DIVIDE, 0);
		}

		public TerminalNode MULTIPLY( ) {
			return getToken(kdlParser.MULTIPLY, 0);
		}

		public TerminalNode MODULUS( ) {
			return getToken(kdlParser.MODULUS, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_operator;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterOperator(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitOperator(this);
		}
	}

	public static class VariableDeclarationContext extends ParserRuleContext {
		public VariableDeclarationContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TypedVariableContext typedVariable( ) {
			return getRuleContext(TypedVariableContext.class, 0);
		}

		public TerminalNode STATEMENT_END( ) {
			return getToken(kdlParser.STATEMENT_END, 0);
		}

		public List<TerminalNode> SEPARATOR( ) {
			return getTokens(kdlParser.SEPARATOR);
		}

		public TerminalNode SEPARATOR(final int i) {
			return getToken(kdlParser.SEPARATOR, i);
		}

		public List<TerminalNode> VARNAME( ) {
			return getTokens(kdlParser.VARNAME);
		}

		public TerminalNode VARNAME(final int i) {
			return getToken(kdlParser.VARNAME, i);
		}

		public TerminalNode ASSIGN( ) {
			return getToken(kdlParser.ASSIGN, 0);
		}

		public ValueExpressionContext valueExpression( ) {
			return getRuleContext(ValueExpressionContext.class, 0);
		}

		public MathExpressionContext mathExpression( ) {
			return getRuleContext(MathExpressionContext.class, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_variableDeclaration;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterVariableDeclaration(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitVariableDeclaration(this);
		}
	}

	public static class VariableAssignmentContext extends ParserRuleContext {
		public VariableAssignmentContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public List<TerminalNode> VARNAME( ) {
			return getTokens(kdlParser.VARNAME);
		}

		public TerminalNode VARNAME(final int i) {
			return getToken(kdlParser.VARNAME, i);
		}

		public TerminalNode ASSIGN( ) {
			return getToken(kdlParser.ASSIGN, 0);
		}

		public ValueExpressionContext valueExpression( ) {
			return getRuleContext(ValueExpressionContext.class, 0);
		}

		public TerminalNode STATEMENT_END( ) {
			return getToken(kdlParser.STATEMENT_END, 0);
		}

		public List<TerminalNode> SEPARATOR( ) {
			return getTokens(kdlParser.SEPARATOR);
		}

		public TerminalNode SEPARATOR(final int i) {
			return getToken(kdlParser.SEPARATOR, i);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_variableAssignment;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterVariableAssignment(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitVariableAssignment(this);
		}
	}

	public static class TypedVariableContext extends ParserRuleContext {
		public TypedVariableContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TypeContext type( ) {
			return getRuleContext(TypeContext.class, 0);
		}

		public TerminalNode VARNAME( ) {
			return getToken(kdlParser.VARNAME, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_typedVariable;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterTypedVariable(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitTypedVariable(this);
		}
	}

	public static class MethodCallStatementContext extends ParserRuleContext {
		public MethodCallStatementContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public MethodCallChainContext methodCallChain( ) {
			return getRuleContext(MethodCallChainContext.class, 0);
		}

		public TerminalNode STATEMENT_END( ) {
			return getToken(kdlParser.STATEMENT_END, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_methodCallStatement;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterMethodCallStatement(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitMethodCallStatement(this);
		}
	}

	public static class MethodCallChainContext extends ParserRuleContext {
		public MethodCallChainContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public MethodCallContext methodCall( ) {
			return getRuleContext(MethodCallContext.class, 0);
		}

		public List<TerminalNode> DOT( ) {
			return getTokens(kdlParser.DOT);
		}

		public TerminalNode DOT(final int i) {
			return getToken(kdlParser.DOT, i);
		}

		public List<RegularMethodCallContext> regularMethodCall( ) {
			return getRuleContexts(RegularMethodCallContext.class);
		}

		public RegularMethodCallContext regularMethodCall(final int i) {
			return getRuleContext(RegularMethodCallContext.class, i);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_methodCallChain;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterMethodCallChain(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitMethodCallChain(this);
		}
	}

	public static class MethodCallContext extends ParserRuleContext {
		public MethodCallContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public RegularMethodCallContext regularMethodCall( ) {
			return getRuleContext(RegularMethodCallContext.class, 0);
		}

		public ObjectiveMethodCallContext objectiveMethodCall( ) {
			return getRuleContext(ObjectiveMethodCallContext.class, 0);
		}

		public StaticMethodCallContext staticMethodCall( ) {
			return getRuleContext(StaticMethodCallContext.class, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_methodCall;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterMethodCall(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitMethodCall(this);
		}
	}

	public static class RegularMethodCallContext extends ParserRuleContext {
		public RegularMethodCallContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode VARNAME( ) {
			return getToken(kdlParser.VARNAME, 0);
		}

		public ParameterSetContext parameterSet( ) {
			return getRuleContext(ParameterSetContext.class, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_regularMethodCall;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterRegularMethodCall(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitRegularMethodCall(this);
		}
	}

	public static class ObjectiveMethodCallContext extends ParserRuleContext {
		public ObjectiveMethodCallContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode VARNAME( ) {
			return getToken(kdlParser.VARNAME, 0);
		}

		public TerminalNode DOT( ) {
			return getToken(kdlParser.DOT, 0);
		}

		public RegularMethodCallContext regularMethodCall( ) {
			return getRuleContext(RegularMethodCallContext.class, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_objectiveMethodCall;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterObjectiveMethodCall(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitObjectiveMethodCall(this);
		}
	}

	public static class StaticMethodCallContext extends ParserRuleContext {
		public StaticMethodCallContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode CLASSNAME( ) {
			return getToken(kdlParser.CLASSNAME, 0);
		}

		public TerminalNode DOT( ) {
			return getToken(kdlParser.DOT, 0);
		}

		public RegularMethodCallContext regularMethodCall( ) {
			return getRuleContext(RegularMethodCallContext.class, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_staticMethodCall;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterStaticMethodCall(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitStaticMethodCall(this);
		}
	}

	public static class ParameterSetContext extends ParserRuleContext {
		public ParameterSetContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode PARAM_OPEN( ) {
			return getToken(kdlParser.PARAM_OPEN, 0);
		}

		public TerminalNode PARAM_CLOSE( ) {
			return getToken(kdlParser.PARAM_CLOSE, 0);
		}

		public List<ParameterContext> parameter( ) {
			return getRuleContexts(ParameterContext.class);
		}

		public ParameterContext parameter(final int i) {
			return getRuleContext(ParameterContext.class, i);
		}

		public List<TerminalNode> SEPARATOR( ) {
			return getTokens(kdlParser.SEPARATOR);
		}

		public TerminalNode SEPARATOR(final int i) {
			return getToken(kdlParser.SEPARATOR, i);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_parameterSet;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterParameterSet(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitParameterSet(this);
		}
	}

	public static class ParameterContext extends ParserRuleContext {
		public ParameterContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public List<TerminalNode> DIGIT( ) {
			return getTokens(kdlParser.DIGIT);
		}

		public TerminalNode DIGIT(final int i) {
			return getToken(kdlParser.DIGIT, i);
		}

		public LiteralContext literal( ) {
			return getRuleContext(LiteralContext.class, 0);
		}

		public TerminalNode CONSTNAME( ) {
			return getToken(kdlParser.CONSTNAME, 0);
		}

		public MethodCallContext methodCall( ) {
			return getRuleContext(MethodCallContext.class, 0);
		}

		public TerminalNode VARNAME( ) {
			return getToken(kdlParser.VARNAME, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_parameter;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterParameter(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitParameter(this);
		}
	}

	public static class MethodDefinitionContext extends ParserRuleContext {
		public MethodDefinitionContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public MethodTypeContext methodType( ) {
			return getRuleContext(MethodTypeContext.class, 0);
		}

		public TypeContext type( ) {
			return getRuleContext(TypeContext.class, 0);
		}

		public TerminalNode VARNAME( ) {
			return getToken(kdlParser.VARNAME, 0);
		}

		public ParameterDefinitionContext parameterDefinition( ) {
			return getRuleContext(ParameterDefinitionContext.class, 0);
		}

		public MethodBodyContext methodBody( ) {
			return getRuleContext(MethodBodyContext.class, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_methodDefinition;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterMethodDefinition(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitMethodDefinition(this);
		}
	}

	public static class MethodTypeContext extends ParserRuleContext {
		public MethodTypeContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode METHOD( ) {
			return getToken(kdlParser.METHOD, 0);
		}

		public TerminalNode FUNCTION( ) {
			return getToken(kdlParser.FUNCTION, 0);
		}

		public TerminalNode RUN( ) {
			return getToken(kdlParser.RUN, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_methodType;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterMethodType(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitMethodType(this);
		}
	}

	public static class ParameterDefinitionContext extends ParserRuleContext {
		public ParameterDefinitionContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode PARAM_OPEN( ) {
			return getToken(kdlParser.PARAM_OPEN, 0);
		}

		public TerminalNode PARAM_CLOSE( ) {
			return getToken(kdlParser.PARAM_CLOSE, 0);
		}

		public List<TypedVariableContext> typedVariable( ) {
			return getRuleContexts(TypedVariableContext.class);
		}

		public TypedVariableContext typedVariable(final int i) {
			return getRuleContext(TypedVariableContext.class, i);
		}

		public List<TerminalNode> SEPARATOR( ) {
			return getTokens(kdlParser.SEPARATOR);
		}

		public TerminalNode SEPARATOR(final int i) {
			return getToken(kdlParser.SEPARATOR, i);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_parameterDefinition;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterParameterDefinition(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitParameterDefinition(this);
		}
	}

	public static class MethodBodyContext extends ParserRuleContext {
		public MethodBodyContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public ReturnStatementContext returnStatement( ) {
			return getRuleContext(ReturnStatementContext.class, 0);
		}

		public List<MethodCallStatementContext> methodCallStatement( ) {
			return getRuleContexts(MethodCallStatementContext.class);
		}

		public MethodCallStatementContext methodCallStatement(final int i) {
			return getRuleContext(MethodCallStatementContext.class, i);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_methodBody;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterMethodBody(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitMethodBody(this);
		}
	}

	public static class ReturnStatementContext extends ParserRuleContext {
		public ReturnStatementContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode RETURN( ) {
			return getToken(kdlParser.RETURN, 0);
		}

		public TerminalNode VARNAME( ) {
			return getToken(kdlParser.VARNAME, 0);
		}

		public LiteralContext literal( ) {
			return getRuleContext(LiteralContext.class, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_returnStatement;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterReturnStatement(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitReturnStatement(this);
		}
	}

	public static class TypeContext extends ParserRuleContext {
		public TypeContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public BasetypeContext basetype( ) {
			return getRuleContext(BasetypeContext.class, 0);
		}

		public TerminalNode CLASSNAME( ) {
			return getToken(kdlParser.CLASSNAME, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_type;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterType(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitType(this);
		}
	}

	public static class BasetypeContext extends ParserRuleContext {
		public BasetypeContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode INT( ) {
			return getToken(kdlParser.INT, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_basetype;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterBasetype(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitBasetype(this);
		}
	}

	public static class SourceContext extends ParserRuleContext {
		public SourceContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public ClazzContext clazz( ) {
			return getRuleContext(ClazzContext.class, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_source;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterSource(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitSource(this);
		}
	}

	public static class ClazzContext extends ParserRuleContext {
		public ClazzContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode CLASS( ) {
			return getToken(kdlParser.CLASS, 0);
		}

		public TerminalNode CLASSNAME( ) {
			return getToken(kdlParser.CLASSNAME, 0);
		}

		public TerminalNode BODY_OPEN( ) {
			return getToken(kdlParser.BODY_OPEN, 0);
		}

		public TerminalNode BODY_CLOSE( ) {
			return getToken(kdlParser.BODY_CLOSE, 0);
		}

		public List<ConstantContext> constant( ) {
			return getRuleContexts(ConstantContext.class);
		}

		public ConstantContext constant(final int i) {
			return getRuleContext(ConstantContext.class, i);
		}

		public List<RunContext> run( ) {
			return getRuleContexts(RunContext.class);
		}

		public RunContext run(final int i) {
			return getRuleContext(RunContext.class, i);
		}

		public List<VariableDeclarationContext> variableDeclaration( ) {
			return getRuleContexts(VariableDeclarationContext.class);
		}

		public VariableDeclarationContext variableDeclaration(final int i) {
			return getRuleContext(VariableDeclarationContext.class, i);
		}

		public List<MethodDefinitionContext> methodDefinition( ) {
			return getRuleContexts(MethodDefinitionContext.class);
		}

		public MethodDefinitionContext methodDefinition(final int i) {
			return getRuleContext(MethodDefinitionContext.class, i);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_clazz;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterClazz(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitClazz(this);
		}
	}

	public static class ConstantContext extends ParserRuleContext {
		public ConstantContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode CONST( ) {
			return getToken(kdlParser.CONST, 0);
		}

		public TerminalNode CONSTNAME( ) {
			return getToken(kdlParser.CONSTNAME, 0);
		}

		public TerminalNode ASSIGN( ) {
			return getToken(kdlParser.ASSIGN, 0);
		}

		public LiteralContext literal( ) {
			return getRuleContext(LiteralContext.class, 0);
		}

		public TerminalNode STATEMENT_END( ) {
			return getToken(kdlParser.STATEMENT_END, 0);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_constant;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterConstant(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitConstant(this);
		}
	}

	public static class RunContext extends ParserRuleContext {
		public RunContext(final ParserRuleContext parent, final int invokingState) {
			super(parent, invokingState);
		}

		public TerminalNode RUN( ) {
			return getToken(kdlParser.RUN, 0);
		}

		public TerminalNode BODY_OPEN( ) {
			return getToken(kdlParser.BODY_OPEN, 0);
		}

		public TerminalNode BODY_CLOSE( ) {
			return getToken(kdlParser.BODY_CLOSE, 0);
		}

		public List<StatementContext> statement( ) {
			return getRuleContexts(StatementContext.class);
		}

		public StatementContext statement(final int i) {
			return getRuleContext(StatementContext.class, i);
		}

		@Override
		public int getRuleIndex( ) {
			return kdlParser.RULE_run;
		}

		@Override
		public void enterRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).enterRun(this);
		}

		@Override
		public void exitRule(final ParseTreeListener listener) {
			if(listener instanceof kdlListener)
				((kdlListener) listener).exitRun(this);
		}
	}
}