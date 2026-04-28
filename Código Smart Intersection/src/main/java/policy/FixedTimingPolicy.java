package policy;
public class FixedTimingPolicy implements TimingPolicy {

    private static final int GREEN_TIME = 30;
    private static final int RED_TIME = 30;

    @Override
    public int getGreenTime() {
        return GREEN_TIME;
    }

    @Override
    public int getRedTime() {
        return RED_TIME;
    }

    @Override
    public String toString() {
        return "FixedTimingPolicy [green=" + GREEN_TIME + "s, red=" + RED_TIME + "s]";
    }
}
