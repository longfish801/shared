/*
 * ArgmentCheckerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * ArgmentCheckerクラスのテスト。
 * @version 1.0.00 2017/06/24
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ArgmentCheckerSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	static final File testDir = PackageDirectory.deepDir(new File('src/test/resources'), ArgmentCheckerSpec.class);
	
	def 'checkNotNull'(){
		given:
		IllegalArgumentException exc = null;
		
		when:
		ArgmentChecker.checkNotNull('引数', null);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数にnullは指定できません。';
		
		when:
		ArgmentChecker.checkNotNull('引数', 'abc');
		then:
		noExceptionThrown();
	}
	
	def 'checkClass'(){
		given:
		IllegalArgumentException exc = null;
		
		when:
		ArgmentChecker.checkClass('引数', null, String.class);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数にnullは指定できません。';
		
		when:
		ArgmentChecker.checkClass('引数', 123, null);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == 'クラスにnullは指定できません。';
		
		when:
		ArgmentChecker.checkClass('引数', 123, String.class);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数が妥当なクラスではありません。arg=[123], 妥当なクラス=class java.lang.String, 引数のクラス=class java.lang.Integer';
		
		when:
		ArgmentChecker.checkClass('引数', 'abc', String.class);
		then:
		noExceptionThrown();
	}
	
	def 'checkClasses'(){
		given:
		IllegalArgumentException exc = null;
		
		when:
		ArgmentChecker.checkClasses('引数', null, [ String.class ]);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数にnullは指定できません。';
		
		when:
		ArgmentChecker.checkClasses('引数', 123, []);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '空のクラス候補を指定するのは不正です。';
		
		when:
		ArgmentChecker.checkClasses('引数', 123, [ String.class ]);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数が妥当なクラスではありません。arg=[123], 妥当なクラス候補=[class java.lang.String], 引数のクラス=class java.lang.Integer';
		
		when:
		ArgmentChecker.checkClasses('引数', 'abc', [ String.class ]);
		then:
		noExceptionThrown();
	}
	
	def 'checkNotEmpty'(){
		given:
		IllegalArgumentException exc = null;
		
		when:
		ArgmentChecker.checkNotEmpty('引数', null);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数にnullは指定できません。';
		
		when:
		ArgmentChecker.checkNotEmpty('引数', '');
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数に空文字を指定するのは不正です。arg=[]';
		
		when:
		ArgmentChecker.checkNotEmpty('引数', ' ');
		then:
		noExceptionThrown()
	}
	
	def 'checkNotBlank'(){
		given:
		IllegalArgumentException exc = null;
		
		when:
		ArgmentChecker.checkNotBlank('引数', null);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数にnullは指定できません。';
		
		when:
		ArgmentChecker.checkNotBlank('引数', '');
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数に空白文字を指定するのは不正です。arg=[]';
		
		when:
		ArgmentChecker.checkNotBlank('引数', ' ');
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数に空白文字を指定するのは不正です。arg=[ ]';
		
		when:
		ArgmentChecker.checkNotBlank('引数', ' 　\t');
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数に空白文字を指定するのは不正です。arg=[ 　\t]';
	}
	
	def 'checkMatchRex'(){
		given:
		IllegalArgumentException exc = null;
		
		when:
		ArgmentChecker.checkMatchRex('引数', null, /[^#]+/);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数にnullは指定できません。';
		
		when:
		ArgmentChecker.checkMatchRex('引数', '', /[^#]+/);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数が正規表現を満たさず不正です。arg=[], rex=[^#]+';
		
		when:
		ArgmentChecker.checkMatchRex('引数', '', /[^#]*/);
		then:
		noExceptionThrown()
		
		when:
		ArgmentChecker.checkMatchRex('引数', 'a#b', /[^#]+/);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数が正規表現を満たさず不正です。arg=[a#b], rex=[^#]+';
	}
	
	def 'checkNotEmptyList'(){
		given:
		IllegalArgumentException exc = null;
		
		when:
		ArgmentChecker.checkNotEmptyList('引数', null);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数にnullは指定できません。';
		
		when:
		ArgmentChecker.checkNotEmptyList('引数', []);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '空の引数を指定するのは不正です。';
	}
	
	def 'checkNotEmptyMap'(){
		given:
		IllegalArgumentException exc = null;
		
		when:
		ArgmentChecker.checkNotEmptyMap('引数', null);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数にnullは指定できません。';
		
		when:
		ArgmentChecker.checkNotEmptyMap('引数', [:]);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '空の引数を指定するのは不正です。';
	}
	
	def 'checkUniqueKey'(){
		given:
		IllegalArgumentException exc = null;
		Map map = [ 'abc' : 123 ];
		
		when:
		ArgmentChecker.checkUniqueKey('引数', 'def', null);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == 'マップにnullは指定できません。';
		
		when:
		ArgmentChecker.checkUniqueKey('引数', null, map);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数にnullは指定できません。';
		
		when:
		ArgmentChecker.checkUniqueKey('引数', 'abc', map);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数は他のキーと重複するため不正です。arg=[abc], map=[abc:123]';
		
		when:
		ArgmentChecker.checkUniqueKey('引数', 'def', map);
		then:
		noExceptionThrown();
	}
	
	def 'checkExistFile'(){
		given:
		IllegalArgumentException exc = null;
		
		when:
		ArgmentChecker.checkExistFile('引数', null);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数にnullは指定できません。';
		
		when:
		ArgmentChecker.checkExistFile('引数', new File(testDir, 'test.txt'));
		then:
		noExceptionThrown();
		
		when:
		ArgmentChecker.checkExistFile('引数', new File(testDir, 'noSuchFile.txt'));
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == /引数は存在しないファイルです。arg=[src\test\resources\io\github\longfish801\shared\ArgmentCheckerSpec\noSuchFile.txt]/;
	}
	
	def 'checkExistDirectory'(){
		given:
		IllegalArgumentException exc = null;
		
		when:
		ArgmentChecker.checkExistDirectory('引数', null);
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == '引数にnullは指定できません。';
		
		when:
		ArgmentChecker.checkExistDirectory('引数', testDir);
		then:
		noExceptionThrown();
		
		when:
		ArgmentChecker.checkExistDirectory('引数', new File(testDir, 'test.txt'));
		then:
		exc = thrown(IllegalArgumentException);
		exc.message == /引数は存在しないフォルダです。arg=[src\test\resources\io\github\longfish801\shared\ArgmentCheckerSpec\test.txt]/;
	}
}
