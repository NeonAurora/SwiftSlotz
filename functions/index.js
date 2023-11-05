const functions = require('firebase-functions');
const admin = require('firebase-admin');
const logger = require("firebase-functions/logger");
admin.initializeApp();

exports.movePastAppointments = functions.database.ref('/users/{userId}/appointments/{appointmentId}')
    .onWrite((change, context) => {
        const appointment = change.after.val();

        // If appointment is deleted or doesn't have a time, exit.
        if (!appointment || !appointment.time || !appointment.date) {
            return null;
        }

        const currentTime = new Date();
        const appointmentTime = new Date(appointment.date + ' ' + appointment.time);

        // If the appointment time has passed, move it to PastAppointments.
        if (appointmentTime < currentTime) {
            const userId = context.params.userId;
            const appointmentId = context.params.appointmentId;

            // Move to PastAppointments and remove from current appointments.
            const pastAppointmentsRef = admin.database().ref(`/users/${userId}/PastAppointments/${appointmentId}`);
            return pastAppointmentsRef.set(appointment)
                .then(() => {
                    return change.after.ref.remove();
                });
        }

        return null;
    });