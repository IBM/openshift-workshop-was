package org.pwte.example.health;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@ApplicationScoped
@Liveness
public class LivenessCheck implements HealthCheck {

	@Override
	public HealthCheckResponse call() {
		ThreadMXBean tBean = ManagementFactory.getThreadMXBean();
		long ids[] = tBean.findMonitorDeadlockedThreads();
		if (ids !=null) {
			ThreadInfo threadInfos[] = tBean.getThreadInfo(ids);
			for (ThreadInfo ti : threadInfos) {
				double seconds = ti.getBlockedTime() / 1000.0;
				if (seconds > 60) {
					System.out.println("Liveness check - blocked " + seconds + " seconds on thread " + ti.toString());
					return HealthCheckResponse.named("Liveness").down().build();
				}
			}
		}
		return HealthCheckResponse.named("Liveness").up().build();
	}

}


	

