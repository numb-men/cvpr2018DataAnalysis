import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.io.*;

/**
 * 功能：爬取顶会论文（标题+摘要），并保存到文件中
 * 编译：javac -cp jsoup-1.11.3.jar; Main.java
 * 运行：java -cp jsoup-1.11.3.jar; Main
 */
public class Main {
	
	public static void main(String[] args) {
		final String ROOT = "http://openaccess.thecvf.com/";
		String papersUrl = "CVPR2018.py";
		String outputFileName = "result.txt";
		final int TIME_OUT = 5000;
		Document doc = null, aPaperDoc = null;
		String aPaperTitle = null, aPaperUrl = null,aPaperAbstract = null;
		int id = 0;
		try {
			doc = Jsoup.connect(ROOT + papersUrl).timeout(TIME_OUT).get();
			File outPutFile = new File(outputFileName);
			if (! outPutFile.exists()){outPutFile.createNewFile();}
			FileWriter writter = new FileWriter(outPutFile.getName(), true);
				
			// 获取所有ptitle链接
			Elements elements = doc.getElementsByClass("ptitle");
			Iterator it = elements.iterator(); 
			while(it.hasNext()) {
				Element e = (Element)it.next();
				aPaperTitle = e.text();
				// 获取文章详情链接
				aPaperUrl = e.getElementsByTag("a").first().attr("href");
				System.out.println(ROOT + aPaperUrl);
				// 防止爬取过快，被服务器拒绝访问
				// Thread.sleep(1000);
				aPaperDoc = Jsoup.connect(ROOT + aPaperUrl).timeout(TIME_OUT).get();
				Element ee = aPaperDoc.getElementById("abstract");
				aPaperAbstract = ee.text();
				writter.write(String.format(
					// 控制最后一篇文章后无换行
					"%d\r\nTitle: %s\r\nAbstract: %s" +
					(!it.hasNext() ? "":"\r\n\r\n\r\n"),
					id++, aPaperTitle, aPaperAbstract
				));
			}
			writter.close();
			System.out.println("\r\n\r\n爬取完毕，一共爬取" + id + "篇论文.");
		} catch (Exception e) {
			System.out.println("爬取错误");
			e.printStackTrace();
		}
	}
}
