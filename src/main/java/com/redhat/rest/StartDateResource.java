package com.redhat.rest;

import java.time.LocalDate;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.redhat.calendar.GoogleCalendarIntegration;


@Path("/startDate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StartDateResource {

    @GET
    public LocalDate getStartDate() {
        return GoogleCalendarIntegration.startDate;
    }

    @POST
    public Response setStartDate(LocalDate startDate) {
        GoogleCalendarIntegration.startDate = startDate;
        return Response.status(Response.Status.OK).build();
    }
}