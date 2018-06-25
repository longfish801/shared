/*
 * ExistResourceSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.lang;

import groovy.util.logging.Slf4j;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import spock.lang.Specification;

/**
 * ExistResourceクラスのテスト。
 * @version 1.0.00 2017/06/16
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ExistResourceSpec extends Specification {
	def '指定クラスと名前に対応するリソースの入力ストリームを返します'(){
		given:
		InputStream stream;
		Closure getText = { InputStream inpputStream ->
			return inpputStream?.withCloseable { IOUtils.toString(it, Charset.defaultCharset()) };
		}
		
		when:
		stream = ExistResource.stream(ExistResourceSpec.class, 'ExistResourceSpec.txt');
		then:
		getText(stream) == 'This is TEST.';
		
		when:
		stream = ExistResource.stream(ExistResourceSpec.class, '.config');
		then:
		getText(stream) == 'This is TEST.';
		
		when:
		stream = ExistResource.stream(ExistResourceSpec.class, 'noSuchResource');
		then:
		stream == null;
		
		when:
		stream = ExistResource.stream(ExistResourceSpec.class, '.noSuchExt');
		then:
		stream == null;
		
		when:
		ExistResource.stream(null, '.config');
		then:
		thrown(IllegalArgumentException);
		
		when:
		ExistResource.stream(ExistResourceSpec.class, ' ');
		then:
		thrown(IllegalArgumentException);
	}
	
	def '指定クラスと名前に対応するリソースをパッケージルートあるいはパッケージから返します'(){
		given:
		URL url;
		
		when:
		url = ExistResource.get(ExistResourceSpec.class, 'ExistResourceSpec.txt');
		then:
		new File(url.getPath()).parentFile.name == 'lang';
		
		when:
		url = ExistResource.get(ExistResourceSpec.class, 'ExistResourceSpec.config');
		then:
		new File(url.getPath()).parentFile.name == 'test';
		
		when:
		url = ExistResource.get(ExistResourceSpec.class, '.txt');
		then:
		new File(url.getPath()).parentFile.name == 'lang';
		
		when:
		url = ExistResource.get(ExistResourceSpec.class, '.config');
		then:
		new File(url.getPath()).parentFile.name == 'test';
		
		when:
		url = ExistResource.get(ExistResourceSpec.class, 'noSuchResource');
		then:
		url == null;
		
		when:
		url = ExistResource.get(ExistResourceSpec.class, '.noSuchExt');
		then:
		url == null;
		
		when:
		url = ExistResource.get(null, 'ExistResourceSpec.config');
		then:
		thrown(IllegalArgumentException);
		
		when:
		url = ExistResource.get(ExistResourceSpec.class, '');
		then:
		thrown(IllegalArgumentException);
	}
	
	def 'コンストラクタ'(){
		when:
		new ExistResource(ExistResourceSpec.class);
		then:
		noExceptionThrown()
		
		when:
		new ExistResource(null);
		then:
		thrown(IllegalArgumentException);
	}
	
	def '名前に対応するリソースの内容を文字列として参照します'(){
		given:
		ExistResource existResource = new ExistResource(ExistResourceSpec.class);
		String text = null;
		
		when:
		text = existResource.text('ExistResourceSpec.txt');
		then:
		text == 'This is TEST.';
		
		when:
		text = existResource.text('.config');
		then:
		text == 'This is TEST.';
	}
	
	def '名前に対応するリソースの入力ストリームを返します'(){
		given:
		ExistResource existResource = new ExistResource(ExistResourceSpec.class);
		InputStream stream;
		Closure getText = { InputStream inpputStream ->
			return inpputStream?.withCloseable { IOUtils.toString(it, Charset.defaultCharset()) };
		}
		
		when:
		stream = existResource.stream('ExistResourceSpec.txt');
		then:
		getText(stream) == 'This is TEST.';
		
		when:
		stream = existResource.stream('.config');
		then:
		getText(stream) == 'This is TEST.';
		
		when:
		stream = existResource.stream('noSuchResource');
		then:
		stream == null;
		
		when:
		stream = existResource.stream('.noSuchExt');
		then:
		stream == null;
		
		when:
		existResource.stream('');
		then:
		thrown(IllegalArgumentException);
	}
	
	def '名前に対応するリソースをパッケージルートあるいはパッケージから返します'(){
		given:
		ExistResource existResource = new ExistResource(ExistResourceSpec.class);
		URL url = null;
		
		when:
		url = existResource.get('ExistResourceSpec.txt');
		then:
		new File(url.getPath()).parentFile.name == 'lang';
		
		when:
		url = existResource.get('ExistResourceSpec.config');
		then:
		new File(url.getPath()).parentFile.name == 'test';
		
		when:
		url = existResource.get('.txt');
		then:
		new File(url.getPath()).parentFile.name == 'lang';
		
		when:
		url = existResource.get('.config');
		then:
		new File(url.getPath()).parentFile.name == 'test';
		
		when:
		url = existResource.get('noSuchResource');
		then:
		url == null;
		
		when:
		url = existResource.get('noSuchExt');
		then:
		url == null;
		
		when:
		existResource.get('');
		then:
		thrown(IllegalArgumentException);
		
		when:
		existResource.get('');
		then:
		thrown(IllegalArgumentException);
	}
	
	def 'リソース名に対応するフォルダ配下のすべてのリソースをパッケージルートおよびパッケージから返します'(){
		given:
		ExistResource existResource = new ExistResource(ExistResourceSpec.class);
		Map<String, URL> map;
		
		when:
		map = existResource.find('existResouceTest');
		then:
		map.sort() == [
			'a.txt': new URL('file:/C:/own/cur/_gstart/longfish801/shared/build/resources/test/existResouceTest/a.txt'),
			'b.txt': new URL('file:/C:/own/cur/_gstart/longfish801/shared/build/resources/test/existResouceTest/b.txt'),
			'c.txt': new URL('file:/C:/own/cur/_gstart/longfish801/shared/build/resources/test/io/github/longfish801/shared/lang/existResouceTest/c.txt'),
		];
		
		when:
		map = existResource.find('existResouceTest', [ 'a.*', 'c.txt' ]);
		then:
		map.sort() == [
			'a.txt': new URL('file:/C:/own/cur/_gstart/longfish801/shared/build/resources/test/existResouceTest/a.txt'),
			'c.txt': new URL('file:/C:/own/cur/_gstart/longfish801/shared/build/resources/test/io/github/longfish801/shared/lang/existResouceTest/c.txt'),
		];
		
		when:
		map = existResource.find('existResouceTest', [ ], [ 'c.*' ]);
		then:
		map.sort() == [
			'a.txt': new URL('file:/C:/own/cur/_gstart/longfish801/shared/build/resources/test/existResouceTest/a.txt'),
			'b.txt': new URL('file:/C:/own/cur/_gstart/longfish801/shared/build/resources/test/existResouceTest/b.txt')
		];
		
		when:
		existResource.find('', []);
		then:
		thrown(IllegalArgumentException);
		
		when:
		existResource.find('dummy', null);
		then:
		thrown(IllegalArgumentException);
	}
}
