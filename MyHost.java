/* Implement this class. */

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/* implemented a comparator for the priority queue */
class PriorityComparator implements Comparator<Task> {
    @Override
    public int compare(Task task1, Task task2) {
        // if the tasks have the same priority, the task with the earliest start time will be first in the queue
        if (task1.getPriority() == task2.getPriority()) {
            return (task1.getStart() > task2.getStart()) ? 1 : -1;
        }
        // if the tasks have different priorities, the task with the highest priority will be first in the queue
        return (task1.getPriority() > task2.getPriority()) ? -1 : 1;
    }
}

public class MyHost extends Host {
    /* used a PriorityBlockingQueue for the queue of tasks so that
     * adding tasks will be thread-safe
    */
    private PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<Task>(10, new PriorityComparator());
    private Task currentTask = null;
    private Task nextPossibleTask = null;

    // used isHostDown to check if the host is down
    private boolean isHostDown = false;

    // used isPreempted to check if the current task has been preempted
    private boolean isPreempted = false;

    // keep track of the start and end time of the current task
    private double startTime = 0;
    private double endTime = 0;
    

    @Override
    public void run() {
        // while host is not down, run tasks
        while (!isHostDown) {
            // only one task can be executed at a time on a host
            synchronized (this) {
                // if the queue is not empty, get the first task in the queue
                if (!queue.isEmpty()) {
                    // get current task
                    currentTask = queue.poll();
                    
                    // wait for getLeft Time
                    try {
                        startTime = Math.round(Timer.getTimeDouble()); // in seconds
                        isPreempted = false;

                        // simulate currentTask execution on host, until it is preempted or it finishes
                        // the currentTask will wait on the current instance's monitor for getLeft Time
                        /* when current task gets into waiting state, the lock on the instance's monitor
                         * is released and another task can get the lock and notify the current task
                         */
                        this.wait(currentTask.getLeft());

                        // if another task with a higher priority notifies the current task, then the current task
                        // goes into running state
                        endTime = Math.round(Timer.getTimeDouble()); // in seconds
                        
                        // calculate how much time the current task has been in execution = endTime - startTime
                        double waitDuration = (endTime - startTime) * 1000; // in milliseconds
                
                        // update the time left for the current task
                        currentTask.setLeft(currentTask.getLeft() - (long)waitDuration);

                        // if currentTask has finished
                        if (currentTask.getLeft() == 0) {
                            currentTask.finish();
                            currentTask = null;
                        }

                        // if currentTask has not finished and was preempted, then add it back to the queue
                        else if (currentTask.getLeft() > 0 && isPreempted == true) {
                            queue.put(currentTask);
                        }
                    } catch (InterruptedException e) {
                        // wait can throw an InterruptedException
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void addTask(Task task) {
        // add a task to the queue
        // if the queue is unbounded, put will never block
        queue.put(task);

        /* only one task can enter this synchronized block at a time on the host
         * while the current task is in waiting state, a new task can get the current host instance's monitor
         * and notify the current task to wake it up
        */
        synchronized (this) {
            // if a currentTask is already in execution on the host
            if (currentTask != null) {
                nextPossibleTask = queue.peek();
                isPreempted = false;
                /* check if there is a task in the queue with a higher priority than the current task
                 * if there is and the current task is preemptible, then the current task is preempted
                */
                if (nextPossibleTask != null && 
                    currentTask.isPreemptible() == true &&
                    nextPossibleTask.getPriority() > currentTask.getPriority()) {
                    isPreempted = true;
                    // new task will notify the one that is in waiting state
                    this.notify();
                }
            }
        }    
    }

    @Override
    public int getQueueSize() {
        // get the size of the queue
        return queue.size();
    }

    @Override
    public long getWorkLeft() {
        /* go through the tasks waiting in the queue and
         * add the work left for each task
        */
        long workLeft = 0;
        for (Task task : queue) {
            workLeft += task.getLeft();
        }
        /* if we have a task in execution on the host, then
         * we add the work left for that task
        */
        if (currentTask != null) {
            // currentTime keeps the current time in seconds
            double currentTime = Math.round(Timer.getTimeDouble());

            /* calculate how much time the current task has been in execution = currentTime - startTime
             * timeLeft keeps the time left for the current task in milliseconds
            */
            double timeLeft = currentTask.getLeft() - (currentTime - startTime) * 1000;
            workLeft += timeLeft;
        }
        return workLeft;
    }

    @Override
    public void shutdown() {
        // shutdown the host
        isHostDown = true;
    }

    // function taskInExecution returns 1 if the host has a task in execution and 0 otherwise
    public int taskInExecution() {
        return currentTask != null ? 1 : 0;
    }
}
