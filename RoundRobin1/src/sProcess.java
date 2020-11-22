public class sProcess {
    public final int cputime;
    public final int ioblocking;
    public int cpudone;
    public int ionext;
    public int numblocked;
    public final int blockingtime;
    public int blockingtimepassed;
    public final int id;

    public sProcess(int id, int cputime, int ioblocking, int blockingtime) {
        this.id = id;
        this.cputime = cputime; //сколько времени выделено на выполнение
        this.ioblocking = ioblocking; //через какой промежуток времени процесс должен блокироваться
        this.cpudone = 0; //сколько времени процесс выполнялся в целом
        this.ionext = 0; //сколько времени прошло с конца предыдущей блокировки
        this.numblocked = 0; //сколько раз был заблокирован
        this.blockingtime = blockingtime; //сколько процесс должен быть заблокированным
        this.blockingtimepassed = 0; //сколько времени прощло с начала блокировки
    }
}
