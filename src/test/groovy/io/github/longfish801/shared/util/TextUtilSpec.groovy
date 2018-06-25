/*
 * TextUtilSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.util.ClassDirectory;
import spock.lang.Specification;
import spock.lang.Unroll;

/**
 * TextUtilのテスト。
 * @version 1.0.00 2017/07/06
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class TextUtilSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	private static final File testDir = new ClassDirectory('src/test/resources').getDeepDir(TextUtilSpec.class);
	
	def '指定された文字列を、改行コードを区切り文字とみなして文字列のリストに変換します'(){
		given:
		List result = null;
		List expected = null;
		
		when:
		result = TextUtil.parseTextLines('''\
			|あああ
			|いいい
			|ううう'''.stripMargin());
		expected = [ 'あああ', 'いいい', 'ううう' ];
		then:
		result == expected;
		
		when:
		result = TextUtil.parseTextLines('''\
			|あああ
			|いいい
			|ううう
			|'''.stripMargin());
		expected = [ 'あああ', 'いいい', 'ううう' ];
		then:
		result == expected;
		
		when:
		result = TextUtil.parseTextLines('''\
			|あああ
			|
			|ううう
			|'''.stripMargin());
		expected = [ 'あああ', '', 'ううう' ];
		then:
		result == expected;
		
		when:
		result = TextUtil.parseTextLines('''\
			|あああ
			|
			|
			|
			|ううう
			|'''.stripMargin());
		expected = [ 'あああ', '', '', '', 'ううう' ];
		then:
		result == expected;
		
		when:
		result = TextUtil.parseTextLines('''\
			|
			|いいい
			|
			|'''.stripMargin());
		expected = [ '', 'いいい' ];
		then:
		result == expected;
		
		when:
		result = TextUtil.parseTextLines('');
		expected = [ ];
		then:
		result == expected;
		
		when:
		result = TextUtil.parseTextLines('''\
			|
			|'''.stripMargin());
		expected = [ ];
		then:
		result == expected;
		
		when:
		result = TextUtil.parseTextLines(null);
		then:
		thrown(IllegalArgumentException);
	}
	
	def '各行の先頭に行番号を付与して返します'(){
		given:
		String result = null;
		String expected = null;
		
		when:
		result = TextUtil.addLineNo(new File(testDir, 'input.txt').getText());
		expected = new File(testDir, 'output.txt').getText();
		then:
		result == expected;
		
		when:
		result = TextUtil.addLineNo('');
		then:
		result == '';
		
		when:
		result = TextUtil.addLineNo('a');
		then:
		result == '1 a';
		
		when:
		result = TextUtil.addLineNo('''\
			あ
			い
			う'''.stripIndent());
		expected = '''\
			1 あ
			2 い
			3 う'''.stripIndent().replaceAll("\n", System.getProperty('line.separator'));
		then:
		result == expected;
		
		when:
		result = TextUtil.addLineNo('''\
			
			2
			'''.stripIndent());
		expected = '''\
			1 
			2 2'''.stripIndent().replaceAll("\n", System.getProperty('line.separator'));
		then:
		result == expected;
		
		when:
		result = TextUtil.addLineNo('''\
			
			
			3
			4'''.stripIndent());
		expected = '''\
			1 
			2 
			3 3
			4 4'''.stripIndent().replaceAll("\n", System.getProperty('line.separator'));
		then:
		result == expected;
		
		when:
		result = TextUtil.addLineNo('''\
			1
			
			
			4'''.stripIndent());
		expected = '''\
			1 1
			2 
			3 
			4 4'''.stripIndent().replaceAll("\n", System.getProperty('line.separator'));
		then:
		result == expected;
		
		when:
		result = TextUtil.addLineNo('''\
			|
			|
			|
			|'''.stripMargin());
		then:
		result == '';
		
		when:
		result = TextUtil.addLineNo('''\
			|1
			|
			|
			| 
			|
			|
			|
			|
			|
			|10'''.stripMargin());
		expected = '''\
			01 1
			02 
			03 
			04  
			05 
			06 
			07 
			08 
			09 
			10 10'''.stripIndent().replaceAll("\n", System.getProperty('line.separator'));
		then:
		result == expected;
	}
	
	@Unroll
	def '区切り文字自身を含めた文字列の分割がされること'(){
		expect:
		TextUtil.split(str, delim) == expect as String[];
		
		where:
		str			| delim	|| expect
		'@ab@@cde@'	| '@'	|| ['@', 'ab', '@', '@', 'cde', '@']
		'@ab@@cde@'	| '@+'	|| ['@', 'ab', '@@', 'cde', '@']
	}
	
	@Unroll
	def '指定された文字列が、ワイルドカードを用いたパターンを満たすか判定します'(){
		expect:
		TextUtil.wildcardMatch(str, includePatterns, excludePatterns) == expect;
		
		where:
		str				| includePatterns		| excludePatterns		|| expect
		'sample.txt'	| []					| []					|| true
		'sample.txt'	| [ '*.txt' ]			| []					|| true
		'sample.txt'	| [ '*.txt', '*.xml' ]	| []					|| true
		'sample.txt'	| [ '*.xml' ]			| []					|| false
		'sample.txt'	| [ ]					| [ '*.txt' ]			|| false
		'sample.txt'	| [ ]					| [ '*.txt', '*.xml' ]	|| false
		'sample.txt'	| [ ]					| [ '*.xml' ]			|| true
		'sample.txt'	| [ '*.txt' ]			| [ '*.xml' ]			|| true
		'sample.txt'	| [ '*.txt', '*.xml' ]	| [ 'sample.*' ]		|| false
		'sample.txt'	| [ 'sample.*' ]		| [ '*.txt' ]			|| false
		'sample.txt'	| [ 'sample.*' ]		| [ '*.txt', '*.xml' ]	|| false
	}
}
