package takeOff;

import java.util.Scanner;

public class TakeOff {

   static int[][] tor = {{-25,  0, 25, 50},
                         {149,187,231,280},
                         {162,204,252,306},
                         {177,223,275,334},
                         {193,244,301,365},
                         {211,266,328,398},
                         {231,291,359,436}};
   static int[][] tod = {{-25,  0, 25, 50},
                         {262,328,401,484},
                         {285,356,437,526},
                         {311,388,475,572},
                         {338,422,517,623},
                         {368,460,564,679},
                         {402,502,614,740}};

    public static int findPaZone(int QNH, int altitude){// Returns Pressure Altitude, when divided by 1000 used as index

        int standard = 1013;
        int difference = Math.abs(standard-QNH) * 30;

        int pressureAltitude = (QNH > standard)? altitude - difference : altitude + difference;

        return pressureAltitude;

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

 /*   // Wind correction
    public static void windFactor(int tor,int tod,int rwy, int windDirection, int windSpeed){
        int wind = hdWindComp(rwy,windDirection,windSpeed);
        if(wind > 0) {
            tor -= 5 * wind;
            tod -= 5 * wind;
        }else if (wind < 0){
            tor += 15 * wind;
            tod += 15 * wind;
        }
    }

  */

    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);
        System.out.println("Please enter the local QNH:");
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

        torUnfactored -= asphaltRunway(torUnfactored);
        todUnfactored -= asphaltRunway(torUnfactored);


        int torr = (int)(torUnfactored * 1.1);
        int todr = (int)(todUnfactored * 1.1);

        System.out.println("torr = " + torr);
        System.out.println("todr = " + todr);

    }
}
