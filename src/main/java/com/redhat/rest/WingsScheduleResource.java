package com.redhat.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.calendar.GoogleCalendarIntegration;
import com.redhat.domain.Mentor;
import com.redhat.domain.MentorAssignment;
import com.redhat.domain.Timeslot;
import com.redhat.domain.WingsSchedule;

import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;

@Path("/wingsSchedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WingsScheduleResource {

    public static final Long SINGLETON_WINGS_SCHEDULE_ID = 1L;

    @Inject
    SolverManager<WingsSchedule, Long> solverManager;
    @Inject
    ScoreManager<WingsSchedule> scoreManager;

    // To try, open http://localhost:8080/timeTable
    @GET
    public WingsSchedule getWingsSchedule() {
        // Get the solver status before loading the solution
        // to avoid the race condition that the solver terminates between them
        SolverStatus solverStatus = getSolverStatus();
        WingsSchedule solution = findById(SINGLETON_WINGS_SCHEDULE_ID);
        scoreManager.updateScore(solution); // Sets the score
        solution.setSolverStatus(solverStatus);
        return solution;
    }

    @POST
    @Path("/solve")
    public void solve() {
        GoogleCalendarIntegration.updateSchedules();
        
        solverManager.solveAndListen(SINGLETON_WINGS_SCHEDULE_ID,
                this::findById,
                this::save);
    }

    public SolverStatus getSolverStatus() {
        return solverManager.getSolverStatus(SINGLETON_WINGS_SCHEDULE_ID);
    }

    @POST
    @Path("/stopSolving")
    public void stopSolving() {
        solverManager.terminateEarly(SINGLETON_WINGS_SCHEDULE_ID);
    }

    @Transactional
    protected WingsSchedule findById(Long id) {
        if (!SINGLETON_WINGS_SCHEDULE_ID.equals(id)) {
            throw new IllegalStateException("There is no wingsSchedule with id (" + id + ").");
        }
        // Occurs in a single transaction, so each initialized lesson references the same timeslot/room instance
        // that is contained by the timeTable's timeslotList/roomList.
        return new WingsSchedule(
                Mentor.listAll(),
                Timeslot.listAll(),
                MentorAssignment.listAll());
    }

    @Transactional
    protected void save(WingsSchedule wingsSchedule) {
        for (MentorAssignment mentorAssignment : wingsSchedule.getMentorAssignments()) {
            // TODO this is awfully naive: optimistic locking causes issues if called by the SolverManager
            MentorAssignment attachedMentorAssignment = MentorAssignment.findById(mentorAssignment.getId());
            attachedMentorAssignment.setMentor(mentorAssignment.getMentor());
            attachedMentorAssignment.setTimeslot(mentorAssignment.getTimeslot());
        }
    }

}
