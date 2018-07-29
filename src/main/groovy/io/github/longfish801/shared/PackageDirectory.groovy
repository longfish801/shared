/*
 * PackageDirectory.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared;

import groovy.util.logging.Slf4j;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * パッケージに対応するフォルダです。
 * @version 1.0.00 2018/07/01
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class PackageDirectory {
	/**
	 * クラスの正規名に対応する階層のフォルダを返します。<br>
	 * たとえば rootDirが /foo/barで、clazzが io.github.Someoneならば、
	 * /foo/bar/io/github/Someoneフォルダを返します。<br>
	 * 戻り値となるフォルダの作成や存在チェックはしていません。
	 * @param rootDir ルートフォルダ
	 * @param clazz フォルダのパスに利用するクラス
	 * @return クラスの正規名に対応するフォルダ
	 */
	static File deepDir(File rootDir, Class clazz){
		ArgmentChecker.checkNotNull('clazz', clazz);
		return new File(rootDir, clazz.canonicalName.replaceAll(Pattern.quote('.'), Matcher.quoteReplacement(File.separator)));
	}
	
	/**
	 * クラスの正規名に対応する階層のフォルダを返します。
	 * @param rootPath ルートフォルダへのパス
	 * @param clazz フォルダのパスに利用するクラス
	 * @return クラスの正規名に対応するフォルダ
	 * @see #deepDir(File, Class)
	 */
	static File deepDir(String rootPath, Class clazz){
		return deepDir(new File(rootPath), clazz);
	}
	
	/**
	 * クラスの正規名に対応するフォルダを返します。<br>
	 * たとえば rootDirが /foo/barで、clazzが io.github.Someoneならば、
	 * /foo/bar/io.github.Someoneフォルダを返します。<br>
	 * 戻り値となるフォルダの作成や存在チェックはしていません。
	 * @param rootDir ルートフォルダ
	 * @param clazz フォルダのパスに利用するクラス
	 * @return クラスのパッケージ名に対応するフォルダ
	 */
	static File flatDir(File rootDir, Class clazz){
		ArgmentChecker.checkNotNull('clazz', clazz);
		return new File(rootDir, clazz.canonicalName);
	}
	
	/**
	 * クラスの正規名に対応するフォルダを返します。
	 * @param rootPath ルートフォルダへのパス
	 * @param clazz フォルダのパスに利用するクラス
	 * @return クラスのパッケージ名に対応するフォルダ
	 */
	static File flatDir(String rootPath, Class clazz){
		return flatDir(new File(rootPath), clazz);
	}
}
