/*
 * ClassSlurperSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.util;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * ClassSlurperクラスのテスト。
 * @version 1.0.00 2017/06/27
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClassSlurperSpec extends Specification {
	def '指定クラスに対応する設定ファイルを参照し、設定値を返します'(){
		given:
		ConfigObject constants = null;
		
		when:
		constants = ClassSlurper.getConfig(ClassSlurperSpec.class);
		then:
		constants.str == 'これはテストです。';
		constants.specialChars == '～①㈱';
		constants.number == 11.7;
		constants.some.list == [ 1, 2, 3 ];
		
		when:
		constants = ClassSlurper.getConfig(InternalClass.class);
		then:
		thrown(Throwable);
		
		when:
		constants = ClassSlurper.getConfig(String.class);
		then:
		constants == null;
		
		when:
		constants = ClassSlurper.getConfig(null);
		then:
		thrown(IllegalArgumentException);
	}
	
	def 'コンストラクタ'(){
		when:
		new ClassSlurper(null);
		then:
		thrown(IllegalArgumentException);
	}
	
	def '設定ファイルを参照し、設定値を返します'(){
		given:
		ConfigObject constants = null;
		
		when:
		constants = new ClassSlurper(ClassSlurperSpec.class).getConfig();
		then:
		constants.str == 'これはテストです。';
		constants.specialChars == '～①㈱';
		constants.number == 11.7;
		constants.some.list == [ 1, 2, 3 ];
	}
	
	/**
	 * 設定ファイルに文法誤りがあるときのテスト用の内部クラス。
	 */
	class InternalClass { }
}
