import java.util.*;

public class Lib{

    // ����
    private static int lineNum = 0;

    // �ַ���
    private static int charNum = 0;

    // �������������ĸ�Ӣ����ĸ��ͷ�������ִ�Сд
    private static int wordNum = 0;

    // ���ʼ��ϣ�<����, ��Ŀ>
    private static Map<String, Integer> wordMap = null;

    // ����õĵ��ʼ���
    private static List<Map.Entry<String, Integer>> wordList = null;

    // �ֽ�����
    private static byte[] titleBytes = null;
	private static byte[] abstractBytes = null;

    // ���ȣ��ֽ����鳤��
    private static int titleBytesLength = 0;
	private static int abstractBytesLength = 0;
	
	// �������
	private static int phraseWordNum = 1;
	
	// Ȩ��
	private static int titleWordWeight = 1;
	private static int abstractWordWeight = 1;
	
	// ����������С��ͷ��ĸ��
	private static final int MIN_WORD_LETTER_NUM = 4;

	
    public static int getCharNum(){return charNum;}
    public static int getWordNum(){return wordNum;}
    public static int getLineNum(){return lineNum;}
    public static List<Map.Entry<String, Integer>> getSortedList(){return wordList;}

    public static void main(String[] args) {
        System.out.println(isSeparator((byte)'&'));
    }

	
    /**
     * ��ʼ������
     */
    public Lib(byte[] tBytes, byte[] aBytes, int phraseWordNum, int titleWordWeight,
				int abstractWordWeight) throws Exception{
					
		if ((tBytes == null || tBytes.length == 0) || aBytes == null || aBytes.length == 0){
			throw new Exception("�ֽ����鲻��Ϊ�գ�");
		}
        this.titleBytes = tBytes;
		this.abstractBytes = aBytes;
		this.titleBytesLength = tBytes.length;
		this.abstractBytesLength = aBytes.length;
		this.phraseWordNum = phraseWordNum;
		this.titleWordWeight = titleWordWeight;
		this.abstractWordWeight = abstractWordWeight;
        this.wordMap = new TreeMap<String, Integer>();
        charNum = 0;
        wordNum = 0;
        lineNum = 0;
        wordList = null;
    }

	/**
	 * ��titleBytes��abstractBytes������Ԥ����
	 */
	public void preProccess(){
		preProccess_(this.titleBytes);
		preProccess_(this.abstractBytes);
	}
	
    /**
     * Ԥ����
     *      ����д��ĸתΪСд��ĸ
     *      �����ֽ������е��ַ������������ַ���//r//n����һ���ַ�
	 *      ����\n����
     *      �����ֽ��������������
	 *
	 * @param bytes �ֽ�����
     */
    static void preProccess_(byte[] bytes){
		int bytesLength = bytes.length;
        // �����ַ���������
        for (int i = 0; i < bytesLength; i ++){
            // Ԥ������д��ĸͳһתΪСд��ĸ��ͬʱ���˷�ascii���ַ�
			if (bytes[i] >= 0 && bytes[i] < 128){
				if (bytes[i] >= 65 && bytes[i] <= 90){
					bytes[i] += 32;
				}
				if (bytes[i] == 10){
					// ��������
					if (checkLine(bytes, i)){
						lineNum ++;
					}
					// ������Ϊ\nʱ��֤����©�ַ�
					if (i-1 >= 0 && bytes[i-1] != 13){
						charNum ++;
					}
				}else{
					charNum ++;
				}
			}
        }
        // ע�����һ�в��Իس���β�������ͬ������һ��
        if (bytes[bytesLength-1] != 10 && checkLine(bytes, bytesLength-1)){
            lineNum ++;
        }
    }
	
	/**
	 * ��titleBytes��abstractBytes�����д��ռ���ͬʱ����Ȩ��
	 */
	public static void collectWord(){
		collectWord_(titleBytes, titleWordWeight);
		collectWord_(abstractBytes, abstractWordWeight);
	}

