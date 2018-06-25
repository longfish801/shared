/*
 * ClassDirectorySpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.util;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * ClassDirectoryのテスト。
 * @version 1.0.00 2017/06/27
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClassDirectorySpec extends Specification {
	def 'コンストラクタ'(){
		when:
		new ClassDirectory();
		then:
		noExceptionThrown()
		
		when:
		new ClassDirectory('src/test/resources');
		then:
		noExceptionThrown()
		
		when:
		File rootDir = null;
		new ClassDirectory(rootDir as File);
		then:
		thrown(IllegalArgumentException);
		
		when:
		new ClassDirectory('src/test/resources');
		then:
		noExceptionThrown()
		
		when:
		new ClassDirectory('');
		then:
		thrown(IllegalArgumentException);
	}
	
	def '指定クラスの正規名に対応する階層構造の配下にあるフォルダを返します'(){
		when:
		File dir = new ClassDirectory().getDeepDir(ClassDirectorySpec.class);
		then:
		dir.name == 'ClassDirectorySpec';
		
		when:
		new ClassDirectory().getDeepDir(null);
		then:
		thrown(IllegalArgumentException);
	}
	
	def '指定クラスの正規名をフォルダ名とするフォルダを返します'(){
		when:
		File dir = new ClassDirectory().getFlatDir(ClassDirectorySpec.class);
		then:
		dir.name == 'io.github.longfish801.shared.util.ClassDirectorySpec';
		
		when:
		new ClassDirectory().getFlatDir(null);
		then:
		thrown(IllegalArgumentException);
	}
}
