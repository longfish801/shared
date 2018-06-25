/*
 * ConfigXmlSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.util;

import groovy.util.logging.Slf4j;

import spock.lang.Specification;
import spock.lang.Unroll;

/**
 * ConfigXmlのテスト。
 * 
 * @version 1.0.00 2017/07/11
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ConfigXmlSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	private static final File testDir = new ClassDirectory('src/test/resources').getDeepDir(ConfigXmlSpec.class);
	
	@Unroll
	def 'ConfigObjectを文字出力ストリームにXML形式で書きこみます'(){
		when:
		Closure outputXml = { String fname ->
			ConfigObject config = new ConfigSlurper().parse(new File(testDir, "${fname}.groovy").toURL());
			StringWriter writer = new StringWriter();
			use(ConfigXml){ config.outputXml(writer) }
			return writer.toString();
		}
		
		then:
		outputXml(target) == new File(testDir, "${expect}.xml").getText();
		
		where:
		target	|| expect
		'01'	|| '01'	// 最小要素
		'02'	|| '02'	// 各データ型
		'03'	|| '03'	// 階層
	}
	
	@Unroll
	def '文字入力ストリームからXMLを解析し ConfigObjectに読みこみます'(){
		when:
		Closure loadXml = { String fname ->
			ConfigObject config = new ConfigObject();
			StringReader reader = new StringReader(new File(testDir, "${target}.xml").getText());
			use(ConfigXml){ config.loadXml(reader) }
			return config;
		}
		Closure loadConfig = { String fname ->
			return new ConfigSlurper().parse(new File(testDir, "${fname}.groovy").toURL());
		}
		
		then:
		loadXml(target).equals(loadConfig(expect)) == true;
		
		where:
		target	| expect
		'01'	| '01'	// 最小要素
		'02'	| '02'	// 各データ型
		'03'	| '03'	// 階層
	}
}
