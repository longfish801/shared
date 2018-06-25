/*
 * ExistResource.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.lang;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.util.TextUtil;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes
import org.apache.commons.io.IOUtils;

/**
 * 実在するほうのリソースを参照します。</br>
 * 通常はパッケージに対応するフォルダにリソースを格納します。</br>
 * パッケージルートに同じ名前のリソースがあると、そちらを参照します。</p>
 *
 * <p>次のような使い方を想定しています。</br>
 * デフォルト値を定義したプロパティファイルを JARファイルに格納します。</br>
 * このプロパティファイルは JARファイル内の、パッケージに対応するフォルダに格納します。<br>
 * ユーザが、デフォルト値とは異なる値を利用したいと考えたとします。
 * 値を編集したプロパティファイルを、クラスパスを通したフォルダ直下に置きます。<br>
 * これはパッケージルートに対応するフォルダのため、そちらのプロパティファイルを参照します。</br>
 * JARファイル内のプロパティファイルを編集しなくとも、簡単に上書きができます。</p>
 * @version 1.0.00 2017/06/16
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ExistResource {
	/** リソースの参照に利用するクラス */
	Class clazz = null;
	
	/**
	 * 指定クラスと名前に対応するリソースの入力ストリームを返します。<br>
	 * リソースがみつからない場合は WARNログを出力し、nullを返します。<br>
	 * 本メソッドはメンバ変数の宣言時などに利用することを想定しています。
	 * @param clazz クラス
	 * @param name リソース名（ドット始まりの場合は接尾辞を指定されたとみなします）
	 * @return リソース名に対応する入力ストリーム（リソースが存在しない場合は nullを返します）
	 * @see #stream(String)
	 */
	static InputStream stream(Class clazz, String name) {
		InputStream stream = new ExistResource(clazz).stream(name);
		if (stream == null) LOG.warn('リソースがみつかりません。clazz={}, name={}', clazz, name);
		return stream;
	}
	
	/**
	 * 指定クラスと名前に対応するリソースをパッケージルートあるいはパッケージから返します。<br>
	 * リソースがみつからない場合は WARNログを出力し、nullを返します。<br>
	 * 本メソッドはメンバ変数の宣言時などに利用することを想定しています。
	 * @param clazz クラス
	 * @param name リソース名（ドット始まりの場合は接尾辞を指定されたとみなします）
	 * @return リソースを読みこむためのURL（存在しない場合は nullを返します）
	 * @see #get(String)
	 */
	static URL get(Class clazz, String name){
		URL url = new ExistResource(clazz).get(name);
		if (url == null) LOG.warn('リソースがみつかりません。clazz={}, name={}', clazz, name);
		return url;
	}
	
	/**
	 * コンストラクタ。
	 * @param clazz リソースの参照に利用するクラス
	 */
	ExistResource(Class clazz){
		ArgmentChecker.checkNotNull('clazz', clazz);
		this.clazz = clazz;
	}
	
	/**
	 * 名前に対応するリソースの内容を文字列として参照します。<br>
	 * 文字コードはデフォルト（{@link Charset#defaultCharset()}）を使用します。
	 * @param name リソース名（ドット始まりの場合は接尾辞を指定されたとみなします）
	 * @return リソース内容の文字列（リソースが存在しない場合は nullを返します）
	 * @see #stream(String)
	 */
	String text(String name){
		return stream(name)?.withCloseable { InputStream stream -> IOUtils.toString(stream, Charset.defaultCharset()) };
	}
	
	/**
	 * 名前に対応するリソースの入力ストリームを返します。
	 * @param name リソース名（ドット始まりの場合は接尾辞を指定されたとみなします）
	 * @return リソースの入力ストリーム（リソースが存在しない場合は nullを返します）
	 * @see #get(String)
	 */
	InputStream stream(String name) {
		return get(name)?.openStream();
	}
	
	/**
	 * 名前に対応するリソースをパッケージルートあるいはパッケージから返します。<br>
	 * まずパッケージルートを探し、なければパッケージから探します。<br>
	 * どちらからもみつからなければ、nullを返します。<br>
	 * リソース名がドット(.)で始まる場合は接尾辞を指定されたとみなし、
	 * クラスの単純名＋接尾辞をリソース名とします。
	 * @param name リソース名（ドット始まりの場合は接尾辞を指定されたとみなします）
	 * @return リソースを読みこむためのURL（存在しない場合は nullを返します）
	 */
	URL get(String name){
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
	 * リソース名に対応するフォルダ配下のすべてのリソースをパッケージルートおよびパッケージから返します。<br>
	 * ファイル名のパターンに関係なく、すべてのファイルについて取得します。
	 * @param name リソース名
	 * @return 指定したリソースからの相対パスと、リソースを読みこむためのURLとのマップ
	 * @see #find(String,List<String>,List<String>)
	 */
	Map<String, URL> find(String name){
		return find(name, [], []);
	}
	
	/**
	 * リソース名に対応するフォルダ配下のすべてのリソースをパッケージルートおよびパッケージから返します。<br>
	 * 結果から除外するファイル名のパターンリストは空リストを指定します。
	 * @param name リソース名
	 * @param includePatterns 結果に含めるファイル名のパターンリスト（ワイルドカードを使用できます）
	 * @return 指定したリソースからの相対パスと、リソースを読みこむためのURLとのマップ
	 * @see #find(String,List<String>,List<String>)
	 */
	Map<String, URL> find(String name, List<String> includePatterns){
		return find(name, includePatterns, []);
	}
	
	/**
	 * リソース名に対応するフォルダ配下のすべてのリソースをパッケージルートおよびパッケージから返します。<br>
	 * フォルダからの相対パスが同じリソースが存在した場合は、パッケージルート配下を優先します。<br>
	 * リソース名に対応するリソースがまったく存在しない場合は空のマップを返します。<br>
	 * リソース名に対応するリソースがファイルだった場合は、そのファイルのリソースのみ返します。<br>
	 * ファイル名のパターンリストについてはクラス{@link CollectFileVisitor}にて利用します。
	 * @param name リソース名
	 * @param includePatterns ファイル名の適合パターンリスト
	 * @param excludePatterns ファイル名の除外パターンリスト
	 * @return 指定したリソースからの相対パスと、リソースを読みこむためのURLとのマップ
	 */
	Map<String, URL> find(String name, List<String> includePatterns, List<String> excludePatterns){
		ArgmentChecker.checkNotBlank('name', name);
		ArgmentChecker.checkNotNull('includePatterns', includePatterns);
		ArgmentChecker.checkNotNull('excludePatterns', excludePatterns);
		
		// 指定Pathインスタンス配下のリソースを探索し、ファイル名がパターンに適合するリソースのマップを返します
		Closure collectResourcesFromPath = { Path path ->
			CollectFileVisitor visitor = new CollectFileVisitor(path);
			visitor.includePatterns = includePatterns;
			visitor.excludePatterns = excludePatterns;
			Files.walkFileTree(path, visitor);
			return visitor.map;
		}
		
		// 指定URIが JARファイルを指すか否かによって Pathインスタンスに変換し、Path配下からリソースを収集します
		Closure collectResources = { URI uri ->
			Map<String, URL> map = [:];
			if (uri.scheme == 'jar'){
				FileSystems.newFileSystem(uri, [:]).withCloseable { FileSystem fileSystem ->
					Path path = fileSystem.getPath(uri.toURL().path.replaceFirst(/^file\:.+\!(\/.+)$/, '$1'));
					map = collectResourcesFromPath(path);
				}
			} else {
				map = collectResourcesFromPath(Paths.get(uri));
			}
			return map;
		}
		
		// パッケージ配下のリソース名に対応するフォルダからリソースを収集します
		URL url = clazz.getResource(name);
		Map resourceMap = [:];
		if (url != null) resourceMap += collectResources(url.toURI());
		
		// パッケージルート配下のリソース名に対応するフォルダからリソースを収集します
		ClassLoader loader = clazz.classLoader ?: ClassLoader.systemClassLoader;
		url = loader.getResource(name);
		if (url != null) resourceMap += collectResources(url.toURI());
		return resourceMap;
	}
	
	/**
	 * ファイル名がパターンに適合するファイルについて URLをマップに格納するクラスです。<br>
	 * マップのキーは、起点となる Pathからの相対パスです。
	 */
	class CollectFileVisitor extends SimpleFileVisitor<Path> {
		/** 起点となる Path */
		Path rootPath = null;
		/** ファイル名の適合パターンリスト */
		List<String> includePatterns = [];
		/** ファイル名の除外パターンリスト */
		List<String> excludePatterns = [];
		/** リソース名と URLのマップ */
		Map<String, URL> map = [:];
		
		/**
		 * コンストラクタ。
		 * @param rootPath 起点となる Path
		 */
		CollectFileVisitor(Path rootPath){
			ArgmentChecker.checkNotNull('rootPath', rootPath);
			this.rootPath = rootPath;
		}
		
		/**
		 * 探索しているファイルの名前がパターンリストを満たすならばマップに追加します。
		 * @param path 探索しているファイル
		 * @param attrs BasicFileAttributes
		 * @return FileVisitResult
		 * @see TextUtil#wildcardMatch(String,List<String>,List<String>)
		 */
		@Override
		FileVisitResult visitFile(Path path, BasicFileAttributes attrs){
			if (TextUtil.wildcardMatch(path.fileName.toString(), includePatterns, excludePatterns)){
				map[rootPath.relativize(path).toString()] = path.toUri().toURL();
			}
			return super.visitFile(path, attrs);
		}
	}
}
