// Generated from kdl.g4 by ANTLR 4.7.1

package main.com.xarql.kdl;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class kdlLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, WS=15, DIGIT=16, UPLETTER=17, 
		DNLETTER=18, LETTER=19, ALPHANUM=20, UNDERSCORE=21, CLASSNAME=22, PROCNAME=23, 
		CONSTNAME=24, ESCAPED_QUOTE=25;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "WS", "DIGIT", "UPLETTER", 
		"DNLETTER", "LETTER", "ALPHANUM", "UNDERSCORE", "CLASSNAME", "PROCNAME", 
		"CONSTNAME", "ESCAPED_QUOTE"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'\"'", "'\n'", "'\r'", "'true'", "'false'", "'boolean'", "'int'", 
		"'string'", "'class'", "'{'", "'}'", "'const'", "'='", "';'", null, null, 
		null, null, null, null, "'_'", null, null, null, "'\\\"'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, "WS", "DIGIT", "UPLETTER", "DNLETTER", "LETTER", "ALPHANUM", 
		"UNDERSCORE", "CLASSNAME", "PROCNAME", "CONSTNAME", "ESCAPED_QUOTE"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public kdlLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "kdl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\33\u009a\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\20\6\20o\n\20\r\20\16\20p\3\20"+
		"\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\5\24}\n\24\3\25\3\25\5\25"+
		"\u0081\n\25\3\26\3\26\3\27\3\27\6\27\u0087\n\27\r\27\16\27\u0088\3\30"+
		"\3\30\6\30\u008d\n\30\r\30\16\30\u008e\3\31\3\31\3\31\6\31\u0094\n\31"+
		"\r\31\16\31\u0095\3\32\3\32\3\32\2\2\33\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27"+
		"-\30/\31\61\32\63\33\3\2\6\5\2\13\f\17\17\"\"\3\2\62;\3\2C\\\3\2c|\2\u00a1"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\3\65\3\2\2\2\5\67\3\2\2\2\79\3\2\2\2\t;\3"+
		"\2\2\2\13@\3\2\2\2\rF\3\2\2\2\17N\3\2\2\2\21R\3\2\2\2\23Y\3\2\2\2\25_"+
		"\3\2\2\2\27a\3\2\2\2\31c\3\2\2\2\33i\3\2\2\2\35k\3\2\2\2\37n\3\2\2\2!"+
		"t\3\2\2\2#v\3\2\2\2%x\3\2\2\2\'|\3\2\2\2)\u0080\3\2\2\2+\u0082\3\2\2\2"+
		"-\u0084\3\2\2\2/\u008a\3\2\2\2\61\u0093\3\2\2\2\63\u0097\3\2\2\2\65\66"+
		"\7$\2\2\66\4\3\2\2\2\678\7\f\2\28\6\3\2\2\29:\7\17\2\2:\b\3\2\2\2;<\7"+
		"v\2\2<=\7t\2\2=>\7w\2\2>?\7g\2\2?\n\3\2\2\2@A\7h\2\2AB\7c\2\2BC\7n\2\2"+
		"CD\7u\2\2DE\7g\2\2E\f\3\2\2\2FG\7d\2\2GH\7q\2\2HI\7q\2\2IJ\7n\2\2JK\7"+
		"g\2\2KL\7c\2\2LM\7p\2\2M\16\3\2\2\2NO\7k\2\2OP\7p\2\2PQ\7v\2\2Q\20\3\2"+
		"\2\2RS\7u\2\2ST\7v\2\2TU\7t\2\2UV\7k\2\2VW\7p\2\2WX\7i\2\2X\22\3\2\2\2"+
		"YZ\7e\2\2Z[\7n\2\2[\\\7c\2\2\\]\7u\2\2]^\7u\2\2^\24\3\2\2\2_`\7}\2\2`"+
		"\26\3\2\2\2ab\7\177\2\2b\30\3\2\2\2cd\7e\2\2de\7q\2\2ef\7p\2\2fg\7u\2"+
		"\2gh\7v\2\2h\32\3\2\2\2ij\7?\2\2j\34\3\2\2\2kl\7=\2\2l\36\3\2\2\2mo\t"+
		"\2\2\2nm\3\2\2\2op\3\2\2\2pn\3\2\2\2pq\3\2\2\2qr\3\2\2\2rs\b\20\2\2s "+
		"\3\2\2\2tu\t\3\2\2u\"\3\2\2\2vw\t\4\2\2w$\3\2\2\2xy\t\5\2\2y&\3\2\2\2"+
		"z}\5#\22\2{}\5%\23\2|z\3\2\2\2|{\3\2\2\2}(\3\2\2\2~\u0081\5\'\24\2\177"+
		"\u0081\5!\21\2\u0080~\3\2\2\2\u0080\177\3\2\2\2\u0081*\3\2\2\2\u0082\u0083"+
		"\7a\2\2\u0083,\3\2\2\2\u0084\u0086\5#\22\2\u0085\u0087\5\'\24\2\u0086"+
		"\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2"+
		"\2\2\u0089.\3\2\2\2\u008a\u008c\5%\23\2\u008b\u008d\5)\25\2\u008c\u008b"+
		"\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f"+
		"\60\3\2\2\2\u0090\u0094\5#\22\2\u0091\u0094\5!\21\2\u0092\u0094\5+\26"+
		"\2\u0093\u0090\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0092\3\2\2\2\u0094\u0095"+
		"\3\2\2\2\u0095\u0093\3\2\2\2\u0095\u0096\3\2\2\2\u0096\62\3\2\2\2\u0097"+
		"\u0098\7^\2\2\u0098\u0099\7$\2\2\u0099\64\3\2\2\2\n\2p|\u0080\u0088\u008e"+
		"\u0093\u0095\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}