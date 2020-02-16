import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectFileMaker {
    public static final double SCALE = 1.0;
    public static final int DIVISIONS = 64;
    private static String s(double x, double y, double z){
        return "v " + x+" "+y+" "+z+"\n";
    }
    public static void makeObjectFile(String function, String varOne, String varTwo, HashMap<String, Double> fixed
    , double minOne, double maxOne, double minTwo, double maxTwo){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("output.obj")));
        function = function.replaceAll(" ","");
        Parser.Expression exp = Parser.parseExpression(function);

        ArrayList<String> strings = new ArrayList<>();

        double incOne = (maxOne - minOne)/DIVISIONS;
        double incTwo = (maxTwo - minTwo)/DIVISIONS;
        double max = - Integer.MIN_VALUE;
        double[][] heights = new double[DIVISIONS][DIVISIONS];

        for(int i = 0;i< DIVISIONS;i++){
            for(int j = 0;j<DIVISIONS;j++){
                fixed.put(varOne, minOne + incOne * i);
                fixed.put(varTwo,  minTwo + incTwo * j);
                double out = exp.evaluate(fixed);
                max = Math.max(max, out);
                heights[i][j] = out;
            }
        }

        for(int i = 0;i< DIVISIONS;i++){
            for(int j = 0;j<DIVISIONS;j++){
                 double z = heights[i][j]/(Math.abs(max)) * SCALE * (maxOne - minOne);
                 bw.write(s(incOne * i + minOne, incTwo * j + minTwo, z));
            }
        }

        for(int i = 0;i< DIVISIONS - 1;i++){
            for(int j = 0;j<DIVISIONS - 1;j++){
                double xa = incOne * i + minOne, xb = incOne * (i+1) + minOne;
                double ya = incTwo * j + minTwo, yb = incTwo * (j + 1) + minOne;
                double za = heights[i][j], zb = heights[i+1][j], zc = heights[i+1][j+1], zd = heights[i][j+1];
                bw.write("f " + ((j*DIVISIONS) + i +1) +"/"+ ((j*DIVISIONS) + i + 1 +1)+"/"+(((j+1)*DIVISIONS) + i + 1) +"\n");

                bw.write("f " + (((j+1)*DIVISIONS) + i+ 1 + 1) +" "+ ((j*DIVISIONS) + i + 1 + 1)+" "+(((j+1)*DIVISIONS) + i + 1)+"\n");
            }
        }


         bw.flush();
        bw.close();
     }catch(Throwable t){}



    }

}
