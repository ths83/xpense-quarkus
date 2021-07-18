package org.thoms;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

// camel-k: dependency=mvn:org.apache.camel.quarkus:camel-quarkus-kafka
// camel-k: dependency=mvn:org.apache.camel.quarkus:camel-quarkus-core

@ApplicationScoped
public class KafkaRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		final var predicate = body().isEqualTo("CREATE/'null'");

		from("kafka:my-topic?brokers=my-cluster-kafka-bootstrap:9092")
				.choice()
					.when(predicate).log("${body}")
					.otherwise().log("Operation not found")
				.end();
	}
}