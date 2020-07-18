var autoRefreshCount = 0;
var autoRefreshIntervalId = null;

function refreshWingsSchedule() {
    $.getJSON("/wingsSchedule", function (wingsSchedule) {
        refreshSolvingButtons(wingsSchedule.solverStatus != null && wingsSchedule.solverStatus !== "NOT_SOLVING");
        $("#score").text("Score: "+ (wingsSchedule.score == null ? "?" : wingsSchedule.score));

        const wingsScheduleByRun = $("#wingsScheduleByRun");
        wingsScheduleByRun.children().remove();
        const pendingMentorAssignments = $("#pendingMentorAssignments");
        pendingMentorAssignments.children().remove();

        const theadByRun = $("<thead>").appendTo(wingsScheduleByRun);
        const headerRowByRun = $("<tr>").appendTo(theadByRun);
        headerRowByRun.append($("<th>Mentor</th>"));
        const mentorList = [...new Set(wingsSchedule.mentorList.map(mentor => mentor.name))];
        $.each(mentorList, (index, mentor) => {
            headerRowByRun
            .append($("<th/>")
                .append($("<span/>").text(mentor)));
        });

        const tbodyByRun = $("<tbody>").appendTo(wingsScheduleByRun);
        // $.each(wingsSchedule.mentorAssignments, (index, assignment) => {
        //     const rowByRun = $("<tr>").appendTo(tbodyByRun);
        //     rowByRun
        //     .append($(`<th class="align-middle"/>`)
        //         .append($("<span/>").text(assignment.mentor.name))
        //     .append($(`<button type="button" class="ml-2 mb-1 btn btn-light btn-sm p-1"/>`)
        //                 .append($(`<small class="fas fa-trash"/>`)
        //                 ).click(() => deleteMentor(assignment.mentor))));
            
        //     $.each(wingsSchedule.roomList, (index, room) => {
        //         rowByStudent.append($("<td/>").prop("id", `timeslot${timeslot.id}room${room.id}`));
        //     });

        //     $.each(mentorList, (index, teacher) => {
        //         rowByRun.append($("<td/>").prop("id", `timeslot${timeslot.id}teacher${convertToId(teacher)}`));
        //     });
        // });

        $.each(wingsSchedule.mentorAssignments, (index, mentorAssignment) => {
            const color = pickColor(mentorAssignment.wingsRun.type);
            const mentorAssignmentElementWithoutDelete = $(`<div class="card mentorAssignment" style="background-color: ${color}"/>`)
                    .append($(`<div class="card-body p-2"/>`)
                            .append($(`<h5 class="card-title mb-1"/>`).text(mentorAssignment.wingsRun.type))
                            .append($(`<p class="card-text ml-2 mb-1"/>`)
                                    .append($(`<em/>`).text(`by ${mentorAssignment.wingsRun.student}`)))
                            .append($(`<small class="ml-2 mt-1 card-text text-muted align-bottom float-right"/>`).text(mentorAssignment.id)));
            const mentorAssignmentElement = mentorAssignmentElementWithoutDelete.clone();
            mentorAssignmentElement.find(".card-body").prepend(
                $(`<button type="button" class="ml-2 btn btn-light btn-sm p-1 float-right"/>`)
                        .append($(`<small class="fas fa-trash"/>`)
                        ).click(() => deleteMentorAssignment(mentorAssignment))
            );
            if (mentorAssignment.mentor == null || true) {
                pendingMentorAssignments.append(mentorAssignmentElement);
            } else {
                // $(`#timeslot${mentorAssignment.timeslot.id}room${mentorAssignment.room.id}`).append(mentorAssignmentElement);
                // $(`#timeslot${mentorAssignment.timeslot.id}teacher${convertToId(mentorAssignment.teacher)}`).append(mentorAssignmentElementWithoutDelete.clone());
                // $(`#timeslot${mentorAssignment.timeslot.id}studentGroup${convertToId(mentorAssignment.studentGroup)}`).append(mentorAssignmentElementWithoutDelete.clone());
            }
        });
    });
}

function convertToId(str) {
    // Base64 encoding without padding to avoid XSS
    return btoa(str).replace(/=/g, "");
}

function solve() {
    $.post("/wingsSchedule/solve", function () {
        refreshSolvingButtons(true);
        autoRefreshCount = 16;
        if (autoRefreshIntervalId == null) {
            autoRefreshIntervalId = setInterval(autoRefresh, 2000);
        }
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Start solving failed.", xhr);
    });
}

function refreshSolvingButtons(solving) {
    if (solving) {
        $("#solveButton").hide();
        $("#stopSolvingButton").show();
    } else {
        $("#solveButton").show();
        $("#stopSolvingButton").hide();
    }
}

function autoRefresh() {
    refreshWingsSchedule();
    autoRefreshCount--;
    if (autoRefreshCount <= 0) {
        clearInterval(autoRefreshIntervalId);
        autoRefreshIntervalId = null;
    }
}

function stopSolving() {
    $.post("/wingsSchedule/stopSolving", function () {
        refreshSolvingButtons(false);
        refreshWingsSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Stop solving failed.", xhr);
    });
}

function addLesson() {
    var subject = $("#lesson_subject").val().trim();
    $.post("/lessons", JSON.stringify({
        "subject": subject,
        "teacher": $("#lesson_teacher").val().trim(),
        "studentGroup": $("#lesson_studentGroup").val().trim()
    }), function () {
        refreshWingsSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Adding lesson (" + subject + ") failed.", xhr);
    });
    $('#lessonDialog').modal('toggle');
}

