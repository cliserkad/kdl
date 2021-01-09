lexer grammar kdlLexer;

@header {
  package com.xarql.kdl.antlr;
}

// skip over comments in lexer
COMMENT: '//' .*? '\n' -> channel(HIDDEN);
BLOCK_COMMENT: '/*' .*? '*/' -> channel(HIDDEN);

// skip over whitespace
WS: [ \t\r\n]+ -> skip;

// quotes
QUOTE: '"';
STRING_LIT: QUOTE (~["\\] | '\\' .)* QUOTE;
CHAR_LIT: '\'' . '\'';

// keywords
TYPE: 'type';
CONST: 'const';
MAIN: 'main';
RETURN: 'return';
USE: 'use';
PATH: 'path';
IF: 'if';
ELSE: 'else';
NULL: 'null';
ASSERT: 'assert';
WHILE: 'while';
FOR: 'for';
THIS: 'this';

	// base types
	BOOLEAN: 'boolean';
	BYTE: 'byte';
	SHORT: 'short';
	CHAR: 'char';
	INT: 'int';
	FLOAT: 'float';
	LONG: 'long';
	DOUBLE: 'double';
	STRING: 'string';

	// boolean values
	TRUE: 'true';
	FALSE: 'false';

// syntax
CURL_L: '{';
CURL_R: '}';
PAREN_L: '(';
PAREN_R: ')';
BRACE_L: '[';
BRACE_R: ']';
DOT: '.';
SLASH: '/';
COMMA: ',';
SEMICOLON: ';';
COLON: ':';
TILDE: '~';
QUESTION_MARK: '?';

	// comparators
	NOT_EQUAL: '!=';
	EQUAL: '=';
	LESS_THAN: '<';
	MORE_THAN: '>';
	LESS_OR_EQUAL: '<=';
	MORE_OR_EQUAL: '>=';
	ADDRESS_EQUAL: '@';
	ADDRESS_NOT_EQUAL: '!@';
	IS_A: '#';
	IS_NOT_A: '!#';

	// operators
	PLUS: '+';
	MINUS: '-';
	MULTIPLY: '*';
	MODULUS: '%';
	NOT: '!';
	INCREMENT: '++';
	DECREMENT: '--';

		// bitwise
		BIT_SHIFT_LEFT: '<<';
        BIT_SHIFT_RIGHT: '>>';
        BIT_SHIFT_RIGHT_UNSIGNED: '>>>';
		BIT_AND: '&';
		BIT_OR: '|';
		BIT_XOR: '^';

	// appenders
	AND: '&&';
	OR: '||';
	XOR: '^^';

// misc
HEX_LIT: '0x' [0-9a-fA-F]*;
BIN_LIT: '0b' [01] [01,_]*;
NUMBER: [0-9] [0-9,_]*;
FRACTION: NUMBER DOT NUMBER;


// match anything that is unmatched and has no syntax characters
IDENTIFIER: ~([0123456789\r\t\n &|+<>=?!*.~:;,(){}'"/%]|'['|']') ~([\r\t\n &|+<>=?!*.~:;,(){}'"/%]|'['|']')*;
