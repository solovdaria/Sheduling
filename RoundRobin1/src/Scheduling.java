// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.



import java.io.*;
import java.util.*;

public class Scheduling {

    private static int processnum = 5; //количество процессов
    private static int run_time_average = 1000;
    private static int run_time_stddev = 100;
    private static int runtime = 1000;
    private static int quantum = 50;
    private static ArrayDeque<sProcess> processVector = new ArrayDeque<>();
    private static Results result = new Results("null", "null", 0);
    private static String resultsFile = "";
    private static String logFile = "";

    private static void Init(String file) {
        File f = new File(file);
        String line;
        int cputime = 0;
        int ioblocking = 0;
        double X = 0.0;
        int block_time_average = 0;
        int block_time_stddev = 0;
        int processid = 0;

        try {
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            while ((line = in.readLine()) != null) {
                if (line.startsWith("summary_file")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    resultsFile = Common.formatString(st.nextToken());
                }
                if (line.startsWith("log_file")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    logFile = Common.formatString(st.nextToken());
                }
                if (line.startsWith("numprocess")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    processnum = Common.s2i(st.nextToken());
                }
                if (line.startsWith("run_time_average")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    run_time_average = Common.s2i(st.nextToken());
                }
                if (line.startsWith("run_time_stddev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    run_time_stddev = Common.s2i(st.nextToken());
                }
                if (line.startsWith("block_time_average")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    block_time_average = Common.s2i(st.nextToken());
                }
                if (line.startsWith("block_time_stddev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    block_time_stddev = Common.s2i(st.nextToken());
                }
                if (line.startsWith("process")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    ioblocking = Common.s2i(st.nextToken());
                    X = Common.R1();
                    while (X == -1.0) {
                        X = Common.R1();
                    }
                    double Y = X * X;
                    X = X * run_time_stddev;
                    cputime = (int) X + run_time_average;
                    Y = Y * block_time_stddev;
                    int blockedtime = (int) Y + block_time_average;
                    processVector.addLast(new sProcess(processid, cputime, ioblocking, blockedtime));
                    processid++;
                }
                if (line.startsWith("quantum")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    quantum = Common.s2i(st.nextToken());
                }
                if (line.startsWith("runtime")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    runtime = Common.s2i(st.nextToken());
                }
            }
            in.close();
        } catch (IOException e) { /* Handle exceptions */ }
    }

    public static void main(String[] args) {
        args = new String[1];
        args[0] = "scheduling.conf";
        File f = new File(args[0]);
        if (!(f.exists())) {
            System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
            System.exit(-1);
        }
        if (!(f.canRead())) {
            System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
            System.exit(-1);
        }
        System.out.println("Working...");
        Init(args[0]);

        result = SchedulingAlgorithm.run(runtime, quantum, processVector, result, logFile);
        output();
    }

    private static void output() {
        try {

          int i = 0;
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            out.println("Scheduling Type: " + result.schedulingType);
            out.println("Scheduling Name: " + result.schedulingName);
            out.println("Simulation Run Time: " + result.compuTime);
            out.println("Mean: " + run_time_average);
            out.println("Standard Deviation: " + run_time_stddev);
            out.println("Process #\tCPU Time\tIO Blocking\tCPU Completed\tCPU Blocked");
            Iterator<sProcess> iter = processVector.iterator();
            while (iter.hasNext()) {
                sProcess process = iter.next();
                out.print(process.id);
                if (i < 100) {
                    out.print("\t\t\t");
                } else {
                    out.print("\t\t");
                }
                out.print(process.cputime);
                if (process.cputime < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.print(Integer.toString(process.ioblocking));
                if (process.ioblocking < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.print(Integer.toString(process.cpudone));
                if (process.cpudone < 100) {
                    out.print(" (ms)\t\t\t");
                } else {
                    out.print(" (ms)\t\t");
                }
                out.println(process.numblocked + " times");
            }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        System.out.println("Completed.");
    }
}

