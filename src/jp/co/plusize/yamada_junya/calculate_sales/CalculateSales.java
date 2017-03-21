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

		HashMap<String,String> branchNameMap = new HashMap<String,String>() ;
		HashMap<String,Long> branchMoneyMap = new HashMap<String,Long>() ;

		HashMap<String,String> commodityNameMap = new HashMap<String,String>() ;
		HashMap<String,Long> commodityMoneyMap = new HashMap<String,Long>() ;


		String fileInputPath = args[0] + File.separator + "branch.lst";
		if(!fileRead(fileInputPath,"支店","\\d{3}",branchNameMap, branchMoneyMap)){
			System.out.println("メソッド分け①でエラー");
			return ;
		}
		fileInputPath = args[0] + File.separator + "commodity.lst";
		if(!fileRead(fileInputPath,"商品","\\w{}",commodityNameMap, commodityMoneyMap )){
			System.out.println("メソッド分け②でエラー");
			return ;
		}


		BufferedReader buffer= null ;
		ArrayList<File> rcdFile = new ArrayList<File>();
		try {
			//ファイル名の検索
			File file = new File(args[0]);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++ ) {
				if (files[i].isFile()) {
					if (files[i].getName().matches("\\d{8}.rcd")) {
						rcdFile.add(files[i]);
						}
				} else if (!files[i].isFile()) {
					System.out.println("予期せぬエラーが発生しました");
				}
			}
			//連番確認
			for (int i = 0; i < rcdFile.size(); i++) {
				if (rcdFile.get(i) == null){
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}

			for (int i = 0 ; i < rcdFile.size() ; i++ ) {
				try {
					FileReader fr;
					fr = new FileReader (rcdFile.get(i));
					buffer = new BufferedReader (fr);
					String s ;
					//rcdFileのString型を、readFileとして宣言
					ArrayList<String> readFile = new ArrayList<String>();
					while((s = buffer.readLine()) != null) {
						readFile.add(s);
					}
					//売上ファイルの中身が4行以上ある場合
					if (readFile.size() != 3) {
						System.out.println("<該当ファイル名>のフォーマットが不正です");
						return;
					}
					//Long型のmapValueに代入、Mapの金額を入れる、Map.get(File.get)
					Long mapValue = branchMoneyMap.get(readFile.get(0));
					//Long型のreadValueに代入
					Long readValue = Long.parseLong(readFile.get(2));

					//支店または商品に該当がなかった場合
					if (mapValue == null || readValue == null) {
						System.out.println("<該当ファイル名>の支店コードが不正です");
						return;
					}

					//Long型の変数 = Long + Long
					Long Value = mapValue + readValue;

					//合計金額が10桁を超えた場合
					if (String.valueOf(Value).length() > 10) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}

					//MoneyMapにキーとバリューをセットで戻す
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
				System.out.println("予期せぬエラーが発生しました") ;
			}
		}

		String fileOutputPath = args[0] + File.separator + "branch.out";
		if(!fileOutput(fileOutputPath,branchNameMap, branchMoneyMap)){
			System.out.println("メソッド分け③でエラー");
			return ;
		}
		fileOutputPath = args[0] + File.separator + "commodity.out";
		if(!fileOutput(fileOutputPath,commodityNameMap, commodityMoneyMap )){
			System.out.println("メソッド分け④でエラー");
			return ;
		}
	}

	//lstファイルの読込み
	public static boolean fileRead(String fileInputPath,String fileName,String pattern,HashMap<String,String> nameMap,HashMap<String,Long> moneyMap) {
    	BufferedReader br = null ;
    	try {
    		File file = new File (fileInputPath) ;
    		if (!file.exists()) {
    			System.out.println(fileName + "定義ファイルが存在しません");
    			return false;
    		}
    		FileReader fr = new FileReader (file) ;
    		br = new BufferedReader (fr) ;
    		String s ;
    		while ((s = br.readLine()) != null) {
    			String[] str = s.split(",") ;
    			if(!str[0].matches(pattern)){
    				System.out.println(fileName + "定義ファイルのフォーマットが不正です①");
    				return false;
    			}
	    		nameMap.put(str[0],str[1]);
	    		moneyMap.put(str[0],0L);
    		}
     	} catch (IOException e) {
       		 System.out.println (fileName + "定義ファイルのフォーマットが不正です②") ;
       	} finally {
       		try {
       			br.close() ;
       		} catch (IOException e) {
      			// TODO 自動生成された catch ブロック
       			System.out.println("予期せぬエラーが発生しました") ;
       		}
       	}
		return true;
    }

	//ファイル並替え
	public static boolean fileOutput(String fileOutputPath,HashMap<String,String> nameMap,HashMap<String,Long> moneyMap ){
		List<Map.Entry<String,Long>>cmList = new ArrayList<Map.Entry<String,Long>>(moneyMap.entrySet());

		Collections.sort(cmList,new Comparator<Map.Entry<String,Long>>() {
			@Override
			public int compare(Entry<String,Long>entry1,Entry<String,Long>entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue()) ;
			}
		});
		BufferedWriter bwc = null;
		//ファイルへの出力
		try {
			File file = new File (fileOutputPath) ;
			FileWriter fw = new FileWriter (file) ;
			bwc = new BufferedWriter (fw) ;
			for (Entry<String,Long>cm : cmList){
				bwc.write((cm.getKey() + "," + nameMap.get(cm.getKey()) + ","+ cm.getValue()) + System.getProperty("line.separator")) ;
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました") ;
			return false;
		} finally {
			try {
				bwc.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました") ;
				return false;
			}
		}
		return true;
	}
}
