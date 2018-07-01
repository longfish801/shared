/*
 * ArgmentChecker.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.lang;

import org.apache.commons.lang3.StringUtils;

/**
 * 引数をチェックします。
 * @version 1.0.00 2017/06/24
 * @author io.github.longfish801
 */
class ArgmentChecker {
	/**
	 * 引数が nullではないことをチェックします。</br>
	 * 不正な値の場合は IllegalArgumentExceptionをスローします。
	 * @param name 引数の名前
	 * @param arg 引数
	 * @throws IllegalArgumentException ${name}にnullにnullは指定できません。
	 */
	static void checkNotNull(String name, Object arg) {
		if (arg == null) throw new IllegalArgumentException("${name}にnullは指定できません。");
	}
	
	/**
	 * 引数が指定されたクラスのインスタンスかチェックします。</br>
	 * 不正な値の場合は IllegalArgumentExceptionをスローします。
	 * @param name 引数の名前
	 * @param arg 引数（nullは不正）
	 * @param clazz クラス（nullは不正）
	 * @throws IllegalArgumentException ${name}は他のキーと重複するため不正です。
	 */
	static void checkClass(String name, Object arg, Class clazz) {
		checkNotNull('クラス', clazz);
		checkNotNull(name, arg);
		if (!(clazz.isInstance(arg))){
			throw new IllegalArgumentException("${name}が妥当なクラスではありません。arg=[${arg}], 妥当なクラス=${clazz}, 引数のクラス=${arg.getClass()}");
		}
	}
	
	/**
	 * 引数が指定されたクラス候補のいずれかのインスタンスであることをチェックします。</br>
	 * 不正な値の場合は IllegalArgumentExceptionをスローします。
	 * @param name 引数の名前
	 * @param arg 引数（nullは不正）
	 * @param classes クラス候補（nullは不正）
	 * @throws IllegalArgumentException ${name}は他のキーと重複するため不正です。
	 */
	static void checkClasses(String name, Object arg, List<Class> classes) {
		checkNotEmptyList('クラス候補', classes);
		checkNotNull(name, arg);
		if (classes.every { !(it.isInstance(arg)) }){
			throw new IllegalArgumentException("${name}が妥当なクラスではありません。arg=[${arg}], 妥当なクラス候補=${classes}, 引数のクラス=${arg.getClass()}");
		}
	}
	
	/**
	 * 引数が空文字ではないことをチェックします。</br>
	 * 不正な値の場合は IllegalArgumentExceptionをスローします。
	 * @param name 引数の名前
	 * @param arg 引数
	 * @throws IllegalArgumentException ${name}に空文字を指定するのは不正です。
	 * @see StringUtils#isEmpty(CharSequence)
	 */
	static void checkNotEmpty(String name, String arg) {
		checkNotNull(name, arg);
		if (StringUtils.isEmpty(arg)) throw new IllegalArgumentException("${name}に空文字を指定するのは不正です。arg=[${arg}]");
	}
	
	/**
	 * 引数が空文字、空白文字から成る文字列ではないことをチェックします。</br>
	 * 不正な値の場合は IllegalArgumentExceptionをスローします。
	 * @param name 引数の名前
	 * @param arg 引数
	 * @throws IllegalArgumentException ${name}に空白文字を指定するのは不正です。
	 * @see StringUtils#isBlank(CharSequence)
	 */
	static void checkNotBlank(String name, String arg) {
		checkNotNull(name, arg);
		if (StringUtils.isBlank(arg)) throw new IllegalArgumentException("${name}に空白文字を指定するのは不正です。arg=[${arg}]");
	}
	
	/**
	 * 引数が正規表現を満たす文字列であることをチェックします。</br>
	 * 不正な値の場合は IllegalArgumentExceptionをスローします。
	 * @param name 引数の名前
	 * @param arg 引数（nullは不正）
	 * @param rex 正規表現文字列
	 * @throws IllegalArgumentException ${name}が正規表現を満たさず不正です。
	 */
	static void checkMatchRex(String name, String arg, String rex) {
		checkNotNull(name, arg);
		if (!(arg ==~ rex)) throw new IllegalArgumentException("${name}が正規表現を満たさず不正です。arg=[${arg}], rex=${rex}");
	}
	
	/**
	 * リストが空ではないことをチェックします。</br>
	 * 不正な値の場合は IllegalArgumentExceptionをスローします。
	 * @param name 引数の名前
	 * @param arg 引数（nullは不正）
	 * @throws IllegalArgumentException 空の${name}を指定するのは不正です。
	 */
	static void checkNotEmptyList(String name, List arg) {
		checkNotNull(name, arg);
		if (arg.size() == 0) throw new IllegalArgumentException("空の${name}を指定するのは不正です。");
	}
	
	/**
	 * マップが空ではないことをチェックします。</br>
	 * 不正な値の場合は IllegalArgumentExceptionをスローします。
	 * @param name 引数の名前
	 * @param arg 引数（nullは不正）
	 * @throws IllegalArgumentException 空の${name}を指定するのは不正です。
	 */
	static void checkNotEmptyMap(String name, Map arg) {
		checkNotNull(name, arg);
		if (arg.size() == 0) throw new IllegalArgumentException("空の${name}を指定するのは不正です。");
	}
	
	/**
	 * 引数がマップのキーとして存在しないことをチェックします。</br>
	 * 不正な値の場合は IllegalArgumentExceptionをスローします。
	 * @param name 引数の名前
	 * @param arg 引数（nullは不正）
	 * @param map マップ（nullは不正）
	 * @throws IllegalArgumentException ${name}は他のキーと重複するため不正です。
	 */
	static void checkUniqueKey(String name, Object arg, Map map) {
		checkNotNull('マップ', map);
		checkNotNull(name, arg);
		if (map.containsKey(arg)) throw new IllegalArgumentException("${name}は他のキーと重複するため不正です。arg=[${arg}], map=${map}");
	}
	
	/**
	 * 引数が存在するファイルであることをチェックします。</br>
	 * 存在するファイルではない場合は IllegalArgumentExceptionをスローします。
	 * @param name 引数の名前
	 * @param arg 引数（nullは不正）
	 * @throws IllegalArgumentException ${name}は存在しないファイルです。
	 */
	static void checkExistFile(String name, File arg) {
		checkNotNull(name, arg);
		if (!arg.isFile()) throw new IllegalArgumentException("${name}は存在しないファイルです。arg=[${arg}]");
	}
	
	/**
	 * 引数が存在するフォルダであることをチェックします。</br>
	 * 存在するフォルダではない場合は IllegalArgumentExceptionをスローします。
	 * @param name 引数の名前
	 * @param arg 引数（nullは不正）
	 * @throws IllegalArgumentException ${name}は存在しないフォルダです。
	 */
	static void checkExistDirectory(String name, File arg) {
		checkNotNull(name, arg);
		if (!arg.isDirectory()) throw new IllegalArgumentException("${name}は存在しないフォルダです。arg=[${arg}]");
	}
}
