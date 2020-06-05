// Generated from kdl.g4 by ANTLR 4.7.1

package main.com.xarql.kdl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

public class kdlLexer extends Lexer {
	public static final int T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, T__5 = 6, T__6 = 7, T__7 = 8, T__8 = 9, T__9 = 10, T__10 = 11, WS = 12, CLASS = 13, CONST = 14, RUN = 15, METHOD = 16, FUNCTION = 17, TRUE = 18, FALSE = 19, RETURN = 20, INT = 21, BODY_OPEN = 22, BODY_CLOSE = 23, PARAM_OPEN = 24, PARAM_CLOSE = 25, DOT = 26, SEPARATOR = 27, STATEMENT_END = 28, ASSIGN = 29, COMPARE = 30, PLUS = 31, MINUS = 32, DIVIDE = 33, MULTIPLY = 34, MODULUS = 35, CONSTNAME = 36, CLASSNAME = 37, VARNAME = 38, ESCAPED_QUOTE = 39, STRING = 40;
	public static final    String[]               ruleNames           = {"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", "T__9", "T__10", "WS", "CLASS", "CONST", "RUN", "METHOD", "FUNCTION", "TRUE", "FALSE", "RETURN", "INT", "BODY_OPEN", "BODY_CLOSE", "PARAM_OPEN", "PARAM_CLOSE", "DOT", "SEPARATOR", "STATEMENT_END", "ASSIGN", "COMPARE", "PLUS", "MINUS", "DIVIDE", "MULTIPLY", "MODULUS", "DIGIT", "UPLETTER", "DNLETTER", "LETTER", "ALPHANUM", "UNDERSCORE", "CONSTNAME", "CLASSNAME", "VARNAME", "ESCAPED_QUOTE", "STRING"};
	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final    String[]               tokenNames;
	public static final    String                 _serializedATN      = "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2*\u00fe\b\1\4\2\t" + "\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13" + "\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" + "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" + "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" + "\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4" + ",\t,\4-\t-\4.\t.\4/\t/\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7" + "\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\6\r" + "|\n\r\r\r\16\r}\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3" + "\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3" + "\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3" + "\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3" + "\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 " + "\3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\5(\u00d4\n(\3)\3" + ")\5)\u00d8\n)\3*\3*\3+\3+\3+\3+\6+\u00e0\n+\r+\16+\u00e1\3,\3,\6,\u00e6" + "\n,\r,\16,\u00e7\3-\3-\3-\7-\u00ed\n-\f-\16-\u00f0\13-\3.\3.\3.\3/\3/" + "\3/\7/\u00f8\n/\f/\16/\u00fb\13/\3/\3/\2\2\60\3\3\5\4\7\5\t\6\13\7\r\b" + "\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26" + "+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I\2K\2M\2O\2" + "Q\2S\2U&W\'Y([)]*\3\2\7\5\2\13\f\17\17\"\"\3\2\62;\3\2C\\\3\2c|\3\2$$" + "\2\u0102\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2" + "\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27" + "\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2" + "\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2" + "\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2" + "\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2" + "\2G\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\3_" + "\3\2\2\2\5a\3\2\2\2\7c\3\2\2\2\te\3\2\2\2\13g\3\2\2\2\ri\3\2\2\2\17k\3" + "\2\2\2\21m\3\2\2\2\23o\3\2\2\2\25q\3\2\2\2\27s\3\2\2\2\31{\3\2\2\2\33" + "\u0081\3\2\2\2\35\u0087\3\2\2\2\37\u008d\3\2\2\2!\u0091\3\2\2\2#\u0095" + "\3\2\2\2%\u0099\3\2\2\2\'\u009e\3\2\2\2)\u00a4\3\2\2\2+\u00ab\3\2\2\2" + "-\u00af\3\2\2\2/\u00b1\3\2\2\2\61\u00b3\3\2\2\2\63\u00b5\3\2\2\2\65\u00b7" + "\3\2\2\2\67\u00b9\3\2\2\29\u00bb\3\2\2\2;\u00bd\3\2\2\2=\u00bf\3\2\2\2" + "?\u00c1\3\2\2\2A\u00c3\3\2\2\2C\u00c5\3\2\2\2E\u00c7\3\2\2\2G\u00c9\3" + "\2\2\2I\u00cb\3\2\2\2K\u00cd\3\2\2\2M\u00cf\3\2\2\2O\u00d3\3\2\2\2Q\u00d7" + "\3\2\2\2S\u00d9\3\2\2\2U\u00db\3\2\2\2W\u00e3\3\2\2\2Y\u00e9\3\2\2\2[" + "\u00f1\3\2\2\2]\u00f4\3\2\2\2_`\7\62\2\2`\4\3\2\2\2ab\7\63\2\2b\6\3\2" + "\2\2cd\7\64\2\2d\b\3\2\2\2ef\7\65\2\2f\n\3\2\2\2gh\7\66\2\2h\f\3\2\2\2" + "ij\7\67\2\2j\16\3\2\2\2kl\78\2\2l\20\3\2\2\2mn\79\2\2n\22\3\2\2\2op\7" + ":\2\2p\24\3\2\2\2qr\7;\2\2r\26\3\2\2\2st\7u\2\2tu\7v\2\2uv\7t\2\2vw\7" + "k\2\2wx\7p\2\2xy\7i\2\2y\30\3\2\2\2z|\t\2\2\2{z\3\2\2\2|}\3\2\2\2}{\3" + "\2\2\2}~\3\2\2\2~\177\3\2\2\2\177\u0080\b\r\2\2\u0080\32\3\2\2\2\u0081" + "\u0082\7e\2\2\u0082\u0083\7n\2\2\u0083\u0084\7c\2\2\u0084\u0085\7u\2\2" + "\u0085\u0086\7u\2\2\u0086\34\3\2\2\2\u0087\u0088\7e\2\2\u0088\u0089\7" + "q\2\2\u0089\u008a\7p\2\2\u008a\u008b\7u\2\2\u008b\u008c\7v\2\2\u008c\36" + "\3\2\2\2\u008d\u008e\7t\2\2\u008e\u008f\7w\2\2\u008f\u0090\7p\2\2\u0090" + " \3\2\2\2\u0091\u0092\7o\2\2\u0092\u0093\7v\2\2\u0093\u0094\7f\2\2\u0094" + "\"\3\2\2\2\u0095\u0096\7h\2\2\u0096\u0097\7p\2\2\u0097\u0098\7e\2\2\u0098" + "$\3\2\2\2\u0099\u009a\7v\2\2\u009a\u009b\7t\2\2\u009b\u009c\7w\2\2\u009c" + "\u009d\7g\2\2\u009d&\3\2\2\2\u009e\u009f\7h\2\2\u009f\u00a0\7c\2\2\u00a0" + "\u00a1\7n\2\2\u00a1\u00a2\7u\2\2\u00a2\u00a3\7g\2\2\u00a3(\3\2\2\2\u00a4" + "\u00a5\7t\2\2\u00a5\u00a6\7g\2\2\u00a6\u00a7\7v\2\2\u00a7\u00a8\7w\2\2" + "\u00a8\u00a9\7t\2\2\u00a9\u00aa\7p\2\2\u00aa*\3\2\2\2\u00ab\u00ac\7k\2" + "\2\u00ac\u00ad\7p\2\2\u00ad\u00ae\7v\2\2\u00ae,\3\2\2\2\u00af\u00b0\7" + "}\2\2\u00b0.\3\2\2\2\u00b1\u00b2\7\177\2\2\u00b2\60\3\2\2\2\u00b3\u00b4" + "\7*\2\2\u00b4\62\3\2\2\2\u00b5\u00b6\7+\2\2\u00b6\64\3\2\2\2\u00b7\u00b8" + "\7\60\2\2\u00b8\66\3\2\2\2\u00b9\u00ba\7.\2\2\u00ba8\3\2\2\2\u00bb\u00bc" + "\7=\2\2\u00bc:\3\2\2\2\u00bd\u00be\7?\2\2\u00be<\3\2\2\2\u00bf\u00c0\7" + "A\2\2\u00c0>\3\2\2\2\u00c1\u00c2\7-\2\2\u00c2@\3\2\2\2\u00c3\u00c4\7/" + "\2\2\u00c4B\3\2\2\2\u00c5\u00c6\7\61\2\2\u00c6D\3\2\2\2\u00c7\u00c8\7" + ",\2\2\u00c8F\3\2\2\2\u00c9\u00ca\7\'\2\2\u00caH\3\2\2\2\u00cb\u00cc\t" + "\3\2\2\u00ccJ\3\2\2\2\u00cd\u00ce\t\4\2\2\u00ceL\3\2\2\2\u00cf\u00d0\t" + "\5\2\2\u00d0N\3\2\2\2\u00d1\u00d4\5K&\2\u00d2\u00d4\5M\'\2\u00d3\u00d1" + "\3\2\2\2\u00d3\u00d2\3\2\2\2\u00d4P\3\2\2\2\u00d5\u00d8\5O(\2\u00d6\u00d8" + "\5I%\2\u00d7\u00d5\3\2\2\2\u00d7\u00d6\3\2\2\2\u00d8R\3\2\2\2\u00d9\u00da" + "\7a\2\2\u00daT\3\2\2\2\u00db\u00df\5K&\2\u00dc\u00e0\5K&\2\u00dd\u00e0" + "\5I%\2\u00de\u00e0\5S*\2\u00df\u00dc\3\2\2\2\u00df\u00dd\3\2\2\2\u00df" + "\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00df\3\2\2\2\u00e1\u00e2\3\2" + "\2\2\u00e2V\3\2\2\2\u00e3\u00e5\5K&\2\u00e4\u00e6\5O(\2\u00e5\u00e4\3" + "\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8" + "X\3\2\2\2\u00e9\u00ee\5M\'\2\u00ea\u00ed\5O(\2\u00eb\u00ed\5I%\2\u00ec" + "\u00ea\3\2\2\2\u00ec\u00eb\3\2\2\2\u00ed\u00f0\3\2\2\2\u00ee\u00ec\3\2" + "\2\2\u00ee\u00ef\3\2\2\2\u00efZ\3\2\2\2\u00f0\u00ee\3\2\2\2\u00f1\u00f2" + "\7^\2\2\u00f2\u00f3\7$\2\2\u00f3\\\3\2\2\2\u00f4\u00f9\7$\2\2\u00f5\u00f8" + "\5[.\2\u00f6\u00f8\n\6\2\2\u00f7\u00f5\3\2\2\2\u00f7\u00f6\3\2\2\2\u00f8" + "\u00fb\3\2\2\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\u00fc\3\2" + "\2\2\u00fb\u00f9\3\2\2\2\u00fc\u00fd\7$\2\2\u00fd^\3\2\2\2\r\2}\u00d3" + "\u00d7\u00df\u00e1\u00e7\u00ec\u00ee\u00f7\u00f9\3\b\2\2";
	public static final    ATN                    _ATN                = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	protected static final DFA[]                  _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
	private static final   String[]               _LITERAL_NAMES      = {null, "'0'", "'1'", "'2'", "'3'", "'4'", "'5'", "'6'", "'7'", "'8'", "'9'", "'string'", null, "'class'", "'const'", "'run'", "'mtd'", "'fnc'", "'true'", "'false'", "'return'", "'int'", "'{'", "'}'", "'('", "')'", "'.'", "','", "';'", "'='", "'?'", "'+'", "'-'", "'/'", "'*'", "'%'", null, null, null, "'\\\"'"};
	private static final   String[]               _SYMBOLIC_NAMES     = {null, null, null, null, null, null, null, null, null, null, null, null, "WS", "CLASS", "CONST", "RUN", "METHOD", "FUNCTION", "TRUE", "FALSE", "RETURN", "INT", "BODY_OPEN", "BODY_CLOSE", "PARAM_OPEN", "PARAM_CLOSE", "DOT", "SEPARATOR", "STATEMENT_END", "ASSIGN", "COMPARE", "PLUS", "MINUS", "DIVIDE", "MULTIPLY", "MODULUS", "CONSTNAME", "CLASSNAME", "VARNAME", "ESCAPED_QUOTE", "STRING"};
	public static final    Vocabulary             VOCABULARY          = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
	public static          String[]               channelNames        = {"DEFAULT_TOKEN_CHANNEL", "HIDDEN"};
	public static          String[]               modeNames           = {"DEFAULT_MODE"};

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

	public kdlLexer(final CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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
	public String[] getChannelNames( ) {
		return channelNames;
	}

	@Override
	public String[] getModeNames( ) {
		return modeNames;
	}

	@Override
	public ATN getATN( ) {
		return _ATN;
	}
}