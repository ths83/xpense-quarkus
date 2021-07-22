package org.thoms.xpenses.controller;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.thoms.xpenses.model.Expense;
import org.thoms.xpenses.model.request.expenses.UpdateExpenseRequest;
import org.thoms.xpenses.services.ExpenseService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/expenses")
public class ExpenseController {

	@Inject
	ExpenseService service;

	@Inject
	@Channel("expenses-channel")
	Emitter<String> emitter;

	@POST
	public Response create(final Expense request) {
		final var expense = service.create(request);
		emitter.send(String.format("CREATE - %s", expense.getId()));
		return Response
				.status(Response.Status.CREATED)
				.entity(expense)
				.build();
	}

	@GET
	@Path("{expenseId}")
	public Response get(@PathParam("expenseId") final String expenseId) {
		emitter.send(String.format("GET - %s", expenseId));
		return Optional.ofNullable(
				Response
						.status(Response.Status.OK)
						.entity(service.get(expenseId)))
				.orElse(Response.status(Response.Status.NOT_FOUND.getStatusCode()))
				.build();
	}

	@GET
	public Response getByActivity(@QueryParam("activityId") final String activityId) {
		emitter.send(String.format("GET BY ACTIVITY - %s", activityId));
		return Optional.ofNullable(
				Response
						.status(Response.Status.OK)
						.entity(service.getByActivity(activityId)))
				.orElse(Response.status(Response.Status.NOT_FOUND.getStatusCode()))
				.build();
	}

	@PUT
	@Path("{expenseId}")
	public Response update(@PathParam("expenseId") final String expenseId, final UpdateExpenseRequest request) {
		emitter.send(String.format("UPDATE - %s", expenseId));
		service.update(expenseId, request);
		return Response.noContent().build();
	}

	@DELETE
	@Path("{expenseId}")
	public Response delete(@PathParam("expenseId") final String expenseId) {
		emitter.send(String.format("DELETE - %s", expenseId));
		service.delete(expenseId);
		return Response.noContent().build();
	}
}