    /**
     * ���㵥��/����������������/����װ�뼯�ϡ�ͳ�Ƹ���
	 *
	 * @param bytes �ֽ�����
	 * @param wordWeight Ȩ��
     */
    static void collectWord_(byte[] bytes, int wordWeight){
		int bytesLength = bytes.length;
        int checkWordResult = -1;
		String wordString = null;
        for (int i = 0; i < bytesLength; i ++){
            if (isLetter(bytes[i])){
                checkWordResult = checkWord(bytes, i, MIN_WORD_LETTER_NUM);
                if (checkWordResult > 0){
					if (phraseWordNum == 1){
						wordString = subBytesToString(bytes, i, checkWordResult);
					} else {
						wordString = checkPhrase(bytes, i, MIN_WORD_LETTER_NUM, phraseWordNum);
					}
					// System.out.println(wordString);
					
					if (wordString != null){
						// ������ɴ��飬���뼯����
						if (wordMap.containsKey(wordString)){
							wordMap.put(wordString, wordMap.get(wordString)+(wordWeight));
						} else{
							wordMap.put(wordString, wordWeight);
						}
					}
                    wordNum ++;
                    // ��ת����ĩβ
                    i = checkWordResult;
                } else{
                    // ���ǵ��ʣ�����ͬ����ת����ĩβ
                    i = - checkWordResult;
                }
                // System.out.println(checkWordResult);
            }
        }
    }

