import java.io.File;
import java.util.Scanner;
import java.util.Vector;

public class Bayes {
    static Vector<String> setdata = new Vector<>();//读入训练数据
    static Vector<String> testdata = new Vector<>();//读入数据
    static Vector<int[]> catagory_R = new Vector<>();//存储类别R的所有数据
    static Vector<int[]> catagory_L = new Vector<>();//存储类别L的所有数据
    static Vector<int[]> catagory_B = new Vector<>();//存储类别B的所有数据
    static double[][][] Bayesmatrix=new double[3][5][4];
    public static boolean loadsetData(String url) {//加载测试的数据文件
        try {
            Scanner in = new Scanner(new File(url));//读入文件
            while (in.hasNextLine()) {
                String str = in.nextLine();//将文件的每一行存到str的临时变量中
                setdata.add(str);//将每一个样本点的数据追加到Vector 中
            }
            return true;
        } catch (Exception e) { //如果出错返回false
            return false;
        }
    }
    public static boolean loadtestData(String url) {//加载测试的数据文件
        try {
            Scanner in = new Scanner(new File(url));//读入文件
            while (in.hasNextLine()) {
                String str = in.nextLine();//将文件的每一行存到str的临时变量中
                testdata.add(str);//将每一个样本点的数据追加到Vector 中
            }
            return true;
        } catch (Exception e) { //如果出错返回false
            return false;
        }
    }

    public static void pretreatment(Vector<String> setdata) {   //数据预处理，将原始数据中的每一个属性值提取出来存放到Vector<double[]>  data中
        int i = 0;
        String t;
        while (i < setdata.size()) {//取出indata中的每一行值
            int[] tem = new int[4];
            t = setdata.get(i);
            String[] sourceStrArray = t.split(",", 5);//使用字符串分割函数提取出各属性值
            switch (sourceStrArray[0]) {
                case "R": {
                    for (int j = 1; j < 5; j++) {
                        tem[j - 1] = Integer.parseInt(sourceStrArray[j]);
                    }
                    catagory_R.add(tem);
                    break;
                }
                case "L": {
                    for (int j = 1; j < 5; j++) {
                        tem[j - 1] = Integer.parseInt(sourceStrArray[j]);
                    }
                    catagory_L.add(tem);
                    break;
                }
                case "B": {
                    for (int j = 1; j < 5; j++) {
                        tem[j - 1] = Integer.parseInt(sourceStrArray[j]);
                    }
                    catagory_B.add(tem);
                    break;
                }
            }
            i++;
        }
    }
    public static void initbayesmatrix(Vector<int[]> catagory,int t) {
        for (int i=0;i<catagory.size();i++){
            for (int j=0;j<4;j++){
                Bayesmatrix[t][catagory.get(i)[j]-1][j]++;
            }
        }
        for (int i=0;i<5;i++){
            for (int j=0;j<4;j++){
                Bayesmatrix[t][i][j]=Bayesmatrix[t][i][j]/catagory.size();
            }
        }
    }
    public static double bayes(int[] x,int t) {
        double[] ai_y = new double[4];
        for (int i = 0; i < 4; i++) {
            ai_y[i] = Bayesmatrix[t][x[i]-1][i];
        }
        return ai_y[0] * ai_y[1] * ai_y[2] * ai_y[3];
    }
    public static void main(String[] args) {


        loadsetData("src/set.txt");
        loadtestData("src/testset.txt");
        pretreatment(setdata);
        double p_yR = (double) catagory_R.size() / (double) (setdata.size());//表示概率p（R）
        double p_yB = (double) catagory_B.size() / (double) (setdata.size());//表示概率p（B）
        double p_yL = (double) catagory_L.size() / (double) (setdata.size());//表示概率p（L）
        initbayesmatrix(catagory_R,0);
        initbayesmatrix(catagory_B,1);
        initbayesmatrix(catagory_L,2);
//        for (int i=0;i<5;i++){
//            for (int j=0;j<4;j++){
//                System.out.print(Bayesmatrix[2][i][j]+"\t");
//            }
//            System.out.println();
//        }
        int[] x = new int[4];
        double x_in_R, x_in_L, x_in_B;
        int sumR=0, sumL=0, sumB=0;
        double correct=0;
        int r = 0;
        while (r < setdata.size()) {

            for (int i = 0; i < 4; i++)
                //读取数字放入数组的第i个元素
                x[i] = Integer.parseInt(setdata.get(r).split(",", 5)[i + 1]);

            x_in_R = bayes(x, 0) * p_yR;
            x_in_B = bayes(x, 1) * p_yB;
            x_in_L = bayes(x, 2) * p_yL;


            if (x_in_B == Math.max(Math.max(x_in_B, x_in_L), x_in_R)) {
                sumB++;
                if(testdata.get(r).split(",",5)[0].equals("B"))
                    correct++;
            } else if (x_in_L == Math.max(Math.max(x_in_B, x_in_L), x_in_R)) {
                sumL++;
                if(testdata.get(r).split(",",5)[0].equals("L"))
                    correct++;
            } else if (x_in_R == Math.max(Math.max(x_in_B, x_in_L), x_in_R)) {
                sumR++;
                if(testdata.get(r).split(",",5)[0].equals("R"))
                    correct++;
            }
            r++;
        }

        System.out.println("使用训练样本进行分类器检验得到结果统计如下：");
        System.out.println("R类有："+sumR+"    实际有R类样本"+catagory_R.size()+"个");
        System.out.println("L类有："+sumL+"    实际有L类样本"+catagory_L.size()+"个");
        System.out.println("B类有："+sumB+"      实际有B类样本"+catagory_B.size()+"个");

        System.out.println("分类的正确率为"+correct*1.0/testdata.size()*100+"%");

    }

}
