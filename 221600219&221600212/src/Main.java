import java.io.*;
import java.util.*;

public class Main{

    // 行数
    static int lineNum = 0;

    // 字符数
    static int charNum = 0;

    // 单词数：至少四个英文字母开头，不区分大小写
    static int wordNum = 0;

    // 论文数
    static int paperNum = 0;

    // 排序好的单词集合
    static List<Map.Entry<String, Integer>> wordList = null;

    // 输入文件字节数组
    static byte[] inputFileBytes = null;

    // 标题字节数组
    static byte[] titleBytes = null;

    // 摘要字节数组
    static byte[] abstractBytes = null;

    // 排序打印出前几的个数
    static int sortedPrintNum = 10;

    // 输入文件名
    static String inputFileName = null;

    // 输出文件名
    static String outputFileName = null;

    // 词组的词数
    static int phraseWordNum = 1;

    // 是否在计算单词、词组频率时加入权重
    static boolean useWordWeight = false;

    // title部分权重
    static int titleWordWeight = 1;

    // abstract部分权重
    static int abstractWordWeight = 1;

    /**
     * 程序入口
     */
    public static void main(String[] args) {
		
        // 初始化
        loadArgs(args);
        checkArgs();
        inputFileBytes = readFileToBytes(inputFileName);
		inputFileBytes = filterAscii(inputFileBytes);
		// System.out.println(new String(inputFileBytes));
        processBytes();

        try {
            Lib core = new Lib(titleBytes, abstractBytes, phraseWordNum,
                                titleWordWeight, abstractWordWeight);

            // 预处理，计算字符数、行数、单词数，排序频率
            core.preProccess();
            core.collectWord();
            core.sortWordMap();

            // 获取结果
            charNum = core.getCharNum();
            wordNum = core.getWordNum();
            lineNum = core.getLineNum() + paperNum;
            wordList = core.getSortedList();

        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        // 结果
        // printResult();
        writeResult();
    }

    /**
     * 程序启动前校验运行参数是否正确
     */
    static void checkArgs(){
        if (inputFileName == null ||
            !(new File(inputFileName).exists())){
            System.out.println("输入文件不存在");
            System.exit(1);
        }
    }

    /**
     * 解析命令行参数
     *
     * @param args 命令行参数数组
     */
    static void loadArgs(String[] args){
        if (args.length > 0 && args != null){
            for (int i = 0; i < args.length; i++){
                switch (args[i]){
                    case "-i":
                        inputFileName = args[++i];
                        break;
                    case "-o":
                        outputFileName = args[++i];
                        break;
                    case "-m":
                        phraseWordNum = Integer.valueOf(args[++i]);
                        break;
                    case "-n":
                        sortedPrintNum = Integer.valueOf(args[++i]);
                        break;
                    case "-w":
                        useWordWeight = (args[i+1].equals("1"));
                        titleWordWeight = (useWordWeight ? 10 : 1);
                        break;
                    default:
                        break;
                }
            }
        } else{
            System.out.println("未输入参数");
            System.exit(1);
        }
//        System.out.println(String.format(
//            "inputFileName:%s\noutputFileName:%s\nphraseWordNum:%s\n" +
//            "sortedPrintNum:%s\nuseWordWeight: %s\n",
//            inputFileName, outputFileName, phraseWordNum,
//            sortedPrintNum, useWordWeight?"use":"no-use"
//        ));
    }

    /**
     * 处理二进制字节数组，将论文标题和摘要分开保存
     */
    static void processBytes(){
        int bytesLength = inputFileBytes.length;
        byte[] tBytes = new byte[bytesLength];
        byte[] aBytes = new byte[bytesLength];
        int i = 0, j = 0, k = 0;
        int tbIndex = 0, abIndex = 0;
        int tbNum = 0, abNum = 0;
        final int TITLE_LABEL_LENGTH = 7; // "Title: "
        final int ABSTRACT_LABEL_LENGTH = 10; // "Abstract: "
        while (i < bytesLength){
            for (j = 0; j < 3 && i < bytesLength; j++){
                for (k = i; k < bytesLength; k++){
                    if (inputFileBytes[k] == 10){
                        break; //换行
                    }
                }
                if (k == bytesLength){k--;}
                if (j == 1 && (k-i-TITLE_LABEL_LENGTH+1 > 0)){
                    // Title 行
                    if (Lib.subBytesToString(inputFileBytes, i, i+TITLE_LABEL_LENGTH)
                            .equals("Title: ")){
                        // System.out.println("add Title.");
                        // System.out.println("<"+
						//	Lib.subBytesToString(inputFileBytes, i+TITLE_LABEL_LENGTH, k+1)+">");
                        System.arraycopy(inputFileBytes, i+TITLE_LABEL_LENGTH,
                            tBytes, tbIndex, k-i-TITLE_LABEL_LENGTH+1);
                        tbIndex += (k-i-TITLE_LABEL_LENGTH+1);

                        tbNum ++;
                        paperNum ++;
                        // System.out.println(new String(tBytes));
                    }
                }
                if (j == 2 && (k-i-ABSTRACT_LABEL_LENGTH+1 > 0)){
                    // Abstract 行
                    if (Lib.subBytesToString(inputFileBytes, i, i+ABSTRACT_LABEL_LENGTH)
                            .equals("Abstract: ")){
                        // System.out.println("add Abstract.");
                        // System.out.println("<"+
						//	Lib.subBytesToString(inputFileBytes, i+ABSTRACT_LABEL_LENGTH, k+1)+">");
                        System.arraycopy(inputFileBytes, i+ABSTRACT_LABEL_LENGTH,
                            aBytes, abIndex, k-i-ABSTRACT_LABEL_LENGTH+1);
                        abIndex += (k-i-ABSTRACT_LABEL_LENGTH+1);

                        abNum ++;
                        // System.out.println(new String(aBytes));
                    }
                }
                i = k + 1;
            }
            for (j = 0; j < 2 && i < bytesLength; j++){
                for (k = i; k < bytesLength; k++){
                    // System.out.println(k);
                    if (inputFileBytes[k] == 10){
                        break; //换行
                    }
                }
                i = k + 1;
            }
        }
        // 复制
        titleBytes = new byte[tbIndex];
        abstractBytes = new byte[abIndex];
        System.arraycopy(tBytes, 0, titleBytes, 0, tbIndex);
        System.arraycopy(aBytes, 0, abstractBytes, 0, abIndex);
        // System.out.println(new String(titleBytes));
        // System.out.println(new String(abstractBytes));
        // System.out.println("title num: " + tbNum + " abstract num: " + abNum);
    }
	
	/**
	 * 过滤掉字节数组中的非ascii码字符
	 *
	 * @param bytes 字节数组
	 *
	 * @return noAsciiBytes
	 */
	static byte[] filterAscii(byte[] bytes){
		byte[] noAsciiBytes_ = new byte[bytes.length];
		int j = 0;
		for (int i = 0; i < bytes.length; i++){
			if (bytes[i] < 128 && bytes[i] >= 0){
				noAsciiBytes_[j++] = bytes[i];
			}
		}
		byte[] noAsciiBytes = new byte[j];
        System.arraycopy(noAsciiBytes_, 0, noAsciiBytes, 0, j);
		return noAsciiBytes;
	}

    /**
     * 读取文件到字节数组中
     *
     * @param fileName 文件名
     *
     * @return bytes 字节数组
     */
    static byte[] readFileToBytes(String fileName){
        byte[] fileBytes = null;

        try {
            File file = new File(fileName);
            if (file.isFile() && file.exists()){
                FileInputStream reader = new FileInputStream(file);
                Long fileLength = file.length();
//                System.out.println("fileLength: " + fileLength);
                fileBytes = new byte[fileLength.intValue()];
                reader.read(fileBytes);
                reader.close();
            }
        }
        catch(FileNotFoundException e){
            System.out.println("文件不存在");
        }
        catch(Exception e){
            System.out.println("读取文件出错");
            e.printStackTrace();
        }

        return fileBytes;
    }

    /**
     * 打印结果到控制台
     */
    static void printResult(){
        System.out.println("characters: " + charNum);
        System.out.println("words: " + wordNum);
        System.out.println("lines: " + lineNum);
        int i = 0;
        for (Map.Entry<String, Integer> entry : wordList) {
            System.out.println("<" + entry.getKey() + ">: " + entry.getValue());
            if (++ i >= sortedPrintNum){
                break;
            }
        }
    }

    /**
     * 输出结果到文件中
     */
    static void writeResult(){
        String resultString = String.format(
            "characters: %d\nwords: %d\nlines: %d\n",
            charNum, wordNum, lineNum
        );
        int i = 0;
        for (Map.Entry<String, Integer> entry : wordList) {
            resultString += String.format("<%s>: %d\n", entry.getKey(), entry.getValue());
            if (++ i >= sortedPrintNum){
                break;
            }
        }
        try{
            File outPutFile = new File(outputFileName);
            if (! outPutFile.exists()){
                outPutFile.createNewFile();
            }
            FileWriter writter = new FileWriter(outPutFile.getName(), false);
            writter.write(resultString);
            writter.close();
        }catch(Exception e){
            System.out.println("写入文件出错");
            e.printStackTrace();
        }
    }

}