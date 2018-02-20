package com.raspisaniyevuzov.app.event;

public class UpdateAvatarEvent {
    public final String path;

    public UpdateAvatarEvent(String path) {
        this.path = path;
    }
}
