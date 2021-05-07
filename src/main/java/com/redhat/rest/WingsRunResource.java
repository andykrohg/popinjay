package com.redhat.rest;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.redhat.domain.MentorAssignment;
import com.redhat.domain.Timeslot;
import com.redhat.domain.WingsRun;

import io.quarkus.panache.common.Sort;

@Path("/wingsRuns")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class WingsRunResource {

    @GET
    public List<Timeslot> getAllWingsRuns() {
        return WingsRun.listAll(Sort.by("student"));
    }

    @POST
    public Response add(WingsRun wingsRun) {
        WingsRun.persist(wingsRun);

        List<MentorAssignment> mentorAssignmentList = new ArrayList<MentorAssignment>();
        for (int i = 0; i < 3; i++) {
            mentorAssignmentList.add(new MentorAssignment(wingsRun, i));
        }

        MentorAssignment.persist(mentorAssignmentList);
        return Response.accepted(mentorAssignmentList).build();
    }

    @DELETE
    @Path("{wingsRunId}")
    public Response delete(@PathParam("wingsRunId") Long wingsRunId) {
        WingsRun wingsRun = WingsRun.findById(wingsRunId);
        if (wingsRun == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        wingsRun.delete();
        return Response.status(Response.Status.OK).build();
    }

}
