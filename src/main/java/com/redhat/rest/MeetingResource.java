package com.redhat.rest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.redhat.calendar.GoogleCalendarIntegration;
import com.redhat.domain.MentorAssignment;

@Path("/meetings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class MeetingResource {
    @POST
    public Response create() throws GeneralSecurityException, IOException {
        List<MentorAssignment> assignments = MentorAssignment.listAll();
        
        assignments.stream().collect(Collectors.groupingBy(MentorAssignment::getWingsRun))
            .forEach((wingsRun,mentorAssignments) -> {
                try {
                    GoogleCalendarIntegration.createEvent(wingsRun, mentorAssignments);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            });

        return Response.accepted(assignments).build();
    }
}
