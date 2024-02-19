Implementare tema 2 APD
In MyDispatcher.java:
 - pe parcurs ce ajung task-urile la dispatcher, in functie de planul de planificare, va fi adaugat unui host/nod
	- la ROUND ROBIN am o variabila atomica in care calculez id-ului ultimului nod la care s-a alocat un task (in cazul in care ar ajunge 2 task-uri simultan la dispatcher sa nu existe race condition)
	- la SHORTEST QUEUE adaug task-ul nodului cu numar minim de taskuri in asteptare sau in executie (am creat o functie noua in MyHost.java "taskInExecution" pentru a verifica daca exista un task in executie pe host)
	- la SIZE INTERVAL TASK ASSIGNMENT in functie de tipul taskului il adaug la host-ul corespunzator
	- la LEAST WORK LEFT adaug task-ul nodului cu cantitatea de calcule ramase de executat minima (in functia "getWorkLeft" din MyHost.java adun tot timpul de executie ramas al task-urilor din coada + cat mai are de rulat in acel moment task-ul curent care se afla in executie)

In MyHost.java: pentru ca in cerinta ni se spune ca fiecare nod are rolul de a executa task-urile pe care le primeste in functie de prioritati si ca are ca mod de stocare a task-urilor o coada am folosit PrioriyBlockingQueue pentru a nu exista probleme de concurenta ; coada primeste un comparator care ordoneaza task-urile descrescator in functie de prioritate, iar daca prioritatile sunt egale, crescator dupa timpul de sosire la nod
 - in functia "addTask", se adauga task-urile care sosesc la nod in coada dupa ordinea din comparator
 - in functia "run", task-ul curent o sa detina monitorul instantei curente a host-ului (s-a executat un bloc synchronized pe this in functia "run")
		- cand se face wait(get left time), task-ul curent intra intr-o stare de waiting, iar monitorul nu mai e detinut de nimeni
		- task-ul o sa ramana in waiting state pana cand alt task il preempteaza sau timpul dat ca argument trece
		- pentru ca un task nou sa-l preempteze pe cel curent va trebui sa detina monitorul instantei curente a host-ului pe care il obtine prin executarea blocului synchronized tot pe this in functia "addTask" si sa dea notify
 		- cand task-ul curent trebuie preemptat, folosim clasa Time.java pentru a calcula cat timp a fost in waiting state, rotunjesc mereu timpul pentru a lucra cu numere intregi si a pune task-urile sa ruleze un nr de secunde intreg (ca in exemplele din cerinta)
