package org.example.hhplusconcertreservationservice.users.application.exception;

public class QueueCreationFailedException extends RuntimeException {

    public QueueCreationFailedException() {
        super(ExceptionMessage.QUEUE_CREATION_FAILED.getMessage());
    }
}

