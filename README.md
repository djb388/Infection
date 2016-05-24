Infection Algorithm

A demonstration of the infection algorithm, which traverses clusters of a disjoint graph, 'infecting' the clusters by updating the website version of its vertices as it progresses.

Requirements: 
Java 8

Running the program:
Just compile the source and run the program without any command line arguments. It uses a simple command line interface.

Testing the algorithm:
There are three tests that can be performed by the user interface. They run 10 iterations of the infection algorithms/clustering algorithm over the entire disjoint graph, randomly selecting vertices to expand on through each iteration.

Population:
The whole program was designed around testing the algorithm on a scalable population, so you can make a few changes to the population. The relation between coach vertices and coached-by vertices is modelled by a compound probability distribution in hopes of getting a realistic distribution of coaches to students. Adjusting the 'student ratio' variable increases the number of times a random number is multiplied by itself, which dictates the number of students every user has.

tiered_infection:
This algorithm is a realistic solution to the premise of the problem. It clusters the entire population, sorts the clusters by size, and distributes updates to entire clusters based on size.
