package com.redhat.rest;

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

import com.redhat.domain.Mentor;
import com.redhat.domain.MentorAssignment;
import com.redhat.domain.Timeslot;

import io.quarkus.panache.common.Sort;

@Path("/mentorAssignments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class MentorAssignmentResource {

    @GET
    public List<Timeslot> getAllMentorAssignments() {
        return Mentor.listAll(Sort.by("timeslot"));
    }

    @DELETE
    @Path("{mentorAssignmentId}")
    public Response delete(@PathParam("mentorAssignmentId") Long mentorAssignmentId) {
        MentorAssignment assignment = MentorAssignment.findById(mentorAssignmentId);
        if (assignment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        assignment.delete();
        return Response.status(Response.Status.OK).build();
    }
}
