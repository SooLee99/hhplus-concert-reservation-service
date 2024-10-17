package org.example.hhplusconcertreservationservice.users.application.exception;

public class QueueEntryNotFoundException extends RuntimeException {

    public QueueEntryNotFoundException() {
        super(ExceptionMessage.QUEUE_ENTRY_NOT_FOUND.getMessage());
    }
}