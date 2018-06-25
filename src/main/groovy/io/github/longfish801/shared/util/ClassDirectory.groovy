/*
 * ClassDirectory.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 指定クラスの所属パッケージに対応するフォルダを返します。
 * @version 1.0.00 2017/06/27
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClassDirectory {
	/** ルートフォルダ */
	File rootDir = null;
	
	/**
	 * コンストラクタ。<br>
	 * カレントフォルダをルートフォルダにします。
	 */
	ClassDirectory(){
		rootDir = new File('.');
	}
	
	/**
	 * コンストラクタ。
	 * @param rootDir ルートフォルダ
	 */
	ClassDirectory(File rootDir){
		ArgmentChecker.checkNotNull('rootDir', rootDir);
		this.rootDir = rootDir;
	}
	
	/**
	 * コンストラクタ。
	 * @param rootPath ルートフォルダへのパス
	 */
	ClassDirectory(String rootPath){
		ArgmentChecker.checkNotBlank('rootPath', rootPath);
		rootDir = new File(rootPath);
	}
	
	/**
	 * 指定クラスの正規名に対応する階層構造の配下にあるフォルダを返します。<br>
	 * 正規名の区切り文字(.)をシステム固有のパス区切り文字に置換し、
	 * ルートフォルダからの相対位置にあるフォルダを返します。
	 * @param clazz クラス
	 * @return クラスの正規名に対応する階層構造の配下にあるフォルダ
	 */
	File getDeepDir(Class clazz) {
		ArgmentChecker.checkNotNull('clazz', clazz);
		String path = clazz.canonicalName.replaceAll(Pattern.quote('.'), Matcher.quoteReplacement(File.separator));
		return new File(rootDir, path);
	}
	
	/**
	 * 指定クラスの正規名をフォルダ名とするフォルダを返します。<br>
	 * ルートフォルダを親フォルダ、正規名をフォルダ名とするフォルダを返します。
	 * @param clazz クラス
	 * @return クラスの正規名をフォルダ名とするフォルダ
	 */
	File getFlatDir(Class clazz) {
		ArgmentChecker.checkNotNull('clazz', clazz);
		return new File(rootDir, clazz.canonicalName);
	}
}
