import os
import re
import subprocess
import pyecharts

# 防止编译多次
has_compile_crawler = False
has_compile_wordcount = False

def useJavaCrawler():
    global has_compile_crawler
    src = os.path.join(os.getcwd(), '"221600219&221600212"\\cvpr\\')
    cwd = src.replace("\\", "/").replace('"', '')
    compile_cmd = "javac -cp jsoup-1.11.3.jar; Main.java"
    run_cmd = "java -cp jsoup-1.11.3.jar; Main"
    if not has_compile_crawler:
        popen = subprocess.Popen(compile_cmd, shell=True, cwd=cwd)
        popen.wait(2) # 编译
        print ("编译Java爬虫脚本完成...")
        has_compile_crawler = True
    print ("启动Java爬虫...")
    popen = subprocess.Popen(run_cmd, shell=True, cwd=cwd)
    popen.wait(600) # 运行Java爬虫
    print ("运行Java爬虫脚本完成...")

def useJavaWordCount(printWordNum, phraseWordNum):
    global has_compile_wordcount
    src = os.path.join(os.getcwd(), '"221600219&221600212"\\src\\')
    cwd = src.replace("\\", "/").replace('"', '')
    compile_cmd = "javac Main.java"
    run_cmd = "java Main -i ../cvpr/result.txt -o output.txt -w 1 -n %d -m %d" % (
        printWordNum, phraseWordNum)
    if not has_compile_wordcount:
        popen = subprocess.Popen(compile_cmd, shell=True, cwd=cwd)
        popen.wait(2) # 编译
        print ("编译Java WordCount脚本完成...")
        has_compile_wordcount = True
    popen = subprocess.Popen(run_cmd, shell=True, cwd=cwd)
    popen.wait(20) # 运行WordCount
    print ("运行Java WordCount脚本完成...")

class CvprAnalysis:

    def __init__(self):
        with open ("221600219&221600212/src/output.txt", "r+") as f:
            self.data = f.read().strip().split('\n')
        self.char_num = self.data[0].split(": ")[1]
        self.word_num = self.data[1].split(": ")[1]
        self.line_num = self.data[2].split(": ")[1]
        self.word_map = {}
        for str in self.data[3:]:
            res = re.search(r"<(.*)>", str.split(": ")[0])
            self.word_map[res.group(1)] = str.split(": ")[1]

    def paintBar(self, phraseWordNum):
        attr = list(self.word_map.keys())
        v1 = list(self.word_map.values())
        bar = pyecharts.Bar("词组词数为%d时的词频统计"%phraseWordNum)
        bar.add("词组", attr, v1, mark_line=["average"], mark_point=["max", "min"],
            is_stack=False, is_more_utils=True)
        # bar.print_echarts_options()
        bar.render(path="cvpr2018DataAnalysis_%d.html"%phraseWordNum)

    def paintWordColud(self, phraseWordNum):
        name = list(self.word_map.keys())
        value = list(self.word_map.values())
        wordcloud = pyecharts.WordCloud("词组词数为%d时的词云图"%phraseWordNum, width=1300, height=620)
        wordcloud.add("", name, value, word_size_range=[10, 100])
        wordcloud.render(path="cvpr2018DataAnalysis_wordcolud%d.html"%phraseWordNum)

if __name__ == '__main__':
    # useJavaCrawler()
    # 分析词组（包含1-5个单词）
    # for i in range(5):
    #     useJavaWordCount(5, i+1)
    #     analyzer = CvprAnalysis()
    #     analyzer.paintBar(5, i+1)
    useJavaWordCount(500, 2)
    analyzer = CvprAnalysis()
    analyzer.paintWordColud(2)

