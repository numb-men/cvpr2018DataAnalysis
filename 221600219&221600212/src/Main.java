import java.io.*;
import java.util.*;

public class Main{

    // ����
    static int lineNum = 0;

    // �ַ���
    static int charNum = 0;

    // �������������ĸ�Ӣ����ĸ��ͷ�������ִ�Сд
    static int wordNum = 0;

    // ������
    static int paperNum = 0;

    // ����õĵ��ʼ���
    static List<Map.Entry<String, Integer>> wordList = null;

    // �����ļ��ֽ�����
    static byte[] inputFileBytes = null;

    // �����ֽ�����
    static byte[] titleBytes = null;

    // ժҪ�ֽ�����
    static byte[] abstractBytes = null;

    // �����ӡ��ǰ���ĸ���
    static int sortedPrintNum = 10;

    // �����ļ���
    static String inputFileName = null;

    // ����ļ���
    static String outputFileName = null;

    // ����Ĵ���
    static int phraseWordNum = 1;

    // �Ƿ��ڼ��㵥�ʡ�����Ƶ��ʱ����Ȩ��
    static boolean useWordWeight = false;

    // title����Ȩ��
    static int titleWordWeight = 1;

    // abstract����Ȩ��
    static int abstractWordWeight = 1;

    /**
     * �������
     */
    public static void main(String[] args) {
		
        // ��ʼ��
        loadArgs(args);
        checkArgs();
        inputFileBytes = readFileToBytes(inputFileName);
		inputFileBytes = filterAscii(inputFileBytes);
		// System.out.println(new String(inputFileBytes));
        processBytes();

        try {
            Lib core = new Lib(titleBytes, abstractBytes, phraseWordNum,
                                titleWordWeight, abstractWordWeight);

            // Ԥ���������ַ�����������������������Ƶ��
            core.preProccess();
            core.collectWord();
            core.sortWordMap();

            // ��ȡ���
            charNum = core.getCharNum();
            wordNum = core.getWordNum();
            lineNum = core.getLineNum() + paperNum;
            wordList = core.getSortedList();

        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        // ���
        // printResult();
        writeResult();
    }

    /**
     * ��������ǰУ�����в����Ƿ���ȷ
     */
    static void checkArgs(){
        if (inputFileName == null ||
            !(new File(inputFileName).exists())){
            System.out.println("�����ļ�������");
            System.exit(1);
        }
    }

    /**
     * ���������в���
     *
     * @param args �����в�������
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
            System.out.println("δ�������");
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
     * ����������ֽ����飬�����ı����ժҪ�ֿ�����
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
                        break; //����
                    }
                }
                if (k == bytesLength){k--;}
                if (j == 1 && (k-i-TITLE_LABEL_LENGTH+1 > 0)){
                    // Title ��
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
                    // Abstract ��
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
                        break; //����
                    }
                }
                i = k + 1;
            }
        }
        // ����
        titleBytes = new byte[tbIndex];
        abstractBytes = new byte[abIndex];
        System.arraycopy(tBytes, 0, titleBytes, 0, tbIndex);
        System.arraycopy(aBytes, 0, abstractBytes, 0, abIndex);
        // System.out.println(new String(titleBytes));
        // System.out.println(new String(abstractBytes));
        // System.out.println("title num: " + tbNum + " abstract num: " + abNum);
    }
	
	/**
	 * ���˵��ֽ������еķ�ascii���ַ�
	 *
	 * @param bytes �ֽ�����
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
     * ��ȡ�ļ����ֽ�������
     *
     * @param fileName �ļ���
     *
     * @return bytes �ֽ�����
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
            System.out.println("�ļ�������");
        }
        catch(Exception e){
            System.out.println("��ȡ�ļ�����");
            e.printStackTrace();
        }

        return fileBytes;
    }

    /**
     * ��ӡ���������̨
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
     * ���������ļ���
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
            System.out.println("д���ļ�����");
            e.printStackTrace();
        }
    }

}