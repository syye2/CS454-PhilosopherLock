
//An implementation that is not starvation free but allows
//multiple non-adjacent threads to run at the same time.
public class PhilosopherLock {
    Filter fLock;
    int[] sticks;
    int philosophers;
    public PhilosopherLock(int n) {
        this.philosophers = n;
        this.sticks = new int[philosophers];
        this.fLock = new Filter(n);
    }
    public void lock() {
        // A thread attempts to get the sticks
        int me = ThreadID.get();
        // It spins forever until a condition is met
        while(true) {
            // Filter.lock() here to ensure every thread sees the same array state
            fLock.lock();
            // The condition to proceed is that a thread has access to both sticks
            if (sticks[me] == 0 && sticks[(me+philosophers-1)%philosophers] == 0) {
                // The condition is met so change the array state accordingly
                sticks[me] = 1;
                sticks[(me+philosophers-1) % philosophers] = 1;
                // It no longer needs access to the array.
                // Filter.unlock() and continue
                fLock.unlock();
                break;
            }
            // The condition is not met. Filter.unlock() so that other threads
            // have a chance to give up the resource.
            fLock.unlock();
        }
    }

    public void unlock() {
        // Gives up the resource by changing the state in the array accordingly
        int me = ThreadID.get();
        fLock.lock();
        ticks[me] = 0;
        sticks[(me+philosophers-1) % philosophers] = 0;
        fLock.unlock();
    }
}

// A implementation that is starvation free but allows only one thread to run
// at any time.
public class PhilosopherLockS {
    Filter fLock;
    int[] sticks;
    // Stores a timestamp to which thread should proceed first
    int[] priority;
    int philosophers;
    public PhilosopherLockS(int n) {
        this.philosophers = n;
        this.sticks = new int[philosophers];
        this.fLock = new Filter(n);
        this.priority = new int[philosophers];
    }
    public void lock() {
        // largest(int[]) returns the largest element in the array
        // lowest(int[]) returns the lowest element in the array that is not 0
        int me = ThreadID.get();
        if (priority[me] == 0) {
            // Assigns a number to the thread the first time it calls lock()
            // The number assigned is the largest number currently in priority + 1
            priority[me] = largest(priority) + 1;
        }
        // It spins forever until a condition is met
        while(true) {
            // Filter.lock() here to ensure every thread sees the same array state
            fLock.lock();
            // The condition to proceed is that a thread has access to both sticks and
            // it has the highest priority (the lowest number out of all interested threads)
            if (sticks[me] == 0 && sticks[(me+philosophers-1)%philosophers] == 0 &&
                priority[me] == lowest(priority)) {
                // The condition is met so change the array state accordingly
                sticks[me] = 1;
                sticks[(me+philosophers-1) % philosophers] = 1;
                // no longer waiting in line so we remove the thread
                // from the priority list
                priority[me] = 0;
                // It no longer needs access to the array.
                // Filter.unlock() and continue
                fLock.unlock();
                break;
            }
            // The condition is not met. Filter.unlock() so that other threads
            // have a chance to give up the resource.
            fLock.unlock();
        }
    }

    public void unlock() {
        int me = ThreadID.get();
        fLock.lock();
        ticks[me] = 0;
        sticks[(me+philosophers-1) % philosophers] = 0;
        fLock.unlock();
    }
}

class Filter{
    int[] level;
    int[] victim;
    public Filter(int n) {
        level = new int[n];
        victim = new int[n]; // use 1..n-1
        for (int i = 0; i < n; i++) {
            level[i] = 0;
            }
        }
    public void lock() {
        int me = ThreadID.get();
        for (int i = 1; i < n; i++) { // attempt to enter level i
            level[me] = i;
            victim[i] = me;
            // spin while conflicts exist
            while ((∃k != me) (level[k] >= i && victim[i] == me)) {};
            }
    }
    public void unlock() {
        int me = ThreadID.get();
        level[me] = 0;
        }
}