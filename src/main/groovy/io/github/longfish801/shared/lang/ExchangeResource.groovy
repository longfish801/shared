/*
 * ExchangeResource.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.lang;

import groovy.util.logging.Slf4j;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;

/**
 * ユーザによる差し替えが可能なリソースです。</br>
 * 通常はパッケージに対応するフォルダにリソースを格納します。</br>
 * パッケージルートに同じ名前のリソースがあると、そちらを参照します。</p>
 *
 * <p>たとえば次のような使い方を想定しています。</br>
 * デフォルト値を定義したプロパティファイルを JARファイルに格納します。</br>
 * このプロパティファイルはクラスファイルと同様に、JARファイル内の、
 * パッケージ名に対応する階層のフォルダに格納します。<br>
 * ユーザが、デフォルト値とは異なる値を利用したいと考えたとします。<br>
 * 値を編集したプロパティファイルを、クラスパスを通したフォルダ直下に置きます。<br>
 * これはパッケージルートに対応するフォルダに相当します。<br>
 * 次回の実行からは、JARファイル内にプロパティファイルが存在しても、
 * パッケージルートに対応するフォルダ内のプロパティファイルを参照します。
 * @version 1.0.00 2017/06/16
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ExchangeResource {
	/** リソースバンドル */
	protected static final ResourceBundle RSRC = ResourceBundle.getBundle(ExchangeResource.class.canonicalName, Locale.default, ExchangeResource.class.classLoader);
	/** ConfigSlurper */
	static ConfigSlurper slurper = new ConfigSlurper();
	
	/**
	 * 指定クラスと名前に対応するリソースをパッケージルートあるいはパッケージから返します。<br>
	 * まずパッケージルートを探し、なければパッケージから探します。<br>
	 * どちらからもみつからなければ、nullを返します。<br>
	 * リソース名がドット(.)で始まる場合は接尾辞とみなし、
	 * クラスの単純名({@link Class#getSimpleName()})＋接尾辞をリソース名とします。
	 * @param clazz リソースの参照に利用するクラス
	 * @param name リソース名（ドット始まりの場合は接尾辞を指定されたとみなします）
	 * @return リソースを読みこむためのURL（存在しない場合は nullを返します）
	 */
	static URL url(Class clazz, String name){
		ArgmentChecker.checkNotNull('clazz', clazz);
		ArgmentChecker.checkNotBlank('name', name);
		
		// ドット始まりの場合はクラスの単純名＋接尾辞をリソース名とします
		if (name.startsWith('.')) name = clazz.simpleName + name;
		
		// パッケージルートからリソースを参照します
		ClassLoader loader = clazz.classLoader ?: ClassLoader.systemClassLoader;
		URL url = loader.getResource(name);
		if (url != null){
			LOG.debug('パッケージルートからリソースを参照しました。clazz={}, name={}, url={}', clazz, name, url);
			return url;
		}
		
		// クラスが属するパッケージに対応するフォルダからリソースを参照します
		url = clazz.getResource(name);
		if (url != null){
			LOG.debug('パッケージからリソースを参照しました。clazz={}, name={}, url={}', clazz, name, url);
		} else {
			LOG.debug('リソースがみつかりません。clazz={}, name={}', clazz, name);
		}
		return url;
	}
	
	/**
	 * 指定クラスと名前に対応するリソースの入力ストリームを返します。
	 * @param clazz リソースの参照に利用するクラス
	 * @param name リソース名（ドット始まりの場合は接尾辞を指定されたとみなします）
	 * @return リソース名に対応する入力ストリーム（リソースが存在しない場合は nullを返します）
	 * @see #get(Class,String)
	 */
	static InputStream stream(Class clazz, String name) {
		return ExchangeResource.url(clazz, name)?.openStream();
	}
	
	/**
	 * 指定クラスと名前に対応するリソースの内容を文字列として参照します。<br>
	 * 文字コードは {@link Charset#defaultCharset()}を使用します。
	 * @param clazz リソースの参照に利用するクラス
	 * @param name リソース名（ドット始まりの場合は接尾辞を指定されたとみなします）
	 * @return リソース内容の文字列（リソースが存在しない場合は nullを返します）
	 * @see #stream(Class,String)
	 */
	static String text(Class clazz, String name){
		return ExchangeResource.stream(clazz, name)?.withCloseable { InputStream stream -> IOUtils.toString(stream, Charset.defaultCharset()) };
	}
	
	/**
	 * 指定クラスと名前に対応するリソース上の設定ファイルを参照し、設定値を返します。<br>
	 * リソース名はクラスの単純名＋拡張子「.groovy」です。
	 * @return ConfigObject（設定ファイルが存在しなければ nullを返します）
	 */
	static ConfigObject config(Class clazz){
		URL url = ExchangeResource.url(clazz, RSRC.getString('EXTENSION'));
		if (url == null) return null;
		return slurper.parse(url);
	}
}
