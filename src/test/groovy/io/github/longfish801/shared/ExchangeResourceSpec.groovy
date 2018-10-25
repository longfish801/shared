/*
 * ExchangeResourceSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared;

import groovy.util.logging.Slf4j;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import spock.lang.Specification;

/**
 * ExchangeResourceクラスのテスト。
 * @version 1.0.00 2017/06/16
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ExchangeResourceSpec extends Specification {
	def 'get'(){
		given:
		URL url;
		
		when: "パッケージから参照するか"
		url = ExchangeResource.url(ExchangeResourceSpec.class, 'ExchangeResourceSpec.txt');
		then:
		new File(url.getPath()).parentFile.name == 'shared';
		
		when: "パッケージルートから参照するか"
		url = ExchangeResource.url(ExchangeResourceSpec.class, 'ExchangeResourceSpec.config');
		then:
		new File(url.getPath()).parentFile.name == 'test';
		
		when: "接尾辞とみなしてパッケージから参照するか"
		url = ExchangeResource.url(ExchangeResourceSpec.class, '.txt');
		then:
		new File(url.getPath()).parentFile.name == 'shared';
		
		when: "接尾辞とみなしてパッケージルートから参照するか"
		url = ExchangeResource.url(ExchangeResourceSpec.class, '.config');
		then:
		new File(url.getPath()).parentFile.name == 'test';
		
		when: "存在しないリソース名"
		url = ExchangeResource.url(ExchangeResourceSpec.class, 'noSuchResource');
		then:
		url == null;
		
		when: "存在しない接尾辞"
		url = ExchangeResource.url(ExchangeResourceSpec.class, '.noSuchExt');
		then:
		url == null;
		
		when: "引数チェック"
		url = ExchangeResource.url(null, 'ExchangeResourceSpec.config');
		then:
		thrown(IllegalArgumentException);
		
		when: "引数チェック"
		url = ExchangeResource.url(ExchangeResourceSpec.class, '');
		then:
		thrown(IllegalArgumentException);
	}
	
	def 'stream'(){
		given:
		InputStream stream;
		Closure getText = { InputStream inpputStream ->
			return inpputStream?.withCloseable { IOUtils.toString(it, Charset.defaultCharset()) };
		}
		
		when: "パッケージから参照するか"
		stream = ExchangeResource.stream(ExchangeResourceSpec.class, 'ExchangeResourceSpec.txt');
		then:
		getText(stream) == 'This is TEST.';
		
		when: "接尾辞とみなしてパッケージルートから参照するか"
		stream = ExchangeResource.stream(ExchangeResourceSpec.class, '.config');
		then:
		getText(stream) == 'This is TEST.';
	}
	
	def 'text'(){
		given:
		String text;
		
		when: "パッケージから参照するか"
		text = ExchangeResource.text(ExchangeResourceSpec.class, 'ExchangeResourceSpec.txt');
		then:
		text == 'This is TEST.';
		
		when: "接尾辞とみなしてパッケージルートから参照するか"
		text = ExchangeResource.text(ExchangeResourceSpec.class, '.config');
		then:
		text == 'This is TEST.';
	}
	
	def 'config'(){
		given:
		ConfigObject cnst = null;
		
		when:
		cnst = ExchangeResource.config(ExchangeResourceSpec.class);
		then:
		cnst.str == 'これはテストです。';
		cnst.specialChars == '～①㈱';
		cnst.number == 11.7;
		cnst.some.list == [ 1, 2, 3 ];
		
		when:
		cnst = ExchangeResource.config(String.class);
		then:
		cnst == null;
	}
}
