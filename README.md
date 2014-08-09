# Akka example: Deploy an actor system locally, remote, and on a cluster

This example shows three different ways to deploy an actor system. First, you need to clone / download this repository.


## Lookup Example

The example is a very simple actor system consisting of three actors:
The master actor is used to lookup lastnames for a given firstname.
The lookup actors look up a given firstname in a hashmap and send back the lastname.
After getting both lastnames for "Martin" ("Odersky") and "Viktor" ("Klang"), the master actor stops.


## Deploy on your local machine

In order to deploy the system on your local machine run the following command:

```bash
activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Local"
```


## Deploy lookup actors on a remote machine

In order to deploy the system on your local machine run the following command:

```bash
activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Remote"
```

If you want to deploy the lookup actors on a remote machine run the following two commands:

```bash
activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Remote Worker"
activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Remote Master"
```


## Deploy the actors in a cluster

In order to deploy the system in a cluster run the following three commands:

```bash
activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Clustering Worker 13371"
activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Clustering Worker 13372"
activator "run-main de.scala_bs.wille.akka.deployByConfigExample.Clustering Master"
```