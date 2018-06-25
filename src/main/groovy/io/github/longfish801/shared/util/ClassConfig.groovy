/*
 * ClassConfig.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.shared.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;
import org.apache.commons.io.FileUtils;

/**
 * 変更可能な設定値をクラス単位で参照／保存するためのクラスです。</p>
 * 
 * <p>初期値は設定値文字列として指定するか、リソースから参照します。<br>
 * 設定値文字列は {@link ConfigSlurper}で解析可能な文字列としてください。<br>
 * リソースとして定義する場合、設定値文字列を初期設定ファイルとして保存してください。<br>
 * 初期設定ファイルはクラスの単純名＋拡張子「.groovy」をリソース名として{@link ClassSlurper}で参照します。<br>
 * 初期設定ファイルが存在しない場合は読みません。</p>
 * 
 * <p>設定値は設定ファイルとして保存します。<br>
 * 設定ファイルは、データフォルダに XML形式のプロパティファイルとして保存します。<br>
 * ルートフォルダとして、デフォルトではシステムプロパティ"java.io.tmpdir"で参照されるフォルダを利用します。
 * ルートフォルダは作成済であることを前提とします。<br>
 * ルートフォルダの直下にルートデータフォルダ gstartフォルダを作成します。</p>
 * ルートデータフォルダ直下に、クラスの正規名でフォルダを作成します。これを、そのクラスに固有のデータフォルダとします。<br>
 * データフォルダには設定ファイル config.xmlを作成します。{@link Properties}を介してXML形式で保存します。<br>
 * 設定ファイル config.xmlの入出力は{@link ConfigXml}を利用します。</p>
 * @version 1.0.00 2016/06/27
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClassConfig extends ConfigObject {
	/** ConfigObject */
	protected static final ConfigObject constants = ClassSlurper.getConfig(ClassConfig.class);
	/** ルートフォルダ */
	static File rootDir = new File(System.getProperty(constants.syspropTmpdir));
	/** データフォルダ */
	File dataDir = null;
	/** 設定ファイル */
	File confFile = null;
	/** クラス */
	Class clazz = null;
	
	/**
	 * 設定値文字列を初期設定値としたClassConfigインスタンスを返します。<br>
	 * 設定値文字列から初期設定値を読みこみます。<br>
	 * 加えて、クラス設定ファイルが存在すれば初期設定値に上書きします。<br>
	 * 参照に失敗した場合は ERRORログを出力します。
	 * @param clazz Class
	 * @param defConfig 設定値文字列
	 * @return ClassConfigインスタンス（読込失敗時は null）
	 */
	static ClassConfig newInstance(Class clazz, String defConfig){
		ClassConfig config = null;
		try {
			config = new ClassConfig(clazz);
			config.loadConfigString(defConfig);
			config.loadConfig();
		} catch (exc){
			LOG.error('設定値の読込に失敗しました。clazz={} defConfig={}', clazz, defConfig, exc);
		}
		return config;
	}
	
	/**
	 * リソース上の初期設定ファイルを初期設定値としたClassConfigインスタンスを返します。<br>
	 * 初期設定ファイルが存在すれば初期設定値を読みこみます。<br>
	 * 加えて、クラス設定ファイルが存在すれば初期設定値に上書きします。<br>
	 * 参照に失敗した場合は ERRORログを出力します。
	 * @param clazz Class
	 * @return ClassConfigインスタンス（読込失敗時はnull）
	 */
	static ClassConfig newInstance(Class clazz){
		ClassConfig config = null;
		try {
			config = new ClassConfig(clazz);
			config.loadConfigResource();
			config.loadConfig();
		} catch (exc){
			LOG.error('設定値の読込に失敗しました。clazz={}', clazz, exc);
		}
		return config;
	}
	
	/**
	 * コンストラクタ。<br>
	 * もし未作成であればデータフォルダを作成します。<br>
	 * データフォルダの作成時には INFOログを出力します。
	 * @param clazz Class
	 * @throws IOException データフォルダの作成に失敗しました。
	 */
	ClassConfig(Class clazz){
		ArgmentChecker.checkNotNull('clazz', clazz);
		this.clazz = clazz;
		
		// もし未作成であればデータフォルダを作成します
		File rootDataDir = new File(rootDir, constants.rootDataDirname);
		dataDir = new ClassDirectory(rootDataDir).getFlatDir(clazz);
		setupDataDir();
		confFile = new File(dataDir, constants.confFilename);
	}
	
	/**
	 * もし未作成であればデータフォルダを作成します。
	 * @throws IOException データフォルダの作成に失敗しました。
	 */
	private void setupDataDir(){
		if (!dataDir.isDirectory()){
			if (!dataDir.mkdirs()){
				throw new IOException("データフォルダの作成に失敗しました。clazz=${clazz}, dataDir=${dataDir}");
			} else {
				LOG.info('データフォルダを作成しました。dataDir={}', dataDir);
			}
		}
	}
	
	/**
	 * データフォルダを削除します。<br>
	 * データフォルダ内のサブフォルダやファイルもすべて削除します。
	 */
	void deleteDataDir() {
		if (dataDir.exists()){
			FileUtils.deleteDirectory(dataDir);
		} else {
			LOG.debug('データフォルダが存在しません。dataDir={}', dataDir);
		}
	}
	
	/**
	 * 設定値文字列を読みこみます。
	 * @param defConfig 設定値文字列
	 */
	void loadConfigString(String defConfig){
		ArgmentChecker.checkNotBlank('defConfig', defConfig);
		merge(new ConfigSlurper().parse(defConfig));
	}
	
	/**
	 * リソース上に初期設定ファイルが存在すれば読みこみます。<br>
	 * 初期設定ファイルが存在しない場合は読み込みません。
	 */
	void loadConfigResource(){
		ConfigObject config = new ClassSlurper(clazz).getConfig();
		if (config == null){
			LOG.debug('リソース上に初期設定ファイルが存在しません。clazz={}', clazz);
		} else {
			merge(config);
		}
	}
	
	/**
	 * データフォルダに設定ファイルが存在すれば読みこみます。
	 */
	void loadConfig(){
		if (confFile.exists()) {
			new FileReader(confFile).withCloseable { reader ->
				use(ConfigXml){ loadXml(reader) }
			}
		}
	}
	
	/**
	 * 設定値をデータフォルダ内の設定ファイルへ保存します。<br>
	 * もし未作成であればデータフォルダを作成します。
	 */
	void saveConfig(){
		setupDataDir();
		new FileWriter(confFile).withCloseable { writer ->
			use(ConfigXml){ outputXml(writer) }
		}
	}
}
