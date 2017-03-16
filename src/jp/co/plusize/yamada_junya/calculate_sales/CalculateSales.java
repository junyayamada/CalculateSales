/**
 *
 */
/**
 * @author trainee158
 *
 */
package jp.co.plusize.yamada_junya.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CalculateSales {
	public static void main(String[] args) {

		//支店定義ファイル
		//ファイルデータの保持
		HashMap<String,String> branchNameMap = new HashMap<String,String>() ;
		HashMap<String,Long> branchMoneyMap = new HashMap<String,Long>() ;

		BufferedReader br = null ;
		try {
			File file = new File (args[0],"branch.lst") ;
			if (!file.exists()) {
				System.out.println("支店定義ファイルが存在しません");
				return ;
			}

			FileReader fr = new FileReader (file) ;
			br = new BufferedReader (fr) ;
			String s ;
			while ((s = br.readLine()) != null) {
				String[] str = s.split(",") ;
				if(!str[0].matches("\\d{3}")||(str.length != 2)){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				branchNameMap.put(str[0],str[1]);
				branchMoneyMap.put(str[0],0L);
			}
		} catch (IOException e) {
			System.out.println ("支店定義ファイルのフォーマットが不正です") ;
		} finally {
			try {
				br.close() ;
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}


		//商品定義ファイル
		//ファイルデータの保持
		HashMap<String,String> commodityNameMap = new HashMap<String,String>() ;
		HashMap<String,Long> commodityMoneyMap = new HashMap<String,Long>() ;

		BufferedReader brf = null ;
		try {
			File file = new File (args[0], "commodity.lst") ;
			if (!file.exists()) {
				System.out.println("商品定義ファイルが存在しません") ;
				return ;
			}
			FileReader fre = new FileReader (file) ;
			brf = new BufferedReader (fre) ;
			String s ;
			while ((s= brf.readLine()) != null) {
				String[] str = s.split(",") ;
				if (!str[0].matches("\\w{8}")||(str.length != 2)){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				commodityNameMap.put(str[0],str[1]) ;
				commodityMoneyMap.put(str[0],0L);
			}
		} catch (IOException e) {
			System.out.println("商品定義ファイルのフォーマットが不正です");
		} finally {
			try {
				brf.close() ;
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}


		//集計
		//ファイルの読み込み、加算
		BufferedReader buffer= null ;
		ArrayList<File> rcdFile = new ArrayList<File>();
		try {
			//パスそのものをFile型で保持
			File file = new File(args[0]);
			//パスから中身（ファイル名）を配列で保持
			File[] files = file.listFiles();

			for (int i = 0; i < files.length; i++ ) {
				//filesの中身がファイルかディレクトリかで分ける
				if (files[i].isFile()) {
					if (files[i].getName().matches("\\d{8}.rcd")) {
						rcdFile.add(files[i]);
						}
				} else if (files[i].isDirectory()) {
					System.out.println("予期せぬエラーが発生しました");
				}
			}
			//連番確認
			for (int i = 0; i < rcdFile.size(); i++) {
				if (rcdFile.get(i) == null){
					System.out.println("売上ファイル名が連番になっていません");
					return ;
				}
			}

			for (int i = 0 ; i < rcdFile.size() ; i++ ) {
				try {
					FileReader fr;
					fr = new FileReader (rcdFile.get(i));
					buffer = new BufferedReader (fr);
					String s ;
					//readFileはrcdFileのString型
					ArrayList<String> readFile = new ArrayList<String>();
					while((s = buffer.readLine()) != null) {
						readFile.add(s);
					}
					//売上ファイルの中身が4行以上ある場合
					if (readFile.size() != 3) {
						System.out.println("<該当ファイル名>のフォーマットが不正です");
						return ;
					}

					//Long型のmapValueに代入、=Mapの金額を入れる、Map.get(File.get)
					Long mapValue = branchMoneyMap.get(readFile.get(0));
					//Long型のreadValueに代入
					Long readValue = Long.parseLong(readFile.get(2));

					//支店または商品に該当がなかった場合
					if (mapValue == null || readValue == null) {
						System.out.println("<該当ファイル名>の支店コードが不正です");
						return ;
					}

					//Long型の変数 = Long + Long
					Long Value = mapValue + readValue;

					//合計金額が10桁を超えた場合
					if (String.valueOf(Value).length() > 10) {
						System.out.println("合計金額が10桁を超えました");
						return ;
					}


					//BranchMoneyMapにキーとバリューをセットで戻す
					branchMoneyMap.put(readFile.get(0),Value);
				} catch(IOException e) {
					System.out.println("予期せぬエラーが発生しました");
				} finally {
					buffer.close();
				}
			}
		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
		} finally {
			try {
				buffer.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}


		//支店別集計ファイル
		//並び替え
		List<Map.Entry<String,Long>>bmList = new ArrayList<Map.Entry<String,Long>>(branchMoneyMap.entrySet());

		Collections.sort(bmList,new Comparator<Map.Entry<String,Long>>() {
			@Override
			public int compare(Entry<String,Long>entry1,Entry<String,Long>entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue()) ;
			}
		});
		//並び替えの内容を表示
		for(Entry<String,Long>bm : bmList) {
//			//コード、支店名、金額
//			System.out.println(bm.getKey() + "," + branchNameMap.get(bm.getKey()) + ","+ bm.getValue()) ;
		}
		BufferedWriter bw = null;
		//ファイルへの出力
		try {
			File file = new File (args[0],"支店別集計ファイル") ;
			FileWriter fw = new FileWriter (file) ;
			bw = new BufferedWriter (fw) ;
			for (Entry<String,Long>bm : bmList){
				bw.write((bm.getKey() + "," + branchNameMap.get(bm.getKey()) + ","+ bm.getValue()) + System.getProperty("line.separator")) ;
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました") ;
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}


		//商品別集計ファイル
		//並び替え
		List<Map.Entry<String,Long>>cmList = new ArrayList<Map.Entry<String,Long>>(commodityMoneyMap.entrySet());

		Collections.sort(cmList,new Comparator<Map.Entry<String,Long>>() {
			@Override
			public int compare(Entry<String,Long>entry1,Entry<String,Long>entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue()) ;
			}
		});
		//並び替えの内容を表示
		for(Entry<String,Long>cm : cmList) {
//			//コード、支店名、金額
//			System.out.println(cm.getKey() + "," + commodityNameMap.get(cm.getKey()) + ","+ cm.getValue()) ;
		}
		BufferedWriter bwc = null;
		//ファイルへの出力
		try {
			File file = new File (args[0],"商品別集計ファイル") ;
			FileWriter fw = new FileWriter (file) ;
			bwc = new BufferedWriter (fw) ;
			for (Entry<String,Long>cm : cmList){
				bwc.write((cm.getKey() + "," + commodityNameMap.get(cm.getKey()) + ","+ cm.getValue()) + System.getProperty("line.separator")) ;
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました") ;
		} finally {
			try {
				bwc.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}

	private static char[] commodityNameMap(String string, String string2) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}