// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.*;
import java.io.*;

public class SchedulingAlgorithm {

    public static Results run(int runtime, int quantum, ArrayDeque<sProcess> processVector, Results result, String resultsFile) {
        int comptime = 0; //system time
        int size = processVector.size();
        ArrayList<sProcess> blockedList = new ArrayList<>();
        ArrayList<sProcess> completedList = new ArrayList<>();
        int q = 0;

        result.schedulingType = "Batch (Nonpreemptive)";
        result.schedulingName = "First-Come First-Served";
        try {
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            sProcess process = processVector.pollFirst();
            out.println("Process: " + process.id + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.blockingtime + ")" + printSystemtime(comptime));
            //пока не закончилось время выполнения программы
            while (comptime < runtime) {
                if (process == null) {
                    //переключится на новый процесс
                    try {
                        process = processVector.pop();
                        out.println("Process: " + process.id + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.blockingtime + ")" + printSystemtime(comptime));
                    } catch (NoSuchElementException e) {
                        out.println("Holostoy");
                    }
                } else {
                    if (process.cpudone == process.cputime) {
                        // процесс выполнился
                        q = 0;
                        completedList.add(process);
                        out.println("Process: " + process.id + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.blockingtime + ")" + printSystemtime(comptime));
                        if (completedList.size() == size) {
                            //если все процессы закончили свою работу
                            result.compuTime = comptime;
                            System.out.println("All processes");
                            break;
                        }
                        process = null;
                    } else if (process.ionext == process.ioblocking) {
                        //процесс переходит в состояние блокировки
                        out.println("Process: " + process.id + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.blockingtime + ")" + printSystemtime(comptime));
                        process.numblocked++;
                        process.ionext = 0;
                        process.blockingtimepassed = -1;
                        blockedList.add(process);
                        q = 0;
                        process = null;
                    } else if (q == quantum) {
                        //процесс исчерпал свой квант времени
                        out.println("Process: " + process.id + " paused... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.blockingtime + ")" + printSystemtime(comptime));
                        q = 0;
                        processVector.addLast(process);
                        process = null;
                    } else if (process.ioblocking > 0) {
                        q++;
                        process.cpudone++;
                        process.ionext++;
                    }
                }

                Iterator<sProcess> iter = blockedList.iterator();
                while (iter.hasNext()) {
                    sProcess p = iter.next();
                    p.blockingtimepassed++;
                    if (p.blockingtimepassed == p.blockingtime) {
                        p.blockingtimepassed = 0;
                        processVector.addLast(p);
                        iter.remove();
                    }
                }
                comptime++;
            }
            out.close();
            for (int j = 0; j < completedList.size(); ++j) {
                sProcess pr = completedList.get(j);
                processVector.push(pr);
            }
            if (process != null) {
                processVector.push(process);
            }
            for(int j = 0; j < blockedList.size(); ++j) {
              processVector.push(blockedList.get(j));
            }
        } catch (IOException e) { /* Handle exceptions */ }


        result.compuTime = comptime;
        return result;
    }

    public static String printSystemtime(int time) {
        return ";   system time: " + time;
    }
}
