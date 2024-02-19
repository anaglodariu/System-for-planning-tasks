# System-for-planning-tasks
Implementation of a task planning system in a datacenter

Overview
==
![image](https://github.com/anaglodariu/System-for-planning-tasks/assets/94357049/f4c91c37-8d4f-4b6d-a592-3bc945f74a67)
- The **dispatcher** (or load balancer, with green in the image) has the role of receiving tasks that arrive in the system (for example, from clients of the datacenter) and sending them to the nodes in the datacenter based on predetermined policies. 
- The **nodes** (marked in blue in the image, and whose number may vary) have the role of executing the tasks they receive according to priorities, to preempt those running for more important tasks, etc. Each node in the system has a queue in which the tasks that will be run later are stored. 
- The main purpose of this theme is implementing the logic from the dispatcher and from the computing nodes.

The dispatcher you need to implement for this theme can work with one of four *scheduling policies*: 
- *Round Robin*
- *Shortest Queue*
- *Size Interval Task Assignment*
- *Least Work Left*


