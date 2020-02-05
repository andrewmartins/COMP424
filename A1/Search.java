import java.util.*;

class NeighbourPoint{
    public double[] point;
    public double value;

    public NeighbourPoint(double[] point, double value){
        this.point = point;
        this.value = value;
    }
}

public class Search{
    public static double f1(double x, double y){
        return Math.sin(2*x) + Math.cos(y/2);
    }
    public static double f2(double x, double y){
        return Math.abs(x-2) + Math.abs((y/2)+1)-4;
    }
    public static ArrayList<NeighbourPoint> genNeighbours(double x0, double y0, double stepSize, int functionType){
        double x1 = x0 + stepSize;
        double x2 = x0 - stepSize;
        double y1= y0 + stepSize;
        double y2 = y0 + stepSize;
        double neighboursPoints[][] = {{x1,y1},{x1,y2},{x2,y1},{x2,y2},{x0,y1},{x0,y2},{x1,y0},{x2,y0}};
        ArrayList<NeighbourPoint> neighSet = new ArrayList<NeighbourPoint>();
        for(double[] point:neighboursPoints){
            if(functionType==1){
                NeighbourPoint neighP = new NeighbourPoint(point, f1(point[0],point[1]));
                neighSet.add(neighP);
            }
            else{
                NeighbourPoint neighP = new NeighbourPoint(point, f2(point[0], point[1]));
                neighSet.add(neighP);
            }
        }
        return neighSet;
    }
    public static NeighbourPoint bestNeigh(ArrayList<NeighbourPoint> neighSet){
        NeighbourPoint maxP = neighSet.get(0);
        for(NeighbourPoint neighPoint:neighSet){
            if(neighPoint.value > maxP.value){
                maxP.point = neighPoint.point;
            }
        }
        return maxP;
    }
    public static double[] hillClimbing(double[] initP, int functionType, double stepSize){
        double step = 0;
        double value;
        if(functionType==1){
            value = f1(initP[0],initP[1]);
        }
        else{
            value = f2(initP[0],initP[1]);
        }
        NeighbourPoint currP = new NeighbourPoint(initP, value);
        NeighbourPoint nextNeighbour = new NeighbourPoint(initP, value);
        double[] move = new double[2];
        while((currP.point[0] <= 10 && currP.point[0] >= 0) && (currP.point[1] <= 10 && currP.point[1] >= 0)){
            step+=1;
            ArrayList<NeighbourPoint> neighbours = genNeighbours(currP.point[0],currP.point[1],stepSize,functionType);
            nextNeighbour = bestNeigh(neighbours);
            move[0] = step;
            if(nextNeighbour.value < currP.value){
                move[1] = currP.value;
                return move;
            }
            else{
                currP = nextNeighbour;
                move[1] = currP.value;
            }
        }
        return move;
    }
    public static ArrayList<NeighbourPoint> sortNeighbours(ArrayList<NeighbourPoint> neighbourPoints){
        for(int i = 0; i<neighbourPoints.size();i++){
            if(i+1 == neighbourPoints.size()){
                break;
            }
            if(neighbourPoints.get(i).value<neighbourPoints.get(i+1).value){
                NeighbourPoint temp = neighbourPoints.get(i);
                neighbourPoints.set(i,neighbourPoints.get(i+1));
                neighbourPoints.set(i+1,temp);
            }
        }
        return neighbourPoints;
    }
    public static ArrayList<NeighbourPoint> updateBeamSet(ArrayList<NeighbourPoint> sortedInitNeighSet, int beamWidth){
        ArrayList<NeighbourPoint> beamSet = new ArrayList<NeighbourPoint>();
        for(int i =0; i< beamWidth; i++){
            beamSet.add(sortedInitNeighSet.get(i));
        }
        return beamSet;
    }
    public static double[] localBeamSearch(int functionType, double stepSize, int beamWidth){
        ArrayList<NeighbourPoint> maxNeighbour = new ArrayList<NeighbourPoint>();
        for(int i=0;i<beamWidth;i++){
            double x0 = Math.random()*10;
            double y0 = Math.random()*10;
            ArrayList<NeighbourPoint> initNeighSet = genNeighbours(x0,y0, stepSize, functionType);
            ArrayList<NeighbourPoint> sortedInitNeighSet = sortNeighbours(initNeighSet);
            maxNeighbour.add(sortedInitNeighSet.get(0));
        }
        ArrayList<NeighbourPoint> beamSet = updateBeamSet(maxNeighbour, beamWidth);
        NeighbourPoint maxPoint = beamSet.get(0);
        double step = 0;
        double[] move = new double[2];
        while((maxPoint.point[0] <= 10 && maxPoint.point[0] >= 0) && maxPoint.point[1] <= 10 && maxPoint.point[1] >= 0){
            step+=1;
            ArrayList<NeighbourPoint> beamNeighChildren = new ArrayList<NeighbourPoint>();
            for(NeighbourPoint nPoint:beamSet){
                ArrayList<NeighbourPoint> beamNeighbours = genNeighbours(nPoint.point[0], nPoint.point[1], stepSize, functionType);
                for(NeighbourPoint beamNPoint:beamNeighbours){
                    beamNeighChildren.add(beamNPoint);
                }
            }
            ArrayList<NeighbourPoint> sortBeamNeighChildren = sortNeighbours(beamNeighChildren);
            beamSet = updateBeamSet(sortBeamNeighChildren, beamWidth);
            NeighbourPoint nextNeighbour = beamSet.get(0);
            if(nextNeighbour.value <= maxPoint.value){
                move[0] = step;
                move[1] = maxPoint.value;
                return move;
            }
            if(nextNeighbour.point[0]>10 || nextNeighbour.point[1]<0 || nextNeighbour.point[1]>10 || nextNeighbour.point[1]<0){
                move[0] = step;
                move[1] = maxPoint.value;
                return move;
            }
            maxPoint=nextNeighbour;
        }
        move[0] = step;
        move[1] = maxPoint.value;
        return move;
    }
    public static double[] standard_dev_average(double[] data){
        double ave1 = 0;
        double ave2 = 0;   
        int j = 0;
        double[] stdd_ave = new double[2];
        for(int i = 0;i<data.length;i++){
            j=i;
            j++;
            ave1 = (j-1)*ave1/j+((data[i])*(data[i]))/j;
            ave2 = (j-1)*ave2/j+data[i]/j;
        }
        double standard_dev = Math.sqrt(ave1-(ave2*ave2));
        stdd_ave[0] = standard_dev;
        stdd_ave[1] = ave2;
        return stdd_ave;
    }
    public static void main(String[] args){
        double[] stepSizes = {0.01, 0.05, 0.1, 0.2};
        int[] beamWidth = {2,4,8,16};
        double[] f1_hillClimb_steps = new double[100];
        double[] f2_hillClimb_steps = new double[100];
        double[] f1_hillClimb_max_value = new double[100];
        double[] f2_hillClimb_max_value = new double[100];
        for(double size:stepSizes){
            for(int i=0; i<100; i++){
                double x0 = Math.random()*10;
                double y0 = Math.random()*10;
                double[] point = {x0,y0};
                double[] f1_value = hillClimbing(point, 1, size);
                double[] f2_value = hillClimbing(point, 2, size);
                f1_hillClimb_steps[i] = f1_value[0];
                f2_hillClimb_steps[i] = f2_value[0];
                f2_hillClimb_max_value[i] = f2_value[1];
                f1_hillClimb_max_value[i] = f1_value[1];
            }
            double[] f1_hc_steps_sa = standard_dev_average(f1_hillClimb_steps);
            double[] f2_hc_steps_sa = standard_dev_average(f2_hillClimb_steps);
            double[] f1_hc_max_value_sa = standard_dev_average(f1_hillClimb_max_value);
            double[] f2_hc_max_value_sa = standard_dev_average(f2_hillClimb_max_value);
            System.out.println("------Hill Climbing-------");
            System.out.println("Step Size: " + size);
            System.out.println("---Function 1---");
            System.out.println("The average number of steps is: " + f1_hc_steps_sa[1]);
            System.out.println("The standard devation of the number of steps is: " + f1_hc_steps_sa[0]);
            System.out.println("The average of the max vlaue is: " + f1_hc_max_value_sa[1]);
            System.out.println("The standard devation of the max value is: " + f1_hc_max_value_sa[0]);
            System.out.println("---Function 2---");
            System.out.println("The average number of steps is: " + f2_hc_steps_sa[1]);
            System.out.println("The standard devation of the number of steps is: " + f2_hc_steps_sa[0]);
            System.out.println("The average of the max vlaue is: " + f2_hc_max_value_sa[1]);
            System.out.println("The standard devation of the max value is: " + f2_hc_max_value_sa[0]);
            double[] f1_LBS_steps = new double[100];
            double[] f2_LBS_steps = new double[100];
            double[] f1_LBS_max_value = new double[100];
            double[] f2_LBS_max_value = new double[100];
            for(int width:beamWidth){
                for(int i=0; i<100; i++){
                    double[] f1_value = localBeamSearch(1,size, width);
                    double[] f2_value = localBeamSearch(2,size, width);
                    f1_LBS_steps[i] = f1_value[0];
                    f2_LBS_steps[i] = f2_value[0];
                    f2_LBS_max_value[i] = f2_value[1];
                    f1_LBS_max_value[i] = f1_value[1];
                }
                double[] f1_lbs_steps_sa = standard_dev_average(f1_LBS_steps);
                double[] f2_lbs_steps_sa = standard_dev_average(f2_LBS_steps);
                double[] f1_lbs_max_value_sa = standard_dev_average(f1_LBS_max_value);
                double[] f2_lbs_max_value_sa = standard_dev_average(f2_LBS_max_value);
                System.out.println("--------Local Beam Search-------");
                System.out.println("Step Size: " + size);
                System.out.println("Width: " + width);
                System.out.println("---Function 1---");
                System.out.println("The average number of steps is: " + f1_lbs_steps_sa[1]);
                System.out.println("The standard devation of the number of steps is: " + f1_lbs_steps_sa[0]);
                System.out.println("The average of the max vlaue is: " + f1_lbs_max_value_sa[1]);
                System.out.println("The standard devation of the max value is: " + f1_lbs_max_value_sa[0]);
                System.out.println("---Function 2---");
                System.out.println("The average number of steps is: " + f2_lbs_steps_sa[1]);
                System.out.println("The standard devation of the number of steps is: " + f2_lbs_steps_sa[0]);
                System.out.println("The average of the max vlaue is: " + f2_lbs_max_value_sa[1]);
                System.out.println("The standard devation of the max value is: " + f2_lbs_max_value_sa[0]);
            }
        }
    }
}