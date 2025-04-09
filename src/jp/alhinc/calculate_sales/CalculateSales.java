package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if (!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		File[] files = new File(args[0]).listFiles();
		//		売上ファイルを格納するため宣言
		List<File> rcdFiles = new ArrayList<>();

		for (int i = 0; i < files.length; i++) {
			//files[i].getName() でファイル名が取得できます
			String fileName = files[i].getName();
			//売上ファイルの条件あうもののみ、List(ArrayList) に追加。
			if (fileName.matches("^[0-9]{8}[.]rcd$")) {
				rcdFiles.add(files[i]);

			}

//			エラー処理　ファイルの連番
			Collections.sort(rcdFiles);
			for(int j = 0; j < rcdFiles.size() - 1; j++) {
				int former = Integer.parseInt(rcdFiles.get(i).getName().substring(0, 8));
				int latter = Integer.parseInt(rcdFiles.get(i + 1).getName().substring(0, 8));

			      //比較する2つのファイル名の先頭から数字の8文字を切り出し、int型に変換します。
				if((latter - former) != 1) {

					//2つのファイル名の数字を比較して、差が1ではなかったら、
					//エラーメッセージをコンソールに表示します。
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}



		}

		BufferedReader br = null;
		//rcdFilesに複数の売上ファイルの情報を格納しているので、その数だけ繰り返します。
		//String fileName =rcdFiles.get(i).getName();でファイル名を取得できる
		for (int i = 0; i < rcdFiles.size(); i++) {
			//判別したファイルをfilereaderへ→bufferに移動して保持
			try {

					FileReader fr = new FileReader(rcdFiles.get(i));
					br = new BufferedReader(fr);

					//売り上げリストの名前を新たに宣言
					List<String> salseLine = new ArrayList<>();

					String line;
					//売上ファイルの1行目には支店コード、2行目には売上金額が入っています。
					//売上ファイルの区切り
					while ((line = br.readLine()) != null) {
						salseLine.add(line);
					}
					String code = salseLine.get(0);
					String value = salseLine.get(1);

					if (!branchNames.containsKey(code)) {
					    //⽀店情報を保持しているMapに売上ファイルの⽀店コードが存在しなかった場合は、
					    //エラーメッセージをコンソールに表⽰します。
					}

					//売上ファイルから読み込んだ売上金額をMapに加算していくために、型の変換を行います。
					long salseValue = Long.parseLong(value);
					//読み込んだ売上金額を加算します。
					//Map(HashMap)から値を取得する.salesLists[0]は支店コード。salseValueは売上金額をキャストしたもの
					Long saleSum = branchSales.get(code) + salseValue;

//					エラー処理　売上金額の桁制限
					if(saleSum >= 10000000000L){
						System.out.println("合計⾦額が10桁を超えました");
						return;
					}


					//加算した売上金額をMapに追加します。salesLists[0]は支店コード。saleSumは加算した金額。
					branchSales.put(code, saleSum);


			} catch (IOException e) {
				System.out.println(UNKNOWN_ERROR);

			} finally {
				// ファイルを開いている場合
				if (br != null) {
					// ファイルを閉じる
					try {
						br.close();
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}

			}
		}
		// 支店別集計ファイル書き込み処理
		if (!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}

	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames,
			Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);

//			エラー処理　ファイルがない例外
			if(!file.exists()){
				System.out.println("支店定義ファイルが存在しません");
				return false;
			}



			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while ((line = br.readLine()) != null) {
				// カンマを基に文字列を分割する
				String[] storeName = line.split(",");


//				エラー処理　ファイルがフォーマットではない例外
				if(!line.matches(",")){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return false;
				}

				//区切った文字を支店名ハッシュマップに保存していく
				branchNames.put(storeName[0], storeName[1]);
				//支店名だけ入れた売上のハッシュマップをいれて固定値の０円を入れている
				branchSales.put(storeName[0], 0L);

			}

		} catch (IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if (br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch (IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames,
			Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)
		BufferedWriter bw = null;
		try {
			File file = new File(path, fileName);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			for (String key : branchNames.keySet()) {
				//keyという変数には、Mapから取得したキーが代入されています。
				//拡張for文で繰り返されているので、1つ目のキーが取得できたら、
				//2つ⽬の取得...といったように、次々とkeyという変数に上書きされていきます。
				String line;
				bw.write(key + "," + branchNames.get(key) + "," + branchSales.get(key));
				bw.newLine();
			}

		} catch (IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if (bw != null) {
				try {
					// ファイルを閉じる
					bw.close();
				} catch (IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}
}