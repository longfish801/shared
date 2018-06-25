/*
 * ClassSlurper.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;
import io.github.longfish801.shared.lang.ExistResource;

/**
 * リソース上の設定ファイルに定義した値を返します。<br>
 * 設定値は{@link ConfigObject}として返します。<br>
 * 設定ファイルは、{@link ConfigSlurper#parse(URL)}で解析可能な Groovyスクリプトです。<br>
 * 設定ファイルをクラスの単純名＋拡張子「.groovy」をリソース名として{@link ExistResource}で参照します。
 * @version 1.0.00 2017/06/27
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClassSlurper {
	/** リソースバンドル */
	protected static ResourceBundle RSRC = ResourceBundle.getBundle(ClassSlurper.class.canonicalName, Locale.default, ClassSlurper.class.classLoader);
	/** ConfigSlurper */
	static ConfigSlurper slurper = new ConfigSlurper();
	/** クラス */
	Class clazz = null;
	
	/**
	 * 指定クラスに対応する設定ファイルを参照し、設定値を返します。<br>
	 * 設定ファイルが存在しない場合は WARNログを出力し、nullを返します。
	 * @param clazz クラス
	 * @return ConfigObject（参照に失敗した場合は null）
	 * @see #getConfig()
	 */
	static ConfigObject getConfig(Class clazz){
		ConfigObject config = new ClassSlurper(clazz).getConfig();
		if (config == null) LOG.warn('設定ファイルが存在しません。clazz={}', clazz);
		return config;
	}
	
	/**
	 * コンストラクタ。
	 * @param clazz Class
	 */
	ClassSlurper(Class clazz){
		ArgmentChecker.checkNotNull('clazz', clazz);
		this.clazz = clazz;
	}
	
	/**
	 * 設定ファイルを参照し、設定値を返します。<br>
	 * リソース名はクラスの単純名＋拡張子「.groovy」です。
	 * @return ConfigObject（設定ファイルが存在しなければ nullを返します）
	 */
	ConfigObject getConfig(){
		URL url = new ExistResource(clazz).get(RSRC.getString('EXTENSION'));
		ConfigObject config = null;
		try {
			if (url != null) config = slurper.parse(url);
		} catch (Throwable exc){
			LOG.error('設定ファイルの解析時に問題が発生しました。url={}', url);
			throw exc;
		}
		return config;
	}
}
