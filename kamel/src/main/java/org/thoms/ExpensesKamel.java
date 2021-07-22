package org.thoms;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

// camel-k: dependency=mvn:org.apache.camel.quarkus:camel-quarkus-kafka
// camel-k: dependency=mvn:org.apache.camel.quarkus:camel-quarkus-core

@ApplicationScoped
public class ExpensesKamel extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		final var predicate = body().startsWith("CREATE");

		from("kafka:my-topic?brokers=my-cluster-kafka-bootstrap:9092")
				.log("${body}");
//				.choice()
//					.when(predicate)
//						.doTry()
//						.to("rest:get:rwxcloud.com")
//						.log("${body}")
//					.end();
	}
}