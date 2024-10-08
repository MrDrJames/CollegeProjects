package Module8;

public class Mod8Op1 {
    public static int counter;

    public static void main(String[] args) {
        Countup up = new Countup("Up");
        Countdown down = new Countdown("Down");
        up.start();
        down.start();
    }
}

class Countup extends Thread {
    public Countup(String string) {
        super(string);
    }

    @Override
    public void run() {
        while (Mod8Op1.counter < 20) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Mod8Op1.counter++;
            System.out.println(Mod8Op1.counter + "+");
        }
    }
}
class Countdown extends Thread {
    public Countdown(String string) {
        super(string);
    }
    @Override
    public void run() {
        while(Mod8Op1.counter < 19){
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // System.out.println(Mod8Op1.counter);
        }
        while (Mod8Op1.counter > 0) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Mod8Op1.counter--;
            System.out.println(Mod8Op1.counter + "-");
            
        }
    }
}