function deleteLesson(lesson) {
    $.delete("/lessons/" + lesson.id, function () {
        refreshWingsSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Deleting lesson (" + lesson.name + ") failed.", xhr);
    });
}

function addTimeslot() {
    $.post("/timeslots", JSON.stringify({
        "dayOfWeek": $("#timeslot_dayOfWeek").val().trim().toUpperCase(),
        "startTime": $("#timeslot_startTime").val().trim(),
        "endTime": $("#timeslot_endTime").val().trim()
    }), function () {
        refreshWingsSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Adding timeslot failed.", xhr);
    });
    $('#timeslotDialog').modal('toggle');
}

function deleteTimeslot(timeslot) {
    $.delete("/timeslots/" + timeslot.id, function () {
        refreshWingsSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Deleting timeslot (" + timeslot.name + ") failed.", xhr);
    });
}

function addRoom() {
    var name = $("#room_name").val().trim();
    $.post("/rooms", JSON.stringify({
        "name": name
    }), function () {
        refreshWingsSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Adding room (" + name + ") failed.", xhr);
    });
    $("#roomDialog").modal('toggle');
}

function deleteRoom(room) {
    $.delete("/rooms/" + room.id, function () {
        refreshWingsSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Deleting room (" + room.name + ") failed.", xhr);
    });
}

function showError(title, xhr) {
    const serverErrorMessage = !xhr.responseJSON ? `${xhr.status}: ${xhr.statusText}` : xhr.responseJSON.message;
    console.error(title + "\n" + serverErrorMessage);
    const notification = $(`<div class="toast" role="alert" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 30rem"/>`)
            .append($(`<div class="toast-header bg-danger">
                            <strong class="mr-auto text-dark">Error</strong>
                            <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>`))
            .append($(`<div class="toast-body"/>`)
                    .append($(`<p/>`).text(title))
                    .append($(`<pre/>`)
                            .append($(`<code/>`).text(serverErrorMessage))
                    )
            );
    $("#notificationPanel").append(notification);
    notification.toast({delay: 30000});
    notification.toast('show');
}

$(document).ready( function() {
    $.ajaxSetup({
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    });
    // Extend jQuery to support $.put() and $.delete()
    jQuery.each( [ "put", "delete" ], function( i, method ) {
        jQuery[method] = function (url, data, callback, type) {
            if (jQuery.isFunction(data)) {
                type = type || callback;
                callback = data;
                data = undefined;
            }
            return jQuery.ajax({
                url: url,
                type: method,
                dataType: type,
                data: data,
                success: callback
            });
        };
    });


    $("#refreshButton").click(function() {
        refreshWingsSchedule();
    });
    $("#solveButton").click(function() {
        solve();
    });
    $("#stopSolvingButton").click(function() {
        stopSolving();
    });
    $("#addLessonSubmitButton").click(function() {
        addLesson();
    });
    $("#addTimeslotSubmitButton").click(function() {
        addTimeslot();
    });
    $("#addRoomSubmitButton").click(function() {
        addRoom();
    });

    refreshWingsSchedule();
});

// ****************************************************************************
// TangoColorFactory
// ****************************************************************************

const SEQUENCE_1 = [0x8AE234, 0xFCE94F, 0x729FCF, 0xE9B96E, 0xAD7FA8];
const SEQUENCE_2 = [0x73D216, 0xEDD400, 0x3465A4, 0xC17D11, 0x75507B];

var colorMap = new Map;
var nextColorCount = 0;

function pickColor(object) {
    let color = colorMap[object];
    if (color !== undefined) {
        return color;
    }
    color = nextColor();
    colorMap[object] = color;
    return color;
}

function nextColor() {
    let color;
    let colorIndex = nextColorCount % SEQUENCE_1.length;
    let shadeIndex = Math.floor(nextColorCount / SEQUENCE_1.length);
    if (shadeIndex === 0) {
        color = SEQUENCE_1[colorIndex];
    } else if (shadeIndex === 1) {
        color = SEQUENCE_2[colorIndex];
    } else {
        shadeIndex -= 3;
        let floorColor = SEQUENCE_2[colorIndex];
        let ceilColor = SEQUENCE_1[colorIndex];
        let base = Math.floor((shadeIndex / 2) + 1);
        let divisor = 2;
        while (base >= divisor) {
            divisor *= 2;
        }
        base = (base * 2) - divisor + 1;
        let shadePercentage = base / divisor;
        color = buildPercentageColor(floorColor, ceilColor, shadePercentage);
    }
    nextColorCount++;
    return "#" + color.toString(16);
}

function buildPercentageColor(floorColor, ceilColor, shadePercentage) {
    let red = (floorColor & 0xFF0000) + Math.floor(shadePercentage * ((ceilColor & 0xFF0000) - (floorColor & 0xFF0000))) & 0xFF0000;
    let green = (floorColor & 0x00FF00) + Math.floor(shadePercentage * ((ceilColor & 0x00FF00) - (floorColor & 0x00FF00))) & 0x00FF00;
    let blue = (floorColor & 0x0000FF) + Math.floor(shadePercentage * ((ceilColor & 0x0000FF) - (floorColor & 0x0000FF))) & 0x0000FF;
    return red | green | blue;
}
