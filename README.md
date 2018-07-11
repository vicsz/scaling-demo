# PCF Memory Scaling Demo

Application demonstrating memory based scaling using PCF.

## Demo Steps

Bind a AutoScaling Service to your Application, and configure it for Container Memory Scaling.

> Note - additional instances are scaled up when the average container memory amount exceeds the High Memory amount set in settings.  Not that this is total container memory and not allocated JVM memory.

<img src="img/scaling.png" width="750">