    /**
     * ���յ���Ƶ������
     */
    public static void sortWordMap(){
        wordList = new ArrayList<Map.Entry<String,Integer>>(wordMap.entrySet());
        Collections.sort(wordList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> word1, 
								Map.Entry<String, Integer> word2) {
                return word2.getValue() - word1.getValue();
            }
        });
    }

    /**
     * ȡ���ֽ������е�ĳһ��ת��String����
     *
     * @param bytes �ֽ�����
     * @param start ��ʼ�±�
     * @param end ��ֹ�±�
     *
     * @return aWordString ��ȡת�ɵ��ַ���
     */
    public static String subBytesToString(byte[] bytes, int start, int end){
        if (end > start){
            byte[] aWordByte = new byte[end-start];
            System.arraycopy(bytes, start, aWordByte, 0, end-start);
            return new String(aWordByte);
        }
        return null;
    }

    /**
     * �жϸû����ַ��������Ƿ��Ƿǿհ���
     *
     * @param bytes �ֽ�����
     * @param lineEnd ���з��±꣨��ĩβ��
     *
     * @return true �ǿ��� fasle �ǿ���
     */
    static boolean checkLine(byte[] bytes, int lineEnd){
        int notBlankCharNum = 0;
        for (int i = lineEnd-1; i >= 0; i --){
            if (bytes[i] == 10){
                // ����ǰһ�з���
                break;
            } else if (!isBlankChar(bytes[i])){
                // ��ǰ��ĸ���ǿո���Ʊ��
                notBlankCharNum ++;
            }
        }
        return (notBlankCharNum > 0);
    }

    /**
     * �ж�byte�ֽ��Ƿ�����ĸ
     *
     * @param b �ֽ�
     *
     * @return true ����ĸ false ������ĸ
    **/
    static boolean isLetter(byte b){
        return (b >= 97 && b <= 122) || (b >= 65 && b <= 90);
    }

    /**
     * �ж�Byte�ֽ��Ƿ�������
     *
     * @param b �ֽ�
     *
     * @return true ������ false ��������
     */
    static boolean isNum(byte b){
        return (b >= 48 && b <= 57);
    }

    /**
     * �ж�byte�ֽ��Ƿ��ǿհ��ַ�
     *
     * @param b �ֽ�
     *
     * @return true �ǿհ��ַ� false ���ǿհ��ַ�
    **/
    static boolean isBlankChar(byte b){
        return (b <= 32 || b == 127);
    }

    /**
     * �ж�Byte�ֽ��Ƿ��Ƿָ���
     *
     * @param b �ֽ�
     *
     * @return true �Ƿָ��� false ���Ƿָ���
     */
    static boolean isSeparator(byte b){
        return !(isLetter(b)|| isNum(b));
    }

	/**
	 * �жϴ�ĳ���±꿪ʼ��һ�γ����Ƿ��ܹ�����Ҫ��Ĵ��鳤��
	 *
	 * @param bytes �ֽ�����
     * @param start ��ʼ�±�
     * @param minWordLength ������С����Ŀ�ͷ��ĸ��
	 * @param phraseWordNum ��������Ĵ���
	 *
	 * @return null ���ܹ��ɴ��飨���Ϸ��ַ����ߴ������㣩
	 *       String �ܹ��ɴ��飬���ش���
	 */
	static String checkPhrase(byte[] bytes, int start, int minWordLength, int phraseWordNum){
		int bytesLength = bytes.length;
		int nowIndex = start;
		int checkWordResult = -1;
		for (int i = 0; nowIndex < bytesLength && i < phraseWordNum; i++){
			checkWordResult = checkWord(bytes, nowIndex, MIN_WORD_LETTER_NUM);
			if (checkWordResult > 0){
				String aWordString = subBytesToString(bytes, nowIndex, checkWordResult);
				// System.out.println(aWordString);
				// ��ת����ĩβ
				nowIndex = checkWordResult;
				// �ж��Ƿ��ǻ��зָ���(\r\n����\n)�����ǣ��޷����ɴ���
				if ((i < phraseWordNum-1) && (nowIndex < bytesLength-1 && bytes[nowIndex] == 10 ||
						(nowIndex < bytesLength-1 && bytes[nowIndex + 1] == 10))){
					// System.out.println("��������");
					return null;
				}
				// �����ָ������ָ������ܲ�ֹһ������һ��û��\n \r\n
				if (i != phraseWordNum-1){
					while (nowIndex < bytesLength && isSeparator(bytes[nowIndex])){
						if (bytes[nowIndex] == 10){
							return null;
						}
						nowIndex ++;
					}
				}
			} else{
				// System.out.println("���ǵ��ʣ��޷����ɴ���");
				return null;
			}
			if (i != phraseWordNum-1 && nowIndex == bytesLength){
				return null;
			}
		}
		// ���㹹�ɵ������������ش���
		return subBytesToString(bytes, start, nowIndex);
	}
	
    /**
     * �жϴ�ĳ���±꿪ʼ��һ�γ����Ƿ��ǵ���
     *
     * @param bytes �ֽ�����
     * @param start ��ʼ�±�
     * @param minWordLength ������С����Ŀ�ͷ��ĸ��
     *
     * @return int < 0 ���ǵ��ʣ����Ĵ�ĩβ�ָ������±�
     *      int > 0 �ǵ��ʣ�����ĩβ�ָ������±�
    **/
    static int checkWord(byte[] bytes, int start, int minWordLength){
        int bytesLength = bytes.length;
        int i = start;
        int checkWordResult = 0;

        if (start > 0 && ! isSeparator(bytes[start-1])){
            checkWordResult = -1;
        } else{
            for (; i < start + minWordLength && i < bytesLength; i++){
                // ������С����Ŀ�ͷ��ĸ��
                if (! isLetter(bytes[i])){
                    checkWordResult = -2;
                    break;
                }
            }
            // �ѵ���β����������С��ͷ��ĸ��
            if (i == bytesLength && i - start < minWordLength){
                checkWordResult = -3;
            }
        }
        for (; i < bytesLength; i++){
            // �������ʽ��������ش�ĩβ���±�
            if (isSeparator(bytes[i])){
                // �ַ����Ƿָ���
                break;
            }
        }

        return checkWordResult < 0 ? -i : i;
    }
}