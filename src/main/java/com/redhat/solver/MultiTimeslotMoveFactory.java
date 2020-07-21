package com.redhat.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.redhat.domain.Mentor;
import com.redhat.domain.MentorAssignment;
import com.redhat.domain.WingsSchedule;

import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;

public class MultiTimeslotMoveFactory implements MoveListFactory<WingsSchedule> {

    @Override
    public List<MultiTimeslotMove> createMoveList(WingsSchedule wingsSchedule) {
        List<MultiTimeslotMove> moveList = new ArrayList<MultiTimeslotMove>();
        wingsSchedule.getMentorAssignments()
            .stream().collect(Collectors.groupingBy(MentorAssignment::getWingsRun))
            .entrySet().stream()
            .forEach(entry -> {
                wingsSchedule.getTimeslotList().stream().forEach(timeslot -> {
                    HashMap<MentorAssignment, Mentor> mentorMap = new HashMap<MentorAssignment, Mentor>();
                    for(MentorAssignment mentorAssignment : entry.getValue()) {
                        mentorMap.put(mentorAssignment, null);
                    }

                    moveList.add(new MultiTimeslotMove(mentorMap, timeslot));
                });
            });

        return moveList;
    }
    
}