/*
 * TextUtil.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

/**
 * 文字列に対する汎用的な処理です。
 * @version 1.0.00 2017/07/06
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class TextUtil {
	/**
	 * 指定された文字列を、改行コードを区切り文字とみなして文字列のリストに変換します。<br>
	 * 改行コードとは "\n", "\r", "\n\r"のいずれかです。返却するリストに改行コードは含みません。<br>
	 * 文字列が空文字の場合、あるいは改行コードの連続のみの場合、空のリストを返します。<br>
	 * 文字列の末尾に改行コードが連続する場合、その箇所を無視します。
	 * @param text 文字列
	 * @return 文字列のリスト
	 */
	static List<String> parseTextLines(String text) {
		ArgmentChecker.checkNotNull('text', text);
		if (text.empty) return [];
		return text.split(/\r\n|[\n\r]/) as List;
	}
	
	/**
	 * 各行の先頭に行番号を付与して返します。<br>
	 * 改行コードはシステム固有の値を使用します。
	 * @param text 文字列
	 * @return 先頭に行番号を付与された文字列
	 * @see #addLineNo(List<String>)
	 */
	static String addLineNo(String text) {
		return addLineNo(parseTextLines(text)).join(System.getProperty('line.separator'));
	}
	
	/**
	 * 各行の先頭に行番号を付与して返します。<br>
	 * 行番号の先頭は０埋めします。行番号と各行の間は半角スペースで連結します。<br>
	 * 末尾に改行コードの連続があっても無視します。<br>
	 * 空文字や、改行コードの連続のみを渡された場合は、空文字を返します。
	 * @param lines 文字列リスト
	 * @return 先頭に行番号を付与した文字列リスト
	 */
	static List<String> addLineNo(List<String> lines) {
		ArgmentChecker.checkNotNull('lines', lines);
		int digitNum = lines.size.toString().length();
		int lineNo = 0;
		return lines.collect { String.format("%0${digitNum}d %s", ++ lineNo, it) };
	}
	
	/**
	 * 区切り文字自身を含めた文字列の分割をします。<br>
	 * 以前は{@link java.util.StringTokenizer}を用いれば可能でしたが、非推奨となったため作成しました。<br>
	 * 区切り文字を含めない場合は{@link String#split(String)}を使用してください。
	 *
	 * @param str 対象文字列
	 * @param delim 正規表現での区切り文字
	 * @return 区切り文字自身を含めて分割された文字列のリスト
	 */
	static String[] split(String str, String delim) {
		Pattern pattern = ~delim;
		Matcher matcher = pattern.matcher(str);
		List splited = [];
		int preEnd = 0;
		while (matcher.find()) {
			int start = matcher.start();
			// ひとつ前の区切り文字の間に文字列があれば、それを格納します
			if (preEnd < start) {
				splited << str.substring(preEnd, start);
			}
			// 区切り文字を結果に含めます
			splited << matcher.group();
			preEnd = matcher.end();
		}
		// 最後の区切り文字以降にまだ文字列があれば、それを格納します
		if (preEnd < str.length()) {
			splited << str.substring(preEnd, str.length());
		}
		return splited as String[];
	}
	
	/**
	 * 指定された文字列が、ワイルドカードを用いたパターンを満たすか判定します。<br>
	 * 以下の条件を両方とも満たすときに trueを返します。</p>
	 * <ul>
	 * <li>パターンリストincludePatternsが空、あるいはパターンのいずれかひとつでも適合する。</li>
	 * <li>パターンリストexcludePatternsが空、あるいはパターンのどれとも適合しない。</li>
	 * </ul>
	 * @param str 文字列
	 * @param includePatterns 適合パターンリスト（ワイルドカードを使用できます）
	 * @param excludePatterns 除外パターンリスト（ワイルドカードを使用できます）
	 * @return 指定された文字列がパターンを満たすか
	 * @see FilenameUtils#wildcardMatch(String,String)
	 */
	static boolean wildcardMatch(String str, List<String> includePatterns, List<String> excludePatterns){
		ArgmentChecker.checkNotNull('str', str);
		ArgmentChecker.checkNotNull('includePatterns', includePatterns);
		ArgmentChecker.checkNotNull('excludePatterns', excludePatterns);
		return ((includePatterns.isEmpty() || includePatterns.any { FilenameUtils.wildcardMatch(str, it) })
			&& (excludePatterns.isEmpty() || excludePatterns.every { !FilenameUtils.wildcardMatch(str, it) }));
	}
}
