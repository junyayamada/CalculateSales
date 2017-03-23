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

		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		String fileInputPath = args[0] + File.separator + "branch.lst";
		if(!fileRead(fileInputPath,"支店","\\d{3}",branchNameMap, branchMoneyMap)){
			return ;
		}
		fileInputPath = args[0] + File.separator + "commodity.lst";
		if(!fileRead(fileInputPath,"商品","\\w{8}",commodityNameMap, commodityMoneyMap )){
			return ;
		}


		//集計
		BufferedReader buffer= null ;
		ArrayList<File> rcdList = new ArrayList<File>();
		try {
			//ファイル名の検索
			File file = new File(args[0]);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++ ) {
				if (files[i].isFile()) {
					if (files[i].getName().matches("^\\d{8}.rcd$")) {
						rcdList.add(files[i]);
					}
				}
			}


			//連番確認
			for (int i = 0; i < rcdList.size()-1; i++) {
				String str1st = rcdList.get(i).getName().substring(0,8);
				String str2nd = rcdList.get(i+1).getName().substring(0,8);
				int Number1st = Integer.parseInt(str1st);
				int Number2nd = Integer.parseInt(str2nd);
				if (Number2nd - Number1st != 1) {
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}

			//売上ファイルの読込みと、中身のチェック
			for (int i = 0 ; i < rcdList.size() ; i++ ) {
				try {
					FileReader fr;
					fr = new FileReader (rcdList.get(i));
					buffer = new BufferedReader (fr);
					String s ;
					ArrayList<String> fileReadList = new ArrayList<String>();
					while((s = buffer.readLine()) != null) {
						fileReadList.add(s);
					}
					//売上ファイルの中身が3行以外の場合
					if (fileReadList.size() != 3) {
						System.out.println(rcdList.get(i).getName() + "のフォーマットが不正です");
						return;
					}
					//売上ファイルの支店コードがない場合
					if (!branchNameMap.containsKey(fileReadList.get(0))) {
						System.out.println(rcdList.get(i).getName() + "の支店コードが不正です");
						return;
					}
					//売上ファイルの商品コードがない場合
					if (!commodityNameMap.containsKey(fileReadList.get(1))) {
						System.out.println(rcdList.get(i).getName() + "の商品コードが不正です");
						return;
					}
					//売上ファイルの金額が数値でない場合
					if (!fileReadList.get(2).matches("^[0-9]*$")) {
						System.out.println("予期せぬエラーが発生しました");
						return ;
					}

					//キーをfileReadList.get(0)として、支店コードから支店定義ファイルの金額を呼び出し
					Long branchMapValue = branchMoneyMap.get(fileReadList.get(0));
					//キーをfileReadList.get(1)として、商品コードから商品定義ファイルの金額を呼び出し
					Long commodityMapValue = commodityMoneyMap.get(fileReadList.get(1));

					//fileReadList.get(2)の金額をLong型に変換
					Long readValue = Long.parseLong(fileReadList.get(2));

					Long branchValue = branchMapValue + readValue;
					Long commodityValue = commodityMapValue + readValue;

					//合計金額が10桁を超えた場合
					if (String.valueOf(branchValue).length() > 10) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					if (String.valueOf(commodityValue).length() > 10) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}

					//MoneyMapにキーとバリューをセットで戻す
					branchMoneyMap.put(fileReadList.get(0),branchValue);
					commodityMoneyMap.put(fileReadList.get(1),commodityValue);

				} catch(IOException e) {
					System.out.println("予期せぬエラーが発生しました");
				} finally {
					buffer.close();
				}
			}
		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
		} finally {
		}

		String fileOutputPath = args[0] + File.separator + "branch.out";
		if(!fileOutput(fileOutputPath,branchNameMap, branchMoneyMap)){
			return ;
		}
		fileOutputPath = args[0] + File.separator + "commodity.out";
		if(!fileOutput(fileOutputPath,commodityNameMap, commodityMoneyMap )){
			return ;
		}
	}

	private static char[] rcdList(int i) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
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
    			if (str.length != 2) {
    				System.out.println(fileName + "定義ファイルのフォーマットが不正です");
    				return false;
    			}
    			if (!str[0].matches(pattern)) {
    				System.out.println(fileName + "定義ファイルのフォーマットが不正です");
    				return false;
    			}
	    		nameMap.put(str[0],str[1]);
	    		moneyMap.put(str[0],0L);
    		}
     	} catch (IOException e) {
       		 System.out.println (fileName + "定義ファイルのフォーマットが不正です");
       	} finally {
       		try {
       			if (br != null) {
       				br.close() ;
       			}
       		} catch (IOException e) {
      			// TODO 自動生成された catch ブロック
       			System.out.println("予期せぬエラーが発生しました") ;
       			return false;
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
				if (bwc != null) {
					bwc.close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("予期せぬエラーが発生しました") ;
				return false;
			}
		}
		return true;
	}
}
