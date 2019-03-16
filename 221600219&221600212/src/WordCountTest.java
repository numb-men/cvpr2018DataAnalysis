import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class WordCountTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    //测试 行数
    public void testLineNum(){
        String titleStr = "name love\r\n";
        String abstractStr = "hi name love\r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    2, 1,1);
            core.preProccess();
            Assert.assertEquals(2, core.getLineNum());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //测试 字符数
    public void testCharNum(){
        String titleStr = "name love\r\n";
        String abstractStr = "hi name love\r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    2, 1,1);
            core.preProccess();
            Assert.assertEquals(23, core.getCharNum());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //测试 单词数 m -1
    public void testWordNum1(){
        String titleStr = "name love\r\n";
        String abstractStr = "hi name love\r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    1, 1,1);
            core.preProccess();
            core.collectWord();
            Assert.assertEquals(4, core.getWordNum());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //测试 单词数 m -1
    public void testWordNum2(){
        String titleStr = " 123abc cbc name love hi-hello\r\n";
        String abstractStr = "hi name love\r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    1, 10,1);
            core.preProccess();
            core.collectWord();
            Assert.assertEquals(5, core.getWordNum());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //测试 单词数 m -2
    public void testWordNum3(){
        String titleStr = " 123abc cbc name love hi-hello\r\n";
        String abstractStr = "hi name love\r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    2, 10,1);
            core.preProccess();
            core.collectWord();
            Assert.assertEquals(5, core.getWordNum());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //测试 单词数 m -2
    public void testWordNum4(){
        String titleStr = " a aaa aaaa aaaaa aa-aaaa\r\n";
        String abstractStr = "hi name love\r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    2, 10,1);
            core.preProccess();
            core.collectWord();
            Assert.assertEquals(5, core.getWordNum());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //测试 单词数 m -2
    public void testWordNum5(){
        String titleStr = " a aaa aa....aa aaaaa aa-aaaa\r\n";
        String abstractStr = "hi name love\r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    2, 10,1);
            core.preProccess();
            core.collectWord();
            Assert.assertEquals(4, core.getWordNum());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //测试 词频 m -2
    public void testPhraseFrequency(){
        String titleStr = " 123abc cbc name love hi-hello\r\n";
        String abstractStr = "hi name love\r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    2, 10,1);
            core.preProccess();
            core.collectWord();
            core.sortWordMap();
            List<Map.Entry<String, Integer>> wordList = core.getSortedList();
            Map.Entry<String, Integer> entry = wordList.get(0);
            Assert.assertEquals("name love", entry.getKey());
            Assert.assertEquals(11, (int)entry.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //测试 词频 m -2
    public void testPhraseFrequency1(){
        String titleStr = " Embodied Question Answering\r\n";
        String abstractStr = "We present a new AI task -- Embodied Question Answering (EmbodiedQA) -- where an agent is spawned at a random location in a 3D environment and asked a question (\"What\r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    2, 10,1);
            core.preProccess();
            core.collectWord();
            core.sortWordMap();
            List<Map.Entry<String, Integer>> wordList = core.getSortedList();
            Map.Entry<String, Integer> entry = wordList.get(0);
            Assert.assertEquals("embodied question", entry.getKey());
            Assert.assertEquals(11, (int)entry.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //测试 词频 m -2
    public void testPhraseFrequency2(){
        String titleStr = " abcd123 cbc name-love hi-hello\r\n";
        String abstractStr = "hi name-love asd name-love\r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    2, 10,1);
            core.preProccess();
            core.collectWord();
            core.sortWordMap();
            List<Map.Entry<String, Integer>> wordList = core.getSortedList();
            Map.Entry<String, Integer> entry = wordList.get(0);
            Assert.assertEquals("name-love", entry.getKey());
            Assert.assertEquals(12, (int)entry.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //测试 词频 m -2
    public void testPhraseFrequency3(){
        String titleStr = " 123abc cbc name love hi-hello\r\n";
        String abstractStr = "hi name love abcd123\r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    2, 10,1);
            core.preProccess();
            core.collectWord();
            core.sortWordMap();
            List<Map.Entry<String, Integer>> wordList = core.getSortedList();
            Map.Entry<String, Integer> entry = wordList.get(1);
            Assert.assertEquals("love abcd123", entry.getKey());
            Assert.assertEquals(1, (int)entry.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    //测试 词频 m -2
    public void testPhraseFrequency4(){
        String titleStr = " 123abc cbc name love hi-hello\r\n";
        String abstractStr = "hi name love aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa  \r\n";
        try {
            Lib core = new Lib(titleStr.getBytes(), abstractStr.getBytes(),
                    2, 10,1);
            core.preProccess();
            core.collectWord();
            core.sortWordMap();
            List<Map.Entry<String, Integer>> wordList = core.getSortedList();
            Map.Entry<String, Integer> entry = wordList.get(0);
            Assert.assertEquals("aaaa aaaa", entry.getKey());
            Assert.assertEquals(13, (int)entry.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
