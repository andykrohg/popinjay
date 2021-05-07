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
import com.redhat.domain.Timeslot;

import io.quarkus.panache.common.Sort;

@Path("/mentors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class MentorResource {

    @GET
    public List<Timeslot> getAllMentors() {
        return Mentor.listAll(Sort.by("name"));
    }

    @POST
    public Response add(Mentor mentor) {
        Mentor.persist(mentor);
        return Response.accepted(mentor).build();
    }

    @DELETE
    @Path("{mentorId}")
    public Response delete(@PathParam("mentorId") Long mentorId) {
        Mentor mentor = Mentor.findById(mentorId);
        if (mentor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        mentor.delete();
        return Response.status(Response.Status.OK).build();
    }

}
