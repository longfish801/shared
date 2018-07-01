/*
 * PackageDirectorySpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.lang;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * PackageDirectoryクラスのテスト。
 * @version 1.0.00 2018/07/01
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class PackageDirectorySpec extends Specification {
	/** ルートフォルダ */
	private static final File rootDir = new File('src/test/resources');
	
	def 'canonicalDir'(){
		given:
		File file;
		
		when:
		file = PackageDirectory.deepDir(rootDir, PackageDirectorySpec.class);
		then:
		file.name == 'PackageDirectorySpec';
	}
	
	def 'flatDir'(){
		given:
		File file;
		
		when:
		file = PackageDirectory.flatDir(rootDir, PackageDirectorySpec.class);
		then:
		file.name == 'io.github.longfish801.shared.lang.PackageDirectorySpec';
	}
}
