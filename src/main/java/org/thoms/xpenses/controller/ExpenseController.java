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
	@Channel("expenses-in")
	Emitter<String> emitter;

	@POST
	public Response create(final Expense request) {
		emitter.send("Created expense " + request.toString());
		return Response
				.status(Response.Status.CREATED)
				.entity(service.create(request))
				.build();
	}

	@GET
	@Path("{expenseId}")
	public Response get(@PathParam("expenseId") final String expenseId) {
		return Optional.ofNullable(
				Response
						.status(Response.Status.OK)
						.entity(service.get(expenseId)))
				.orElse(Response.status(Response.Status.NOT_FOUND.getStatusCode()))
				.build();
	}

	@GET
	public Response getByActivity(@QueryParam("activityId") final String activityId) {
		return Optional.ofNullable(
				Response
						.status(Response.Status.OK)
						.entity(service.getByActivity(activityId)))
				.orElse(Response.status(Response.Status.NOT_FOUND.getStatusCode()))
				.build();
	}

	@PUT
	@Path("/expenses/{expenseId}")
	public Response update(@PathParam("expenseId") final String expenseId, final UpdateExpenseRequest request) {
		service.update(expenseId, request);
		return Response.noContent().build();
	}

	@DELETE
	@Path("/{activityId}/expenses/{expenseId}")
	public Response delete(@PathParam("activityId") final String activityId, @PathParam("expenseId") final String expenseId) {
		service.delete(activityId, expenseId);
		return Response.noContent().build();
	}
}
