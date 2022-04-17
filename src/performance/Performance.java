package performance;

import java.util.Scanner;

public class Performance {

 private final static int[][] tor = {{-25,  0, 25, 50},
            {149,187,231,280},
            {162,204,252,306},
            {177,223,275,334},
            {193,244,301,365},
            {211,266,328,398},
            {231,291,359,436},
            {253,319,393,477},
            {277,349,430,522},
            {304,382,472,572},
            {333,419,517,627},
            {365,460,567,688}};
 private final static int[][] tod = {{-25,  0, 25, 50},
            {262,328,401,484},
            {285,356,437,526},
            {311,388,475,572},
            {338,422,517,623},
            {368,460,564,679},
            {402,502,614,740},
            {438,547,670,808},
            {478,597,732,882},
            {522,652,799,963},
            {571,713,874,1053},
            {624,780,956,1152}};

private final static int[][] lr = {{-25,  0, 25, 50},
            {149,164,179,194},
            {154,170,186,201},
            {160,176,192,209},
            {166,183,200,216},
            {172,190,207,225},
            {179,197,215,233},
            {186,205,223,242},
            {193,212,232,251},
            {200,221,241,261},
            {208,229,250,271},
            {217,238,260,282}};

private final static int[][] ld ={{-25,0,25,50},
            {358,373,388,403},
            {363,379,395,410},
            {369,385,401,418},
            {375,392,409,425},
            {381,399,416,434},
            {388,406,424,442},
            {395,414,432,451},
            {402,421,441,460},
            {410,430,450,470},
            {417,438,459,480},
            {426,447,469,491}};

    public static int[][] getLr() {
        return lr;
    }

    public static int[][] getLd() {
        return ld;
    }

    public static int[][] getTor() {
        return tor;
    }

    public static int[][] getTod() {
        return tod;
    }

    public static int findPaZone(int QNH, int altitude){// Returns Pressure Altitude, when divided by 1000 used as index

        int standard = 1013;
        int difference = Math.abs(standard-QNH) * 30;

        return (QNH > standard)? altitude - difference : altitude + difference;

    }

    public static int findTempZone(int[][] arr, int temp){
        return (temp >= arr[0][0] && temp < arr[0][1])?0:(temp >= arr[0][1] && temp < arr[0][2])? 1 :(temp >= arr[0][2] && temp < arr[0][3])? 2:-1;
    }

    // Interpolates succesfully
    public static int interpolate(int[][] arr, int QNH, int altitude, int temp){

        int paZone = findPaZone(QNH, altitude)/1000;
        int tempZone = findTempZone(arr,temp);
        int pa = findPaZone(QNH, altitude);

        int i = arr[paZone+2][tempZone];
        int i2 = arr[paZone+1][tempZone];
        int i3 = arr[paZone+2][tempZone+1];
        int i4 = arr[paZone+1][tempZone+1];

        double altInterpolation1 = (((i-i2)/1000.0) * (pa - paZone * 1000 )) + arr[paZone+1][tempZone];
        double altInterpolation2 = (((i3-i4)/1000.0) * (pa - paZone * 1000 )) + arr[paZone+1][tempZone+1];
        double tempInterpolation = ((altInterpolation2 - altInterpolation1)/25)*(temp - arr[0][tempZone]) +altInterpolation1;
        return (int)Math.round(tempInterpolation);
    }

    // Paved Runway correction
    public static int asphaltRunway(int tor){
        return tor/10;
    }

    // Succesfully calculates the headwind component
    public static int hdWindComp(int rwy, int windDirection, int windSpeed){
        double dif = Math.abs(rwy-windDirection);
        return (int) Math.round(windSpeed * Math.cos(Math.toRadians(dif)));
    }

    public static void calculateTO(){
        Scanner s = new Scanner(System.in);
        System.out.println("Please enter the information for Departure Aerodrome:" + "\nlocal QNH:");
        int QNH = s.nextInt();
        System.out.println("Please enter the aerodrome elevation:");
        int altitude = s.nextInt();
        System.out.println("Please enter the temperature:");
        int temp = s.nextInt();

        int torUnfactored = interpolate(tor,QNH, altitude, temp);
        int todUnfactored = interpolate(tod,QNH,altitude,temp);

        System.out.println("Please enter the runway heading(0-360):");
        int rwy = s.nextInt();
        System.out.println("Please enter the wind direction(0-360)");
        int wind = s.nextInt();
        System.out.println("Please enter wind speed(kts):");
        int windSpeed = s.nextInt();
        int hdWindComp = hdWindComp(rwy,wind,windSpeed);

        torUnfactored = (hdWindComp>=0)? torUnfactored - (5 * hdWindComp) : torUnfactored + (15 * hdWindComp);
        todUnfactored = (hdWindComp>=0)? todUnfactored - (5 * hdWindComp) : todUnfactored + (15 * hdWindComp);

        todUnfactored -= asphaltRunway(torUnfactored);
        torUnfactored -= asphaltRunway(torUnfactored);

        int torr = (int)(torUnfactored * 1.1);
        int todr = (int)(todUnfactored * 1.1);

        System.out.println("torr = " + torr);
        System.out.println("todr = " + todr);
    }

    public static void calculateLanding(){
        Scanner s = new Scanner(System.in);
        System.out.println("Please enter the information for Landing Aerodrome:" + "\nlocal QNH:");
        int QNH = s.nextInt();
        System.out.println("Please enter the aerodrome elevation:");
        int altitude = s.nextInt();
        System.out.println("Please enter the temperature:");
        int temp = s.nextInt();

        int lrUnfactored = interpolate(lr,QNH,altitude,temp);
        int ldUnfactored = interpolate(ld,QNH,altitude,temp);

        System.out.println("Please enter the runway heading(0-360):");
        int rwy = s.nextInt();
        System.out.println("Please enter the wind direction(0-360)");
        int wind = s.nextInt();
        System.out.println("Please enter wind speed(kts):");
        int windSpeed = s.nextInt();
        int hdWindComp = hdWindComp(rwy,wind,windSpeed);

        lrUnfactored = (hdWindComp>=0)? lrUnfactored - (5 * hdWindComp) : lrUnfactored + (15 * hdWindComp);
        ldUnfactored = (hdWindComp>=0)? ldUnfactored - (5 * hdWindComp) : ldUnfactored + (15 * hdWindComp);

        ldUnfactored -= asphaltRunway(lrUnfactored);
        lrUnfactored -= asphaltRunway(lrUnfactored);

        int lrr = (int)(lrUnfactored * 1.67);
        int ldr = (int)(ldUnfactored * 1.67);


        System.out.println("lrr = " + lrr);
        System.out.println("ldr = " + ldr);

    }



}
