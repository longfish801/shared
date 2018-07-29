/*
 * PackageDirectorySpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * PackageDirectoryクラスのテスト。
 * @version 1.0.00 2018/07/01
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class PackageDirectorySpec extends Specification {
	/** ルートフォルダへのパス */
	static final String rootPath = 'src/test/resources';
	/** ルートフォルダ */
	static final File rootDir = new File(rootPath);
	
	def 'deepDir'(){
		given:
		File file;
		
		when:
		file = PackageDirectory.deepDir(rootDir, PackageDirectorySpec.class);
		then:
		file.name == 'PackageDirectorySpec';
		
		when:
		file = PackageDirectory.deepDir(rootPath, PackageDirectorySpec.class);
		then:
		file.name == 'PackageDirectorySpec';
	}
	
	def 'flatDir'(){
		given:
		File file;
		
		when:
		file = PackageDirectory.flatDir(rootDir, PackageDirectorySpec.class);
		then:
		file.name == 'io.github.longfish801.shared.PackageDirectorySpec';
		
		when:
		file = PackageDirectory.flatDir(rootPath, PackageDirectorySpec.class);
		then:
		file.name == 'io.github.longfish801.shared.PackageDirectorySpec';
	}
}
