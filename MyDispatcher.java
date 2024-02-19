/* Implement this class. */

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDispatcher extends Dispatcher {
    /* planning policy for RR: in order of arrival, a task is given
     * to the next host in the list of hosts
     */

    /* used atomic variable for lastIdHost for the RR policy, instead of
     * a synchronized block
     * the variable is initialized with -1, because the first host needs to have hostId 0
     */
    private AtomicInteger lastIdHost = new AtomicInteger(-1);
    
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task) {
        if (algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            int hostId = lastIdHost.incrementAndGet() % hosts.size(); 
            hosts.get(hostId).addTask(task);
        } else if (algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
            // used synchronized block to avoid race conditions between tasks 
            // with the same start time
            synchronized (this) {
                // task will go to the first host with the minimum number of tasks
                int minTasks = Integer.MAX_VALUE;
                int hostId = -1;
                for (int i = 0; i < hosts.size(); i++) {
                    // keep track of the number of tasks waiting in the queue and the task in execution
                    int tasks = hosts.get(i).getQueueSize() + ((MyHost)hosts.get(i)).taskInExecution();
                    if (tasks < minTasks) {
                        minTasks = tasks;
                        hostId = i;
                    }
                }
                hosts.get(hostId).addTask(task);
            }
        } else if (algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
            // we only have 3 hosts for this algorithm
            // if the type of the task is short, it will go to the first host
            if (task.getType() == TaskType.SHORT) 
                hosts.get(0).addTask(task);
            // if the type of the task is medium, it will go to the second host
            else if (task.getType() == TaskType.MEDIUM)
                hosts.get(1).addTask(task);
            // if the type of the task is long, it will go to the third host
            else if (task.getType() == TaskType.LONG)
                hosts.get(2).addTask(task);
            
        } else if (algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
            // used synchronized block to avoid race conditions between tasks 
            // with the same start time
            synchronized (this) {
                // get the first host with the minimum work left
                long minWorkLeft = Long.MAX_VALUE;
                int hostId = -1;
                for (int i = 0; i < hosts.size(); i++) {
                    long workLeft = hosts.get(i).getWorkLeft();
                    if (workLeft < minWorkLeft) {
                        minWorkLeft = workLeft;
                        hostId = i;
                    }
                }
                hosts.get(hostId).addTask(task);
            }
        }
    }
}
